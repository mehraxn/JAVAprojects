# Testing the Notification Service

This guide lists the exact commands used to validate the project. The results of actually running them are recorded in `TEST_RESULTS.md`. Everything is dependency-free: only a JDK (21 used for validation) is required — no Maven, Gradle, or JUnit. All senders are `MockNotificationSender` instances; no real delivery service is ever contacted.

Quick version: run `./scripts/test.sh` (Linux/macOS/Git Bash) or `.\scripts\test.ps1` (Windows PowerShell) — they perform steps A–E below.

## A) Clean

Linux/macOS/Git Bash:

```bash
rm -rf out test-out
```

Windows PowerShell:

```powershell
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```

## B) Strict compile — application

```text
javac -Xlint:all -Werror -d out src/notificationservice/*.java
```

Every warning is an error; the build must be completely clean.

## C) Strict compile — tests

```text
javac -Xlint:all -Werror -cp out -d test-out tests/notificationservice/*.java
```

## D) Run automated tests

Linux/macOS/Git Bash (classpath separator `:`):

```bash
java -cp "out:test-out" notificationservice.TestRunner
```

Windows PowerShell (classpath separator `;`):

```powershell
java -cp "out;test-out" notificationservice.TestRunner
```

Expected: `PASS` per test, a summary like `Tests passed: 47, failed: 0 (136 checks total)`, `RESULT: PASS`, and exit code 0. Any failure prints `FAIL` with the reason and exits 1.

## E) Run the CLI demos

```text
java -cp out notificationservice.Main help
java -cp out notificationservice.Main demo
java -cp out notificationservice.Main send EMAIL learner@example.com "Welcome"
java -cp out notificationservice.Main retry-demo
java -cp out notificationservice.Main validation-demo
java -cp out notificationservice.Main missing-sender-demo
```

Expected:

- `demo` shows `[MOCK EMAIL]`/`[MOCK APP]` deliveries, one intentional SMS failure, a successful retry, and a history where all three notifications end `SENT` (the SMS with `attempts=2`). Exit code 0.
- `send` prints the mock delivery and a `SENT` result, exit code 0.
- `retry-demo` shows a retry success and then a retry exhaustion (`false` once the 2-attempt limit is reached). Exit code 0.
- `validation-demo` prints `ACCEPTED`/`REJECTED` lines per sample recipient. Exit code 0.
- `missing-sender-demo` shows a clean `FAILED` record with a "No mock sender is registered" error — it exits 0 because the failure is the demonstration.

Error behavior (each exits non-zero with a message on stderr, no stack trace):

```text
java -cp out notificationservice.Main frobnicate            # unknown command
java -cp out notificationservice.Main send                  # missing arguments
java -cp out notificationservice.Main send SMS abc123 "Hi"  # invalid recipient
java -cp out notificationservice.Main send FAX 123 "Hi"     # unknown channel
```

## F) Scripts

Linux/macOS/Git Bash:

```bash
./scripts/test.sh
```

Windows PowerShell:

```powershell
.\scripts\test.ps1
```

Both scripts: clean → strict compile app → strict compile tests → TestRunner → demo → retry-demo → validation-demo, stopping at the first failure. `test.sh` picks the right classpath separator automatically, so it also works from Git Bash on Windows.

## G) Cleanup

Linux/macOS/Git Bash:

```bash
rm -rf out test-out
```

Windows PowerShell:

```powershell
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```
