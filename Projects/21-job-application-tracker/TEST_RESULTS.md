# Test Results

Date: 2026-07-11

All commands below were actually executed on a Windows 11 machine from this project directory. No system-wide JDK was installed, so validation used a portable Eclipse Temurin JDK 21.0.11 (`javac`/`java` only — the project needs no build tool or libraries). Nothing in this file is estimated or assumed.

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK Temurin 21.0.11+10 (portable; no system JDK installed) |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/jobapplicationtracker/*.java` — clean, including the former 'this' escape warning (fixed by making `JobApplication` final) |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/jobapplicationtracker/*.java` — clean |
| Automated tests | PASS | `TestRunner`: 43 tests passed, 0 failed, 104 assertion checks total, exit code 0 |
| CLI demo | PASS | `java -cp out jobapplicationtracker.Main demo` — in-memory workflow printed, exit code 0 |
| CLI file workflow | PASS | `add` (x2, incl. company with comma), `list`, `update-status`, `search`, `summary` against a temp CSV — all exit code 0, data round-tripped |
| CLI error handling | PASS | No command, unknown command, unknown ID, invalid status, and non-numeric ID each printed a clear message (no stack trace) and exited 1; `help` exited 0 |
| CSV save/load tests | PASS | Round-trip, comma/quote escaping, empty/missing/header-only files, blank lines, wrong header, wrong field count, invalid ID/date/status, duplicate IDs, malformed quoting — all covered in the automated tests above |
| scripts/test.ps1 | PASS | Full pipeline (clean, strict compiles, tests, demo), exit code 0 |
| scripts/test.sh | PASS | Run via Git Bash on Windows; picks the `;` classpath separator automatically, exit code 0 |

Test breakdown: 15 `JobApplication` tests, 13 `TrackerService` tests (in-memory fake repository), 15 `CsvApplicationRepository` tests (temp files via `Files.createTempFile`, deleted afterwards).

## Known limitations

- Local CSV storage only — no database.
- No GUI, no web UI.
- No authentication or user accounts.
- No multi-user support or concurrent-writer protection.
- No encryption for stored files.
- No atomic file replacement or backups.
- Intended as a Java OOP/file-persistence learning project.
