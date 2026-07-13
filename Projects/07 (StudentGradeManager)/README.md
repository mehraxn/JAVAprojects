# Student Grade Manager

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

## Quick start

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

## Limitations

- No database
- No HTTP API
- No login/authentication
- No weighted grading
- No file import/export
- No production school information system guarantees
- Intended as a Java OOP/service-layer learning project
