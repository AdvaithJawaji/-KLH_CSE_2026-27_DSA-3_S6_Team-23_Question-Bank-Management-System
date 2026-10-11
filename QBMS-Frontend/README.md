# Question Bank Management System — Frontend

A single self-contained `index.html` — glassmorphic UI, login screen, working
Add/Search/Generate Paper/Usage History, all running on live in-memory sample
data in the browser. No build step, no npm install, no dependencies.

## How to run in VS Code

**Option A — Live Server extension (recommended, auto-reloads on save)**
1. Open this folder in VS Code (`File > Open Folder...`).
2. Install the **"Live Server"** extension (by Ritwick Dey) from the Extensions tab if you don't have it.
3. Right-click `index.html` in the file explorer → **"Open with Live Server"**.
4. It opens automatically at something like `http://127.0.0.1:5500`.

**Option B — no extension needed**
1. Just double-click `index.html` in your file explorer — it opens directly in your browser (`file://...`). Everything on this page works fully offline with no server at all.

**Option C — Python's built-in server (if you have Python installed)**
```bash
cd QBMS-Frontend
python -m http.server 8000
```
Then open `http://localhost:8000` in your browser.

## Login

- `admin` / `admin123` — Admin role
- `faculty1` / `faculty123` — Faculty role

## What's real vs. what's a demo

- **Real (works in your browser session):** login, adding questions, searching
  (Naive/KMP/Z-Function/Rabin-Karp/Aho-Corasick multi-keyword/Fuzzy edit-distance),
  generating a paper (real selection + real distribution charts), usage history.
  Generated papers can be customized by adding another bank question, editing a
  question, or removing it from that paper without deleting it from the bank.
- **Not yet connected:** this is a standalone frontend. It does **not** read or
  write your Java backend's `data/questions.txt` etc. Everything lives in a
  JavaScript array that resets if you refresh the page.

## Wiring it to the real Java backend (next step)

To make this show the actual `data/questions.txt` from your console app instead
of the built-in sample data, add a small `WebServer.java` using the JDK's
built-in `com.sun.net.httpserver.HttpServer` (no Spring Boot, no external
framework — still satisfies the "Java + files only" project constraint) that:
- serves this `index.html`,
- exposes endpoints like `/api/questions`, `/api/search`, `/api/paper` backed
  by your existing `QuestionManager` / `SearchEngine`,
- and this page's JavaScript calls those endpoints with `fetch()` instead of
  reading the local sample arrays.

Ask for this as the next phase whenever you're ready.
