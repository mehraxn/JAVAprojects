# Surefire vs Failsafe

## Learning goals

- Understand the difference between unit and integration test plugins.
- Learn test naming conventions.
- Know why separating tests matters.

## Surefire

The Maven Surefire plugin runs unit tests during the `test` phase.

Common names:

- `OrderServiceTest`
- `ProductTest`
- `AccountCalculatorTest`

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
</plugin>
```

## Failsafe

The Maven Failsafe plugin usually runs integration tests during `integration-test` and `verify`.

Common names:

- `OrderRepositoryIT`
- `ProductApiIT`
- `InvoiceImportIT`

## Why separate them?

Unit tests should be fast and isolated. Integration tests may use a database, files, or a larger runtime setup.

## Common mistakes

- Naming integration tests `*Test` and accidentally running slow tests every time.
- Putting unit tests behind Failsafe.
- Skipping integration tests in CI without noticing.
- Depending on test order.

## Mini exercises

1. Rename a slow repository test from `ProductRepositoryTest` to `ProductRepositoryIT`.
2. Explain which Maven plugin should run it.
3. Decide whether a pure domain object test is unit or integration.

## Quick summary

Surefire is for unit tests. Failsafe is for integration tests.
