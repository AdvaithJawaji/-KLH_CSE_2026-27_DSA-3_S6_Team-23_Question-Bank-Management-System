import services.AuthService;
import services.PaperGenerator;
import services.QuestionManager;
import services.SearchEngine;
import services.UsageTracker;
import storage.PaperFileHandler;
import storage.QuestionFileHandler;
import storage.UsageFileHandler;
import storage.UserFileHandler;
import ui.ConsoleUI;

/*
 * Main — Question Bank Management System v3.0
 *
 * Wires up all layers:
 *   storage  → services  → algorithms  → ui
 *
 * Run from the QuestionBankManagementSystem/ folder so the relative
 * data/ paths resolve correctly.
 *
 * Compile:
 *   Windows PowerShell:
 *     mkdir out; javac -d out -sourcepath src (Get-ChildItem -Recurse src\*.java | % FullName)
 *
 *   Or using VS Code tasks (Ctrl+Shift+B → "QBMS: Compile", then "QBMS: Run")
 */
public class Main {
    public static void main(String[] args) {

        // ---- Storage layer ----
        QuestionFileHandler questionFileHandler = new QuestionFileHandler("data/questions.txt");
        UserFileHandler     userFileHandler     = new UserFileHandler("data/users.txt");
        UsageFileHandler    usageFileHandler    = new UsageFileHandler("data/usage_history.txt");
        PaperFileHandler    paperFileHandler    = new PaperFileHandler("data/generated_papers.txt");

        // ---- Service layer ----
        QuestionManager questionManager = new QuestionManager(questionFileHandler);
        AuthService     authService     = new AuthService(userFileHandler);
        UsageTracker    usageTracker    = new UsageTracker(usageFileHandler);
        SearchEngine    searchEngine    = new SearchEngine();
        PaperGenerator  paperGenerator  = new PaperGenerator(paperFileHandler, usageFileHandler, usageTracker);

        // ---- UI layer ----
        ConsoleUI ui = new ConsoleUI(authService, questionManager, searchEngine, paperGenerator, usageTracker);
        ui.start();
    }
}
