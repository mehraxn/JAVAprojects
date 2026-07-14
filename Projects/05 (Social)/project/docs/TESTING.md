# Testing Guide

## Running tests

```bash
./mvnw clean test          # or: mvn clean test
bash scripts/test.sh
```

```powershell
.\mvnw.cmd clean test      # or: mvn clean test
.\scripts\test.ps1
```

The Maven wrapper (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/`) downloads the pinned Maven version on first
use, so no local Maven install is required.

## Coverage

```bash
./mvnw clean test jacoco:report      # or: mvn clean test jacoco:report
```

Report: `target/site/jacoco/index.html` (generated; not committed). No coverage threshold is enforced.

## Test structure

```
test/
├── example/TestExample.java     # professor test (R1..R5) — do not modify
└── custom/
    ├── CustomTestBase.java                    # resets the in-memory schema per test
    ├── CustomPersonAndFriendshipTest.java
    ├── CustomGroupTest.java
    ├── CustomPostPaginationTest.java
    └── CustomStatisticsAndEndToEndTest.java
```

## H2 test database

Tests use the `socialPUTest` persistence unit: in-memory H2 with `hibernate.hbm2ddl.auto=create-drop`.
`CustomTestBase` calls `JPAUtil.setTestMode()` before each test, which closes the previous
`EntityManagerFactory` (dropping the schema) so the next operation rebuilds a fresh, empty schema.
This makes tests isolated and order-independent. The factory is closed once per class in `@AfterAll`.

## Running one test class

```bash
./mvnw -Dtest=CustomGroupTest test
# or:
mvn -Dtest=CustomGroupTest test
```

## Notes / common failures

- `target/` and Surefire reports (`target/surefire-reports/`) are generated and must not be committed.
- Post-ordering tests use tiny `Thread.sleep` calls so timestamps are strictly increasing; ordering
  is additionally deterministic via the `timestamp DESC, id DESC` tie-breaker in JPQL.
- Hibernate prints a few informational log lines about its built-in connection pool; these are
  framework logs, not test failures.
