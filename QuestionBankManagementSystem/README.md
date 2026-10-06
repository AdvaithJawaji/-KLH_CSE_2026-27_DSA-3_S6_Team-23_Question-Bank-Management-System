# Question Bank Management System — Phase 2

DSA-3 (25CS2103E) course project. Java-only, file-storage-based, terminal
application (no database, no GUI) that will grow into a full DSA-driven
question bank and paper generator across the phases described in the
project spec.

## What Phase 1 delivered

- Project skeleton (`src/model`, `src/storage`, `src/structures`, `data/`, `output/`)
- Model classes: `Question`, `User`, `UsageRecord`, `Paper`, `SearchResult`
- File-storage classes: `QuestionFileHandler`, `UserFileHandler`, `UsageFileHandler`, `PaperFileHandler`
  - Auto-create `data/` and its files on first run — never crash on a missing file
  - Skip malformed lines instead of throwing
  - Human-readable `|`-delimited records, with `|` inside text escaped as `\p`
- One custom structure, `CustomArrayList<T>`, used everywhere instead of `java.util.ArrayList`
  so the DSA engine (added in later phases) stays free of library collections

## What Phase 2 adds

- **Console menu loop** (`ui/ConsoleUI.java`, `ui/Menu.java`) — the app is now
  interactive: it shows a login prompt, then a numbered main menu, and keeps
  looping until you choose Exit.
- **File-based login** (`services/AuthService.java`) — checks username/password
  against `data/users.txt`. A default `admin` / `admin123` / `ADMIN` account is
  seeded automatically the first time the file is empty.
- **Question CRUD** (`services/QuestionManager.java`):
  - **Add** — prompts for Subject/Unit/Difficulty/CO/Marks/Text/Tags, auto-generates
    the next sequential ID (`Q001`, `Q002`, ...), appends to file.
  - **View All** — lists every question currently in the bank.
  - **Update** — looks up by ID, re-prompts for every field, rewrites the file.
  - **Delete** — looks up by ID, asks for a `YES` confirmation, rewrites the file.
- **Input validation** (`ui/InputValidator.java`) — reprompts on empty input,
  non-numeric marks, invalid difficulty, etc. Never crashes on bad input.

Menu items for Search, Question Paper Generation, and Usage History are
present but stubbed ("coming in Phase 3" / a later phase) — selecting them
prints a message instead of crashing, since those algorithms haven't been
implemented yet.

## File formats

```
data/questions.txt        ID | Subject | Unit | Difficulty | CO | Marks | Question Text | Tags
data/users.txt            ID | Username | Password | Role   (ADMIN / FACULTY)
data/usage_history.txt    Question ID | Paper ID | Date | User | Usage Count
data/generated_papers.txt Paper ID | Date | Subject | Total Marks | QuestionID1,QuestionID2,...
```

## How to run

Requires a JDK (this project was written/tested against Java 17+ syntax but
only uses core language features, so any recent JDK works).

The readable Java source files are in `src/`. Compiled `.class` files are
generated in `out/` when you build locally; they are build artifacts, not
source code, and are excluded from Git.

```bash
cd QuestionBankManagementSystem
javac -d out $(find src -name "*.java")
java -cp out Main
```

Run it from the `QuestionBankManagementSystem/` folder so the relative
`data/` path resolves correctly (or edit the paths in `Main.java`).

## Sample login

A default admin account is auto-seeded into `data/users.txt` the first time
the app runs (if the file is empty):

```
username: admin
password: admin123
role: ADMIN
```

## Sample session

```
=========================================
   QUESTION BANK MANAGEMENT SYSTEM
=========================================

1. Login
0. Exit
Choose an option: 1
Username: admin
Password: admin123
Login successful. Welcome, admin!

Logged in as: admin (ADMIN)
1. Add Question
2. View All Questions
3. Update Question
4. Delete Question
5. Search Questions            [coming in Phase 3]
6. Generate Question Paper      [coming in a later phase]
7. View Usage History           [coming in a later phase]
8. Logout
0. Exit
Choose an option: 1
--- Add Question ---
Subject: Data Structures
Unit (e.g. Unit 3): Unit 3
Difficulty (Easy/Medium/Hard): Medium
Course Outcome (e.g. CO3): CO3
Marks: 10
Question Text: Explain the working of a stack using array implementation.
Tags (space-separated, optional): stack array
Added: Q001 [Data Structures / Unit 3 / Medium / CO3 / 10m] Explain the working of a stack using array implementation.
```

## What's next

Phase 3 adds the first search algorithms: Naïve Pattern Matching and KMP,
wired into a `SearchEngine` and the "Search Questions" menu option. No
database will be introduced at any phase.
