package algorithms;

/*
 * Algorithm: KMP (Knuth-Morris-Pratt) string matcher
 * Complexity: O(n + m) where n = text length, m = pattern length
 *
 * How it works:
 *  1. Build a failure function (partial match table) for the pattern.
 *  2. Scan the text; when a mismatch occurs, use the failure function
 *     to skip back only as far as necessary — never re-examine a character.
 *
 * Returns the index of the first occurrence, or -1 if not found.
 * comparisons[] is incremented for each character comparison so the
 * caller can report algorithm statistics.
 */
public class KMPMatcher {

    private long comparisons;

    public KMPMatcher() {
        this.comparisons = 0;
    }

    /** Reset comparison counter before each new search. */
    public void resetComparisons() { comparisons = 0; }

    public long getComparisons() { return comparisons; }

    /**
     * Returns true if pattern is found anywhere inside text (case-insensitive).
     */
    public boolean contains(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) return true;
        if (text == null || text.isEmpty()) return false;

        String t = text.toLowerCase();
        String p = pattern.toLowerCase();
        int n = t.length();
        int m = p.length();

        int[] fail = buildFailure(p);
        int j = 0; // index into pattern

        for (int i = 0; i < n; i++) {
            comparisons++;
            while (j > 0 && t.charAt(i) != p.charAt(j)) {
                j = fail[j - 1];
                comparisons++;
            }
            if (t.charAt(i) == p.charAt(j)) {
                j++;
            }
            if (j == m) {
                return true; // found at index (i - m + 1)
            }
        }
        return false;
    }

    /**
     * Builds the KMP failure (partial match) table for the given pattern.
     * fail[i] = length of the longest proper prefix of pattern[0..i]
     *           that is also a suffix.
     */
    private int[] buildFailure(String pattern) {
        int m = pattern.length();
        int[] fail = new int[m];
        fail[0] = 0;
        int k = 0;
        for (int i = 1; i < m; i++) {
            while (k > 0 && pattern.charAt(i) != pattern.charAt(k)) {
                k = fail[k - 1];
            }
            if (pattern.charAt(i) == pattern.charAt(k)) {
                k++;
            }
            fail[i] = k;
        }
        return fail;
    }
}
