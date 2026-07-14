# CSV Analytics Engine

## Overview

A command-line Java CSV analytics engine. It reads tabular CSV data with a hand-written, quote-aware parser, validates it into `DataSet`/`DataRow` models, and runs filtering, grouping, and numeric statistics — with no external dependencies at all.

## What This Project Demonstrates

- Custom CSV parsing (quoted commas, escaped quotes, malformed-input rejection)
- CSV writing/export with safe quoting and a verified read/write round trip
- Validated tabular data models with defensive copies — external mutation cannot corrupt stored data
- Filtering by exact value, grouping with counts, and `BigDecimal` numeric statistics
- Honest handling of missing and invalid numeric cells (counted separately, never silently dropped)
- A command-based CLI with correct exit codes and friendly error messages
- Dependency-free automated tests (64 tests, 149 checks) with a custom runner
- Strict compilation: everything builds under `javac -Xlint:all -Werror`

## Features

- Parse and validate quote-aware CSV data.
- Summarize rows, columns, and headers.
- Filter exact values and group rows with counts.
- Calculate `BigDecimal` min, max, sum, average, missing, and invalid counts.
- Export filtered datasets with safe quoting and read/write round-trip support.

## Tech Stack

- Java 21 standard library.
- Plain `javac`/`java`; no Maven or CSV library.
- `BigDecimal` numeric aggregation.
- Dependency-free tests plus Bash and PowerShell scripts.

## Architecture / Design

```text
CsvReader → DataSet/DataRow → AnalyticsService → NumericStatistics
                                      ↓
                                  CsvWriter
```

Parsing, validated tabular state, analytics, and output are separate components. Models expose defensive copies and unmodifiable views so analysis cannot mutate source data.

## Commands

| Command | What it does |
|---|---|
| `help` | Print usage and available commands |
| `demo` | Self-contained sample workflow using temporary files (cleans up after itself) |
| `summary <file.csv>` | Row count, column count, column names |
| `stats <file.csv> <column>` | Count, missing, invalid, min, max, sum, average |
| `group <file.csv> <column>` | Count rows per column value (empty values group as `(missing)`) |
| `filter <file.csv> <column> <value>` | Print rows matching an exact value |
| `export-filtered <in.csv> <out.csv> <column> <value>` | Write matching rows to a new CSV |

Column names are matched case-insensitively; filter values are matched exactly. Errors (missing file, malformed CSV, unknown command/column, missing arguments) print a clear message and exit non-zero — no stack traces.

## How to Run

```text
javac -Xlint:all -Werror -d out src/csvanalyticsengine/*.java

java -cp out csvanalyticsengine.Main help
java -cp out csvanalyticsengine.Main demo
java -cp out csvanalyticsengine.Main summary examples/sales.csv
java -cp out csvanalyticsengine.Main stats examples/sales.csv amount
java -cp out csvanalyticsengine.Main group examples/sales.csv category
java -cp out csvanalyticsengine.Main filter examples/sales.csv category Food
java -cp out csvanalyticsengine.Main export-filtered examples/sales.csv filtered.csv category Food
```

`examples/sales.csv` ships with deliberate edge cases: a quoted comma, escaped quotes, a missing amount, an invalid amount, and a negative amount. Generated CSVs like `filtered.csv` are gitignored; only `examples/*.csv` is tracked.

## Project Structure

```text
src/csvanalyticsengine/     Application source (parser, writer, models, analytics, CLI)
tests/csvanalyticsengine/   Dependency-free tests and TestRunner
examples/sales.csv          Sample data with edge cases
scripts/test.sh, test.ps1   One-command validation (clean, compile, test, smoke test)
README.md, TESTING.md       Documentation
TEST_RESULTS.md             Actual recorded validation results
```

## Main classes

- `CsvReader` — state-based CSV parser; rejects duplicate/empty headers, inconsistent row widths, and malformed quoting with line numbers.
- `CsvWriter` — CSV output with quoting/escaping; line breaks in values are a documented, rejected limitation.
- `DataSet` / `DataRow` — validated columns and rows; all getters return defensive copies or unmodifiable views.
- `AnalyticsService` — filtering, grouping, counting, and statistics.
- `NumericStatistics` — immutable result object (count, missing, invalid, min, max, sum, average) with internal consistency checks.
- `Main` — argument parsing and output only; the CLI logic lives in a testable `run(args, out, err)` method.

## Testing

- `TESTING.md` — exact commands for strict compile, the test runner, the CLI workflow, and error-behavior checks.
- `TEST_RESULTS.md` — the honest record of the validation actually performed.
- Quick version: `./scripts/test.sh` (Linux/macOS/Git Bash) or `.\scripts\test.ps1` (Windows PowerShell).

## Known Limitations

- Local CSV files only — no database, no cloud storage
- Whole-file loading — not designed for huge files (no streaming)
- No GUI, no charts or visualization
- No authentication or multi-user support
- Multiline CSV values are not supported

## Resume Value

Built a dependency-free Java CSV analytics engine with quote-aware parsing, validated tabular models, filtering, grouping, precise statistics, safe export, CLI commands, and automated tests.

## Possible future improvements

- Streaming reader for large files
- More aggregations (median, percentiles, group-by sums)
- Column type inference
- Date-typed columns and date-range filters
