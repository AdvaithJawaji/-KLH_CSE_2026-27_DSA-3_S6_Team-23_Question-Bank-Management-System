package storage;

import model.Question;
import structures.CustomArrayList;

import java.io.*;

/*
 * Storage: QuestionFileHandler
 * Purpose: Read/write data/questions.txt using plain Java File I/O.
 * Loads every record into a CustomArrayList<Question> for the DSA engine
 * to process in memory. Never crashes on a missing/empty/malformed file.
 */
public class QuestionFileHandler {

    private final String filePath;

    public QuestionFileHandler(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    /** Creates the data/ directory and the file itself if either is missing. */
    private void ensureFileExists() {
        File file = new File(filePath);
        File parent = file.getParentFile();
        try {
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Warning: could not initialize file " + filePath + " (" + e.getMessage() + ")");
        }
    }

    /** Loads all valid question records. Malformed lines are silently skipped. */
    public CustomArrayList<Question> loadAll() {
        CustomArrayList<Question> questions = new CustomArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return questions;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Question q = Question.fromFileLine(line);
                if (q != null) {
                    questions.add(q);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read " + filePath + " (" + e.getMessage() + ")");
        }
        return questions;
    }

    /** Overwrites the file with the full current in-memory list (used after add/update/delete). */
    public void saveAll(CustomArrayList<Question> questions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (int i = 0; i < questions.size(); i++) {
                writer.write(questions.get(i).toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Warning: could not write " + filePath + " (" + e.getMessage() + ")");
        }
    }

    /** Appends a single new question without rewriting the whole file. */
    public void append(Question question) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(question.toFileLine());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Warning: could not append to " + filePath + " (" + e.getMessage() + ")");
        }
    }
}
