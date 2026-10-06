package model;

/*
 * Model: Question
 * Purpose: Represents a single question record loaded from data/questions.txt
 * Fields: ID | Subject | Unit | Difficulty | CO | Marks | Question Text | Tags
 *
 * This class is a plain data holder (POJO). It knows how to turn itself into
 * a delimited line for file storage, and how to be rebuilt from one.
 * The delimiter used is "|". If question text or tags contain "|", they are
 * escaped as "\p" on write and restored on read so records never break.
 */
public class Question {

    public static final String DELIMITER = "\\|";
    public static final String DELIMITER_LITERAL = "|";
    private static final String ESCAPED_PIPE = "\\p";

    private String id;          // e.g. Q001
    private String subject;     // e.g. Data Structures
    private String unit;        // e.g. Unit 3
    private String difficulty;  // Easy / Medium / Hard
    private String co;          // e.g. CO3
    private int marks;          // e.g. 10
    private String questionText;
    private String tags;        // space or comma separated keywords

    public Question(String id, String subject, String unit, String difficulty,
                     String co, int marks, String questionText, String tags) {
        this.id = id;
        this.subject = subject;
        this.unit = unit;
        this.difficulty = difficulty;
        this.co = co;
        this.marks = marks;
        this.questionText = questionText;
        this.tags = tags;
    }

    // ---------- Getters / Setters ----------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getCo() { return co; }
    public void setCo(String co) { this.co = co; }

    public int getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    // ---------- Serialization ----------

    /** Escape delimiter characters so a record always stays on one line. */
    private static String escape(String value) {
        if (value == null) return "";
        return value.replace(DELIMITER_LITERAL, ESCAPED_PIPE);
    }

    private static String unescape(String value) {
        if (value == null) return "";
        return value.replace(ESCAPED_PIPE, DELIMITER_LITERAL);
    }

    /** Convert this question into one line for questions.txt */
    public String toFileLine() {
        return String.join(DELIMITER_LITERAL,
                escape(id),
                escape(subject),
                escape(unit),
                escape(difficulty),
                escape(co),
                String.valueOf(marks),
                escape(questionText),
                escape(tags));
    }

    /**
     * Parse one line from questions.txt back into a Question.
     * Returns null if the line is malformed (caller should skip it, not crash).
     */
    public static Question fromFileLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length != 8) return null;
        try {
            int marks = Integer.parseInt(parts[5].trim());
            return new Question(
                    unescape(parts[0]),
                    unescape(parts[1]),
                    unescape(parts[2]),
                    unescape(parts[3]),
                    unescape(parts[4]),
                    marks,
                    unescape(parts[6]),
                    unescape(parts[7])
            );
        } catch (NumberFormatException e) {
            return null; // invalid marks field -> skip malformed record
        }
    }

    @Override
    public String toString() {
        return id + " [" + subject + " / " + unit + " / " + difficulty + " / " + co
                + " / " + marks + "m] " + questionText;
    }
}
