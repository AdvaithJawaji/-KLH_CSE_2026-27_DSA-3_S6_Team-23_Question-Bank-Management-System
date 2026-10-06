package services;

import algorithms.AhoCorasick;
import algorithms.EditDistanceMatcher;
import algorithms.KMPMatcher;
import algorithms.NaiveMatcher;
import algorithms.RabinKarpMatcher;
import algorithms.ZMatcher;
import model.Question;
import model.SearchResult;
import structures.CustomArrayList;

/*
 * Service: SearchEngine
 * Purpose: Unified search interface over all DSA string-matching algorithms.
 *
 * Supported algorithms:
 *   1 - Naive (brute force O(n·m))
 *   2 - KMP   (O(n+m))
 *   3 - Z-Function (O(n+m))
 *   4 - Rabin-Karp (O(n+m) avg)
 *   5 - Aho-Corasick (multi-keyword, O(n+Σ|patterns|))
 *   6 - Fuzzy / Edit Distance (Levenshtein DP, O(n·m))
 *
 * Each search returns a SearchResult containing:
 *  - matched question IDs
 *  - total character comparisons made
 *  - wall-clock execution time in ms
 *  - big-O complexity string
 */
public class SearchEngine {

    public static final int ALGO_NAIVE         = 1;
    public static final int ALGO_KMP           = 2;
    public static final int ALGO_Z_FUNCTION    = 3;
    public static final int ALGO_RABIN_KARP    = 4;
    public static final int ALGO_AHO_CORASICK  = 5;
    public static final int ALGO_FUZZY         = 6;

    /** Default fuzzy threshold — edit distance ≤ 2 counts as a match */
    public static final int DEFAULT_FUZZY_THRESHOLD = 2;

    private final NaiveMatcher naive = new NaiveMatcher();
    private final KMPMatcher kmp = new KMPMatcher();
    private final ZMatcher zFunc = new ZMatcher();
    private final RabinKarpMatcher rabinKarp = new RabinKarpMatcher();
    private final EditDistanceMatcher fuzzy = new EditDistanceMatcher();

    /**
     * Searches all questions using the specified algorithm.
     *
     * @param algorithmId   One of the ALGO_* constants.
     * @param query         The search term (single keyword; for Aho-Corasick,
     *                      space-separate multiple keywords).
     * @param questions     The full in-memory question list.
     * @return              A SearchResult with matched IDs and performance stats.
     */
    public SearchResult search(int algorithmId, String query, CustomArrayList<Question> questions) {
        String algoName = algoName(algorithmId);
        SearchResult result = new SearchResult(algoName, query);

        long startNs = System.nanoTime();

        switch (algorithmId) {
            case ALGO_NAIVE -> {
                searchNaive(query, questions, result);
                result.setComplexity("O(n · m)");
            }
            case ALGO_KMP -> {
                searchKMP(query, questions, result);
                result.setComplexity("O(n + m)");
            }
            case ALGO_Z_FUNCTION -> {
                searchZFunction(query, questions, result);
                result.setComplexity("O(n + m)");
            }
            case ALGO_RABIN_KARP -> {
                searchRabinKarp(query, questions, result);
                result.setComplexity("O(n + m) avg, O(n·m) worst");
            }
            case ALGO_AHO_CORASICK -> {
                searchAhoCorasick(query, questions, result);
                result.setComplexity("O(n + Σ|patterns| + matches)");
            }
            case ALGO_FUZZY -> {
                searchFuzzy(query, questions, result);
                result.setComplexity("O(n · m) Levenshtein DP");
            }
            default -> result.setComplexity("Unknown");
        }

        long elapsedNs = System.nanoTime() - startNs;
        result.setExecutionTimeMs(elapsedNs / 1_000_000.0);

        return result;
    }

    // ----------------------------------------------------------------
    // -- Private helpers per algorithm -------------------------------
    // ----------------------------------------------------------------

    private void searchNaive(String query, CustomArrayList<Question> questions, SearchResult result) {
        naive.resetComparisons();
        String q = query.trim().toLowerCase();
        for (int i = 0; i < questions.size(); i++) {
            Question qu = questions.get(i);
            String hay = buildHaystack(qu);
            if (naive.contains(hay, q)) {
                result.addMatch(qu.getId());
            }
        }
        result.setComparisons(naive.getComparisons());
    }

    private void searchKMP(String query, CustomArrayList<Question> questions, SearchResult result) {
        kmp.resetComparisons();
        String q = query.trim().toLowerCase();
        for (int i = 0; i < questions.size(); i++) {
            Question qu = questions.get(i);
            if (kmp.contains(buildHaystack(qu), q)) {
                result.addMatch(qu.getId());
            }
        }
        result.setComparisons(kmp.getComparisons());
    }

    private void searchZFunction(String query, CustomArrayList<Question> questions, SearchResult result) {
        zFunc.resetComparisons();
        String q = query.trim().toLowerCase();
        for (int i = 0; i < questions.size(); i++) {
            Question qu = questions.get(i);
            if (zFunc.contains(buildHaystack(qu), q)) {
                result.addMatch(qu.getId());
            }
        }
        result.setComparisons(zFunc.getComparisons());
    }

    private void searchRabinKarp(String query, CustomArrayList<Question> questions, SearchResult result) {
        rabinKarp.resetComparisons();
        String q = query.trim().toLowerCase();
        for (int i = 0; i < questions.size(); i++) {
            Question qu = questions.get(i);
            if (rabinKarp.contains(buildHaystack(qu), q)) {
                result.addMatch(qu.getId());
            }
        }
        result.setComparisons(rabinKarp.getComparisons());
    }

    private void searchAhoCorasick(String query, CustomArrayList<Question> questions, SearchResult result) {
        // Build automaton from space-separated keywords
        String[] keywords = query.trim().toLowerCase().split("\\s+");
        AhoCorasick ac = new AhoCorasick();
        for (String kw : keywords) {
            if (!kw.isEmpty()) ac.addPattern(kw);
        }
        ac.buildFailLinks();

        long totalComparisons = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question qu = questions.get(i);
            ac.resetComparisons();
            CustomArrayList<String> hits = ac.search(buildHaystack(qu));
            totalComparisons += ac.getComparisons();
            if (hits.size() > 0) {
                result.addMatch(qu.getId());
            }
        }
        result.setComparisons(totalComparisons);
    }

    private void searchFuzzy(String query, CustomArrayList<Question> questions, SearchResult result) {
        fuzzy.resetComparisons();
        String q = query.trim().toLowerCase();
        for (int i = 0; i < questions.size(); i++) {
            Question qu = questions.get(i);
            if (fuzzy.containsFuzzy(buildHaystack(qu), q, DEFAULT_FUZZY_THRESHOLD)) {
                result.addMatch(qu.getId());
            }
        }
        result.setComparisons(fuzzy.getComparisons());
    }

    // ----------------------------------------------------------------
    // -- Utility -----------------------------------------------------
    // ----------------------------------------------------------------

    /**
     * Builds the searchable text from a question: text + tags + subject.
     */
    private String buildHaystack(Question q) {
        return q.getQuestionText() + " " + q.getTags() + " " + q.getSubject() + " " + q.getUnit();
    }

    public static String algoName(int algorithmId) {
        return switch (algorithmId) {
            case ALGO_NAIVE -> "Naive";
            case ALGO_KMP -> "KMP";
            case ALGO_Z_FUNCTION -> "Z-Function";
            case ALGO_RABIN_KARP -> "Rabin-Karp";
            case ALGO_AHO_CORASICK -> "Aho-Corasick";
            case ALGO_FUZZY -> "Fuzzy (Edit Distance)";
            default -> "Unknown";
        };
    }
}
