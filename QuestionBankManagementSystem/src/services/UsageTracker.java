package services;

import model.Question;
import model.UsageRecord;
import storage.UsageFileHandler;
import structures.CustomArrayList;

/*
 * Service: UsageTracker
 * Purpose: Maintains an in-memory map of question-ID → usage count,
 *          backed by data/usage_history.txt via UsageFileHandler.
 *
 * PaperGenerator calls recordUsage() for every question placed into a paper.
 * ConsoleUI calls printTopUsed() to show the usage history view.
 *
 * Note: We use a CustomArrayList<UsageRecord> instead of java.util.HashMap
 *       to keep the DSA requirement of no library collections.
 *       Lookup is O(n) — fine for a question bank of typical size.
 */
public class UsageTracker {

    private final UsageFileHandler fileHandler;
    private final CustomArrayList<UsageRecord> records;

    public UsageTracker(UsageFileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.records = fileHandler.loadAll();
    }

    /** Record one usage event and persist it to disk. */
    public void recordUsage(UsageRecord rec) {
        records.add(rec);
        fileHandler.append(rec);
    }

    /** Returns the total times a question has appeared in any generated paper. */
    public int getUsageCount(String questionId) {
        int total = 0;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getQuestionId().equals(questionId)) {
                total += records.get(i).getUsageCount();
            }
        }
        return total;
    }

    /** Returns all usage records (newest last). */
    public CustomArrayList<UsageRecord> getAllRecords() {
        return records;
    }

    /**
     * Returns question IDs sorted by descending usage count,
     * limited to top-N entries. Uses a simple insertion sort (stable, in-place).
     * @param questionList full question list (for name lookup)
     * @param topN         how many rows to return (0 = all)
     */
    public void printUsageReport(CustomArrayList<Question> questionList, int topN) {
        // Build aggregated counts per question ID
        CustomArrayList<String> ids    = new CustomArrayList<>();
        CustomArrayList<Integer> counts = new CustomArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            String qid = records.get(i).getQuestionId();
            int idx = indexOf(ids, qid);
            if (idx == -1) {
                ids.add(qid);
                counts.add(records.get(i).getUsageCount());
            } else {
                counts.set(idx, counts.get(idx) + records.get(i).getUsageCount());
            }
        }

        if (ids.isEmpty()) {
            System.out.println("  No usage history yet — generate a paper first.");
            return;
        }

        // Insertion sort by count descending
        for (int i = 1; i < ids.size(); i++) {
            String keyId = ids.get(i);
            int keyCnt   = counts.get(i);
            int j = i - 1;
            while (j >= 0 && counts.get(j) < keyCnt) {
                ids.set(j + 1, ids.get(j));
                counts.set(j + 1, counts.get(j));
                j--;
            }
            ids.set(j + 1, keyId);
            counts.set(j + 1, keyCnt);
        }

        int limit = (topN > 0 && topN < ids.size()) ? topN : ids.size();
        System.out.printf("  %-8s %-60s %-10s%n", "ID", "Question (truncated)", "Usage");
        System.out.println("  " + "-".repeat(80));
        for (int i = 0; i < limit; i++) {
            String qid = ids.get(i);
            int cnt    = counts.get(i);
            String text = getQuestionText(questionList, qid);
            String shortText = text.length() > 56 ? text.substring(0, 53) + "..." : text;
            System.out.printf("  %-8s %-60s %-10d%n", qid, shortText, cnt);
        }
    }

    /** Print the raw usage log (most recent first). */
    public void printRawLog(int limit) {
        if (records.isEmpty()) {
            System.out.println("  No usage records yet.");
            return;
        }
        System.out.printf("  %-10s %-8s %-12s %-12s %-6s%n",
                "Question", "Paper", "Date", "User", "Count");
        System.out.println("  " + "-".repeat(52));
        int start = Math.max(0, records.size() - limit);
        for (int i = records.size() - 1; i >= start; i--) {
            UsageRecord r = records.get(i);
            System.out.printf("  %-10s %-8s %-12s %-12s %-6d%n",
                    r.getQuestionId(), r.getPaperId(), r.getDate(), r.getUser(), r.getUsageCount());
        }
    }

    // ---- private helpers ----

    private int indexOf(CustomArrayList<String> list, String val) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(val)) return i;
        }
        return -1;
    }

    private String getQuestionText(CustomArrayList<Question> questions, String id) {
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId().equalsIgnoreCase(id)) {
                return questions.get(i).getQuestionText();
            }
        }
        return "(question not found)";
    }
}
