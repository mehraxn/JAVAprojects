# Expense Tracker Testing

All commands run from the project root. The automated tests need only a JDK — CSV tests use `Files.createTempFile` and delete their files; nothing is written inside the repository.

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
javac -Xlint:all -Werror -d out src/expensetracker/*.java
```

## C) Strict compile: tests

```text
javac -Xlint:all -Werror -cp out -d test-out tests/expensetracker/*.java
```

## D) Run the automated tests

Linux/macOS:

```text
java -cp "out:test-out" expensetracker.TestRunner
```

Windows (PowerShell or Git Bash — Windows Java uses `;`):

```text
java -cp "out;test-out" expensetracker.TestRunner
```

The runner prints per-suite PASS/FAIL counts and a final summary, and exits 0 only if every check passes.

### What the suites cover

| Suite | Coverage |
|---|---|
| `ExpenseTest` | Field validation, trimming, zero/negative amounts, BigDecimal scale preservation, immutability (final class, private final fields, no setters) |
| `ExpenseServiceTest` | Add/remove/find, duplicate IDs, category (case-insensitive), month, date-range, and amount-range filters, exact BigDecimal totals (including 0.10 × 10 = 1.00), category/monthly totals, highest expense, unmodifiable results |
| `CsvExpenseStoreTest` | Round trip with commas/quotes/scale/dates, atomic save leaving no temp files, duplicate IDs, malformed rows, invalid dates/amounts, missing/empty/blank/header-only files, directory paths |
| `MainTest` | Exit codes and output for `help`, `demo`, `csv-demo`, `report-demo`, `validation-demo`, no-args default, and unknown commands |

## E) Run the CLI demos

```text
java -cp out expensetracker.Main help
java -cp out expensetracker.Main demo
java -cp out expensetracker.Main csv-demo
java -cp out expensetracker.Main report-demo
java -cp out expensetracker.Main validation-demo
```

All of these must exit 0 (the validation-demo failures are intentional demonstrations). `java -cp out expensetracker.Main bogus` must print an error to stderr and exit non-zero.

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

- `new Expense(..., BigDecimal.ZERO, ...)` → `IllegalArgumentException`
- Add two expenses with the same ID → `IllegalArgumentException`
- `filterByDateRange(later, earlier)` → `IllegalArgumentException`
- `filterByAmountRange(new BigDecimal("-1"), ...)` → `IllegalArgumentException`
- Load a CSV row with `2026-02-30` → `IOException` naming the line
- Load a CSV with a duplicate ID → `IOException`
- Add to any returned list → `UnsupportedOperationException`
- Save over an existing CSV → contents fully replaced, no `.csv.tmp` files left behind
