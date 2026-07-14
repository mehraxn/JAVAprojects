# Batch Import and Transaction Strategy

## Learning goals

- Compare all-or-nothing and partial-success imports.
- Choose a transaction strategy.
- Collect errors without hiding failures.

## All-or-nothing import

All rows must be valid, or no rows are saved.

Use when:

- partial data would be misleading;
- rows depend on each other;
- consistency is more important than importing what you can.

## Partial-success import

Valid rows are imported, invalid rows are skipped and reported.

Use when:

- rows are independent;
- users can correct failed rows later;
- importing some data is valuable.

## Transaction strategies

### One transaction for entire file

Good for all-or-nothing.

```text
begin
read and validate
save all
commit
if failure: rollback
```

### One transaction per row or batch

Good for large files or partial success.

```text
for each batch:
  begin
  save valid rows
  commit
```

## Common mistakes

- Skipping invalid rows without reporting them.
- Importing partial data when the business expects all-or-nothing.
- Holding one huge transaction for an unnecessarily large file.
- Mixing validation errors and system failures as if they are the same.

## Mini exercises

1. Choose a strategy for product inventory import.
2. Choose a strategy for invoice lines where all lines must belong to one invoice.
3. Design an `ImportResult` for partial success.

## Quick summary

Import strategy is a business decision. Define whether the file is all-or-nothing or partial success before writing code.
