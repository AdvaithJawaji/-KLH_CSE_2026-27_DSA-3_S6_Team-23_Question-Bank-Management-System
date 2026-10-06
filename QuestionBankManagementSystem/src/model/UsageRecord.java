package model;

/*
 * Model: UsageRecord
 * Purpose: One row of data/usage_history.txt
 * Fields: Question ID | Paper ID | Date | User | Usage Count
 *
 * Appended every time a question is placed into a generated paper.
 * PaperGenerator/UsageTracker (Phase 2+) read all records for a question
 * and sum Usage Count to penalize overused questions.
 */
public class UsageRecord {

    public static final String DELIMITER = "\\|";
    public static final String DELIMITER_LITERAL = "|";

    private String questionId;
    private String paperId;
    private String date;   // yyyy-MM-dd
    private String user;
    private int usageCount;

    public UsageRecord(String questionId, String paperId, String date, String user, int usageCount) {
        this.questionId = questionId;
        this.paperId = paperId;
        this.date = date;
        this.user = user;
        this.usageCount = usageCount;
    }

    public String getQuestionId() { return questionId; }
    public String getPaperId() { return paperId; }
    public String getDate() { return date; }
    public String getUser() { return user; }
    public int getUsageCount() { return usageCount; }

    public String toFileLine() {
        return String.join(DELIMITER_LITERAL, questionId, paperId, date, user, String.valueOf(usageCount));
    }

    public static UsageRecord fromFileLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length != 5) return null;
        try {
            int count = Integer.parseInt(parts[4].trim());
            return new UsageRecord(parts[0], parts[1], parts[2], parts[3], count);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return questionId + " used in " + paperId + " on " + date + " by " + user + " (count=" + usageCount + ")";
    }
}
