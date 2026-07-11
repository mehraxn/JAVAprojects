# Testing the Job Application Tracker

This guide lists the exact commands used to validate the project. The results of actually running them are recorded in `TEST_RESULTS.md`. Everything is dependency-free: only a JDK (21 used for validation) is required — no Maven, Gradle, or JUnit.

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
javac -Xlint:all -Werror -d out src/jobapplicationtracker/*.java
```

Every warning is an error; the build must be completely clean.

## C) Strict compile — tests

```text
javac -Xlint:all -Werror -cp out -d test-out tests/jobapplicationtracker/*.java
```

## D) Run automated tests

Linux/macOS/Git Bash (classpath separator `:`):

```bash
java -cp "out:test-out" jobapplicationtracker.TestRunner
```

Windows PowerShell (classpath separator `;`):

```powershell
java -cp "out;test-out" jobapplicationtracker.TestRunner
```

Expected: `PASS` per test, a summary like `Tests passed: 43, failed: 0 (104 checks total)`, `RESULT: PASS`, and exit code 0. Any failure prints `FAIL` with the reason and exits 1. CSV tests use `Files.createTempFile` and delete their temp files — no CSV files are left in the repository.

## E) Run the CLI demo

```text
java -cp out jobapplicationtracker.Main demo
```

Expected: a sample in-memory workflow (add, update, search, summary), `Demo completed successfully.`, exit code 0. Nothing is written to disk.

## F) Manual CLI workflow

Uses a local `applications.csv` (gitignored; created on first `add`):

```text
java -cp out jobapplicationtracker.Main add applications.csv "Google" "Backend Engineer" APPLIED "Applied online"
java -cp out jobapplicationtracker.Main list applications.csv
java -cp out jobapplicationtracker.Main update-status applications.csv 1 INTERVIEW
java -cp out jobapplicationtracker.Main search applications.csv backend
java -cp out jobapplicationtracker.Main summary applications.csv
```

Error behavior worth testing (each exits non-zero with a clear message, no stack trace):

```text
java -cp out jobapplicationtracker.Main                                  # no command
java -cp out jobapplicationtracker.Main frobnicate                       # unknown command
java -cp out jobapplicationtracker.Main update-status applications.csv 99 OFFER    # unknown ID
java -cp out jobapplicationtracker.Main add applications.csv "X" "Y" NOTASTATUS    # bad status
```

## G) Scripts

Linux/macOS/Git Bash:

```bash
./scripts/test.sh
```

Windows PowerShell:

```powershell
.\scripts\test.ps1
```

Both scripts: clean → strict compile app → strict compile tests → run TestRunner → run CLI demo, and stop at the first failure. `test.sh` picks the right classpath separator automatically, so it also works from Git Bash on Windows.

## H) Cleanup

Linux/macOS/Git Bash:

```bash
rm -rf out test-out
rm -f applications.csv
```

Windows PowerShell:

```powershell
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
Remove-Item applications.csv -ErrorAction SilentlyContinue
```
