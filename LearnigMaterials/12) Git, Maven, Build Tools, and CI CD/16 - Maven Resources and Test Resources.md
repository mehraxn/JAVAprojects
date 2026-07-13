# Maven Resources and Test Resources

## Learning goals

- Understand `src/main/resources` and `src/test/resources`.
- Load classpath resources.
- Keep test data separate from application resources.

## Resource folders

```text
src/main/resources
src/test/resources
```

Main resources are packaged with the application. Test resources are available during tests.

## Examples

Main resources:

- application configuration;
- message templates;
- default data files.

Test resources:

- sample CSV files;
- test configuration;
- expected report files.

## Loading a resource

```java
try (InputStream input = getClass().getResourceAsStream("/sample-products.csv")) {
    if (input == null) {
        throw new IllegalStateException("Resource not found");
    }
}
```

## Common mistakes

- Reading test files with hard-coded absolute paths.
- Putting test data in main resources.
- Forgetting the leading slash for classpath lookup.
- Assuming the working directory is always the same.

## Mini exercises

1. Put a CSV file in `src/test/resources`.
2. Load it from a JUnit test.
3. Explain why this is better than a local desktop path.

## Quick summary

Maven resources make configuration and test files available in a predictable way.
