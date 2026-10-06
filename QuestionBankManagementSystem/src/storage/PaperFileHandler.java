package storage;

import model.Paper;
import structures.CustomArrayList;

import java.io.*;

/*
 * Storage: PaperFileHandler
 * Purpose: Read/append data/generated_papers.txt.
 * PaperGenerator (later phase) appends one line per generated paper here.
 */
public class PaperFileHandler {

    private final String filePath;

    public PaperFileHandler(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    private void ensureFileExists() {
        File file = new File(filePath);
        File parent = file.getParentFile();
        try {
            if (parent != null && !parent.exists()) parent.mkdirs();
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            System.out.println("Warning: could not initialize file " + filePath + " (" + e.getMessage() + ")");
        }
    }

    public CustomArrayList<Paper> loadAll() {
        CustomArrayList<Paper> papers = new CustomArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return papers;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Paper p = Paper.fromFileLine(line);
                if (p != null) papers.add(p);
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read " + filePath + " (" + e.getMessage() + ")");
        }
        return papers;
    }

    public void append(Paper paper) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(paper.toFileLine());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Warning: could not append to " + filePath + " (" + e.getMessage() + ")");
        }
    }
}
