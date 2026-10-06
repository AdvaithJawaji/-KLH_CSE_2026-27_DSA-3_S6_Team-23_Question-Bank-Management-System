package services;

import model.Question;
import storage.QuestionFileHandler;
import structures.CustomArrayList;

/*
 * Service: QuestionManager
 * Purpose: Business logic for Question CRUD. Keeps an in-memory
 * CustomArrayList<Question> in sync with data/questions.txt through
 * QuestionFileHandler. UI classes call this - they never touch the
 * file handler or file format directly.
 *
 * ID generation: sequential Q001, Q002, ... never reused, never duplicated,
 * independent of any database auto-increment mechanism.
 */
public class QuestionManager {

    private final QuestionFileHandler fileHandler;
    private CustomArrayList<Question> questions;

    public QuestionManager(QuestionFileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.questions = fileHandler.loadAll();
    }

    public CustomArrayList<Question> getAll() {
        return questions;
    }

    public int count() {
        return questions.size();
    }

    /** Generates the next sequential ID, e.g. Q001, Q002, ... based on the highest existing numeric suffix. */
    public String generateNextId() {
        int max = 0;
        for (int i = 0; i < questions.size(); i++) {
            String id = questions.get(i).getId();
            if (id != null && id.length() > 1 && id.charAt(0) == 'Q') {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {
                    // non-numeric suffix, skip
                }
            }
        }
        int next = max + 1;
        return String.format("Q%03d", next);
    }

    /** Adds a new question, auto-assigning its ID, and persists it. Returns the created Question. */
    public Question addQuestion(String subject, String unit, String difficulty, String co,
                                 int marks, String questionText, String tags) {
        String id = generateNextId();
        Question q = new Question(id, subject, unit, difficulty, co, marks, questionText, tags);
        questions.add(q);
        fileHandler.append(q);
        return q;
    }

    /** Finds a question by ID, or null if not found. */
    public Question findById(String id) {
        if (id == null) return null;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId().equalsIgnoreCase(id.trim())) {
                return questions.get(i);
            }
        }
        return null;
    }

    private int indexOf(String id) {
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId().equalsIgnoreCase(id.trim())) return i;
        }
        return -1;
    }

    /**
     * Updates an existing question's fields in place, then rewrites the whole file.
     * Returns true if the question was found and updated, false if the ID doesn't exist.
     */
    public boolean updateQuestion(String id, String subject, String unit, String difficulty,
                                   String co, int marks, String questionText, String tags) {
        int idx = indexOf(id);
        if (idx == -1) return false;

        Question q = questions.get(idx);
        q.setSubject(subject);
        q.setUnit(unit);
        q.setDifficulty(difficulty);
        q.setCo(co);
        q.setMarks(marks);
        q.setQuestionText(questionText);
        q.setTags(tags);

        fileHandler.saveAll(questions);
        return true;
    }

    /** Removes a question by ID and rewrites the file. Returns true if it existed. */
    public boolean deleteQuestion(String id) {
        int idx = indexOf(id);
        if (idx == -1) return false;
        questions.removeAt(idx);
        fileHandler.saveAll(questions);
        return true;
    }

    /** Reloads the in-memory list from disk (useful after external file edits). */
    public void reload() {
        questions = fileHandler.loadAll();
    }
}
