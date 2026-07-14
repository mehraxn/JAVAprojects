# Design Decisions

Concise record of the main decisions and their trade-offs. Fuller rationale lives in the per-phase
docs (`AUDIT_PHASE_1.md`, `PHASE_2..5`).

## Maven and Java Version

Targets **Java 21 (LTS)** via `maven.compiler.release=21` and a modern `maven-compiler-plugin`. The
original pom targeted Java 25, which could not compile on the available JDK 21. Java 21 is portable and
matches the CI image. A Maven wrapper pins the Maven version.

## JPA / Hibernate Persistence

Jakarta Persistence + Hibernate ORM. Entities use field access, string codes as natural IDs for
topology entities, and `IDENTITY` generation for `Measurement`/`Parameter`.

## H2 for Local and Test Persistence

H2 in-memory keeps the project self-contained and fast. Two persistence units exist: `weatherReportPU`
(runtime/dev default) and `weatherReportTestPU` (tests). Trade-off: no production datastore/profile.

## Facade and Operation Layer

A single `WeatherReport` facade delegates to `*Operations` interfaces via `OperationsFactory`. Keeps
the public surface small and swappable. Trade-off: authorization/validation is currently duplicated
across operation classes (centralization deferred — it would require threading the project's *checked*
exceptions through a shared component).

## Repository Pattern

Generic `CRUDRepository` centralizes EntityManager/transaction handling; `MeasurementRepository` adds
typed JPQL finders. Trade-off: `CRUDRepository.read()` (read-all) still exists for small entity sets.

## JPQL Query Filtering

Reports and lookups use `TypedQuery` with named parameters and inclusive date ranges, backed by
composite indexes on `MEASUREMENTS`. Replaced earlier "load all then filter in Java" patterns.

## Date Range Behavior

Inclusive bounds (`timestamp >= start AND timestamp <= end`); a null bound means unbounded; `start`
after `end` is rejected. Parsing is centralized in `DateParsingUtils` using `WeatherReport.DATE_FORMAT`.

## Report Edge-Case Behavior

Per the README spec: **sample** variance (`/(n-1)`), std dev = √variance, both `0` for < 2 measurements
(mean is still the actual value for a single measurement); outlier iff `|x − mean| >= 2σ`. Added a
guard so that when `σ == 0` (identical values) there are **no** outliers, instead of flagging every
value. Load ratios are **percentages (0–100)** in both reports (README wording); report collections are
returned as unmodifiable views.

## CSV Import Strategy

**Partial import**: valid rows persist; malformed rows are skipped and recorded (row-numbered) in an
`ImportResult`. UTF-8 + try-with-resources; a small dependency-free parser (`CsvUtils`) handles quotes/
escapes. The public `void` API is preserved; a result-returning method was added alongside.

## Logging Strategy

log4j2. The import service logs an info summary and per-row warnings; no `printStackTrace`/`System.out`
in service code; exception causes are preserved.

## Threshold Alerting Behavior

`checkMeasurement` compares each imported value against the sensor's threshold and notifies operators on
violation. Its structure (sensor lookup via `new CRUDRepository<>(Sensor.class).read()`) is **kept as-is
on purpose**: the professor tests mock `CRUDRepository` construction, and the README requires that
structure. `AlertingService` stays static for the same reason (base tests use `mockStatic`).

## Public API Compatibility

Facade, `*Operations` interfaces, report interfaces, and existing method signatures were preserved
across all phases; new capabilities were added as new methods/classes.

## Known Trade-Offs

Denormalized `Measurement` codes (simple, no joins, but no referential integrity); some EAGER
collections; dead code kept to avoid risk (`CRUDRepository.clearAll()`, `Gateway.now`); `User` role
stored as ordinal enum (left unchanged to avoid a storage-format change). See `FINAL_REVIEW.md` for the
remaining list.
