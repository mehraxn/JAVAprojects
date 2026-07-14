# Quiz / Exam Platform Testing

All commands run from the project root. The automated tests need only a JDK — no database, network, or files.

## A) Clean

Linux/macOS/Git Bash:

```text
rm -rf out test-out
```

Windows PowerShell:

```text
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```

## B) Strict compile: application

```text
javac -Xlint:all -Werror -d out src/quizexamplatform/*.java
```

## C) Strict compile: tests

```text
javac -Xlint:all -Werror -cp out -d test-out tests/quizexamplatform/*.java
```

## D) Run the automated tests

Linux/macOS:

```text
java -cp "out:test-out" quizexamplatform.TestRunner
```

Windows (PowerShell or Git Bash — Windows Java uses `;`):

```text
java -cp "out;test-out" quizexamplatform.TestRunner
```

The runner prints per-suite PASS/FAIL counts and a final summary, and exits 0 only if every check passes.

### What the suites cover

| Suite | Coverage |
|---|---|
| `QuestionTest` | Prompt/option validation, duplicate (case-insensitive) rejection, correct-index range, unmodifiable and defensive options |
| `QuizTest` | Title validation, default and custom passing percentage, duplicate prompts, empty-quiz start, attempt snapshot independence |
| `QuizAttemptTest` | Record/replace answers, index validation, incomplete-finish rejection, finish/lock, stable result, snapshot independence, custom threshold |
| `QuizResultTest` | Score/percentage/threshold logic (100%, exact 60%, below, custom), unmodifiable and defensive answer results, `AnswerResult` feedback |
| `ScoreBoardTest` | Ranking (score desc, name asc ties), best-score-per-participant, validation, unmodifiable ranking |
| `MainTest` | Exit codes and output for `help`, `demo`, `attempt-demo`, `validation-demo`, `scoreboard-demo`, no-args default, and unknown commands |

## E) Run the CLI demos

```text
java -cp out quizexamplatform.Main help
java -cp out quizexamplatform.Main demo
java -cp out quizexamplatform.Main attempt-demo
java -cp out quizexamplatform.Main validation-demo
java -cp out quizexamplatform.Main scoreboard-demo
```

All of these must exit 0 (the validation-demo failures are intentional demonstrations). `java -cp out quizexamplatform.Main bogus` must print an error to stderr and exit non-zero.

## F) Scripts

Linux/macOS/Git Bash:

```text
./scripts/test.sh
```

Windows PowerShell:

```text
.\scripts\test.ps1
```

Both scripts clean, strict-compile the app and tests, run the full test suite, run all four demo commands, and remove `out/` and `test-out/` afterward.

## G) Cleanup

Linux/macOS/Git Bash:

```text
rm -rf out test-out
```

Windows PowerShell:

```text
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```

## Manual edge cases worth trying

- Create a question with duplicate options (`"Yes"`, `"yes"`) → `IllegalArgumentException`
- Create a question with a correct index out of range → `IllegalArgumentException`
- `new Quiz(title, 150)` → `IllegalArgumentException`
- `quiz.startQuiz(...)` on an empty quiz → `IllegalStateException`
- Add a question to a quiz after starting an attempt → the attempt's question count is unchanged
- `attempt.finish()` before answering everything → `IllegalStateException`
- Record an answer or finish again after finishing → `IllegalStateException`
- Record the same participant twice with a lower score → the scoreboard keeps the higher one
- Two participants with equal scores → ranked by name ascending
