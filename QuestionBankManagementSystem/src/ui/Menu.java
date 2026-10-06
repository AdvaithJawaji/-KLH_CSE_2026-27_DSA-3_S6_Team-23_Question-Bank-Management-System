package ui;

/*
 * UI helper: Menu
 * Purpose: All console menu printing — no business logic here.
 */
public class Menu {

    public static void printBanner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║     QUESTION BANK MANAGEMENT SYSTEM  v3.0       ║");
        System.out.println("║     DSA-Driven Engine · File Storage · Java      ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();
    }

    public static void printLoggedOutMenu() {
        System.out.println("  1. Login");
        System.out.println("  0. Exit");
        System.out.print("Choose: ");
    }

    public static void printMainMenu(String username, String role) {
        System.out.println();
        System.out.println("  Logged in as: " + username + " (" + role + ")");
        System.out.println("  ─────────────────────────────────────────────");
        System.out.println("  1. Add Question");
        System.out.println("  2. View All Questions");
        System.out.println("  3. Update Question");
        System.out.println("  4. Delete Question");
        System.out.println("  ─────────────────────────────────────────────");
        System.out.println("  5. Search Questions         [KMP / Z / RK / AC / Fuzzy]");
        System.out.println("  6. Generate Question Paper  [0/1 Knapsack DP]");
        System.out.println("  7. View Usage History       [Sorted by count]");
        System.out.println("  ─────────────────────────────────────────────");
        if ("ADMIN".equalsIgnoreCase(role)) {
            System.out.println("  8. Add User Account        [Admin only]");
        }
        System.out.println("  9. Logout");
        System.out.println("  0. Exit");
        System.out.print("Choose: ");
    }

    public static void printSearchMenu() {
        System.out.println("  Select search algorithm:");
        System.out.println("  1. Naive (Brute Force)       O(n·m)");
        System.out.println("  2. KMP (Knuth-Morris-Pratt)  O(n+m)");
        System.out.println("  3. Z-Function                O(n+m)");
        System.out.println("  4. Rabin-Karp (Rolling Hash) O(n+m) avg");
        System.out.println("  5. Aho-Corasick (Multi-KW)   O(n+Σ|patterns|)");
        System.out.println("  6. Fuzzy / Edit Distance     O(n·m) Levenshtein");
        System.out.println("  0. Back");
        System.out.print("Choose: ");
    }
}
