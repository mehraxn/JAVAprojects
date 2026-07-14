# Import Error Reporting with Row Numbers

## Learning goals

- Report row-level import errors clearly.
- Use row number, raw line, field name, and message.
- Distinguish warning from error.

## ImportError object

```java
public record ImportError(
        int rowNumber,
        String rawLine,
        String fieldName,
        String message,
        Severity severity) {
}

public enum Severity {
    WARNING,
    ERROR
}
```

## Why row numbers matter

If the user sees only "invalid price", they do not know where to fix the file. Row numbers make errors actionable.

## Example validation

```java
if (price.signum() < 0) {
    errors.add(new ImportError(
            rowNumber,
            line,
            "price",
            "Price must not be negative",
            Severity.ERROR));
}
```

## Warning vs error

- Error: row rejected.
- Warning: row accepted, but something should be reviewed.

Example warning: optional description is blank.

## Common mistakes

- Reporting errors without row numbers.
- Hiding the raw line when debugging malformed data.
- Treating warnings as failures without documenting it.
- Returning only a count of skipped rows.

## Mini exercises

1. Create an `ImportError` for a blank product ID.
2. Create a warning for a missing optional note.
3. Add errors to an `ImportResult`.

## Quick summary

Good import errors tell the user exactly where the problem is and what field needs attention.
