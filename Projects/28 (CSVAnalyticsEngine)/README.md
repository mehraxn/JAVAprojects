# CSV Analytics Engine

A standard-Java console project that reads tabular CSV data and performs small, reusable analyses. It supports quoted commas and quotation marks without relying on an external CSV library.

## Features

- Load UTF-8 CSV data and expose column names and row count.
- Parse header and data rows into `DataSet` and `DataRow` objects.
- Preserve empty cells as missing values.
- Filter rows by an exact column value.
- Group rows and count groups by a selected column.
- Calculate minimum, maximum, average, sum, and valid-value count for numeric columns.
- Report missing and nonnumeric values separately while calculating statistics from valid values.
- Reject duplicate/empty headers, inconsistent row widths, unknown columns, and malformed quoted fields.
- Write a data set back to CSV with safe quoting.

## Java concepts practiced

- Object-oriented modeling and separation of responsibilities
- `List`, `Map`, and `Set` collections
- File I/O with `Path`, `Files`, and UTF-8
- `BigDecimal` arithmetic and rounding
- CSV state-based parsing
- Validation, defensive copies, and exception handling

## Main classes

- `DataRow` — one ordered mapping of columns to values.
- `DataSet` — validated columns and rows.
- `CsvReader` — CSV file parser.
- `CsvWriter` — CSV file writer.
- `AnalyticsService` — filtering, grouping, counting, and numeric calculations.
- `NumericStatistics` — result object for numeric analysis.
- `Main` — command-line demonstration.

## How it works

The first nonblank line is treated as the header. Later blank lines are ignored, while every nonblank row must have the same number of fields as the header. Column lookup is case-insensitive. Numeric analysis uses `BigDecimal`: blank cells increment the missing count, nonnumeric cells increment the invalid count, and only valid numbers affect the minimum, maximum, sum, and average. If a column has no valid numbers, minimum, maximum, and average are reported as `n/a`.

The parser supports commas and doubled quotation marks inside quoted fields. Multiline fields are intentionally outside this beginner-level implementation.

## Example usage

Compile from the project folder:

```text
javac -d out src/csvanalyticsengine/*.java
```

Run with a CSV path, an optional numeric column, and an optional grouping column:

```text
java -cp out csvanalyticsengine.Main sales.csv amount category
```

Example input:

```csv
item,category,amount
Notebook,Office,4.50
Coffee,Food,3.20
Unknown,Food,not-available
Tea,Food,
```

This reports four rows, statistics based on the two valid amounts, one invalid amount, one missing amount, and group sizes for `category`.

## Possible future improvements

- Delimiter selection and multiline quoted fields
- Date and boolean column summaries
- Median and percentile calculations
- Streaming support for files too large to hold in memory
- Interactive column selection and report export
- Automated unit tests
