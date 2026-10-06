package storage;

import model.UsageRecord;
import structures.CustomArrayList;

import java.io.*;

/*
 * Storage: UsageFileHandler
 * Purpose: Read/append data/usage_history.txt.
 * Usage history is append-only during normal operation (every time a
 * question enters a generated paper, one line is added) - PaperGenerator
 * (Phase 10) will read loadAll() to compute per-question usage counts.
 */
public class UsageFileHandler {

    private final String filePath;

    public UsageFileHandler(String filePath) {
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

    public CustomArrayList<UsageRecord> loadAll() {
        CustomArrayList<UsageRecord> records = new CustomArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return records;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                UsageRecord r = UsageRecord.fromFileLine(line);
                if (r != null) records.add(r);
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read " + filePath + " (" + e.getMessage() + ")");
        }
        return records;
    }

    public void append(UsageRecord record) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(record.toFileLine());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Warning: could not append to " + filePath + " (" + e.getMessage() + ")");
        }
    }
}
