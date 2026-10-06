package algorithms;

/*
 * Algorithm: Z-Function (Gusfield's linear-time string matching)
 * Complexity: O(n + m)
 *
 * How it works:
 *  Build the concatenated string  S = pattern + "$" + text
 *  Compute Z[i] = length of the longest substring starting at S[i]
 *  that is also a prefix of S.
 *  A match occurs whenever Z[i] == m (pattern length).
 *
 * Returns true if the pattern is found anywhere in text (case-insensitive).
 */
public class ZMatcher {

    private long comparisons;

    public ZMatcher() { this.comparisons = 0; }

    public void resetComparisons() { comparisons = 0; }
    public long getComparisons() { return comparisons; }

    public boolean contains(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) return true;
        if (text == null || text.isEmpty()) return false;

        String t = text.toLowerCase();
        String p = pattern.toLowerCase();
        int m = p.length();

        // Concatenate: pattern + sentinel + text
        String s = p + "$" + t;
        int n = s.length();
        int[] z = new int[n];

        // Standard Z-array computation
        int l = 0, r = 0;
        for (int i = 1; i < n; i++) {
            if (i < r) {
                z[i] = Math.min(r - i, z[i - l]);
            }
            while (i + z[i] < n) {
                comparisons++;
                if (s.charAt(z[i]) == s.charAt(i + z[i])) {
                    z[i]++;
                } else {
                    break;
                }
            }
            if (i + z[i] > r) {
                l = i;
                r = i + z[i];
            }
            if (z[i] == m) {
                return true; // match found at position i - m - 1 in text
            }
        }
        return false;
    }
}
