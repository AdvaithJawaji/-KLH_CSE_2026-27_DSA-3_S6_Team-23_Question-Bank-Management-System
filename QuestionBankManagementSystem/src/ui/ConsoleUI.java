package ui;

import java.util.Scanner;
import model.Paper;
import model.Question;
import model.SearchResult;
import services.AuthService;
import services.PaperGenerator;
import services.QuestionManager;
import services.SearchEngine;
import services.UsageTracker;
import structures.CustomArrayList;

/*
 * UI: ConsoleUI  (Complete — all menu options implemented)
 * Purpose: Drives the terminal session — login gate, then the full menu loop.
 *
 * Menu:
 *   1. Add Question
 *   2. View All Questions
 *   3. Update Question
 *   4. Delete Question
 *   5. Search Questions       ← DSA algorithms (Naive/KMP/Z/RabinKarp/AhoCorasick/Fuzzy)
 *   6. Generate Question Paper ← 0/1 Knapsack DP + usage-aware sort
 *   7. View Usage History      ← sorted by usage count
 *   8. Logout
 *   0. Exit
 */
public class ConsoleUI {

    private final Scanner scanner;
    private final InputValidator input;
    private final AuthService authService;
    private final QuestionManager questionManager;
    private final SearchEngine searchEngine;
    private final PaperGenerator paperGenerator;
    private final UsageTracker usageTracker;

    public ConsoleUI(AuthService authService, QuestionManager questionManager,
                     SearchEngine searchEngine, PaperGenerator paperGenerator,
                     UsageTracker usageTracker) {
        this.scanner        = new Scanner(System.in);
        this.input          = new InputValidator(scanner);
        this.authService    = authService;
        this.questionManager = questionManager;
        this.searchEngine   = searchEngine;
        this.paperGenerator = paperGenerator;
        this.usageTracker   = usageTracker;
    }

    public void start() {
        Menu.printBanner();
        boolean running = true;
        try {
            while (running) {
                if (!authService.isLoggedIn()) {
                    running = loggedOutLoop();
                } else {
                    running = loggedInLoop();
                }
            }
        } finally {
            System.out.println("\nGoodbye!");
        }
    }

    // ---- Logged-out state ------------------------------------------

    private boolean loggedOutLoop() {
        Menu.printLoggedOutMenu();
        int choice = input.readIntInRange("", 0, 1);
        return switch (choice) {
            case 1 -> {
                handleLogin();
                yield true;
            }
            case 0 -> false;
            default -> true;
        };
    }

    private void handleLogin() {
        String username = input.readNonEmptyLine("Username: ");
        String password = input.readNonEmptyLine("Password: ");
        var user = authService.login(username, password);
        if (user == null) {
            System.out.println("[!] Invalid username or password.");
        } else {
            System.out.println("[✓] Welcome, " + user.getUsername() + "! Role: " + user.getRole());
        }
    }

    // ---- Logged-in state -------------------------------------------

    private boolean loggedInLoop() {
        var user = authService.getCurrentUser();
        Menu.printMainMenu(user.getUsername(), user.getRole());
        int choice = input.readIntInRange("", 0, 9);
        switch (choice) {
            case 1 -> handleAddQuestion();
            case 2 -> handleViewAll();
            case 3 -> handleUpdateQuestion();
            case 4 -> handleDeleteQuestion();
            case 5 -> handleSearch();
            case 6 -> handleGeneratePaper();
            case 7 -> handleUsageHistory();
            case 8 -> {
                if (user.isAdmin()) handleAddUser();
                else System.out.println("[!] Admin access required.");
            }
            case 9 -> {
                authService.logout();
                System.out.println("[✓] Logged out.");
            }
            case 0 -> { return false; }
            default -> { }
        }
        return true;
    }

    // ---- 1. Add Question -------------------------------------------

    private void handleAddQuestion() {
        System.out.println("\n--- Add Question ---");
        String subject    = input.readNonEmptyLine("Subject: ");
        String unit       = input.readNonEmptyLine("Unit (e.g. Unit 3): ");
        String difficulty = input.readDifficulty("Difficulty (Easy/Medium/Hard): ");
        String co         = input.readNonEmptyLine("Course Outcome (e.g. CO3): ");
        int marks         = input.readPositiveInt("Marks: ");
        String text       = input.readNonEmptyLine("Question Text: ");
        String tags       = input.readLineAllowEmpty("Tags (space-separated, optional): ");

        Question created = questionManager.addQuestion(subject, unit, difficulty, co, marks, text, tags);
        System.out.println("[✓] Added: " + created);
    }

    // ---- 2. View All -----------------------------------------------

    private void handleViewAll() {
        CustomArrayList<Question> all = questionManager.getAll();
        System.out.println("\n--- All Questions (" + questionManager.count() + ") ---");
        if (all.isEmpty()) {
            System.out.println("  No questions in the bank yet.");
            return;
        }
        for (int i = 0; i < all.size(); i++) {
            Question q = all.get(i);
            System.out.printf("  [%s] %-18s %-8s %-8s %2dm  %s%n",
                    q.getId(), q.getSubject(), q.getUnit(), q.getDifficulty(),
                    q.getMarks(), q.getQuestionText());
        }
    }

    // ---- 3. Update Question ----------------------------------------

    private void handleUpdateQuestion() {
        System.out.println("\n--- Update Question ---");
        String id = input.readNonEmptyLine("Question ID to update: ").toUpperCase();
        Question existing = questionManager.findById(id);
        if (existing == null) {
            System.out.println("[!] No question found with ID " + id);
            return;
        }
        System.out.println("Current: " + existing);
        System.out.println("Enter new values (leave blank = keep current):");

        String subject    = inputOrKeep("Subject",    existing.getSubject());
        String unit       = inputOrKeep("Unit",       existing.getUnit());
        String difficulty = inputDifficultyOrKeep("Difficulty (Easy/Medium/Hard)", existing.getDifficulty());
        String co         = inputOrKeep("CO",         existing.getCo());
        int marks         = inputMarksOrKeep("Marks", existing.getMarks());
        String text       = inputOrKeep("Question Text", existing.getQuestionText());
        String tags       = input.readLineAllowEmpty("Tags (optional, blank = keep '" + existing.getTags() + "'): ");
        if (tags.isEmpty()) tags = existing.getTags();

        boolean ok = questionManager.updateQuestion(id, subject, unit, difficulty, co, marks, text, tags);
        System.out.println(ok ? "[✓] Question updated." : "[!] Update failed.");
    }

    // ---- 4. Delete Question ----------------------------------------

    private void handleDeleteQuestion() {
        System.out.println("\n--- Delete Question ---");
        String id = input.readNonEmptyLine("Question ID to delete: ").toUpperCase();
        Question existing = questionManager.findById(id);
        if (existing == null) {
            System.out.println("[!] No question found with ID " + id);
            return;
        }
        System.out.println("About to delete: " + existing);
        String confirm = input.readNonEmptyLine("Type YES to confirm: ");
        if (confirm.equalsIgnoreCase("YES")) {
            questionManager.deleteQuestion(id);
            System.out.println("[✓] Question deleted.");
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    // ---- 5. Search -------------------------------------------------

    private void handleSearch() {
        System.out.println("\n--- Search Questions ---");
        Menu.printSearchMenu();
        int algoChoice = input.readIntInRange("", 0, 6);
        if (algoChoice == 0) return;

        boolean multiKeyword = (algoChoice == SearchEngine.ALGO_AHO_CORASICK);
        String prompt = multiKeyword
                ? "Enter keywords (space-separated, all will be searched): "
                : "Enter search term: ";
        String query = input.readNonEmptyLine(prompt);

        System.out.println("\n[~] Searching using " + SearchEngine.algoName(algoChoice) + "...");
        SearchResult result = searchEngine.search(algoChoice, query, questionManager.getAll());
        result.printReport();

        CustomArrayList<String> matchedIds = result.getMatchedQuestionIds();
        if (matchedIds.isEmpty()) {
            System.out.println("  No matches found.");
            return;
        }
        System.out.println("  Matching questions:");
        for (int i = 0; i < matchedIds.size(); i++) {
            Question q = questionManager.findById(matchedIds.get(i));
            if (q != null) {
                System.out.printf("  [%s] %-18s %-8s %2dm  %s%n",
                        q.getId(), q.getSubject(), q.getDifficulty(),
                        q.getMarks(), q.getQuestionText());
            }
        }
    }

    // ---- 6. Generate Paper -----------------------------------------

    private void handleGeneratePaper() {
        System.out.println("\n--- Generate Question Paper ---");

        // List available subjects
        CustomArrayList<Question> all = questionManager.getAll();
        CustomArrayList<String> subjects = new CustomArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            String s = all.get(i).getSubject();
            if (!subjects.contains(s)) subjects.add(s);
        }
        System.out.println("Available subjects:");
        System.out.println("  0. All Subjects");
        for (int i = 0; i < subjects.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, subjects.get(i));
        }

        int subChoice = input.readIntInRange("Choose subject (0 = all): ", 0, subjects.size());
        String subject = (subChoice == 0) ? "" : subjects.get(subChoice - 1);

        int count       = input.readPositiveInt("Number of questions: ");
        int targetMarks = input.readPositiveInt("Target total marks: ");

        String username = authService.getCurrentUser().getUsername();
        System.out.println("\n[~] Generating paper using 0/1 Knapsack DP...");

        Paper paper = paperGenerator.generatePaper(subject, count, targetMarks, all, username);

        System.out.println("\n[✓] Paper Generated!");
        System.out.println("  Paper ID   : " + paper.getPaperId());
        System.out.println("  Subject    : " + paper.getSubject());
        System.out.println("  Date       : " + paper.getDate());
        System.out.println("  Total Marks: " + paper.getTotalMarks());
        System.out.println("  Questions  : " + paper.getQuestionIds().size());
        System.out.println();

        CustomArrayList<String> ids = paper.getQuestionIds();
        System.out.printf("  %-5s %-8s %-18s %-8s %5s  Question%n",
                "#", "ID", "Subject", "Diff", "Marks");
        System.out.println("  " + "-".repeat(72));
        for (int i = 0; i < ids.size(); i++) {
            Question q = questionManager.findById(ids.get(i));
            if (q != null) {
                System.out.printf("  %-5d %-8s %-18s %-8s %5d  %s%n",
                        i + 1, q.getId(), q.getSubject(), q.getDifficulty(),
                        q.getMarks(), q.getQuestionText());
            }
        }
    }

    // ---- 7. Usage History ------------------------------------------

    private void handleUsageHistory() {
        System.out.println("\n--- Usage History ---");
        System.out.println("  1. Top questions by usage count");
        System.out.println("  2. Full usage log (recent first)");
        System.out.println("  0. Back");
        int choice = input.readIntInRange("", 0, 2);
        switch (choice) {
            case 1 -> {
                int topN = input.readPositiveInt("Show top N questions (e.g. 10): ");
                usageTracker.printUsageReport(questionManager.getAll(), topN);
            }
            case 2 -> {
                int limit = input.readPositiveInt("Show last N records (e.g. 20): ");
                usageTracker.printRawLog(limit);
            }
            case 0 -> { }
            default -> { }
        }
    }

    // ---- 8. Add User (Admin only) ----------------------------------

    private void handleAddUser() {
        System.out.println("\n--- Add User Account (Admin) ---");
        String uname = input.readNonEmptyLine("New username: ");
        String pass  = input.readNonEmptyLine("Password: ");
        System.out.println("Role: 1=ADMIN  2=FACULTY");
        int roleChoice = input.readIntInRange("", 1, 2);
        String role = (roleChoice == 1) ? "ADMIN" : "FACULTY";

        boolean ok = authService.addUser(uname, pass, role);
        if (ok) {
            System.out.println("[✓] User '" + uname + "' (" + role + ") created.");
        } else {
            System.out.println("[!] Username already exists.");
        }
    }

    // ---- Input helpers ---------------------------------------------

    private String inputOrKeep(String fieldName, String current) {
        System.out.print(fieldName + " (blank = keep '" + current + "'): ");
        String line = scanner.nextLine().trim();
        return line.isEmpty() ? current : line;
    }

    private String inputDifficultyOrKeep(String prompt, String current) {
        while (true) {
            System.out.print(prompt + " (blank = keep '" + current + "'): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) return current;
            if (line.equalsIgnoreCase("Easy") || line.equalsIgnoreCase("Medium")
                    || line.equalsIgnoreCase("Hard")) {
                return line.substring(0, 1).toUpperCase() + line.substring(1).toLowerCase();
            }
            System.out.println("Enter Easy, Medium, or Hard.");
        }
    }

    private int inputMarksOrKeep(String prompt, int current) {
        System.out.print(prompt + " (blank = keep " + current + "): ");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return current;
        try {
            int val = Integer.parseInt(line);
            if (val > 0) return val;
        } catch (NumberFormatException ignored) {}
        System.out.println("  Invalid — keeping " + current);
        return current;
    }
}
