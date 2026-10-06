package model;

import structures.CustomArrayList;

/*
 * Model: SearchResult
 * Purpose: Holds the outcome of running a search/matching algorithm so the
 * UI and PerformanceAnalyzer (later phases) can display a uniform report:
 *   Algorithm | Query | Matches | Comparisons | Execution Time | Complexity
 * Not persisted to file - this is an in-memory reporting object only.
 */
public class SearchResult {

    private String algorithmName;
    private String query;
    private CustomArrayList<String> matchedQuestionIds;
    private long comparisons;
    private double executionTimeMs;
    private String complexity;

    public SearchResult(String algorithmName, String query) {
        this.algorithmName = algorithmName;
        this.query = query;
        this.matchedQuestionIds = new CustomArrayList<>();
        this.comparisons = 0;
        this.executionTimeMs = 0.0;
        this.complexity = "";
    }

    public String getAlgorithmName() { return algorithmName; }
    public String getQuery() { return query; }
    public CustomArrayList<String> getMatchedQuestionIds() { return matchedQuestionIds; }
    public long getComparisons() { return comparisons; }
    public double getExecutionTimeMs() { return executionTimeMs; }
    public String getComplexity() { return complexity; }

    public void addMatch(String questionId) { matchedQuestionIds.add(questionId); }
    public void setComparisons(long comparisons) { this.comparisons = comparisons; }
    public void setExecutionTimeMs(double executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    public void setComplexity(String complexity) { this.complexity = complexity; }

    public void printReport() {
        System.out.println("---- SEARCH RESULT ----");
        System.out.println("Algorithm      : " + algorithmName);
        System.out.println("Query          : " + query);
        System.out.println("Matches        : " + matchedQuestionIds.size());
        System.out.println("Comparisons    : " + comparisons);
        System.out.printf("Execution Time : %.4f ms%n", executionTimeMs);
        System.out.println("Complexity     : " + complexity);
        System.out.println("------------------------");
    }
}
