# Test Results

Date: 2026-07-11

All commands below were actually executed on a Windows 11 machine from this project directory. No system-wide JDK was installed, so validation used a portable Eclipse Temurin JDK 21.0.11 (`javac`/`java` only — the project needs no build tool or libraries). Nothing in this file is estimated or assumed.

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK Temurin 21.0.11+10 (portable; no system JDK installed) |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/csvanalyticsengine/*.java` — clean |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/csvanalyticsengine/*.java` — clean |
| Automated tests | PASS | `TestRunner`: 64 tests passed, 0 failed, 149 assertion checks total, exit code 0 |
| CLI demo | PASS | `java -cp out csvanalyticsengine.Main demo` — full workflow incl. round trip, temp files deleted, exit code 0 |
| CLI summary/stats/group smoke tests | PASS | Against `examples/sales.csv`: summary (6 rows, 5 columns), stats on `amount` (4 valid, 1 missing, 1 invalid, min -5.75, max 35.00, sum 45.95, average 11.4875), group counts (Food 3, Books 2, Electronics 1) — all exit code 0 |
| CLI filter / export-filtered | PASS | `filter` printed 3 Food rows; `export-filtered` wrote 3 rows with correct quoting (verified raw file content) and the output was re-read with `summary` — exit code 0 |
| CSV round-trip test | PASS | CsvWriter output re-read by CsvReader in the automated tests, the demo, and the export smoke test — values incl. commas/quotes preserved |
| Error exit-code tests | PASS | Missing file, malformed CSV (unclosed quote), unknown command, unknown column, and missing arguments each printed a clean stderr message (no stack trace) and exited 1 |
| scripts/test.ps1 | PASS | Full pipeline (clean, strict compiles, 64 tests, demo, 3 CLI commands), exit code 0 |
| scripts/test.sh | PASS | Run via Git Bash on Windows; picks the `;` classpath separator automatically, exit code 0 |

Test breakdown: 12 `CsvReader`, 8 `CsvWriter`, 8 `DataSet`, 8 `DataRow`, 5 `NumericStatistics`, 12 `AnalyticsService`, 11 CLI smoke tests (calling `Main.run` directly with captured streams). All file-based tests use `Files.createTempFile` and delete their temp files.

## Known limitations

- Local CSV files only — no database.
- Whole-file loading; no streaming support for very large files.
- No GUI.
- No authentication.
- No chart visualization.
- Multiline CSV values are not supported.
- Designed as a Java file-processing and analytics learning project.
