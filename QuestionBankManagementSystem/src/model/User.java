package model;

/*
 * Model: User
 * Purpose: Represents a login account loaded from data/users.txt
 * Fields: ID | Username | Password | Role   (Role = ADMIN or FACULTY)
 */
public class User {

    public static final String DELIMITER = "\\|";
    public static final String DELIMITER_LITERAL = "|";

    private final String id;
    private final String username;
    private final String password;
    private final String role; // ADMIN or FACULTY

    public User(String id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }

    public String toFileLine() {
        return String.join(DELIMITER_LITERAL, id, username, password, role);
    }

    public static User fromFileLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length != 4) return null;
        return new User(parts[0], parts[1], parts[2], parts[3]);
    }

    @Override
    public String toString() {
        return id + " (" + username + ", " + role + ")";
    }
}
