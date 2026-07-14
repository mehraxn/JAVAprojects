# Expense Tracker

## Overview

An educational Java expense tracker with service-layer logic and CSV persistence, built entirely with the Java standard library. Money values are handled with `BigDecimal` throughout — no framework, database, or external dependency, and plain `javac`/`java` is enough to build, run, and test it.

## What This Project Demonstrates

- An immutable, validated `Expense` domain model (final class, private final fields, no setters)
- `BigDecimal` money handling — no `double` anywhere in money math, `compareTo` for comparisons
- Service-layer expense management with category/month/date-range/amount-range filtering
- Spending reports: overall, per-category, and per-month totals plus highest expense
- CSV persistence with quoted-field parsing (commas and quotes in values)
- Atomic-style CSV saving (temp file + replace, so a failed save never corrupts the target)
- Defensive/unmodifiable lists and maps from every query method
- Dependency-free automated tests (custom assertion helper + test runner)
- Strict compilation with `-Xlint:all -Werror`

## Features

- Add, list, find by ID, and remove expenses (duplicate IDs rejected)
- Filter by category (case-insensitive), calendar month, date range, or amount range
- Total spending, totals by category, totals by month, highest expense
- Save/load expenses as UTF-8 CSV with a fixed header
- Command-based CLI demos

## Main classes

- `Expense` — immutable, validated expense model.
- `ExpenseService` — in-memory expense collection, filters, and reports.
- `ExpenseStore` — persistence interface.
- `CsvExpenseStore` — CSV reader and writer with atomic-style saving.
- `Main` — CLI commands (`help`, `demo`, `csv-demo`, `report-demo`, `validation-demo`).

## Why BigDecimal?

Money is stored as `BigDecimal`, not `double`, because binary floating point cannot represent most decimal fractions exactly (`0.1 + 0.2 != 0.3`). Totals use `BigDecimal.ZERO` and `add`, and comparisons use `compareTo`, so `0.10` added ten times is exactly `1.00` — the automated tests verify this.

## Tech Stack

- Java 21 standard library.
- Plain `javac`/`java`; no Maven or external dependencies.
- `BigDecimal`, `java.time`, and UTF-8 CSV persistence.
- Dependency-free tests plus Bash and PowerShell scripts.

## Architecture / Design

`Expense` is the validated domain record. `ExpenseService` owns CRUD, filtering, totals, and reports through the `ExpenseStore` abstraction, while `CsvExpenseStore` implements local quote-aware persistence and safe file replacement.

## Project Structure

```text
.
├── src/expensetracker/     # Model, service, store abstraction/CSV store, CLI
├── tests/expensetracker/   # Custom tests and runner
├── scripts/                # Cross-platform validation scripts
├── TESTING.md
└── TEST_RESULTS.md
```

## How to Run

```text
javac -Xlint:all -Werror -d out src/expensetracker/*.java

java -cp out expensetracker.Main help
java -cp out expensetracker.Main demo
java -cp out expensetracker.Main csv-demo
java -cp out expensetracker.Main report-demo
java -cp out expensetracker.Main validation-demo
```

Running with no command prints the usage text. `demo` creates, lists, filters, and totals sample expenses. `csv-demo` saves to a temporary CSV file, reloads it, verifies the round trip, and deletes the file. `report-demo` shows all the spending reports. `validation-demo` intentionally triggers validation failures and exits 0 because the rejections are the point. `Main.run(args, out, err)` returns an exit code (0 for valid commands, non-zero for unknown ones) and only `main` calls `System.exit`.

## Validation rules

- ID, title, and category cannot be null or blank (values are trimmed; line breaks rejected).
- Date and amount cannot be null.
- Amount must be greater than zero.
- Duplicate expense IDs are rejected by the service and by CSV load/save.
- Date ranges must have `start <= end`; amount ranges need `0 <= min <= max`.

## CSV persistence

`CsvExpenseStore` writes UTF-8 CSV with the fixed header `id,title,amount,category,date` and ISO dates (`yyyy-MM-dd`). Fields containing commas or quotes are quoted and escaped; multiline fields are intentionally unsupported. Missing, empty, and blank files load as an empty list; malformed rows, wrong headers, invalid dates/amounts, and duplicate IDs are rejected with an `IOException` naming the line.

**Atomic-style save:** `save` writes to a temporary file in the target directory, then moves it over the target with `REPLACE_EXISTING` (and `ATOMIC_MOVE` where the filesystem supports it, falling back automatically). If saving fails halfway, the original file is untouched and the temp file is cleaned up.

Generated CSV files are ignored by Git (`*.csv` in `.gitignore`); the demos only ever write to system temp files that they delete.

Example CSV:

```text
id,title,amount,category,date
E-001,Groceries,42.75,Food,2026-07-02
E-002,"Lunch, coffee",18.50,Food,2026-07-03
```

## Testing

The project ships with dependency-free automated tests (custom `Assert` helper including `assertBigDecimalEquals`, plus a `TestRunner`) covering the model, service, CSV persistence, and CLI:

```text
javac -Xlint:all -Werror -cp out -d test-out tests/expensetracker/*.java
java -cp "out;test-out" expensetracker.TestRunner   # Windows (use out:test-out on Linux/macOS)
```

Or run everything with one script: `./scripts/test.sh` (Linux/macOS/Git Bash) or `.\scripts\test.ps1` (Windows PowerShell). See [TESTING.md](TESTING.md) for the full procedure and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest recorded results.

## Java concepts practiced

- Classes, interfaces, encapsulation, immutability, and service/store responsibilities
- `List`, `Map`, `Set`, and `Optional`
- `BigDecimal`, `LocalDate`, and `YearMonth`
- File I/O with `Path` and `Files`, including atomic-style replacement
- Checked and unchecked exceptions
- Defensive, unmodifiable collection views
- Exit codes and testable CLI entry points

## Known Limitations

- Local in-memory service with explicit CSV persistence — no database and no automatic saving
- No HTTP API, login, or authentication
- No GUI or charts
- No recurring expenses, currency codes, or currency conversion
- The educational CSV parser does not support multiline fields
- No production accounting or audit guarantees — this is a learning project

## Resume Value

Built a Java expense tracker with validated `BigDecimal` records, CSV persistence, filtering, totals, category/date reports, safe file replacement, CLI workflows, and automated tests.

## Possible future improvements

- Interactive menu input and expense editing
- Configurable currencies and formatted reports
- Automatic backups
- Budgets and spending alerts
