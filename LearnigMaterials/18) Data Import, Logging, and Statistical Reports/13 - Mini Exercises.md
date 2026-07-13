# Mini Exercises

## 1. Import product inventory

Read a CSV with:

```text
id,name,quantity,price
```

Requirements:

- skip blank lines;
- reject blank IDs;
- reject negative quantity;
- reject negative price;
- return an `ImportResult`.

## 2. Import student scores

Read scores from CSV and skip rows where score is outside `0-100`.

## 3. Parse invoice dates

Parse invoice dates with `DateTimeFormatter` and reject invalid date ranges.

## 4. Add logging

Log import start, skipped rows, successful import summary, and unexpected failures.

## 5. Build a report

Create a report with:

- count;
- min;
- max;
- average;
- standard deviation.

## 6. Build a histogram

Group scores into letter-grade buckets.

## 7. Handle empty data

Define what every report field should return when there is no data.

## Quick summary

Data import and reporting exercises combine file reading, validation, dates, logging, immutable results, and statistics.
