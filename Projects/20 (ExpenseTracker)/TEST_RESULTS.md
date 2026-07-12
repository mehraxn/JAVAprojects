# Test Results

Date: 2026-07-12

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 (Microsoft build), Windows 11 |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/expensetracker/*.java` |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/expensetracker/*.java` |
| Automated tests | PASS | 142 checks, 0 failures (`expensetracker.TestRunner`) |
| Expense model tests | PASS | 27 checks: validation, trimming, BigDecimal scale, immutability |
| Service-layer tests | PASS | 51 checks: add/remove/find, all four filters, reports, unmodifiable results |
| CSV persistence tests | PASS | 32 checks: round trip, quoting, malformed data, missing/empty files, atomic save |
| BigDecimal total tests | PASS | Exact decimal totals incl. 0.10 x 10 = 1.00; compareTo-based assertions |
| Main CLI tests | PASS | 32 checks: help/demo/csv-demo/report-demo/validation-demo, unknown commands |
| Main demo | PASS | `demo` exits 0 with exact totals |
| CSV demo | PASS | Saves to a temp CSV, reloads, verifies count and total, deletes the file |
| Report demo | PASS | Totals, category/month breakdowns, highest expense, range filters |
| scripts/test.sh | PASS | Full pipeline in Git Bash (compile, tests, demos, cleanup) |
| scripts/test.ps1 | PASS | Full pipeline in Windows PowerShell 5.1 |

## Known limitations

- Local in-memory service with explicit CSV persistence — no database.
- No HTTP API and no authentication/users.
- No recurring expenses and no currency conversion.
- No charts/GUI.
- The CSV parser does not support multiline fields.
- No production accounting guarantees — intended as a Java file-I/O and service-layer learning project.

## Notes

- Tests were run on Windows 11 with the classpath separator `;`. On Linux/macOS use `out:test-out`.
- CSV tests and demos write only to system temporary files and delete them; no CSV files are created inside the repository.
