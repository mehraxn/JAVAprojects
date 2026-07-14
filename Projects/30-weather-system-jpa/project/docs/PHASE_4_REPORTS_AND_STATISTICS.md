# Phase 4 Report and Statistics Performance + Correctness

Date: 2026-07-14
Scope: `Projects/06 (WeatherSystem)/project` only.

## Goal

Make the report/statistics layer more correct, predictable, and safe without breaking the
professor/base tests or changing the public API. All behavior decisions were checked against the
README spec (section 3.3 / R1 / R2) and the base tests, which are the ground truth.

## Baseline

Committed project **before** Phase 4 changes, `mvn clean test` from a path without spaces:

```
Tests run: 130, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS
```

(In the repo's actual `06 (WeatherSystem)` path, 11 CSV tests error with `...%20...` — pre-existing,
environmental, documented since Phase 2.)

## Report Classes Reviewed

| Report class | Main metrics | Data source | Findings (Phase 4) |
|---|---|---|---|
| `SensorReportImpl` | mean, sample variance, std dev, min/max, outliers, value histogram | measurement list from `MeasurementRepository.findBySensorCodeAndDateRange` (Phase 3) | n=1 mean was wrongly 0; all-equal values flagged every value as an outlier (std dev 0); `getOutliers`/`getHistogram` returned mutable internals |
| `GatewayReportImplementation` | most/least active sensors, sensor load ratio, outlier sensors, battery charge, inter-arrival Duration histogram | `MeasurementRepository.findByGatewayCodeAndDateRange` (Phase 3) + gateway params | load ratio returned a fraction (inconsistent with README "percentage" and with NetworkReport); `getSensorsLoadRatio`/`getHistogram` returned mutable maps; `getGatewayParameterValue` could NPE if gateway absent |
| `NetworkReportImpl` | most/least active gateways, gateway load ratio, LocalDateTime histogram | `MeasurementRepository.findByNetworkCodeAndDateRange` (Phase 3) | per-gateway counts recomputed on every getter; `getGatewaysLoadRatio`/`getMostActive*`/`getHistogram` returned mutable collections |

All three already read only their own measurement subset via the Phase 3 range queries — **no
read-all-then-filter remained in report paths** entering Phase 4 (that was fixed in Phase 3).

## Statistics Behavior (authoritative, matches README section 3.3)

| Case | mean | variance | std dev | outliers |
|---|---|---|---|---|
| 0 measurements | 0.0 | 0.0 | 0.0 | empty |
| 1 measurement | the value | 0.0 | 0.0 | empty |
| all equal values (n≥2) | the value | 0.0 | 0.0 | empty |
| n ≥ 2 general | Σx / n | **sample**: Σ(x−mean)² / (n−1) | √variance | see outlier rule |

- **Variance formula:** SAMPLE variance (`/(n-1)`), exactly as the README specifies ("sample
  variance"). Not changed.
- **Fix — one measurement:** `mean` now returns the measured value (previously incorrectly `0.0`).
  Variance/std dev remain `0.0` for n < 2 (README: "not meaningful … must be set to 0").
- Min/max are taken over the non-outlier values (unchanged); for n ≤ 1 they equal the single value,
  and 0.0 for empty data.

## Outlier Behavior

- **Rule (unchanged, matches README):** a measurement is an outlier iff
  `|x − mean| >= 2 * stdDev` (README uses "at least", i.e. `>=`).
- **Fix — zero standard deviation:** when `stdDev == 0` (all values identical) the outlier set is now
  **empty**. Previously `|x − mean| >= 2*0` was `0 >= 0`, which flagged *every* value as an outlier and
  emptied min/max/histogram. Task 8 explicitly calls for this guard; it is a defensible correctness
  fix beyond the literal formula and is consistent with the spec's principle that degenerate cases
  (e.g. n < 2) produce no outliers. No base test exercises all-equal data, so nothing breaks.
- **Gateway `getOutlierSensors` left unchanged:** its rule
  `|sensorMean − EXPECTED_MEAN| >= 2 * EXPECTED_STD_DEV` uses a *configured* gateway parameter, not a
  computed std dev, and matches the README literally. Changing it would alter a business formula, so
  it was preserved (only the NPE guard below was added). Documented as a known edge for review.

## Histogram Behavior

Convention (README "Histogram range semantics"): buckets are left-closed/right-open `[start, end)`
except the last, which is closed `[start, end]` so the maximum is always included. All histograms are
`SortedMap`s ordered by ascending bucket start. Reviewed and confirmed already-correct; no formula
changes:

- **Sensor (Double buckets):** empty data → empty map; all-equal / single value → one bucket
  (`min == max`); otherwise 20 buckets with the last one closed so the max value is included.
- **Gateway (Duration buckets):** < 2 measurements → empty map; all inter-arrival deltas equal → one
  bucket; otherwise 20 buckets. With second-granularity timestamps and `min != max`, the step
  (`(max−min)/20`) is always > 0, so no zero-step division occurs in practice (noted as a theoretical
  edge only).
- **Network (LocalDateTime buckets):** empty data → empty map; otherwise hour/day boundary buckets
  with the last bucket closed. (This report intentionally uses calendar-boundary buckets rather than a
  fixed count; left as-is to preserve behavior — see Known Limitations.)

The only Phase 4 change to histograms is that all `getHistogram()` methods now return **unmodifiable**
sorted maps.

## Load Ratio Behavior

- **Chosen unit: PERCENTAGE (0–100)** in both reports.
- The README describes both load ratios as a "percentage" (R1: gateways vs network total; R2: sensors
  vs gateway total). `NetworkReportImpl.getGatewaysLoadRatio` already multiplied by 100, but
  `GatewayReportImplementation.getSensorsLoadRatio` returned a bare fraction — inconsistent.
- **Fix:** `getSensorsLoadRatio` now multiplies by 100, so both reports are consistent and match the
  README wording. The base tests only assert emptiness/non-emptiness of these maps (no pinned values),
  so this change does not affect them. Documented here and in the README.

## Performance Improvements

- Reports use the Phase 3 repository date-range queries (no full-table read-and-filter in report
  paths). Avoids loading measurements unrelated to the requested sensor/gateway/network + interval.
- `NetworkReportImpl` now computes per-gateway measurement counts **once** at construction instead of
  recomputing them in each of `getMostActiveGateways`, `getLeastActiveGateways`, and
  `getGatewaysLoadRatio`. (`GatewayReportImplementation` already cached `countBySensor` this way.)
- No timing/benchmark claims are made.

## Return Value Safety

All report collection getters now return read-only views (declared types and ordering preserved):
- `SensorReportImpl`: `getOutliers()` → `unmodifiableList`; `getHistogram()` → `unmodifiableSortedMap`.
- `GatewayReportImplementation`: `getSensorsLoadRatio()` → `unmodifiableMap`; `getHistogram()` →
  `unmodifiableSortedMap` (all three return points); most/least-active already returned immutable
  `List.of`/`toList()` results.
- `NetworkReportImpl`: `getMostActiveGateways()`/`getLeastActiveGateways()` → `unmodifiableList`;
  `getGatewaysLoadRatio()` → `unmodifiableMap`; `getHistogram()` → `unmodifiableSortedMap`.

## StatisticsUtils

Not added. The mean/variance/std-dev/outlier logic lives only in `SensorReportImpl`; the other reports
compute different aggregates (counts, per-sensor averages). Extracting a shared utility for a single
consumer would be over-engineering (Task 13 explicitly allows keeping the logic in place when a utility
would require restructuring). No duplication was introduced.

## Tests Added

Under `src/test/java/com/weather/report/test/custom/`:

- **`CustomSensorReportStatisticsTest`** (13, no DB — constructs `SensorReportImpl` directly):
  empty/null, one measurement (mean = value), multi-value mean, sample variance & std dev, two
  distinct values, all-equal (zero variance, no outliers), a clear outlier detected, zero-std-dev →
  no outliers, histogram covers all non-outliers incl. the max, ascending bucket order, and
  unmodifiable outliers/histogram.
- **`CustomReportLoadRatioAndFilteringTest`** (8, extends `BasePersistenceTest`): gateway & network
  load ratios are percentages summing to 100, unit consistency across report types, inclusive
  date-range subsets for both reports, unmodifiable gateway/network collections, and a 100-row smoke
  check.

Existing Phase 3 custom tests (`CustomMeasurementRepositoryQueryTest` 12,
`CustomRepositoryFilteringSmokeTest` 3) remain green. Custom total: 36.

## Test Result

- Spaceless path (committed pom, after changes): **151 run, 0 failures, 0 errors — BUILD SUCCESS**
  (115 base + 36 custom).
- Repo path `06 (WeatherSystem)`: 151 run, 0 failures, **11 errors** — the pre-existing CSV `%20`
  environmental errors only; all report/statistics changes verified green in the spaceless run.

## Known Limitations (for Phase 5 / 6)

- **Phase 5 (CSV/import/logging/validation):** the `%20` path issue (root cause of the 11 environmental
  errors), UTF-8 + try-with-resources, row-numbered errors / `ImportResult`, `checkMeasurement`
  per-row sensor full-scan, centralized date parsing, alert testability.
- **Reports still to consider later:** the Network histogram uses calendar (hour/day) buckets rather
  than the fixed-20-bucket convention used elsewhere — confirm against the intended spec; the Gateway
  `getOutlierSensors` behavior when `EXPECTED_STD_DEV` is 0 or the parameter is missing (currently
  flags all deviating sensors, per the literal formula).
- **General cleanup (Phase 6):** `CRUDRepository.clearAll()` dead no-op; `Gateway.now` dead field;
  `User` ordinal enum storage; EAGER collections; duplicated authorization/validation.
