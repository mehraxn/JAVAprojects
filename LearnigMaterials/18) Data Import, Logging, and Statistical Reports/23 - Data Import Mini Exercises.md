# Data Import Mini Exercises

## Learning goals

- Practice CSV import and report workflows.
- Combine validation, logging, result objects, and statistics.

## Exercise 1: Simple CSV import

Read product rows:

```text
id,name,quantity,price
```

Validate required fields and numeric values.

## Exercise 2: Header validation

Reject files whose header does not exactly match the expected columns.

## Exercise 3: Row-number errors

Create `ImportError` objects with row number, field, raw line, and message.

## Exercise 4: Date parsing

Read invoice rows with `yyyy-MM-dd` dates and reject invalid dates.

## Exercise 5: ImportResult

Return rows read, imported, skipped, warnings, and errors.

## Exercise 6: Logging

Log import start, skipped row count, successful completion, and unexpected failure.

## Exercise 7: Report calculations

Calculate count, min, max, average, median, and standard deviation.

## Exercise 8: Histogram

Build buckets for student scores or order totals.

## Exercise 9: Outlier detection

Identify values more than two standard deviations from the average. Handle zero standard deviation.

## Exercise 10: Financial report

Use `BigDecimal` to calculate invoice totals and average invoice value.

## Quick summary

Data import mini exercises combine file handling, validation, structured errors, logging, and report math.
