package storage;

import model.User;
import structures.CustomArrayList;

import java.io.*;

/*
 * Storage: UserFileHandler
 * Purpose: Read/write data/users.txt (lightweight file-based authentication).
 * On first run, if the file is empty, seeds one default admin account so the
 * application is never unusable out of the box.
 */
public class UserFileHandler {

    private final String filePath;

    public UserFileHandler(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    private void ensureFileExists() {
        File file = new File(filePath);
        File parent = file.getParentFile();
        try {
            if (parent != null && !parent.exists()) parent.mkdirs();
            if (!file.exists()) {
                file.createNewFile();
                seedDefaultAdmin();
            } else if (file.length() == 0) {
                seedDefaultAdmin();
            }
        } catch (IOException e) {
            System.out.println("Warning: could not initialize file " + filePath + " (" + e.getMessage() + ")");
        }
    }

    private void seedDefaultAdmin() {
        User admin = new User("U001", "admin", "admin123", "ADMIN");
        append(admin);
    }

    public CustomArrayList<User> loadAll() {
        CustomArrayList<User> users = new CustomArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                User u = User.fromFileLine(line);
                if (u != null) users.add(u);
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read " + filePath + " (" + e.getMessage() + ")");
        }
        return users;
    }

    public void saveAll(CustomArrayList<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (int i = 0; i < users.size(); i++) {
                writer.write(users.get(i).toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Warning: could not write " + filePath + " (" + e.getMessage() + ")");
        }
    }

    public void append(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(user.toFileLine());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Warning: could not append to " + filePath + " (" + e.getMessage() + ")");
        }
    }
}
