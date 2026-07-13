# Data Import Overview

## Learning goals

- Understand why applications import data.
- Learn the main parts of an import workflow.
- Design an import result instead of only printing messages.

## Why import data?

Applications often need to load existing information from files:

- product inventory CSV;
- order history CSV;
- student scores CSV;
- employee records CSV;
- invoice data CSV.

## Import workflow

```text
open file
  ↓
read header
  ↓
read rows
  ↓
validate each row
  ↓
convert fields
  ↓
save accepted rows
  ↓
return import result
```

## What can happen?

- Empty file.
- Missing header.
- Invalid row length.
- Blank required field.
- Invalid number.
- Invalid date.
- Duplicate ID.
- Row skipped with a warning.

## Import result object

```java
public record ImportResult(
        int rowsRead,
        int rowsImported,
        int rowsSkipped,
        List<String> errors,
        List<String> warnings) {
}
```

Returning a result makes the import testable.

## Common mistakes

- Printing errors but returning no structured result.
- Stopping the whole import for one bad row when skipping is acceptable.
- Ignoring character encoding.
- Letting parsing code and business rules become one large method.

## Mini exercise

Design an import result for a product inventory CSV with counts, errors, and warnings.

## Quick summary

A good import workflow reads, validates, converts, saves, and reports what happened.
