package algorithms;

/*
 * Algorithm: Edit Distance (Levenshtein) — used for Fuzzy / typo-tolerant search
 * Complexity: O(m · n) where m, n are the lengths of the two strings
 *
 * How it works:
 *  Classic DP table:
 *   dp[i][j] = minimum edits (insert/delete/replace) to transform a[0..i-1] → b[0..j-1]
 *
 *  containsFuzzy() checks whether any individual word in the text is within
 *  'threshold' edits of the query word — giving typo-tolerance at the word level.
 */
public class EditDistanceMatcher {

    private long comparisons;

    public EditDistanceMatcher() { this.comparisons = 0; }

    public void resetComparisons() { comparisons = 0; }
    public long getComparisons() { return comparisons; }

    /**
     * Classic Levenshtein edit distance between two strings.
     */
    public int editDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int m = a.length(), n = b.length();
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        for (int j = 0; j <= n; j++) prev[j] = j;

        for (int i = 1; i <= m; i++) {
            curr[0] = i;
            for (int j = 1; j <= n; j++) {
                comparisons++;
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    curr[j] = prev[j - 1];
                } else {
                    curr[j] = 1 + Math.min(prev[j - 1], Math.min(prev[j], curr[j - 1]));
                }
            }
            // swap
            int[] tmp = prev; prev = curr; curr = tmp;
        }
        return prev[n];
    }

    /**
     * Returns the minimum edit distance between the query and any single
     * whitespace-separated word in the text.
     */
    public int bestWordDistance(String query, String text) {
        String[] words = text.toLowerCase().split("\\s+");
        int best = Integer.MAX_VALUE;
        for (String word : words) {
            // strip non-alpha
            String clean = word.replaceAll("[^a-z]", "");
            if (clean.isEmpty()) continue;
            int d = editDistance(query, clean);
            if (d < best) best = d;
        }
        return best == Integer.MAX_VALUE ? query.length() : best;
    }

    /**
     * Returns true if any word in 'text' is within 'threshold' edits of 'query'.
     * threshold=0 → exact match; threshold=1 → one typo allowed; threshold=2 → two typos.
     */
    public boolean containsFuzzy(String text, String query, int threshold) {
        if (query == null || query.isEmpty()) return true;
        if (text == null || text.isEmpty()) return false;
        return bestWordDistance(query.toLowerCase(), text) <= threshold;
    }
}
