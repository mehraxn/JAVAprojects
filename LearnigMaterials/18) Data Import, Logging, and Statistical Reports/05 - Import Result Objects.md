# Import Result Objects

## Learning goals

- Return structured import results.
- Make imports testable.
- Preserve errors and warnings without relying on console output.

## Example result

```java
public final class ImportResult {
    private final int rowsRead;
    private final int rowsImported;
    private final int rowsSkipped;
    private final List<String> errors;
    private final List<String> warnings;

    public ImportResult(int rowsRead, int rowsImported, int rowsSkipped,
                        List<String> errors, List<String> warnings) {
        this.rowsRead = rowsRead;
        this.rowsImported = rowsImported;
        this.rowsSkipped = rowsSkipped;
        this.errors = List.copyOf(errors);
        this.warnings = List.copyOf(warnings);
    }
}
```

## Why it matters

Tests can assert:

- rows read;
- rows imported;
- rows skipped;
- exact error messages;
- warning count.

## Warning vs error

An error usually means a row was rejected.

A warning usually means the row was accepted, but something should be reviewed.

## Common mistakes

- Returning only `true` or `false`.
- Printing errors but not returning them.
- Exposing mutable error lists.
- Mixing import results with domain objects.

## Mini exercise

Create an `ImportResult` record or class and write two example assertions against it.

## Quick summary

Import result objects turn file processing into testable business behavior.
