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
├── example/                       # example tests (ExampleTest, ExampleQueriesTest)
├── it/polito/po/test/             # professor tests (TestR1..R4) + mountain_huts.csv resource
└── custom/                        # project-added tests
    ├── CustomAltitudeRangeTest.java
    ├── CustomMunicipalityAndHutTest.java
    ├── CustomRegionTest.java
    └── CustomCsvImportTest.java
```

## Test data

- `data/mountain_huts.csv` — the real dataset (94 municipalities, 167 huts). `CustomCsvImportTest`
  imports it (via the project-root-relative path) and asserts those counts.
- `test/it/polito/po/test/mountain_huts.csv` — the same dataset as a test resource; the professor
  tests copy it to a temp file and import it.
- Malformed-CSV cases use JUnit `@TempDir` temporary files (never committed).

## Notes / common failures

- `target/` (including `target/surefire-reports/`) is generated and must not be committed.
- Tests are order-independent and do not rely on timing.
- Altitude-range membership is left-open/right-closed (`min < a <= max`); tests assert both boundaries.
