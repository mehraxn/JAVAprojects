# Validating Imported Rows

## Learning goals

- Validate required fields.
- Validate row length.
- Collect row-level errors.

## Row validation example

```java
private static ProductRow parseProductRow(String line, int rowNumber) {
    String[] columns = line.split(",");
    if (columns.length != 3) {
        throw new IllegalArgumentException("Row " + rowNumber + " must have 3 columns");
    }

    String id = columns[0].trim();
    String name = columns[1].trim();
    String priceText = columns[2].trim();

    if (id.isEmpty()) {
        throw new IllegalArgumentException("Row " + rowNumber + " has blank id");
    }
    if (name.isEmpty()) {
        throw new IllegalArgumentException("Row " + rowNumber + " has blank name");
    }

    BigDecimal price = new BigDecimal(priceText);
    if (price.signum() < 0) {
        throw new IllegalArgumentException("Row " + rowNumber + " has negative price");
    }

    return new ProductRow(id, name, price);
}
```

## Skipping invalid rows

Some imports should continue after a bad row:

```java
try {
    ProductRow row = parseProductRow(line, rowNumber);
    imported.add(row);
} catch (IllegalArgumentException ex) {
    errors.add(ex.getMessage());
    skipped++;
}
```

## Common validation checks

- Required fields.
- Correct number of columns.
- Number parsing.
- Date parsing.
- Duplicate IDs.
- Relationship checks after loading references.

## Common mistakes

- Not including row numbers in errors.
- Failing the whole import without a clear reason.
- Accepting partially parsed rows.

## Mini exercise

Validate a student score row with `id`, `name`, and `score`. Score must be between 0 and 100.

## Quick summary

Import validation should be strict enough to protect data and clear enough to explain skipped rows.
