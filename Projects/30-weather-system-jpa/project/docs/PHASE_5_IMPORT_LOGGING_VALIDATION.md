# Phase 5 CSV Import, Logging, Validation, and Alerting

Date: 2026-07-14
Scope: `Projects/06 (WeatherSystem)/project` only.

## Goal

Make the data-import and validation layer robust and professional: safe file reading (UTF-8,
try-with-resources, preserved causes, tolerant path resolution), a small dependency-free CSV parser,
centralized date parsing, row-numbered import errors via `ImportResult`, consistent logging, and
tested threshold-alerting behavior — without breaking the professor/base tests or changing report
formulas / repository performance work.

## Baseline

Committed project **before** Phase 5, `mvn clean test` from a path without spaces:

```
Tests run: 151, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS
```

In the repo's actual `06 (WeatherSystem)` path, 11 CSV base tests errored with `...%20...`
(pre-existing since Phase 2). **Phase 5 fixes this** (see below): the full suite now passes in the
spaced path too.

## Import Classes Reviewed

| Class | Method | Finding (before) | Change (Phase 5) |
|---|---|---|---|
| `DataImportingService` | `storeMeasurements(String)` | `new FileReader` (platform charset); no try-with-resources (reader closed only on success); `String.split(",")`; swallowed root cause (`throw new RuntimeException(msg)` with no cause); malformed row aborted the whole import; no row numbers / summary; failed on `%20` paths | UTF-8 `Files.newBufferedReader` + try-with-resources; tolerant file resolution (decodes `%20`); `CsvUtils.parseLine`; centralized date parsing; per-row validation with row-numbered `ImportResult`; **partial import** (skip + record bad rows); cause preserved; logging added. Public `void` signature preserved (delegates to new `storeMeasurementsWithResult`). |
| `DataImportingService` | `checkMeasurement(Measurement)` | per-row full sensor scan via `new CRUDRepository<>(Sensor.class).read()` | **Intentionally unchanged** — the professor tests mock `CRUDRepository` construction and the README (section R1) requires this structure. Documented as required-by-contract, not a defect to "fix". |
| `AlertingService` | `notifyThresholdViolation`, `notifyDeletion` | static, log-only | Left as-is: static API is required by the base tests' `mockStatic(AlertingService.class)`. Testability is achieved through that same static-mock mechanism (see tests). |

New helper classes: `util.CsvUtils`, `util.DateParsingUtils`, `util.ValidationUtils`,
`services.ImportResult`, `services.ImportError`.

## CSV Parsing Behavior

`CsvUtils.parseLine` is a small, dependency-free parser (no external CSV library). It supports:
- simple comma-separated values;
- double-quoted fields (surrounding quotes removed);
- commas inside quoted fields;
- escaped double quotes inside quotes (`""` → `"`);
- empty and trailing-empty fields (preserved).

Limitations (documented, not fixed): it does not join quoted fields spanning multiple physical lines
(not needed by this project's fixtures). Field values are returned verbatim; the import trims each
field (the sample CSVs use `", "` separators, i.e. space-padded values).

## Import Strategy

**Partial import.** Header is line 1 and is skipped (with a soft validation warning if it does not
match the expected columns). For each subsequent non-blank line:
- valid rows are persisted and counted in `rowsImported`;
- malformed rows are skipped, counted in `rowsSkipped`, and recorded as `ImportError`
  (`rowNumber`, `rawLine`, `fieldName`, `message`);
- blank lines are ignored (not counted).

`ImportResult` exposes `rowsRead`, `rowsImported`, `rowsSkipped`, `getErrors()`, `getWarnings()`
(both lists unmodifiable). Only an unreadable/missing/blank path aborts the whole import (unchecked
`IllegalArgumentException`); an I/O error rethrows as `RuntimeException` **with the cause preserved**.
This is a deliberate improvement over the previous "one bad row aborts everything" behavior; the
sample fixtures are clean, so base-test counts (e.g. 166 measurements) are unchanged.

Public API: `WeatherReport.importDataFromFile(String)` and
`DataImportingService.storeMeasurements(String)` remain `void`. A new
`DataImportingService.storeMeasurementsWithResult(String)` returns the `ImportResult`; the `void`
method delegates to it.

## Date Parsing

Centralized in `util.DateParsingUtils` using `DateTimeFormatter.ofPattern(WeatherReport.DATE_FORMAT)`
(`yyyy-MM-dd HH:mm:ss`):
- `parseDateTime(String)` — required value; throws `IllegalArgumentException` (with the offending
  value) on null/blank/malformed input;
- `parseNullable(String)` — returns `null` for a `null` input, else parses;
- `validateRange(start, end)` — rejects `start` after `end`.

Replaced the duplicated ad-hoc parsing in `DataImportingService` (split), `GatewayReportImplementation`
(`split`), and `NetworkReportImpl` (four `substring` blocks). Behavior for valid inputs is identical;
invalid inputs now fail with a clear message instead of `ArrayIndexOutOfBounds`/`NumberFormatException`.

## Validation Improvements

`util.ValidationUtils` provides unchecked `requireNotBlank` / `requireNonNull` used by the import path.
The operations layer keeps its own **checked** `InvalidInputDataException` validation unchanged —
centralizing it would require threading checked exceptions through every operations class (broad
refactor, and the exceptions are pinned by base tests), so it was deliberately left for a later phase.

## Logging Improvements

`DataImportingService` now uses a log4j2 `Logger`:
- `info` — import summary (`ImportResult`) per file;
- `warn` — each skipped row and an unexpected header.

No `printStackTrace`/`System.out`/`System.err` in service/import code. `log4j2.xml` is unchanged and
valid. No sensitive data is logged.

## Threshold Alerting

Threshold comparison (in `checkMeasurement`, unchanged) per `ThresholdType`:
- `LESS_THAN` / `GREATER_THAN`: strict; `LESS_OR_EQUAL` / `GREATER_OR_EQUAL`: inclusive;
- `EQUAL` / `NOT_EQUAL`: within/outside `EPSILON` (1e-9).

An alert (`AlertingService.notifyThresholdViolation`) fires only when a violation is detected AND the
measurement's network exists AND has at least one operator. Boundary behavior is now covered by tests
(e.g. value equal to a `GREATER_THAN` threshold does not trigger; a value above does).

## Tests Added

Under `src/test/java/com/weather/report/test/custom/` (72 custom tests total; +36 this phase):
- **`CustomCsvParsingTest`** (8): quoted values, embedded commas, escaped quotes, empty/trailing
  fields, column counts, blank-line detection.
- **`CustomDateParsingValidationTest`** (8): valid/invalid/null/blank dates, boundary dates,
  start-after-end, `ValidationUtils` helpers.
- **`CustomDataImportingServiceTest`** (14): null/blank/missing path, empty & header-only files,
  wrong header warning, row-numbered errors (bad value/date, too few columns, blank field), successful
  and partial (mixed valid/invalid) imports with persistence checks, blank-line handling, unmodifiable
  error list.
- **`CustomThresholdAlertingTest`** (6): above/equal/below `GREATER_THAN`, below `LESS_THAN`,
  no-threshold, and mixed rows — verified via `mockStatic(AlertingService)`.

Test resource CSVs were not added: the import tests use JUnit `@TempDir` files (no committed fixtures),
which also avoids re-introducing the `%20` concern in a resource-loaded test.

## The `%20` fix (resolved)

The base tests load fixtures via `getResource("csv/...").getPath()`, which percent-encodes spaces.
`resolveReadableFile` now falls back to a `URLDecoder.decode(..., UTF-8)` variant when the raw path is
not a file, so `...06%20(WeatherSystem)...` resolves to `...06 (WeatherSystem)...`. As a result the
full suite (**187 tests**) now passes in the actual repo path — not only in a spaceless copy.

## Test Result

- Spaceless path: **187 run, 0 failures, 0 errors — BUILD SUCCESS** (115 base + 72 custom).
- Repo path `06 (WeatherSystem)` (`.\mvnw.cmd clean test` and `scripts/test.sh`):
  **187 run, 0 failures, 0 errors — BUILD SUCCESS** (the 11 environmental errors are gone).

## Known Limitations (for Phase 6)

- `checkMeasurement` keeps the per-row full sensor scan (required by the mock-based professor tests) —
  not a performance target that can be changed without breaking the contract.
- Operations-layer validation/authorization is still duplicated across `*OperationsImpl` (centralizing
  needs checked-exception plumbing) — Phase 6 cleanup candidate.
- `CsvUtils` is intentionally minimal (no multi-line quoted fields).
- General cleanup remains: `CRUDRepository.clearAll()` no-op, `Gateway.now` dead field, `User` ordinal
  enum storage, EAGER collections, Mockito self-attach warning, optional GitHub Actions/JaCoCo.
