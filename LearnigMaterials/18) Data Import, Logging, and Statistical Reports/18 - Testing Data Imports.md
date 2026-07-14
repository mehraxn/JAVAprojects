# Testing Data Imports

## Learning goals

- Test import workflows.
- Cover invalid files and bad rows.
- Use temporary files safely.

## Important test cases

Test:

- valid file;
- empty file;
- header only;
- invalid rows;
- duplicate rows;
- bad date;
- wrong number of columns;
- missing file;
- skipped rows;
- import summary counts.

## Example test idea

```java
@Test
void importSkipsInvalidPrice() throws IOException {
    Path file = Files.createTempFile("products", ".csv");
    Files.writeString(file, "id,name,price\nP1,Notebook,-5\n");

    ImportResult result = importer.importProducts(file);

    assertEquals(1, result.rowsRead());
    assertEquals(0, result.rowsImported());
    assertEquals(1, result.rowsSkipped());
}
```

## Temporary files

Use test-created temporary files instead of absolute local paths.

## Common mistakes

- Testing only a perfect file.
- Depending on files from a desktop path.
- Not deleting temporary files when needed.
- Ignoring row-number errors.

## Mini exercises

1. Test a header-only file.
2. Test a bad date.
3. Test a duplicate product ID.
4. Test missing file behavior.

## Quick summary

Import tests should cover real failure modes, not only the happy path.
