# Job Application Tracker

## Overview

A command-line Java job application tracker with CSV persistence. Records job applications, searches and filters them, updates statuses, shows status totals, and preserves everything in a UTF-8 CSV file — with no external dependencies at all.

## What This Project Demonstrates

- Java OOP: encapsulated domain model with validation (`JobApplication`)
- Service/repository separation (`TrackerService` / `ApplicationRepository` / `CsvApplicationRepository`)
- CSV file persistence with hand-written, quote-aware parsing and escaping
- Search, filter, update, and summary workflows
- Defensive copies so callers can never corrupt stored state
- Dependency-free automated tests (43 tests, 104 checks) with a custom runner
- Strict compilation: everything builds under `javac -Xlint:all -Werror`
- A simple command-based CLI with correct exit codes and friendly error messages

## Features

- Add applications (company, role, status, optional notes; dated today, sequential IDs)
- List applications in insertion order
- Update an application's status by ID
- Case-insensitive search across company and role
- Filter by status and summary counts per status
- Save/load CSV — missing files load as an empty tracker, malformed files are rejected before touching in-memory data
- Safe in-memory demo workflow

## Tech Stack

- Java 21 standard library.
- UTF-8 CSV persistence with quote-aware parsing.
- Plain `javac`/`java`; no Maven or external dependencies.
- Dependency-free tests plus Bash and PowerShell scripts.

## Architecture / Design

```text
CLI → TrackerService → ApplicationRepository → CsvApplicationRepository → CSV file
```

`JobApplication` owns validated record state, the service owns search/filter/update workflows, and the repository abstraction isolates file persistence. Loading validates a full file before replacing in-memory data.

## Project Structure

```text
src/jobapplicationtracker/     Application source (model, service, repository, CLI)
tests/jobapplicationtracker/   Dependency-free tests and TestRunner
scripts/test.sh, test.ps1      One-command validation (clean, compile, test, smoke test)
README.md, TESTING.md          Documentation
TEST_RESULTS.md                Actual recorded validation results
```

## How to Run

Compile (strict flags — the build treats every warning as an error):

```text
javac -Xlint:all -Werror -d out src/jobapplicationtracker/*.java
```

Run the demo (in-memory only, writes nothing to disk):

```text
java -cp out jobapplicationtracker.Main demo
```

Use the CLI (the CSV file is created on the first `add`):

```text
java -cp out jobapplicationtracker.Main help
java -cp out jobapplicationtracker.Main add applications.csv "Google" "Backend Engineer" APPLIED "Applied online"
java -cp out jobapplicationtracker.Main list applications.csv
java -cp out jobapplicationtracker.Main update-status applications.csv 1 INTERVIEW
java -cp out jobapplicationtracker.Main search applications.csv backend
java -cp out jobapplicationtracker.Main summary applications.csv
```

Statuses: `APPLIED`, `SCREENING`, `INTERVIEW`, `OFFER`, `REJECTED`, `WITHDRAWN` (case-insensitive on input). Invalid commands and inputs exit non-zero with a clear message and no stack trace. Local CSV files like `applications.csv` are gitignored.

## CSV format

```csv
id,company,role,applicationDate,status,notes
1,Northwind,Java Developer,2026-06-24,INTERVIEW,Technical interview scheduled
2,"Acme, Inc.",Backend Engineer,2026-06-30,SCREENING,"Recruiter call, Tuesday"
```

Dates use `yyyy-MM-dd`. Commas and quotation marks round-trip through quoted fields; multiline values are intentionally not supported. Loading validates the whole file (header, field count, IDs, dates, statuses, quoting, duplicates) before replacing any in-memory records.

## Testing

- `TESTING.md` — exact commands for strict compile, the test runner, the CLI workflow, and the scripts.
- `TEST_RESULTS.md` — the honest record of the validation actually performed.
- Quick version: `./scripts/test.sh` (Linux/macOS/Git Bash) or `.\scripts\test.ps1` (Windows PowerShell).

## Known Limitations

- Local CLI only — no GUI, no web UI
- CSV file storage only — no database
- No user accounts, no multi-user support, no concurrent-writer protection
- No encryption for stored files, no cloud sync
- No atomic file replacement or backups
- Status transitions are not constrained to a workflow

## Resume Value

Built a Java job-application tracker with repository/service separation, CSV persistence, quote-aware parsing, status updates, search, summaries, defensive copies, CLI error handling, and automated tests.

## Possible future improvements

- Editing company, role, date, and notes from the CLI
- Date-range searches and response-rate statistics
- Follow-up reminders
- Atomic file replacement and backup copies
