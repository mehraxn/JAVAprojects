# Student Grade Manager

## Overview

Educational Java student grade manager focused on OOP, subject-based grades, statistics, reports, validation, and defensive snapshots.

The project is dependency-free and runs with plain `javac`/`java`.

## Features

- Student domain model
- GradeBook service layer
- Student registration with duplicate-ID protection
- Subject-based grade recording
- Grade validation from 0 to 100
- Average, highest, and lowest grade calculations
- Pass/fail evaluation
- Letter-grade evaluation
- Student transcript snapshots
- Class average report
- Subject performance report
- Failing-students report
- Student ranking by average
- Defensive snapshots so public callers cannot mutate internal state
- Command-based CLI demos
- Dependency-free automated tests
- Strict compilation with `javac -Xlint:all -Werror`

## What This Project Demonstrates

- Object-oriented domain modeling and encapsulation.
- Collections for subject grades, searching, summaries, and ranking.
- Validation at service and domain boundaries.
- Immutable reports and defensive snapshots.
- Dependency-free CLI and automated-test design.

## Main classes

- `Student`: owns student identity, subject-based grades, validation, and per-student calculations.
- `GradeBook`: service layer for student registration, grade recording, searching, ranking, and reports.
- `StudentSnapshot`: immutable public view of a student transcript.
- `SubjectGradeSummary`: immutable public view of one subject's class performance.
- `GradeBookReport`: immutable class-level report.
- `Main`: CLI/demo entry point only.

## Behavior notes

- Student IDs, student names, and subject names are trimmed.
- Blank or null IDs, names, and subjects are rejected.
- Grades must be finite numbers between 0 and 100.
- Boundary grades `0` and `100` are valid.
- Passing threshold is `60.0`.
- Letter-grade rules:
  - `A`: 90-100
  - `B`: 80-89.99
  - `C`: 70-79.99
  - `D`: 60-69.99
  - `F`: below 60
- Students with no grades have average/highest/lowest `0.0`, are not passing, and use letter grade `N/A`.
- Ranking is deterministic: average descending, then student name, then student ID.
- Public GradeBook methods return snapshots, reports, or unmodifiable lists instead of internal `Student` objects.
- Data is in memory only for the current program run.

## Tech Stack

- Java 21 standard library.
- Plain `javac`/`java`; no Maven, Gradle, or external runtime dependencies.
- Dependency-free custom test runner and assertion helpers.
- Bash and PowerShell validation scripts.

## Project Structure

```text
.
├── src/studentgrademanager/     # Application and domain code
├── tests/studentgrademanager/   # Dependency-free automated tests
├── scripts/                     # Bash and PowerShell test scripts
├── TESTING.md
└── TEST_RESULTS.md
```

## How to Run

From this project folder:

```powershell
javac -Xlint:all -Werror -d out src/studentgrademanager/*.java

java -cp out studentgrademanager.Main help
java -cp out studentgrademanager.Main demo
java -cp out studentgrademanager.Main grade-demo
java -cp out studentgrademanager.Main report-demo
java -cp out studentgrademanager.Main ranking-demo
java -cp out studentgrademanager.Main search-demo
java -cp out studentgrademanager.Main validation-demo
```

No command defaults to `help`.

## Testing

Linux/macOS/Git Bash:

```bash
bash scripts/test.sh
```

Windows PowerShell:

```powershell
.\scripts\test.ps1
```

The scripts strictly compile the application, strictly compile the dependency-free tests, run the test runner, run each CLI demo, and remove generated build folders.

See [TESTING.md](TESTING.md) for the exact procedure and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest recorded validation results.

## Known Limitations

- No database
- No HTTP API
- No login/authentication
- No weighted grading
- No file import/export
- No production school information system guarantees
- Intended as a Java OOP/service-layer learning project

## Resume Value

Built a dependency-free Java grade-management system with student registration, subject grades, statistics, reports, ranking, defensive snapshots, CLI demonstrations, and automated tests.
