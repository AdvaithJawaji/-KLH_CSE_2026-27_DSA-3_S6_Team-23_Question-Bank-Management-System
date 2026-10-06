package algorithms;

/*
 * Algorithm: Naive (Brute Force) string matcher — baseline for comparisons
 * Complexity: O(n · m)  worst case
 *
 * How it works:
 *  For every position i in the text, try to match pattern starting at i.
 *  If any character mismatches, move to position i+1 and restart.
 *
 * Useful for comparing performance against KMP and Z-Function.
 */
public class NaiveMatcher {

    private long comparisons;

    public NaiveMatcher() { this.comparisons = 0; }

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

        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m) {
                comparisons++;
                if (t.charAt(i + j) != p.charAt(j)) break;
                j++;
            }
            if (j == m) return true; // match at i
        }
        return false;
    }
}
