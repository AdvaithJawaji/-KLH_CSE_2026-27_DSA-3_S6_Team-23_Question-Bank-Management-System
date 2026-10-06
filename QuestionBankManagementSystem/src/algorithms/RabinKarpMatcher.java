package algorithms;

/*
 * Algorithm: Rabin-Karp rolling-hash string matcher
 * Average Complexity: O(n + m)  Worst case: O(n·m)
 *
 * How it works:
 *  Hash the pattern. Slide a window of the same length across the text,
 *  maintaining a rolling hash. When hashes match, do a character-level
 *  verification to handle collisions.
 *
 * Returns true if the pattern is found anywhere in text (case-insensitive).
 */
public class RabinKarpMatcher {

    private static final int BASE  = 31;   // prime base for polynomial hash
    private static final int MOD   = 1_000_000_007; // large prime modulus

    private long comparisons;

    public RabinKarpMatcher() { this.comparisons = 0; }

    public void resetComparisons() { comparisons = 0; }
    public long getComparisons() { return comparisons; }

    public boolean contains(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) return true;
        if (text == null || text.isEmpty()) return false;

        String t = text.toLowerCase();
        String p = pattern.toLowerCase();
        int n = t.length();
        int m = p.length();
        if (m > n) return false;

        // Precompute BASE^(m-1) mod MOD
        long power = 1;
        for (int i = 0; i < m - 1; i++) power = (power * BASE) % MOD;

        // Hash of pattern and first window of text
        long patHash = 0, winHash = 0;
        for (int i = 0; i < m; i++) {
            patHash = (patHash * BASE + (p.charAt(i) - 'a' + 1)) % MOD;
            winHash = (winHash * BASE + (t.charAt(i) - 'a' + 1)) % MOD;
        }

        for (int i = 0; i <= n - m; i++) {
            comparisons++;
            if (winHash == patHash) {
                // Hash hit — verify character by character
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    comparisons++;
                    if (t.charAt(i + j) != p.charAt(j)) { match = false; break; }
                }
                if (match) return true;
            }
            // Roll the hash
            if (i < n - m) {
                winHash = (BASE * (winHash - (t.charAt(i) - 'a' + 1) * power % MOD + MOD) % MOD
                          + (t.charAt(i + m) - 'a' + 1)) % MOD;
            }
        }
        return false;
    }
}
