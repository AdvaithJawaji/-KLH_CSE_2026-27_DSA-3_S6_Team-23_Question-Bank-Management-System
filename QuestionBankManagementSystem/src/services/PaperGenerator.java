package services;

import model.Paper;
import model.Question;
import model.UsageRecord;
import storage.PaperFileHandler;
import storage.UsageFileHandler;
import structures.CustomArrayList;

import java.time.LocalDate;

/*
 * Service: PaperGenerator
 * Purpose: Select a balanced set of questions for a question paper using a
 *          0/1 Knapsack DP approach, then record usage.
 *
 * Algorithm — 0/1 Knapsack DP:
 *  - Items     = questions
 *  - Weight    = question marks
 *  - Capacity  = targetMarks
 *  - Value     = 1 per question (we want to maximize the number of questions
 *                that fit within targetMarks)
 *
 *  After the DP table is filled, backtrack to recover the selected subset.
 *  Questions with higher prior usage are penalized by being moved to the
 *  back of the candidate list before the DP runs (lower priority pool).
 *
 * If an exact-fit paper cannot be generated (the DP produces fewer questions
 * than requested), the generator fills remaining slots greedily from the
 * unused pool to still return a full paper.
 */
public class PaperGenerator {

    private final PaperFileHandler paperFileHandler;
    private final UsageFileHandler usageFileHandler;
    private final UsageTracker usageTracker;

    // Paper ID counter — read highest existing ID from file on init
    private int nextPaperNum;

    public PaperGenerator(PaperFileHandler paperFileHandler,
                          UsageFileHandler usageFileHandler,
                          UsageTracker usageTracker) {
        this.paperFileHandler = paperFileHandler;
        this.usageFileHandler = usageFileHandler;
        this.usageTracker     = usageTracker;
        this.nextPaperNum     = computeNextPaperNum();
    }

    /**
     * Generate a question paper.
     *
     * @param subject     Filter to this subject, or null/"" for all subjects.
     * @param count       Desired number of questions.
     * @param targetMarks Total marks the paper should carry.
     * @param allQuestions Full in-memory question list.
     * @param username    The currently logged-in user (for the usage record).
     * @return The generated Paper (also persisted to file).
     */
    public Paper generatePaper(String subject, int count, int targetMarks,
                               CustomArrayList<Question> allQuestions, String username) {

        // ---- 1. Filter by subject ----
        CustomArrayList<Question> pool = new CustomArrayList<>();
        for (int i = 0; i < allQuestions.size(); i++) {
            Question q = allQuestions.get(i);
            if (subject == null || subject.isEmpty() || subject.equalsIgnoreCase("All")
                    || q.getSubject().equalsIgnoreCase(subject)) {
                pool.add(q);
            }
        }

        // ---- 2. Sort pool: lower usage first (penalize overused questions) ----
        mergeSort(pool, 0, pool.size() - 1);

        // ---- 3. Limit pool to 'count' candidates for the DP (efficiency) ----
        //         but keep the full pool for greedy fill-up
        int dpCap = Math.min(pool.size(), count * 4); // widen slightly for better combos

        // ---- 4. 0/1 Knapsack DP ----
        CustomArrayList<Question> selected = knapsackSelect(pool, dpCap, count, targetMarks);

        // ---- 5. Greedy fill-up if DP couldn't fill the paper ----
        if (selected.size() < count) {
            for (int i = 0; i < pool.size() && selected.size() < count; i++) {
                Question q = pool.get(i);
                if (!contains(selected, q.getId())) {
                    selected.add(q);
                }
            }
        }

        // ---- 6. Build Paper object ----
        String paperId = String.format("P%03d", nextPaperNum++);
        String date    = LocalDate.now().toString();
        String subLabel = (subject == null || subject.isEmpty()) ? "All Subjects" : subject;

        int actualMarks = 0;
        CustomArrayList<String> ids = new CustomArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            ids.add(selected.get(i).getId());
            actualMarks += selected.get(i).getMarks();
        }

        Paper paper = new Paper(paperId, date, subLabel, actualMarks, ids);

        // ---- 7. Persist paper and usage records ----
        paperFileHandler.append(paper);
        for (int i = 0; i < selected.size(); i++) {
            UsageRecord rec = new UsageRecord(
                    selected.get(i).getId(), paperId, date, username, 1);
            usageFileHandler.append(rec);
            usageTracker.recordUsage(rec);
        }

        return paper;
    }

    // ----------------------------------------------------------------
    // -- 0/1 Knapsack DP --------------------------------------------
    // ----------------------------------------------------------------

    /**
     * Selects up to 'maxCount' questions from pool[0..poolCap-1]
     * whose total marks ≤ targetMarks, maximising the number of questions.
     */
    private CustomArrayList<Question> knapsackSelect(CustomArrayList<Question> pool,
                                                     int poolCap, int maxCount, int targetMarks) {
        // dp[i][w] = max number of questions we can pick from pool[0..i-1]
        //            with total weight ≤ w AND count ≤ maxCount
        // To keep it simple: dp[w] = max questions with total marks ≤ w
        // Two constraints (count AND marks) → extend to 2-D

        int n = Math.min(poolCap, pool.size());
        if (n == 0 || targetMarks <= 0) return new CustomArrayList<>();

        // dp[i][j] = max #questions using first i items, exactly j marks used
        // We'll use a 1-D rolling array for marks
        // But we also cap by maxCount, so value = #questions selected

        int W = targetMarks;
        // dp[w] = max questions whose total marks == w (or ≤ W for relaxation)
        int[] dp = new int[W + 1];
        int[][] choice = new int[n + 1][W + 1]; // 1 = taken, 0 = not taken

        for (int i = 1; i <= n; i++) {
            int wi = pool.get(i - 1).getMarks();
            // traverse in reverse to avoid using item twice (0/1)
            for (int w = W; w >= wi; w--) {
                if (dp[w - wi] + 1 > dp[w]) {
                    dp[w] = dp[w - wi] + 1;
                    choice[i][w] = 1;
                } else {
                    choice[i][w] = 0;
                }
            }
        }

        // Find the weight w that gives the maximum number of questions ≤ maxCount
        int bestW = 0, bestCount = 0;
        for (int w = 0; w <= W; w++) {
            if (dp[w] > bestCount && dp[w] <= maxCount) {
                bestCount = dp[w];
                bestW = w;
            }
        }

        // Backtrack to recover selected items
        CustomArrayList<Question> selected = new CustomArrayList<>();
        int w = bestW;
        for (int i = n; i >= 1 && w > 0; i--) {
            if (choice[i][w] == 1) {
                selected.add(pool.get(i - 1));
                w -= pool.get(i - 1).getMarks();
            }
        }
        return selected;
    }

    // ----------------------------------------------------------------
    // -- Merge Sort (by usage count ascending) -----------------------
    // ----------------------------------------------------------------

    private void mergeSort(CustomArrayList<Question> list, int l, int r) {
        if (l >= r) return;
        int mid = (l + r) / 2;
        mergeSort(list, l, mid);
        mergeSort(list, mid + 1, r);
        merge(list, l, mid, r);
    }

    private void merge(CustomArrayList<Question> list, int l, int mid, int r) {
        int n1 = mid - l + 1, n2 = r - mid;
        Question[] left  = new Question[n1];
        Question[] right = new Question[n2];
        for (int i = 0; i < n1; i++) left[i]  = list.get(l + i);
        for (int i = 0; i < n2; i++) right[i] = list.get(mid + 1 + i);

        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            int uL = usageTracker.getUsageCount(left[i].getId());
            int uR = usageTracker.getUsageCount(right[j].getId());
            if (uL <= uR) list.set(k++, left[i++]);
            else          list.set(k++, right[j++]);
        }
        while (i < n1) list.set(k++, left[i++]);
        while (j < n2) list.set(k++, right[j++]);
    }

    // ----------------------------------------------------------------
    // -- Helpers -----------------------------------------------------
    // ----------------------------------------------------------------

    private boolean contains(CustomArrayList<Question> list, String id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) return true;
        }
        return false;
    }

    /** Returns the next P### number by scanning persisted papers. */
    private int computeNextPaperNum() {
        CustomArrayList<Paper> papers = paperFileHandler.loadAll();
        int max = 0;
        for (int i = 0; i < papers.size(); i++) {
            String pid = papers.get(i).getPaperId();
            if (pid != null && pid.length() > 1 && pid.charAt(0) == 'P') {
                try {
                    int num = Integer.parseInt(pid.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return max + 1;
    }

    public int getPapersGenerated() { return nextPaperNum - 1; }
}
