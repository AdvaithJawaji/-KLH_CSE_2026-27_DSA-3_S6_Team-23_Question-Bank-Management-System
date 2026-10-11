# Question Bank Management System (QBMS)

A DSA-focused project for managing academic questions, searching the bank efficiently, generating balanced question papers, and tracking usage history. The repository contains:

- A Java-based backend console application in `QuestionBankManagementSystem/`
- A browser-based front-end demo in `QBMS-Frontend/`
- File-based persistence using text files instead of a database
- Core data-structure and algorithm implementation required for a practical question bank system

This project was built as a learning and implementation-focused academic system to demonstrate real algorithmic thinking in software engineering.

## Project Overview

The Question Bank Management System is designed to help faculty or administrators:

- add, update, delete, and view questions
- organize questions by subject, unit, difficulty, marks, course outcome, and tags
- search the question bank using several string-matching algorithms
- generate balanced papers based on mark targets and subject filters
- track which questions are used most often
- manage user accounts with role-based access

The solution is intentionally built without external frameworks or a database, so it stays close to the DSA and file-handling principles taught in the course.

## What We Implemented

### 1. User Authentication and Role Management

The Java backend includes a login system with:

- username/password validation
- default admin account creation on first run
- role-based access (`ADMIN` and `FACULTY`)
- admin-only user creation

This logic is handled mainly through:

- `src/services/AuthService.java`
- `src/storage/UserFileHandler.java`
- `src/ui/ConsoleUI.java`

### 2. Question CRUD Operations

Faculty/admin users can:

- add questions with subject, unit, difficulty, CO, marks, text, and tags
- display all questions currently stored in the bank
- update question details by ID
- delete questions with confirmation

The question model stores the following fields:

- question ID (`Q001`, `Q002`, ...)
- subject
- unit
- difficulty (`Easy`, `Medium`, `Hard`)
- course outcome (`COx`)
- marks
- question text
- tags

This is implemented across:

- `src/model/Question.java`
- `src/services/QuestionManager.java`
- `src/storage/QuestionFileHandler.java`

### 3. Search Engine with Multiple Algorithms

The project includes a unified search engine that supports the following algorithms:

- Naive String Matching
- KMP (Knuth-Morris-Pratt)
- Z-Function Algorithm
- Rabin-Karp String Matching
- Aho-Corasick (multi-keyword matching)
- Fuzzy matching via Levenshtein Edit Distance

The search compares student/teacher query strings against question text, subject, tags, and unit values. Search results include:

- matched question IDs
- execution time in milliseconds
- number of character comparisons performed
- algorithm complexity notation

Files involved:

- `src/services/SearchEngine.java`
- `src/algorithms/*.java`
- `src/model/SearchResult.java`

### 4. Question Paper Generation

The system can generate a question paper by choosing a suitable set of questions from the question bank based on:

- selected subject or all subjects
- number of questions required
- target total marks

The generator uses:

- usage-aware sorting to avoid repeatedly picking the same questions
- 0/1 Knapsack Dynamic Programming to find a balanced set under the target marks
- greedy fill-up if the exact desired set is not fully possible

This is implemented in:

- `src/services/PaperGenerator.java`

### 5. Usage Tracking and Question Popularity Analysis

Any question that appears in a generated paper is logged in the usage history and counted. The system can show:

- the most frequently used questions
- full usage log with paper and user details
- recent usage records

This feature helps evaluate which questions are repeatedly used and supports balanced paper construction.

Files involved:

- `src/services/UsageTracker.java`
- `src/storage/UsageFileHandler.java`
- `src/model/UsageRecord.java`

### 6. Console UI and Interactive Workflow

A menu-driven Java terminal UI allows the user to:

- log in
- add/edit/delete questions
- search
- generate papers
- view usage reports
- add users (admin only)
- log out / exit

The user interface lives in:

- `src/ui/Menu.java`
- `src/ui/ConsoleUI.java`
- `src/ui/InputValidator.java`

### 7. Browser Front-End Demo

The repository also includes `QBMS-Frontend/`, which contains a polished, glassmorphism-style browser UI. It demonstrates the question bank system in the browser with:

- login screen
- add question form
- search interface
- question paper generation
- usage history tabs
- live in-memory sample data

This frontend is a conceptual demo and not yet connected to the Java backend's files directly.

## Algorithms Used in the Project

The project is centered on algorithmic implementations, especially in the search engine.

### 1. Naive Pattern Matching

Used as the baseline search strategy.

- Compares the query against every position in the text
- Simple and easy to understand
- Time complexity: O(n × m)

File: `src/algorithms/NaiveMatcher.java`

### 2. KMP (Knuth-Morris-Pratt)

Improves on naive matching by using a failure table (prefix-function).

- Avoids redundant comparisons after a mismatch
- Efficient for repeated searches
- Time complexity: O(n + m)

File: `src/algorithms/KMPMatcher.java`

### 3. Z-Function Algorithm

Builds the Z-array to match the pattern efficiently.

- Helps determine prefix matches starting at each index
- Useful for linear-time pattern matching
- Time complexity: O(n + m)

File: `src/algorithms/ZMatcher.java`

### 4. Rabin-Karp Algorithm

Uses hashing to compare substrings efficiently.

- Computes rolling hash values to detect matches quickly
- Good for multiple pattern checks and hash-based matching
- Average time complexity: O(n + m)
- Worst-case time complexity: O(n × m)

File: `src/algorithms/RabinKarpMatcher.java`

### 5. Aho-Corasick Automaton

A multi-pattern matching algorithm.

- Builds a trie of all keywords
- Uses failure links to scan the text in one pass
- Ideal for searching multiple keywords simultaneously
- Time complexity: O(n + Σ|patterns| + matches)

File: `src/algorithms/AhoCorasick.java`

### 6. Fuzzy Matching using Levenshtein Edit Distance

Matches approximate text that may contain minor typing errors.

- Counts how many insertions, deletions, or substitutions are needed to transform one string into another
- Very useful for forgiving search behavior
- Time complexity: O(n × m)

File: `src/algorithms/EditDistanceMatcher.java`

### 7. 0/1 Knapsack Dynamic Programming

Used for drawing a balanced paper with a target total marks.

- Each question is an item
- Marks are the weight
- The goal is to maximize the number of selected questions within the total mark limit
- Dynamic programming enables selection while respecting constraints

This is the core algorithm behind the paper generation strategy in `PaperGenerator.java`.

### 8. Merge Sort

Used to sort questions by usage count before selection.

- Helps prioritize less-used questions during paper generation
- Stable divide-and-conquer sorting strategy
- Time complexity: O(n log n)

This is used inside `PaperGenerator.java`.

### 9. Custom Array-based Data Structure

Instead of relying on `java.util.ArrayList`, the project uses its own custom array implementation:

- `src/structures/CustomArrayList.java`

This keeps the project aligned with the DSA requirement and supports dynamic resizing without Java collection classes.

## Project Structure

```text
QBMS-Final/
├── .gitignore
├── README.md
├── .vscode/
│   ├── launch.json
│   └── tasks.json
├── QuestionBankManagementSystem/
│   ├── data/
│   │   ├── generated_papers.txt
│   │   ├── questions.txt
│   │   ├── usage_history.txt
│   │   └── users.txt
│   ├── out/
│   │   └── compiled Java classes
│   ├── src/
│   │   ├── algorithms/
│   │   │   ├── AhoCorasick.java
│   │   │   ├── EditDistanceMatcher.java
│   │   │   ├── KMPMatcher.java
│   │   │   ├── NaiveMatcher.java
│   │   │   ├── RabinKarpMatcher.java
│   │   │   └── ZMatcher.java
│   │   ├── model/
│   │   │   ├── Paper.java
│   │   │   ├── Question.java
│   │   │   ├── SearchResult.java
│   │   │   ├── UsageRecord.java
│   │   │   └── User.java
│   │   ├── services/
│   │   │   ├── AuthService.java
│   │   │   ├── PaperGenerator.java
│   │   │   ├── QuestionManager.java
│   │   │   ├── SearchEngine.java
│   │   │   └── UsageTracker.java
│   │   ├── storage/
│   │   │   ├── PaperFileHandler.java
│   │   │   ├── QuestionFileHandler.java
│   │   │   ├── UsageFileHandler.java
│   │   │   └── UserFileHandler.java
│   │   ├── structures/
│   │   │   └── CustomArrayList.java
│   │   ├── ui/
│   │   │   ├── ConsoleUI.java
│   │   │   ├── InputValidator.java
│   │   │   └── Menu.java
│   │   └── Main.java
│   └── README.md
├── QBMS-Frontend/
│   ├── index.html
│   └── README.md
└── README.md
```

## Tech Stack

### Backend

- Java (JDK 17+ compatible)
- File-based storage
- Custom data structures and algorithm implementations
- Console-based UI

### Frontend Demo

- HTML
- CSS
- JavaScript
- No build tools required

## Run Instructions

### Backend (Java Console App)

From the repository root:

```bash
cd QuestionBankManagementSystem
javac -d out $(find src -name "*.java")
java -cp out Main
```

Or on Windows PowerShell:

```powershell
cd QuestionBankManagementSystem
mkdir out
Get-ChildItem -Recurse src\*.java | ForEach-Object { javac -d out $_.FullName }
java -cp out Main
```

### Frontend Demo

Open the browser demo folder:

```bash
cd QBMS-Frontend
python -m http.server 8000
```

Then visit:

```text
http://localhost:8000
```

You can also open `index.html` directly in a browser without a local server.

## Default Login Credentials

The system auto-creates an admin account if the user file is empty.

```text
Username: admin
Password: admin123
Role: ADMIN
```

The frontend demo also includes sample faculty credentials:

```text
Username: faculty1
Password: faculty123
```

## Example Workflow

1. Log in with the admin account
2. Add several questions across subjects and units
3. Use Search to test different string-matching algorithms
4. Generate a question paper for a target number of questions and marks
5. View usage history to see which questions were used most often
6. Continue updating the bank and regenerate papers as needed

## Notes on the Project

- Data is stored in plain text files, not a database
- The project is designed to be simple, transparent, and algorithm-focused
- The search engine demonstrates multiple DSA-based approaches side-by-side
- The frontend is a visual demo, while the Java backend is the real functional system

## Future Enhancements

Possible improvements for the next phases include:

- full web connectivity between frontend and Java backend
- topic-based categorization and advanced filtering
- export to PDF/Word format
- improved paper balancing based on difficulty and course outcomes
- richer analytics dashboards
- secure user authentication with encrypted passwords

## Summary

This project combines:

- data modeling
- file handling
- authentication
- CRUD logic
- algorithmic search
- dynamic programming for paper generation
- usage tracking
- UI design

The result is a fully functional academic question bank management system with a strong emphasis on algorithmic implementation and practical software design.

## Contributors

This repository was developed by the team working on the DSA-3 Question Bank Management System.

If you are using or extending this project, feel free to update or improve the documentation and functionality as needed.
