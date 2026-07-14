# Quiz / Exam Platform

An educational Java quiz/exam platform focused on object-oriented design and business logic, built entirely with the Java standard library. No framework, database, or external dependency — plain `javac`/`java` is enough to build, run, and test it.

## What it demonstrates

- A `Question` domain model and a `Quiz` question bank with validation
- Attempt lifecycle management: record answers, replace before finishing, lock after finishing
- Incomplete-attempt protection and finished-attempt locking
- Automatic grading with a **configurable pass threshold**
- Detailed per-question answer feedback (`AnswerResult`)
- Scoreboard ranking (best score per participant, ties broken by name)
- **Defensive design**: question snapshots per attempt, unmodifiable/defensive result data
- Dependency-free automated tests (custom assertion helper + test runner)
- Strict compilation with `-Xlint:all -Werror`

## Features

- Create validated multiple-choice questions and build quizzes
- Start attempts, record answers, and replace answers before finishing
- Reject incomplete attempts; lock attempts once finished
- Calculate score, percentage, and pass/fail against the quiz threshold
- Rank participants on a scoreboard
- Command-based CLI demos

## Main classes

- `Question` — immutable multiple-choice question with unmodifiable options.
- `Quiz` — question bank with a configurable passing percentage.
- `QuizAttempt` — one participant's attempt over a **snapshot** of the quiz's questions.
- `AnswerResult` — per-question feedback (selected vs. correct option).
- `QuizResult` — graded outcome with percentage and pass/fail.
- `ScoreBoard` — ranks results, keeping the best score per participant.
- `Main` — CLI commands (`help`, `demo`, `attempt-demo`, `validation-demo`, `scoreboard-demo`).

## Quick start

```text
javac -Xlint:all -Werror -d out src/quizexamplatform/*.java

java -cp out quizexamplatform.Main help
java -cp out quizexamplatform.Main demo
java -cp out quizexamplatform.Main attempt-demo
java -cp out quizexamplatform.Main validation-demo
java -cp out quizexamplatform.Main scoreboard-demo
```

Running with no command prints the usage text. `demo` builds a quiz, takes an attempt, grades it, and ranks it. `attempt-demo` walks through the attempt lifecycle (replace, incomplete-finish rejection, snapshot independence, post-finish locking). `validation-demo` intentionally triggers validation failures and exits 0 because the rejections are the point. `scoreboard-demo` shows ranking, tie-breaking, and best-score-per-participant. `Main.run(args, out, err)` returns an exit code (0 for valid commands, non-zero for unknown ones) and only `main` calls `System.exit`.

## Behavior notes

- **Default passing percentage is 60%.** Use `new Quiz(title, percentage)` for a custom threshold (0–100), or `new Quiz(title)` for the default.
- **Attempts use a question snapshot.** `quiz.startQuiz(name)` copies the current questions and the passing percentage into the attempt, so adding questions to the quiz afterward does not affect an already-started attempt.
- **Answer replacement before finish is allowed by design** — recording an answer for an already-answered question overwrites the previous choice. After `finish()`, the attempt is locked: no more answers and no re-finishing.
- **The scoreboard keeps the best score per participant.** A later, lower score does not replace an earlier, higher one; ties are broken by participant name (case-insensitive, ascending).
- **Duplicate options and duplicate question prompts are rejected case-insensitively.**
- Percentage is integer division of correct answers over total (e.g. 3/5 = 60%).

## Testing

The project ships with dependency-free automated tests (custom `Assert` helper + `TestRunner`) covering the question model, quiz, attempt lifecycle, results, scoreboard, and CLI:

```text
javac -Xlint:all -Werror -cp out -d test-out tests/quizexamplatform/*.java
java -cp "out;test-out" quizexamplatform.TestRunner   # Windows (use out:test-out on Linux/macOS)
```

Or run everything with one script: `./scripts/test.sh` (Linux/macOS/Git Bash) or `.\scripts\test.ps1` (Windows PowerShell). See [TESTING.md](TESTING.md) for the full procedure and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest recorded results.

## Java concepts practiced

- Classes, encapsulation, immutability, and final domain/result types
- `List`, `Map`, `Set`, and option modelling
- Defensive copies, snapshots, and unmodifiable collection views
- Lifecycle state management (open → answered → finished/locked)
- Sorting with `Comparator` and tie-breaking
- Exit codes and testable CLI entry points

## Limitations

- In-memory only — no database and no persistent question bank
- No HTTP API, login, or authentication
- No timer, proctoring, or exam-security features
- No randomized question order
- No GUI
- Intended as a Java OOP/business-logic learning project, not production exam software

## Possible future improvements

- Persistent question banks loaded from files
- Randomized question and option order
- Weighted questions and partial credit
- Multiple attempts history per participant
