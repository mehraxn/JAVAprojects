# Data Import Logging and Report Exercises

## 1. Import CSV rows

Read:

```text
id,name,quantity,price
```

Requirements:

- use UTF-8;
- skip empty lines;
- validate required fields;
- skip invalid rows;
- return `ImportResult`.

## 2. Parse dates

Read invoice rows with a date column in `yyyy-MM-dd` format. Reject invalid dates.

## 3. Add logging

Log:

- import started;
- row skipped;
- import completed;
- unexpected failure.

## 4. Build a report

Create a report with:

- count;
- min;
- max;
- average.

## 5. Build a histogram

Group values into buckets and document boundary rules.

## 6. Handle empty data

Define what the report returns for an empty list.
