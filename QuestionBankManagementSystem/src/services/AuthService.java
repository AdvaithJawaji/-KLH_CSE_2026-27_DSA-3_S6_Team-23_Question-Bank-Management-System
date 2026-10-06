package services;

import model.User;
import storage.UserFileHandler;
import structures.CustomArrayList;

/*
 * Service: AuthService
 * Purpose: Lightweight file-based authentication against data/users.txt.
 * Not enterprise auth - just enough to demonstrate file handling and to
 * gate faculty/admin-only menu options.
 */
public class AuthService {

    private final UserFileHandler fileHandler;
    private CustomArrayList<User> users;
    private User currentUser;

    public AuthService(UserFileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.users = fileHandler.loadAll();
    }

    /** Attempts login. On success, sets currentUser and returns the User; on failure returns null. */
    public User login(String username, String password) {
        if (username == null || password == null) return null;
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u.getUsername().equals(username.trim()) && u.getPassword().equals(password)) {
                currentUser = u;
                return u;
            }
        }
        return null;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    /** Admin-only: create a new user account. Returns false if the username is already taken. */
    public boolean addUser(String username, String password, String role) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equalsIgnoreCase(username.trim())) {
                return false;
            }
        }
        String id = String.format("U%03d", users.size() + 1);
        User u = new User(id, username.trim(), password, role.trim().toUpperCase());
        users.add(u);
        fileHandler.append(u);
        return true;
    }

    public int userCount() {
        return users.size();
    }
}
