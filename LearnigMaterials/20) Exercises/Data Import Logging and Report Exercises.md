# Data Import Logging and Report Exercises

These exercises connect Java IO, validation, logging, immutable result objects, and report generation. Use small generic domains such as products, invoices, customers, employees, orders, payments, books, or courses.

## Exercise 1: Import product rows from CSV

Difficulty: Easy

## Goal

Build a CSV import method that reads product rows and returns a clear import result.

## Background

Import code should separate file reading, row parsing, validation, and result reporting. A caller should know how many rows were accepted and why invalid rows were skipped.

## Input Format

```text
id,name,quantity,price
1,Notebook,20,4.99
2,Pen,100,1.25
```

## Requirements

- Read the file with UTF-8.
- Skip the header row.
- Skip blank lines.
- Validate required fields.
- Convert quantity to `int`.
- Convert price to `BigDecimal`.
- Return an `ImportResult` containing accepted count, skipped count, and error messages.

## Expected Behavior

Valid rows are converted into product objects. Invalid rows are skipped without crashing the whole import.

## Test Cases

- A valid file imports all rows.
- A file with one invalid row reports one skipped row.
- An empty file returns zero accepted rows.
- A missing required field creates an error message with the row number.

## Hints

Use a small helper method such as `parseProduct(String line, int rowNumber)`.

## Common Mistakes

- Parsing money with `double`.
- Returning only a list and losing skipped-row details.
- Stopping the whole import for one bad row.

## Bonus Challenge

Add a maximum error limit. Stop importing after five invalid rows and report that the import was stopped early.

## Solution Outline

Read all lines, skip the header, parse each row, collect valid products, collect row-level errors, and return an immutable result object.

## Exercise 2: Handle quoted CSV values

Difficulty: Medium

## Goal

Support CSV values that contain commas inside quotes.

## Background

Simple `split(",")` works only for basic CSV files. Real CSV data often contains quoted values such as `"Notebook, Large"`.

## Input Format

```text
id,name,quantity,price
1,"Notebook, Large",20,4.99
2,"Pen Blue",100,1.25
```

## Requirements

- Parse commas inside quoted fields correctly.
- Preserve spaces inside quoted product names.
- Reject rows with an unclosed quote.
- Keep row-numbered error messages.

## Expected Behavior

The product name `Notebook, Large` is treated as one value, not two values.

## Test Cases

- Quoted comma in a name is accepted.
- Plain rows still work.
- Unclosed quote is rejected.
- Too many columns are rejected.

## Hints

Write a small character-by-character parser or clearly document when a real CSV library should be used.

## Common Mistakes

- Using `split(",")` and producing shifted columns.
- Silently accepting malformed quoted data.
- Removing meaningful spaces from names.

## Bonus Challenge

Support escaped quotes inside quoted fields.

## Solution Outline

Track whether the parser is currently inside quotes. Treat commas as separators only when the parser is outside quotes.

## Exercise 3: Parse invoice dates safely

Difficulty: Easy

## Goal

Import invoices with a strict `yyyy-MM-dd` date format.

## Background

Date parsing should be predictable. Invalid dates should be reported as import errors instead of becoming incorrect records.

## Input Format

```text
invoiceNumber,customerName,issuedOn,total
INV-1001,Alice,2026-01-15,120.00
```

## Requirements

- Parse dates with `DateTimeFormatter.ISO_LOCAL_DATE`.
- Reject invalid dates.
- Reject future dates if the business rule requires historical invoices only.
- Include row numbers in error messages.

## Expected Behavior

Rows with valid dates are accepted. Rows with invalid dates are skipped and explained.

## Test Cases

- `2026-01-15` is accepted.
- `15-01-2026` is rejected.
- `2026-02-30` is rejected.
- Blank date is rejected.

## Hints

Use `LocalDate.parse(value, formatter)` inside a small parsing method.

## Common Mistakes

- Accepting multiple undocumented date formats.
- Using old `Date` APIs for new code.
- Hiding parsing failures.

## Bonus Challenge

Inject a `Clock` so future-date validation is testable.

## Solution Outline

Parse the date using `LocalDate`, catch parsing exceptions, convert failures into row errors, and keep the import running.

## Exercise 4: Design an import result object

Difficulty: Medium

## Goal

Create an immutable result object for import operations.

## Background

A good import result explains what happened without exposing mutable internal collections.

## Requirements

- Include accepted row count.
- Include skipped row count.
- Include a list of row-level errors.
- Include a boolean `successful()` method.
- Make the error list immutable to callers.

## Expected Behavior

Callers can inspect import status but cannot modify the result object after creation.

## Test Cases

- A result with no errors is successful.
- A result with errors is not fully successful.
- Modifying the original error list does not change the result.
- The returned error list cannot be modified.

## Hints

Use `List.copyOf(errors)` in the constructor.

## Common Mistakes

- Returning mutable lists directly.
- Treating partial success as total success.
- Storing only a single error message.

## Bonus Challenge

Add `hasWarnings()` separately from hard validation errors.

## Solution Outline

Create a record or final class with validated constructor arguments and defensive copying.

## Exercise 5: Add structured logging to an import

Difficulty: Medium

## Goal

Log import progress and failures in a consistent format.

## Background

Logging should help developers understand what happened without exposing sensitive data or hiding errors.

## Requirements

- Log when the import starts.
- Log when the import finishes.
- Log each skipped row at warning level.
- Log unexpected failures at error level.
- Avoid logging full customer payment details.

## Expected Behavior

The logs explain the import lifecycle and row failures while keeping data exposure low.

## Test Cases

- Start message is written once.
- Finish message includes accepted and skipped counts.
- Invalid rows produce warning messages.
- Unexpected exceptions are logged and rethrown or converted consistently.

## Hints

Use placeholders in logger calls instead of string concatenation.

## Common Mistakes

- Logging sensitive values.
- Catching exceptions and doing nothing.
- Logging every valid row in large imports.

## Bonus Challenge

Add an import run ID and include it in every log message for one import execution.

## Solution Outline

Wrap the import flow with start and finish logs, log row-level validation failures as warnings, and keep unexpected failure handling consistent.

## Exercise 6: Build an invoice summary report

Difficulty: Easy

## Goal

Create a report that summarizes invoice totals.

## Background

Reports should define how empty input, rounding, and invalid values are handled.

## Requirements

- Accept a list of invoices.
- Calculate count.
- Calculate minimum total.
- Calculate maximum total.
- Calculate average total.
- Use `BigDecimal` for money.

## Expected Behavior

The report returns correct values for normal data and documented values for empty data.

## Test Cases

- Three invoices produce correct count, min, max, and average.
- One invoice has the same min, max, and average.
- Empty input returns a clear empty report.
- Negative totals are rejected or documented if allowed.

## Hints

Avoid converting money to `double` for report calculations.

## Common Mistakes

- Dividing `BigDecimal` without a rounding mode.
- Returning `null` for empty report values without documenting it.
- Mutating the input list.

## Bonus Challenge

Add total sum and average rounded to two decimal places.

## Solution Outline

Validate input, loop through invoice totals, calculate report fields, and return an immutable report object.

## Exercise 7: Create report buckets

Difficulty: Medium

## Goal

Group order totals into named buckets.

## Background

Buckets are useful only when their boundary rules are explicit and tested.

## Bucket Rules

- `SMALL`: less than 50
- `MEDIUM`: 50 up to 199.99
- `LARGE`: 200 or more

## Requirements

- Count how many orders fall into each bucket.
- Define inclusive and exclusive boundaries clearly.
- Return zero for buckets with no orders.
- Add tests for boundary values.

## Expected Behavior

Each order total belongs to exactly one bucket.

## Test Cases

- 49.99 is `SMALL`.
- 50.00 is `MEDIUM`.
- 199.99 is `MEDIUM`.
- 200.00 is `LARGE`.
- Empty input returns all buckets with zero counts.

## Hints

An enum can make bucket names safer than raw strings.

## Common Mistakes

- Off-by-one boundary errors.
- Missing empty buckets from the result.
- Using floating point numbers for money.

## Bonus Challenge

Return bucket percentages in addition to counts.

## Solution Outline

Initialize all bucket counts to zero, classify each order total, update the matching bucket, and test every boundary.

## Exercise 8: Detect report outliers

Difficulty: Medium

## Goal

Flag values that are unusually high compared with the rest of the data.

## Background

Outlier logic should be simple, documented, and testable. For this exercise, use a rule-based threshold rather than complex statistics.

## Requirements

- Accept a list of payment totals.
- Calculate the average.
- Flag payments greater than three times the average.
- Handle empty lists safely.
- Return flagged payment IDs.

## Expected Behavior

Only payments that exceed the documented threshold are flagged.

## Test Cases

- No values returns no outliers.
- Similar values return no outliers.
- One very large value is flagged.
- A value exactly equal to the threshold follows the documented boundary rule.

## Hints

Write a method named `isOutlier(payment, average)` to keep the rule visible.

## Common Mistakes

- Changing the threshold without updating tests.
- Including invalid negative values in the average.
- Returning full payment details when only IDs are needed.

## Bonus Challenge

Add a second rule using median-based comparison and compare the results.

## Solution Outline

Validate input, calculate average with `BigDecimal`, apply the threshold rule, and return an immutable list of IDs.

## Exercise 9: Test import failure cases

Difficulty: Hard

## Goal

Write tests that prove the import code handles bad files safely.

## Background

Import code often fails in edge cases. Tests should cover malformed rows, missing files, invalid values, and partial success.

## Requirements

- Test missing file behavior.
- Test invalid number parsing.
- Test invalid date parsing.
- Test partial success with mixed valid and invalid rows.
- Test that row numbers are correct in error messages.

## Expected Behavior

The import result is predictable for expected data issues, and unexpected technical failures are handled consistently.

## Test Cases

- Missing file throws or returns a documented failure.
- Invalid quantity creates a row error.
- Invalid date creates a row error.
- Mixed input imports valid rows and reports invalid rows.
- Error messages identify the correct row number.

## Hints

Use temporary files in tests instead of relying on files from your desktop.

## Common Mistakes

- Testing only the happy path.
- Hardcoding machine-specific file paths.
- Ignoring line endings across operating systems.

## Bonus Challenge

Add tests for UTF-8 names such as `José` and `Müller`.

## Solution Outline

Create small test files at runtime, run the importer, assert result counts, and assert row-numbered errors.

## Exercise 10: Create a complete import-to-report workflow

Difficulty: Hard

## Goal

Combine CSV import, validation, logging, and report generation in one backend-style workflow.

## Background

Real application code usually connects several small components. The service method should coordinate them without doing every detail itself.

## Requirements

- Create a `CsvImportService`.
- Create an `InvoiceValidator`.
- Create an `InvoiceReportService`.
- Create an immutable `ImportResult`.
- Create an immutable `InvoiceReport`.
- Log start, skipped rows, and completion.
- Return both import status and report output.

## Expected Behavior

The workflow imports valid invoices, reports invalid rows, creates a report from accepted rows, and gives the caller a clear summary.

## Test Cases

- All valid rows produce a successful import and complete report.
- Mixed rows produce partial success and a report from valid rows only.
- Empty file produces an empty report and clear import status.
- Invalid money values are reported with row numbers.

## Hints

Keep parsing, validation, reporting, and orchestration in separate classes.

## Common Mistakes

- Putting all logic in one method.
- Generating a report from invalid rows.
- Logging sensitive payment details.
- Returning mutable lists.

## Bonus Challenge

Add a command-line entry point that accepts an input file path and prints a short summary.

## Solution Outline

The workflow service reads the file, delegates row parsing and validation, collects accepted invoices, builds the report, logs lifecycle events, and returns one immutable result object.
