# Test Results

Date: 2026-07-12

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 (Microsoft build), Windows 11 |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/quizexamplatform/*.java` |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/quizexamplatform/*.java` |
| Automated tests | PASS | 167 checks, 0 failures (`quizexamplatform.TestRunner`) |
| Question tests | PASS | 27 checks: validation, duplicate options, option safety |
| Quiz tests | PASS | 29 checks: threshold config, duplicate prompts, attempt snapshot |
| Attempt lifecycle tests | PASS | 30 checks: record/replace/finish/lock, snapshot, custom threshold |
| Result tests | PASS | 34 checks: score, percentage, pass/fail, `AnswerResult` feedback |
| ScoreBoard tests | PASS | 21 checks: ranking, ties, best-score-per-participant |
| Main CLI tests | PASS | 26 checks: help/demo/attempt/validation/scoreboard, unknown commands |
| Main demo | PASS | `demo` exits 0 |
| Attempt demo | PASS | `attempt-demo` shows replace/finish/lock and snapshot independence |
| Scoreboard demo | PASS | `scoreboard-demo` shows ranking, ties, and best-score behavior |
| scripts/test.sh | PASS | Full pipeline in Git Bash (compile, tests, demos, cleanup) |
| scripts/test.ps1 | PASS | Full pipeline in Windows PowerShell 5.1 |

## Known limitations

- In-memory quiz platform only — no database.
- No HTTP API and no authentication/users.
- No persistent question bank.
- No randomized question order.
- No timer/proctoring or exam-security features.
- No GUI.
- Intended as a Java OOP/business-logic learning project.

## Notes

- Tests were run on Windows 11 with the classpath separator `;`. On Linux/macOS use `out:test-out`.
- The suite is pure in-memory logic; no files or processes are created.
