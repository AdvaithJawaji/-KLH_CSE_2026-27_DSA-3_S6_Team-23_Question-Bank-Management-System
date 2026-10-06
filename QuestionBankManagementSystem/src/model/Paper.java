package model;

import structures.CustomArrayList;

/*
 * Model: Paper
 * Purpose: A generated question paper, stored in data/generated_papers.txt
 * File line fields: Paper ID | Date | Subject | Total Marks | QuestionID1,QuestionID2,...
 *
 * Unit/Difficulty/CO distribution are re-derivable from the question IDs plus
 * questions.txt, so only the essential, non-derivable fields are persisted
 * (keeps the file small and avoids duplicated/inconsistent data).
 */
public class Paper {

    public static final String DELIMITER = "\\|";
    public static final String DELIMITER_LITERAL = "|";
    private static final String ID_SEPARATOR = ",";

    private String paperId;
    private String date;
    private String subject;
    private int totalMarks;
    private CustomArrayList<String> questionIds;

    public Paper(String paperId, String date, String subject, int totalMarks, CustomArrayList<String> questionIds) {
        this.paperId = paperId;
        this.date = date;
        this.subject = subject;
        this.totalMarks = totalMarks;
        this.questionIds = questionIds;
    }

    public String getPaperId() { return paperId; }
    public String getDate() { return date; }
    public String getSubject() { return subject; }
    public int getTotalMarks() { return totalMarks; }
    public CustomArrayList<String> getQuestionIds() { return questionIds; }

    public String toFileLine() {
        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < questionIds.size(); i++) {
            if (i > 0) ids.append(ID_SEPARATOR);
            ids.append(questionIds.get(i));
        }
        return String.join(DELIMITER_LITERAL, paperId, date, subject, String.valueOf(totalMarks), ids.toString());
    }

    public static Paper fromFileLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length != 5) return null;
        try {
            int totalMarks = Integer.parseInt(parts[3].trim());
            CustomArrayList<String> ids = new CustomArrayList<>();
            if (!parts[4].isEmpty()) {
                for (String id : parts[4].split(ID_SEPARATOR)) {
                    if (!id.trim().isEmpty()) ids.add(id.trim());
                }
            }
            return new Paper(parts[0], parts[1], parts[2], totalMarks, ids);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return paperId + " (" + subject + ", " + date + ", " + totalMarks + " marks, "
                + questionIds.size() + " questions)";
    }
}
