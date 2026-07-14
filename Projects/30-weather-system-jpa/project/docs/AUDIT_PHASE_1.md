# Phase 1 Audit

Date: 2026-07-14
Scope: `Projects/06 (WeatherSystem)/project` only. No business logic, JPA mappings, report
formulas, repository queries, or public APIs were changed in this phase.

## Project Summary

WeatherReport is a Maven-based Java backend for a weather-monitoring domain. It uses:

- **JPA / Hibernate** (`hibernate-core` 7.1.7, `jakarta.persistence-api` 3.2.0) for persistence.
- **H2** (2.4.240) as an in-memory database (`jdbc:h2:mem:wrdb`).
- A layered architecture:
  - `model/entities` — JPA entities (Network, Gateway, Sensor, Measurement, Operator, Parameter, User, embeddable Threshold).
  - `repositories` — a generic `CRUDRepository<T, ID>` plus a `MeasurementRepository`.
  - `operations` — the facade/service layer (`WeatherReport` facade + `*Operations`/`*OperationsImpl` + `OperationsFactory`) holding authorization, validation, and CRUD orchestration.
  - `reports` — statistics/reporting classes (Sensor/Gateway/Network reports, histograms, outliers).
  - `services` — `DataImportingService` (CSV import) and `AlertingService` (notifications/logging).
  - `persistence` — `PersistenceManager` (EntityManagerFactory lifecycle).
- **JUnit 5** (5.13.4) + **Mockito** (5.20.0) for tests, with professor/base tests `Test_R0..R4b`.
- **log4j2** for logging.

The domain models a topology (Network → Gateway → Sensor), sensor measurements imported from CSV,
thresholds with alerting, and statistical reports (mean/variance/outliers/histograms).

## Current Structure

```
project/
├── pom.xml
├── .gitignore                      (improved this phase)
├── .gitlab-ci.yml                  (GitLab CI: mvn test + junit report)
├── README.md / README_it.md        (EN/IT project description)
├── checklist.md
├── scripts/                        (added this phase: test.sh, test.ps1)
├── docs/                           (added this phase: this audit + fix plan)
├── TEST_RESULTS.md                 (added this phase)
└── src/
    ├── main/java/com/weather/report/
    │   ├── WeatherReport.java                 (facade entry point)
    │   ├── exceptions/                        (WeatherReportException + 4 subclasses)
    │   ├── model/                             (UserType, ThresholdType, Timestamped)
    │   │   └── entities/                      (Network, Gateway, Sensor, Measurement,
    │   │                                       Operator, Parameter, User, Threshold)
    │   ├── operations/                        (facade/service layer + OperationsFactory)
    │   ├── persistence/PersistenceManager.java
    │   ├── reports/                           (Sensor/Gateway/Network reports + Range types)
    │   ├── repositories/                      (CRUDRepository, MeasurementRepository)
    │   └── services/                          (DataImportingService, AlertingService)
    ├── main/resources/
    │   ├── META-INF/persistence.xml           (single PU: weatherReportTestPU)
    │   ├── log4j2.xml
    │   └── csv/                               (14 sample CSV files S_1xx.csv)
    └── test/java/com/weather/report/test/
        ├── BasePersistenceTest.java
        ├── base/Test_R0..R4b.java             (115 professor tests)
        └── custom/.gitkeep                    (empty — no custom tests yet)
```

The real Maven project **is** inside the `project/` folder (the parent `06 (WeatherSystem)/`
folder holds only learning materials, which were not touched).

## Build and Test Baseline

- **Java:** OpenJDK 21.0.11 (Microsoft). **Maven:** 3.9.16. **Wrapper:** none.
- `mvn clean test` on the committed pom **FAILS at compile**:
  `error: release version 25 not supported`. Tests never start.
- Diagnostic only (scratch copy, Java-21 override, not committed): the source compiles and
  **all 115 base tests pass** (R0:1, R1:29, R2:33, R3:31, R4:10, R4b:11). See `TEST_RESULTS.md`.

**Conclusion:** the code is healthy; the build is blocked purely by the Java 25 target on a JDK 21
runtime plus an obsolete compiler plugin.

## Maven Configuration Findings

| Item | Value |
|---|---|
| groupId / artifactId / version | `it.polito.oop.project` / `WeatherReport` / `1.0.0` |
| Java target | `<release>25</release>` + `<java.version>25</java.version>` |
| maven-compiler-plugin | **3.8.1** (2019-era; cannot target Java 25) |
| maven-surefire-plugin | 3.5.3 |
| JUnit | junit-jupiter 5.13.4 |
| Mockito | mockito-core 5.20.0 |
| jakarta.persistence-api | 3.2.0 |
| Hibernate | hibernate-core 7.1.7.Final (relocated → `org.hibernate.orm`) |
| H2 | 2.4.240 |
| Logging | log4j-api / log4j-core 2.23.1 |

Findings:
- **[CRITICAL] Java 25 target vs JDK 21 runtime.** The build cannot succeed on this machine.
  Java 25 is also extremely new and unlikely to match the CI runner. Java 21 (LTS) is safer and
  is what the environment provides.
- **[HIGH] `maven-compiler-plugin` 3.8.1** is far too old to emit Java 25 bytecode even on a
  JDK 25; it should be modern (e.g. 3.13.0+) regardless of the version decision.
- **[MEDIUM] No Maven wrapper** (`mvnw`/`mvnw.cmd`), so builds depend on a locally installed
  Maven; `scripts/test.*` already fall back to system `mvn`.
- **[LOW] `<java.version>` property is unused** because the compiler plugin hardcodes `<release>`.

## Persistence Configuration Findings

`src/main/resources/META-INF/persistence.xml`:
- Defines **exactly one** persistence unit: `weatherReportTestPU`.
- H2 **in-memory**: `jdbc:h2:mem:wrdb;DB_CLOSE_DELAY=0`, user `sa`.
- `hibernate.hbm2ddl.auto = create`, `show_sql = true`, `format_sql = true`.
- Entities listed: User, Measurement, Network, Operator, Gateway, Parameter, Sensor.
- No explicit dialect (Hibernate auto-detects H2 — acceptable).

`PersistenceManager`:
- `PU_NAME = "weatherReportPU"` is the **default** `currentPUName`.
- `TEST_PU_NAME = "weatherReportTestPU"` is only selected when `setTestMode()` is called.

Findings:
- **[CRITICAL] PU name mismatch.** The default PU (`weatherReportPU`) **does not exist** in
  `persistence.xml`. Any code path that obtains an EntityManager without first calling
  `setTestMode()` will fail with a "no persistence unit named weatherReportPU" error. Tests pass
  only because `BasePersistenceTest.baseSetUp()` calls `PersistenceManager.setTestMode()`.
- **[MEDIUM] Comment vs config drift.** `PersistenceManager.setTestMode()` and `getCurrentFactory()`
  comment that the test PU is `create-drop`, but `persistence.xml` uses `create`. Behaviour is fine
  for in-memory tests (schema recreated each factory), but the comment is misleading.
- **Recommended (Phase 2, safe):** either add a real `weatherReportPU` production unit, or change
  the default `currentPUName` to the existing test PU, or rename consistently. Deferred — needs a
  decision on whether a separate runtime PU is intended.

## Entity and Mapping Findings

| Entity | Table | PK | Notable mappings |
|---|---|---|---|
| Network | NETWORKS | `code` (String) | `@ManyToMany(EAGER)` operators via `NETWORK_OPERATORS`; `@OneToMany(mappedBy=network)` gateways (LAZY default). `equals/hashCode` on `code`. |
| Gateway | GATEWAYS | `code` (String) | `@OneToMany(mappedBy=gateway, cascade=ALL, orphanRemoval=true, EAGER)` parameters; `@ManyToOne` network (`network_code`). Stray unused field `LocalDateTime now`. `equals/hashCode` on `code`. |
| Sensor | SENSORS | `code` (String) | `@ManyToOne` gateway (`gateway_code`, LAZY-by-spec but ManyToOne defaults to EAGER); `@Embedded` Threshold. Extends `Timestamped`. No `equals/hashCode`. |
| Measurement | MEASUREMENTS | `id` (IDENTITY) | Denormalized `networkCode`/`gatewayCode`/`sensorCode` **strings** (no FK relationships); `measurement_value`, `measurement_timestamp`. No indexes. |
| Operator | OPERATORS | `email` (String) | Plain fields; `equals/hashCode` on `email`. |
| Parameter | PARAMETERS | `id` (IDENTITY) | `@ManyToOne(LAZY)` gateway; `uniqueConstraint(gateway_code, code)`; `code` `nullable=false`. |
| User | WR_USER | `username` (String) | `@Enumerated` (**ORDINAL by default**) `user_type`. Package-private constructor. |
| Threshold | (embeddable) | — | `threshold_value`; `@Enumerated(STRING)` `threshold_type`. |

Findings (documented only — no mapping changes this phase):
- **[HIGH] `Measurement` has no index** on `sensorCode`, `gatewayCode`, `networkCode`, or
  `timestamp`. Every report filters/queries by these columns; full scans will dominate cost as data
  grows. Candidate for `@Index` / composite indexes in Phase 3.
- **[HIGH] `User.type` uses ordinal enum** (`@Enumerated` with no `EnumType.STRING`). Ordinal storage
  is brittle: reordering/inserting `UserType` values silently corrupts existing rows. `Threshold`
  already does it correctly with `EnumType.STRING`; `User` should match. (Deferred — changing storage
  format could affect any persisted data / expectations.)
- **[MEDIUM] EAGER collections.** `Gateway.parameters` (EAGER) and `Network.operators` (EAGER, ManyToMany)
  are always loaded. With `Gateway` also cascading ALL + orphanRemoval, loading gateways pulls all
  parameters; loading networks pulls all operators. Fine at test scale, wasteful at volume. Review in
  Phase 3.
- **[MEDIUM] Denormalized Measurement codes.** Measurements store plain code strings rather than
  relationships. This is a deliberate design (keeps measurements decoupled), but it means there is
  **no referential integrity** and no join-based querying; reports must match on strings. Not a bug,
  but it is why report queries currently full-scan. Keep the design; add indexes + JPQL.
- **[LOW] `Gateway.now` field** is an unused `LocalDateTime` that will be persisted as a column.
  Dead field; remove later.
- **[LOW] `Sensor` lacks `equals/hashCode`** (other code-keyed entities have them). Low impact given
  it is managed by JPA, but inconsistent.
- **Table names are safe** (`WR_USER` avoids the reserved `USER`). Good.

## Repository and Query Findings

`CRUDRepository<T, ID>`:
- Opens a **new `EntityManager` per operation** and closes it in `finally` (correct lifecycle).
- `read()` (all) runs `SELECT e FROM <SimpleName> e` — returns **every row**.
- `read(id)`, `create`, `update` (merge), `delete` handle transactions and rollback correctly.
- `create()` catches `EntityExistsException`/`RollbackException` and rethrows as `RuntimeException`
  so the service layer can map to `IdAlreadyInUseException`. Works, but loses exception typing.
- `clearAll()` is an empty no-op kept for compatibility (dead code).
- `getEntityName()` is defined but `read()` actually uses `entityClass.getSimpleName()` directly
  (minor inconsistency; harmless because entity name == simple name here).

`MeasurementRepository`:
- `findBySensorAndDateRange(sensorCode, start, end)` builds proper JPQL with optional date bounds and
  `ORDER BY timestamp`. This is the **good pattern** and is already used by `SensorReport`.

Findings:
- **[HIGH] Reports bypass the repository and full-scan.** `GatewayReportImplementation` and
  `NetworkReportImpl` call `new CRUDRepository<>(Measurement.class).read()` to load **all**
  measurements, then filter by gateway/network and date **in Java**. This is the primary performance
  bottleneck. There is no `findByGatewayAndDateRange` / `findByNetworkAndDateRange` equivalent.
  (Phase 3/4.)
- **[HIGH] `DataImportingService.checkMeasurement` full-scans sensors per row.** For every imported
  measurement it calls `sensorRepository.read()` (all sensors) and filters in Java — O(rows × sensors).
  Should be `read(sensorCode)`. (Phase 5.)
- **[MEDIUM] `getSensors(String...)` / `getGateways(...)` load all then filter.**
  `SensorOperationsImplement.getSensors` calls `read()` and filters in Java even when specific codes
  are requested; could `read(code)` per code. `GatewayOperationsImplements.getGateways` already does
  per-code reads.
- **[MEDIUM] No pagination anywhere.** `read()`-all APIs cannot scale.
- **[LOW] Broad `catch (Exception)` → `RuntimeException`** in the repository loses the original
  exception type/message granularity for callers.
- **EntityManagerFactory** is cached statically in `PersistenceManager` (not recreated per call) —
  good; only per-request EntityManagers are created.

## Report and Statistics Findings

`SensorReportImpl` (built from a pre-filtered measurement list — good, uses the repo query):
- **[HIGH] Zero-standard-deviation outlier bug.** Outliers are `|value - mean| >= 2*stdDev`. When
  `count >= 2` but all values are equal, `stdDev = 0`, so the limit is `0` and **every** measurement
  satisfies `>= 0` → all classified as outliers → `nonOutliers` empty → min/max = 0 and histogram
  empty. Almost certainly not intended. (Phase 4.)
- **[MEDIUM] Single-measurement statistics return 0.** For `count < 2`, `mean/variance/stdDev = 0`
  and outliers empty; a lone measurement reports `mean = 0.0` rather than its own value. May or may
  not match the spec — needs confirmation. (Phase 4.)
- **[LOW] Mutable returns.** `getOutliers()` returns the internal `List`; `getHistogram()` returns the
  internal `SortedMap`. Callers can mutate report internals.
- Histogram handles the `min == max` single-bucket case and last-bucket-inclusive convention correctly.
- `outliers.contains(m)` relies on `Measurement` identity (no `equals`), which is fine here because the
  same object instances are reused.

`GatewayReportImplementation`:
- **[HIGH] Full-scan** via `measurementRepo.read()` then Java filtering (see repositories).
- **[HIGH] Outlier-sensor detection shares the zero-std bug.** `|sensorMean - expectedMean| >= 2*expectedStd`;
  if the gateway's `EXPECTED_STD_DEV` parameter is 0 (or missing → default 0), every sensor becomes an
  outlier. (Phase 4.)
- **[HIGH] Ratio-unit inconsistency.** `getSensorsLoadRatio()` returns a **fraction** (`count/total`),
  whereas `NetworkReportImpl.getGatewaysLoadRatio()` returns a **percentage** (`*100`). One of them is
  wrong relative to the spec/tests; must be made consistent. (Phase 4.)
- **[MEDIUM] `getGatewayParameterValue` can NPE.** `repo.read(code)` may return `null` (gateway
  removed) and is dereferenced without a null check.
- **[MEDIUM] Custom date parser** `toLocalDateTime` uses `split(" ")`/`split("-")`/`split(":")` with no
  validation; throws unindexed `ArrayIndexOutOfBoundsException`/`NumberFormatException` on bad input.
- Duration histogram handles `min == max` and 20-bucket partitioning with correct closed/half-open logic.

`NetworkReportImpl`:
- **[HIGH] Full-scan** via `repo.read()` + Java filtering.
- **[HIGH] Ratio unit** is **percentage** here (`/total * 100`) — conflicts with the Gateway report's
  fraction (see above).
- **[MEDIUM] Fragile `substring`-based date parsing** repeated three times (constructor + histogram
  bounds), each throwing on malformed input.
- **[MEDIUM] Histogram convention differs.** Uses hour/day boundary buckets (≤48h → hourly, else daily)
  rather than the fixed 20-bucket scheme documented for the gateway histogram. May be intentional per
  spec, but the inconsistency should be confirmed.
- **[LOW] Mutable returns.** `getGatewaysLoadRatio()` returns the internal `HashMap`; `getHistogram()`
  returns the internal `TreeMap`.

Cross-cutting:
- **[MEDIUM] Date parsing is duplicated in ≥3 styles** (report `split`, report `substring`, service
  `split`, and `SensorOperationsImplement` correctly uses `DateTimeFormatter` with
  `WeatherReport.DATE_FORMAT`). Centralize on the `DateTimeFormatter` approach. (Phase 5.)

## CSV Import Findings

`DataImportingService.storeMeasurements`:
- **[HIGH] Default platform charset.** Uses `new FileReader(filePath)` (platform default), not UTF-8.
- **[HIGH] No try-with-resources.** `BufferedReader` is closed only on the happy path
  (`reader.close()` after the loop); an exception mid-parse leaks the reader.
- **[HIGH] Swallowed root cause.** `catch (Exception e)` throws
  `new RuntimeException("Error reading CSV file: " + filePath)` **without** passing `e` — the original
  cause (bad number, missing column, IO error, and its row) is lost.
- **[MEDIUM] `String.split(",")`** cannot handle quoted fields, embedded commas, or trailing empties.
- **[MEDIUM] No row numbers / no import summary.** Rows with `< 5` columns are silently skipped; there
  is no `ImportResult` reporting imported/skipped/failed counts.
- **[MEDIUM] No existence/topology validation.** Measurements are persisted regardless of whether the
  referenced sensor/gateway/network exist (consistent with the denormalized design, but unvalidated).
- **[MEDIUM] Per-row sensor full-scan** in `checkMeasurement` (see repositories) — should read by code.
- Header handling: reads line 1 (header), then advances to line 2 before the loop — works, but there is
  no header validation.
- Threshold comparison logic (LESS_THAN … NOT_EQUAL, with EPSILON for equality) is correct.

## Authorization and Validation Findings

- **[MEDIUM] Authorization is duplicated across every operations class.** Each of
  `NetworkOperationsImpl` (`validateUserIsMaintainer`), `GatewayOperationsImplements`
  (`validateMaintainer`), `SensorOperationsImplement` (`validateMaintainer`), and
  `TopologyOperationsImpl` (inline `user == null || type != MAINTAINER`) reimplements the same
  maintainer check with slightly different null/blank handling and messages. No central
  `AuthorizationService`.
- **[MEDIUM] Code-format validation is duplicated.** Network/Gateway/Sensor code checks exist in both
  `TopologyOperationsImpl` (`isValidNetworkCode/Gateway/Sensor`) and the individual operations classes
  (regex `GW_\d{4}`, `S_\d{6}`, `NET_` + 2 digits). Same rules, multiple copies, minor divergence
  (e.g. `SensorOperationsImplement` also checks `isBlank`).
- **[LOW] Inconsistent validation order.** Some methods validate the user first, others validate input
  first; error precedence differs across classes.
- **Exception hierarchy is clean:** `WeatherReportException` base with `ElementNotFoundException`,
  `IdAlreadyInUseException`, `InvalidInputDataException`, `UnauthorizedException`. Good foundation for
  centralization.
- `AlertingService` logs email/SMS notifications; hard to unit-test because it is static and only logs
  (no injectable sink). Noted for Phase 5 testability.

## Test Coverage Findings

- **Base/professor tests:** `Test_R0..R4b` = **115 tests**, all passing (under the Java-21 diagnostic
  build). They exercise requirements R0–R4 through the `WeatherReport` facade.
- **Custom tests:** none — `src/test/java/com/weather/report/test/custom/` contains only `.gitkeep`.
- **Gaps (candidates for later custom tests, not added in Phase 1):**
  - Persistence configuration (default PU mismatch / runtime path without `setTestMode`).
  - Repository query methods (`findBySensorAndDateRange`; future gateway/network range queries).
  - Report edge cases: empty data, single measurement, all-equal values (zero-std outliers),
    histogram boundary/last-bucket inclusion, load-ratio units.
  - CSV import edge cases: bad rows, missing columns, non-UTF-8, missing sensor/threshold, alerting.
  - Topology consistency and deletion rules (cascade/orphanRemoval behaviour).
  - Authorization (viewer vs maintainer) and input validation ordering.
  - Immutability of report return values.

## Generated File Cleanup

- Removed `target/` (Maven build output) from the project.
- Removed **8** `.DS_Store` files (macOS junk) from `src/` subtrees.
- No stray `*.class` files were present outside `target/`.
- `.gitignore` expanded to cover `target/`, `data/`, `.vscode/`, `.idea/`, `*.iml`, `*.class`,
  `*.log`, `*.tmp`, `.DS_Store`, `Thumbs.db`.

## Risk Level

**Critical (breaks runtime/tests as committed):**
- C1. `pom.xml` targets Java 25 with `maven-compiler-plugin` 3.8.1 → `mvn clean test` fails to compile
  on the JDK 21 environment. Nothing runs.
- C2. `PersistenceManager` default PU `weatherReportPU` does not exist in `persistence.xml`; any
  runtime path that skips `setTestMode()` fails.

**High (correctness / performance):**
- H1. Reports (`Gateway`, `Network`) load **all** measurements and filter in Java (no date-range JPQL).
- H2. `Measurement` has no indexes on its query columns.
- H3. Zero-standard-deviation outlier bug in `SensorReportImpl` and `GatewayReportImplementation`.
- H4. Load-ratio unit inconsistency (fraction vs percentage) between Gateway and Network reports.
- H5. CSV import: default charset, no try-with-resources, root cause swallowed.
- H6. `checkMeasurement` full-scans all sensors per imported row.
- H7. `User.type` stored as ordinal enum (brittle).

**Medium (design / robustness):**
- M1. EAGER collections (`Gateway.parameters`, `Network.operators`).
- M2. Duplicated authorization and code-format validation across operations classes.
- M3. Fragile, duplicated date parsing (`split`/`substring`) in reports and import.
- M4. Mutable collections returned by reports.
- M5. `getGatewayParameterValue` potential NPE.
- M6. No pagination; `getSensors`/`getGateways` load-all-then-filter.
- M7. Repository broad exception wrapping loses typing.
- M8. Missing Maven wrapper.

**Low (naming / cleanup / style):**
- L1. `Gateway.now` dead field.
- L2. `Sensor` missing `equals/hashCode`.
- L3. `CRUDRepository.clearAll()` empty no-op; `getEntityName()` vs `getSimpleName()` inconsistency.
- L4. Unused `<java.version>` property.
- L5. Misleading `create-drop` comments vs `create` config.
- L6. Mockito self-attach warning.

## Do Not Fix Yet

Deferred to later phases (documented, not changed in Phase 1):
- The Java version / compiler plugin decision (Phase 2) — do not change the committed pom until the
  target JDK is confirmed against the grading/CI environment.
- The persistence-unit mismatch resolution (Phase 2) — needs a decision on whether a separate runtime
  PU is intended before editing `persistence.xml` or `PersistenceManager`.
- All JPA mapping changes: indexes, EAGER→LAZY, `User` enum storage, removing `Gateway.now` (Phase 3).
- All report formula changes: zero-std outliers, single-measurement stats, load-ratio units, histogram
  conventions, immutability (Phase 4).
- Repository query additions (gateway/network date-range) and their adoption by reports (Phase 3/4).
- CSV import rewrite: UTF-8, try-with-resources, row-numbered errors, `ImportResult`, existence
  validation (Phase 5).
- Authorization/validation centralization (Phase 5/6).
- Custom test suite (Phase 6).
