# Testing Guide

## 1. Test commands

Linux / macOS / Git Bash:

```bash
./mvnw clean test          # or: mvn clean test
bash scripts/test.sh       # convenience wrapper
```

Windows PowerShell:

```powershell
.\mvnw.cmd clean test      # or: mvn clean test
.\scripts\test.ps1
```

## 2. Maven wrapper usage

The repo ships a Maven wrapper (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`), so no
local Maven install is required — the wrapper downloads the pinned Maven version on first use. Prefer
`./mvnw` over a system `mvn`.

## 3. Test structure

```
src/test/java/com/weather/report/test/
├── BasePersistenceTest.java     # shared setup: setTestMode(), users, helpers
├── base/                        # professor tests (Test_R0..R4b) — do not modify
└── custom/                      # project-added tests
```

## 4. Base / professor tests

`Test_R0..R4b` (115 tests) validate requirements R0–R4 through the `WeatherReport` facade. They must
keep passing and must not be modified.

## 5. Custom tests (`test/custom`)

- `CustomMeasurementRepositoryQueryTest`, `CustomRepositoryFilteringSmokeTest` — repository JPQL
  queries, boundaries, counts, ordering, safe parameter binding.
- `CustomSensorReportStatisticsTest`, `CustomReportLoadRatioAndFilteringTest` — statistics/outlier/
  histogram edge cases, load-ratio unit, date subsets, immutability.
- `CustomCsvParsingTest`, `CustomDataImportingServiceTest`, `CustomDateParsingValidationTest` — CSV
  parsing, file/row validation, row-numbered errors, partial import, date parsing.
- `CustomThresholdAlertingTest` — threshold boundary behavior via `mockStatic(AlertingService)`.
- `CustomAuthorizationBehaviorTest`, `CustomTopologyAndDeletionTest`,
  `CustomPersistenceConfigurationTest`, `CustomEndToEndWorkflowTest` — authorization, topology/deletion,
  both persistence units, and a full create→connect→import→report smoke flow.

## 6. H2 test database

Tests use the `weatherReportTestPU` persistence unit (in-memory H2, schema created per factory).
`BasePersistenceTest` calls `PersistenceManager.setTestMode()` in `@BeforeEach` and
`PersistenceManager.close()` in `@AfterEach`, giving each test class a clean database. Tests must not
depend on execution order.

## 7. Running one test class

```bash
./mvnw -Dtest=CustomEndToEndWorkflowTest test
# or:
mvn -Dtest=CustomEndToEndWorkflowTest test
```

## 8. Running coverage

```bash
./mvnw clean test jacoco:report      # or: mvn clean test jacoco:report
```

Report: `target/site/jacoco/index.html`. No coverage threshold is enforced (the build never fails on
low coverage).

## 9. Common test failures

- **`Error reading CSV file: ...%20...`** — historically occurred when the checkout path contained
  spaces; resolved in Phase 5 (the import decodes percent-encoded paths).
- **`release version 25 not supported`** — occurred before Phase 2; the project now targets Java 21.
- **Mockito self-attach warning** — informational only on current JDKs.

## 10. Final validation checklist

- `./mvnw clean test` is green.
- `target/` and other generated files are not committed.
- Custom tests live only under `test/custom` and don't modify base tests.
- `TEST_RESULTS.md` reflects the actual last run.
