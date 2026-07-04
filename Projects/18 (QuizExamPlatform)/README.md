# Quiz Exam Platform

An in-memory Java application for multiple-choice quizzes, attempts, answers, and results.

## Implemented features

- Create validated questions with at least two unique options.
- Mark one valid option as correct using a zero-based index.
- Build quizzes and start participant attempts.
- Record or change one answer per question before finishing.
- Require every question to be answered before completion.
- Calculate correct-answer count, integer percentage, and pass/fail result.
- Show selected answers, correct answers, and per-question correctness.
- Record participant percentages in a sorted scoreboard.

The passing score is 60%. This project intentionally uses no file I/O.

## Structure

- `Question` stores prompt, options, and the correct answer.
- `Quiz` owns the question bank and starts attempts.
- `QuizAttempt` records selected answers.
- `AnswerResult` describes one graded answer.
- `QuizResult` calculates and summarizes the final result.
- `ScoreBoard` stores participant percentages.
- `Main` demonstrates a complete quiz attempt.

Source files are under `src/quizexamplatform` and use only standard Java.

## Run

```powershell
javac -d out src\quizexamplatform\*.java
java -cp out quizexamplatform.Main
```

See `TESTING.md` for manual test cases.
