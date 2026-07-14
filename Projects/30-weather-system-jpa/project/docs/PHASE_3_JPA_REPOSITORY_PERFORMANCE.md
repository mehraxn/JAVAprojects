# Phase 3 JPA and Repository Performance

Date: 2026-07-14
Scope: `Projects/06 (WeatherSystem)/project` only.

## Goal

Move measurement filtering out of Java and into the database. Before this phase, the Gateway and
Network reports loaded **every** row from `MEASUREMENTS` via `CRUDRepository.read()` and filtered by
code and date in Java. This phase adds focused JPQL/`TypedQuery` finders on `MeasurementRepository`,
routes the reports through them, adds supporting indexes, tightens repository exception messages, and
covers the new query methods with custom tests — **without** changing report formulas, histogram or
outlier logic (Phase 4), CSV import (Phase 5), or public APIs.

## Baseline

Committed project **before** Phase 3 changes, `mvn clean test` from a path without spaces:

```
Tests run: 115, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS
```

(In the repository's actual path `06 (WeatherSystem)`, 11 CSV tests error with `...%20...` — a
pre-existing, environmental issue documented in Phase 2, unrelated to repository code.)

## Read-All Filtering Findings

| Class | Method | Old behavior | New behavior |
|---|---|---|---|
| `GatewayReportImplementation` | constructor | `new CRUDRepository<>(Measurement.class).read()` then Java `stream().filter(gatewayCode).filter(>=start).filter(<=end)` | `new MeasurementRepository().findByGatewayCodeAndDateRange(code, start, end)` (JPQL, DB-side) |
| `NetworkReportImpl` | constructor | `new CRUDRepository<>(Measurement.class).read()` then manual `for` loop filtering by networkCode + date | `new MeasurementRepository().findByNetworkCodeAndDateRange(code, start, end)` (JPQL, DB-side) |
| `SensorReportImpl` (via `SensorOperationsImplement`) | `getSensorReport` | already used `MeasurementRepository.findBySensorAndDateRange(...)` | unchanged (kept working; method now delegates to `findBySensorCodeAndDateRange`) |

Not changed in Phase 3 (documented for later, out of scope here):
- `DataImportingService.checkMeasurement` loads all sensors per imported row — belongs to the CSV
  import path (Phase 5).
- `SensorOperationsImplement.getSensors(String...)`, `getGateways`, `getNetworks` do entity (not
  measurement) lookups; low impact, left as-is to preserve behavior.

## New Repository Methods

All added to `MeasurementRepository`. All use `TypedQuery` with **named parameters**; the JPQL field
name is always a fixed literal chosen inside the class (never caller input); results are ordered by
ascending `timestamp` and are never `null`. Date bounds are **inclusive**, a `null` bound means
unbounded, and `start` after `end` throws `IllegalArgumentException` (the repository layer uses Java
exceptions, consistent with the existing code).

| Method | Purpose | JPQL summary | Validation | Returns |
|---|---|---|---|---|
| `findBySensorCodeAndDateRange(code, start, end)` | sensor rows in range | `WHERE m.sensorCode = :code [AND m.timestamp >= :start] [AND <= :end] ORDER BY m.timestamp ASC` | code non-blank; range | `List<Measurement>` |
| `findByGatewayCodeAndDateRange(code, start, end)` | gateway rows in range | `WHERE m.gatewayCode = :code ...` | code non-blank; range | `List<Measurement>` |
| `findByNetworkCodeAndDateRange(code, start, end)` | network rows in range | `WHERE m.networkCode = :code ...` | code non-blank; range | `List<Measurement>` |
| `findBySensorCode(code)` | all sensor rows | delegates with `null, null` | code non-blank | `List<Measurement>` |
| `findByGatewayCode(code)` | all gateway rows | delegates with `null, null` | code non-blank | `List<Measurement>` |
| `findByNetworkCode(code)` | all network rows | delegates with `null, null` | code non-blank | `List<Measurement>` |
| `findByDateRange(start, end)` | all rows in range | `WHERE 1=1 [AND >= :start] [AND <= :end] ORDER BY m.timestamp ASC` | range | `List<Measurement>` |
| `countBySensorCode(code)` | count for sensor | `SELECT COUNT(m) ... WHERE m.sensorCode = :code` | code non-blank | `long` |
| `countByGatewayCode(code)` | count for gateway | `SELECT COUNT(m) ... WHERE m.gatewayCode = :code` | code non-blank | `long` |
| `countByNetworkCode(code)` | count for network | `SELECT COUNT(m) ... WHERE m.networkCode = :code` | code non-blank | `long` |
| `countByDateRange(start, end)` | count in range | `SELECT COUNT(m) ... [AND >= :start] [AND <= :end]` | range | `long` |
| `findBySensorAndDateRange(code, start, end)` | **existing API kept** | now delegates to `findBySensorCodeAndDateRange` | (as above) | `List<Measurement>` |

`Measurement` has no parameter-code field, so `findByParameterCodeAndDateRange` was intentionally not
added.

## EntityManager Lifecycle Review

Reviewed `PersistenceManager` and `CRUDRepository`:
- **`EntityManagerFactory`** is created once and cached statically in `PersistenceManager`
  (`getCurrentFactory()` only rebuilds after `close()`/`setTestMode()`). No wasteful per-call factory
  creation — **left unchanged**.
- **`EntityManager`** is created per operation and closed in a `finally` block in every
  `CRUDRepository` method and in every new `MeasurementRepository` method — verified. No leaks.
- **Transactions**: `create`/`update`/`delete` use `begin`/`commit` and roll back in `catch` when the
  transaction is still active — verified, unchanged. Read/query methods are non-transactional (correct
  for reads).
- **`clearAll()`** is an empty static no-op; it is not relied upon by tests (base tests reset state via
  `setTestMode()` + `close()`), so it was left as-is and remains flagged as dead code for a later
  cleanup phase.

No structural changes were made to `PersistenceManager` (no new framework, no DI).

## Indexes Added

Added to `Measurement` via `@Table(indexes = …)` (table `MEASUREMENTS`). Column names match the
entity's mapped columns (`sensorCode`, `gatewayCode`, `networkCode` use default names;
`measurement_timestamp` is the `@Column` name for `timestamp`):

| Index | Columns | Supports |
|---|---|---|
| `idx_meas_sensor_ts` | `sensorCode, measurement_timestamp` | `findBySensorCode*`, `countBySensorCode`, sensor report |
| `idx_meas_gateway_ts` | `gatewayCode, measurement_timestamp` | `findByGatewayCode*`, `countByGatewayCode`, gateway report |
| `idx_meas_network_ts` | `networkCode, measurement_timestamp` | `findByNetworkCode*`, `countByNetworkCode`, network report |
| `idx_meas_ts` | `measurement_timestamp` | `findByDateRange`, `countByDateRange` |

The composite indexes' leading column also serves code-only lookups, so separate single-column code
indexes were not needed. Schema generation with these indexes was verified by a full green test run
(130 tests). No columns were renamed and no uniqueness constraints were added (uniqueness could change
business behavior — deferred). The reserved-name concern (`WR_USER` table for `User`) is already safe;
no table renames were made.

## Exception Handling Changes

In `CRUDRepository`, the three write paths already preserved the root cause; the messages were made
specific to aid diagnosis (behavior/typing unchanged — still `RuntimeException` with cause, so the
service layer's mapping to `IdAlreadyInUseException` still works):
- create (duplicate): `"Entity already exists: <Entity>"`
- create (other): `"Failed to create entity <Entity>"`
- update: `"Failed to update entity <Entity>"`
- delete: `"Failed to delete entity <Entity>"`

New `MeasurementRepository` methods validate inputs up front with `IllegalArgumentException`
(null/blank code, `start` after `end`), consistent with the repository layer's Java-exception style.

## Tests Added

Under `src/test/java/com/weather/report/test/custom/` (both extend `BasePersistenceTest` for the test
persistence unit and clean per-test schema; measurements are created directly through the repository,
so these tests do **not** depend on CSV import):

- **`CustomMeasurementRepositoryQueryTest`** (12 tests): find-by sensor/gateway/network returns only
  matching rows; inclusive date range; start-only and end-only boundaries inclusive; no-match returns
  empty (not null); invalid range rejected; null/blank code rejected; count queries correct;
  ascending-timestamp ordering; and a safe-parameter-binding test (an injection-style code string is
  treated as a literal and matches nothing).
- **`CustomRepositoryFilteringSmokeTest`** (3 tests): seeds 200 rows (2 sensors × 100 hourly points)
  and verifies code+range queries return exactly the expected rows in order, and that
  `countByDateRange` matches `findByDateRange().size()`. No timing assertions (correctness, not a
  benchmark).

## Behavior Preserved

- Public APIs unchanged (facade, `*Operations` interfaces, report interfaces, existing
  `findBySensorAndDateRange` signature all preserved).
- Gateway/Network report outputs are identical: the new queries reproduce the exact previous filter
  (code match + inclusive `>= start` / `<= end`, `null` = unbounded), and date-string parsing in the
  reports is unchanged.
- Report formulas, histogram bucketing, and outlier logic were **not** changed.
- All 115 professor/base tests still pass (spaceless run); 15 new custom tests pass → 130 total.

## Known Limitations (for later phases)

- **Phase 4 (reports):** zero-standard-deviation outlier bug (`SensorReportImpl`,
  `GatewayReportImplementation.getOutlierSensors`); single-measurement statistics returning 0;
  load-ratio unit inconsistency (Gateway fraction vs Network percentage); histogram boundary/convention
  review; reports still return internally-mutable collections; potential NPE in
  `getGatewayParameterValue`. Reports could also adopt `countBy*` where a full list is not needed.
- **Phase 5 (CSV/import/logging/validation):** CSV path `%20` handling (the environmental test errors),
  UTF-8 + try-with-resources, row-numbered errors / `ImportResult`, `checkMeasurement` per-row sensor
  full-scan, centralized date parsing, alert testability.
- **Later cleanup:** `CRUDRepository.clearAll()` dead no-op; `Gateway.now` dead field; `User` ordinal
  enum storage; EAGER collections.
