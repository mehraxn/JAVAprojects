# Performance and Quality Fix Plan

This is the roadmap for the phases after the Phase 1 baseline audit
(`docs/AUDIT_PHASE_1.md`). Each phase is self-contained; run the full test suite after every phase.
Risk IDs (C1, H1, …) refer to the "Risk Level" section of the audit.

## Phase 2 — Build and Repository Stability

Goal: make `mvn clean test` build and run on the target environment.

Planned fixes:
- **Java version decision (C1).** Confirm the grading/CI JDK. Recommended: set
  `maven-compiler-plugin` to a modern version (e.g. 3.13.0) and `<release>` to **21** (LTS, matches the
  local + likely CI JDK). Only keep Java 25 if the CI runner actually provides JDK 25.
- **Remove the unused `<java.version>` property** (or wire it into the plugin) once the release is fixed.
- **Persistence unit consistency (C2).** Decide between: (a) add a real `weatherReportPU` production
  unit to `persistence.xml`, or (b) make the default `currentPUName` the existing test PU, or
  (c) rename to a single consistent PU. Fix the misleading `create-drop` comments.
- **Maven wrapper (M8).** Add `mvnw`/`mvnw.cmd` so builds are reproducible without a system Maven.
- **.gitignore** — already cleaned in Phase 1; re-verify after wrapper is added.
- **Test scripts** — already added (`scripts/test.sh`, `scripts/test.ps1`); re-verify they run green.
- **TEST_RESULTS.md** — update with a real committed-pom `mvn clean test` PASS once C1 is resolved.
- **README cleanup** — note build/run/test instructions and the chosen JDK.

Exit criteria: committed `mvn clean test` passes all 115 base tests with no source changes.

## Phase 3 — JPA and Repository Performance  ✅ COMPLETED (2026-07-14)

Goal: stop full-table scans; push filtering into the database.

See `docs/PHASE_3_JPA_REPOSITORY_PERFORMANCE.md` for full details. Result: 130 tests pass
(115 base + 15 custom) from a spaceless path; BUILD SUCCESS.

Fixes done:
- ✅ **Query methods instead of read-all filtering (H1).** Added `findBy{Sensor,Gateway,Network}Code`,
  `findBy{Sensor,Gateway,Network}CodeAndDateRange`, `findByDateRange`, and `countBy*` to
  `MeasurementRepository` (JPQL + `TypedQuery` + named params). Existing `findBySensorAndDateRange`
  kept as a delegating alias.
- ✅ **JPQL date-range queries adopted by the reports.** `GatewayReportImplementation` and
  `NetworkReportImpl` now filter in the database (done in Phase 3, not deferred). Behaviour identical.
- ✅ **Indexes (H2).** Added composite `@Index`es on `MEASUREMENTS`:
  `(sensorCode, measurement_timestamp)`, `(gatewayCode, …)`, `(networkCode, …)`, and
  `(measurement_timestamp)`.
- ✅ **EntityManager lifecycle review.** Confirmed EMF cached once; EM closed in `finally` everywhere;
  rollback on active tx. No structural change needed.
- ✅ **Transaction handling.** Left as-is (already correct).
- ✅ **Repository exception handling (M7).** Cause preserved; messages made entity-specific. Kept
  `RuntimeException` typing so service-layer mapping to `IdAlreadyInUseException` still works.

Deferred out of Phase 3 (low impact / different layer):
- ⏭ **EAGER review (M1)** — moved to a later cleanup (mapping change, no test pressure now).
- ⏭ **`getSensors`/`getGateways` (M6)** — entity (not measurement) lookups; left to preserve behavior.

Exit criteria met: reports issue targeted queries; all tests still pass.

## Phase 4 — Report Performance and Correctness  ✅ COMPLETED (2026-07-14)

Goal: reports query only what they need and produce correct, consistent numbers.

See `docs/PHASE_4_REPORTS_AND_STATISTICS.md`. Result: 151 tests pass (115 base + 36 custom) from a
spaceless path; BUILD SUCCESS. All decisions were checked against the README spec and base tests.

Fixes done:
- ✅ **Reports query needed data only.** Confirmed (done in Phase 3); no read-all in report paths.
- ✅ **Empty-data behavior.** Confirmed zeroed stats + empty collections for 0 measurements.
- ✅ **Single-measurement behavior (Sensor).** `mean` now returns the value (was 0); variance/stdDev
  stay 0 for n<2 per README.
- ✅ **Zero-standard-deviation outliers (H3).** `SensorReportImpl` now returns no outliers when
  `stdDev == 0`. Gateway `getOutlierSensors` left as spec-literal (uses configured `EXPECTED_STD_DEV`,
  not a computed std dev) — documented, not silently changed.
- ✅ **Histogram edge cases.** Reviewed: last-bucket inclusive, `min==max` single bucket, empty→empty
  all already correct; only made returns immutable. Network calendar-bucket scheme left as-is
  (flagged for spec confirmation).
- ✅ **Immutable report returns (M4).** All list/map/sorted-map getters return unmodifiable views.
- ✅ **Load-ratio consistency (H4).** Unified to **percentage (0–100)** in both reports (README calls
  both "percentage"); Gateway `getSensorsLoadRatio` now ×100.
- ✅ **NPE guard (M5)** added in `getGatewayParameterValue`.
- ✅ **Repeated calc (bonus).** `NetworkReportImpl` caches per-gateway counts once.
- ✅ **Custom report tests** added (see Phase 4 doc).

Kept per spec (NOT changed): sample variance (`/(n-1)`), outlier rule `>= 2σ`.

Exit criteria met: report correctness bugs fixed with tests; base tests still pass.

## Phase 5 — CSV Import, Logging, and Validation  ✅ COMPLETED (2026-07-14)

Goal: robust, observable import and de-duplicated cross-cutting logic.

See `docs/PHASE_5_IMPORT_LOGGING_VALIDATION.md`. Result: **187 tests pass (115 base + 72 custom) in
BOTH the spaceless path and the real `06 (WeatherSystem)` path** — the 11 long-standing environmental
CSV errors are resolved. BUILD SUCCESS.

Fixes done:
- ✅ **Robust CSV reading (H5).** Try-with-resources; I/O errors rethrow with the cause preserved.
- ✅ **UTF-8** explicitly via `Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)`.
- ✅ **`%20` path handling (bonus, root cause of the 11 errors).** `resolveReadableFile` decodes
  percent-encoded paths from `URL.getPath()`.
- ✅ **Row-numbered errors + `ImportResult`/`ImportError`.** Public `void` API preserved; new
  `storeMeasurementsWithResult` returns the summary. **Partial-import** strategy documented.
- ✅ **CSV parser (`CsvUtils`).** Small, dependency-free; handles quotes/escapes/empty/trailing.
- ✅ **Centralized date parser (M3).** `DateParsingUtils` reused by import + Gateway/Network reports
  (removed `split`/`substring` duplication).
- ✅ **Logging cleanup.** `DataImportingService` logs summary (info) + skipped rows/header (warn); no
  `printStackTrace`/`System.out` in service code.
- ✅ **Validation utilities (M2, partial).** `ValidationUtils` (unchecked) for the import path.
- ✅ **Threshold-alerting tests.** Boundary behavior covered via `mockStatic(AlertingService)`.

Deliberately NOT changed (with reasons):
- ⛔ **Sensor lookup by code (H6)** in `checkMeasurement` — the professor tests mock
  `CRUDRepository` construction and the README requires this structure; kept as-is.
- ⛔ **Alerting injectable handler** — the static API is required by `mockStatic`-based base tests.
- ⏭ **Operations-layer validation/authorization centralization (M2)** — needs checked-exception
  plumbing across all operations classes; deferred to Phase 6.

Exit criteria met: import is robust and tested; behavior changes documented; suite green in both paths.

## Phase 6 — Final Tests, Docs, and CI  ✅ COMPLETED (2026-07-14)

See `docs/FINAL_REVIEW.md`. Result: **204 tests pass (115 base + 89 custom) in the real repo path**;
~85% line coverage; BUILD SUCCESS.

Done:
- ✅ **Custom test suite** extended: added E2E workflow, authorization, topology/deletion, and
  persistence-configuration tests (17 new; 89 custom total).
- ✅ **README polish** + **architecture docs** (`ARCHITECTURE.md`, `TESTING.md`, `DECISIONS.md`,
  `FINAL_REVIEW.md`) with Mermaid diagrams.
- ✅ **GitHub Actions** workflow (`.github/workflows/java-ci.yml`); existing `.gitlab-ci.yml` kept.
- ✅ **JaCoCo** coverage (no enforced threshold): `mvn clean test jacoco:report`.
- ✅ **Final TEST_RESULTS** updated honestly; generated files cleaned.

Deliberately NOT done (documented, would add risk/scope):
- ⏭ **Authorization centralization (M2)** — deferred; needs checked-exception plumbing across all
  operations classes and touches heavily-tested code. Listed as future work.
- ⏭ **Dead-code removal** (`Gateway.now`, `CRUDRepository.clearAll()`, `User` ordinal enum) — left as-is
  to avoid behavior/schema risk near release; low priority.

## Future Work (beyond Phase 6 — not current features)

- Operations-layer authorization/validation centralization.
- PostgreSQL profile + Docker Compose; production deployment profile.
- External notification adapter (email/SMS) behind an interface.
- REST API and/or a small dashboard UI.
- Larger-dataset benchmarking; second-level cache evaluation.
- More integration tests; remove documented dead code.

## Safety Rules for Later Phases

- Preserve the public API (`WeatherReport` facade + `*Operations` interfaces) unless explicitly approved.
- Run the full test suite after every phase; never claim a pass without running it.
- Do not mix unrelated fixes in one change.
- Document every behavior change (especially report formulas and ratio units).
- Do not fake or estimate test results.
