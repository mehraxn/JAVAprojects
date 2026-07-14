# Testing the Authentication System

This guide lists the exact commands used to validate the project. The results of actually running them are recorded in `TEST_RESULTS.md`. Everything is dependency-free: only a JDK (21 used for validation) is required — no Maven, Gradle, or JUnit. All passwords below are throwaway local demo values — never use real passwords while testing this project.

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
javac -Xlint:all -Werror -d out src/authenticationsystem/*.java
```

Every warning is an error; the build must be completely clean.

## C) Strict compile — tests

```text
javac -Xlint:all -Werror -cp out -d test-out tests/authenticationsystem/*.java
```

## D) Run automated tests

Linux/macOS/Git Bash (classpath separator `:`):

```bash
java -cp "out:test-out" authenticationsystem.TestRunner
```

Windows PowerShell (classpath separator `;`):

```powershell
java -cp "out;test-out" authenticationsystem.TestRunner
```

Expected: `PASS` per test, a summary like `Tests passed: 46, failed: 0 (139 checks total)`, `RESULT: PASS`, and exit code 0. Any failure prints `FAIL` with the reason and exits 1. The suite takes a few seconds on purpose — PBKDF2 runs 120,000 iterations per hash. Session-expiry tests use the injected `MutableClock`; there is no `Thread.sleep` anywhere.

## E) Run the CLI demos

```text
java -cp out authenticationsystem.Main help
java -cp out authenticationsystem.Main demo
java -cp out authenticationsystem.Main register-demo
java -cp out authenticationsystem.Main login-demo
java -cp out authenticationsystem.Main authorization-demo
java -cp out authenticationsystem.Main expiry-demo
```

Expected:

- `demo` — registers a USER, seeds a demo ADMIN, logs both in, shows the USER blocked from the ADMIN action, performs the ADMIN action, logs out, and shows the dead token. Only masked tokens are printed. Exit code 0.
- `register-demo` — valid registration, case-insensitive duplicate rejection, weak-password rejection. Exit code 0.
- `login-demo` — correct password issues a session; wrong password and unknown username both print `null`. Exit code 0.
- `authorization-demo` — the full role matrix including an invalid token. Exit code 0.
- `expiry-demo` — a 30-minute session checked at 10:00/10:29/10:31 via the injected Clock; instant, no waiting. Exit code 0.

Error behavior (exit non-zero, message on stderr, no stack trace):

```text
java -cp out authenticationsystem.Main frobnicate     # unknown command
java -cp out authenticationsystem.Main                # no command
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

Both scripts: clean → strict compile app → strict compile tests → TestRunner → demo → authorization-demo → expiry-demo, stopping at the first failure. `test.sh` picks the right classpath separator automatically, so it also works from Git Bash on Windows.

## G) Cleanup

Linux/macOS/Git Bash:

```bash
rm -rf out test-out
```

Windows PowerShell:

```powershell
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```
