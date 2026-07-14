# Test Results

Date: 2026-07-14 (updated in Phase 6 — Final Polish)

## Final Validation (Phase 6)

### Environment

| Item | Result |
|---|---|
| Java version | openjdk 21.0.11 2026-04-21 LTS (Microsoft) |
| Maven version | Apache Maven 3.9.16 (Maven wrapper present) |
| Maven wrapper present | YES (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/`) |
| OS / shell | Windows 11; PowerShell 5.1 + Git Bash |

### Results

| Check | Result | Notes |
|---|---:|---|
| Full Maven test suite (`.\mvnw.cmd clean test`, real repo path) | PASS | **204 run, 0 failures, 0 errors — BUILD SUCCESS.** |
| Base tests | PASS | 115 (Test_R0..R4b). |
| Custom tests | PASS | 89 across 12 custom classes. |
| Repository query tests | PASS | CustomMeasurementRepositoryQueryTest 12, CustomRepositoryFilteringSmokeTest 3. |
| Report/statistics tests | PASS | CustomSensorReportStatisticsTest 13, CustomReportLoadRatioAndFilteringTest 8. |
| CSV/import tests | PASS | CustomCsvParsingTest 8, CustomDataImportingServiceTest 14, CustomDateParsingValidationTest 8. |
| Validation/authorization tests | PASS | CustomAuthorizationBehaviorTest 7, CustomTopologyAndDeletionTest 7, CustomPersistenceConfigurationTest 2, CustomThresholdAlertingTest 6, CustomEndToEndWorkflowTest 1. |
| scripts/test.sh | PASS | 204 run, 0 failures, 0 errors — BUILD SUCCESS (real path). |
| scripts/test.ps1 | NOT RUN | Not executed this session (identical wrapper invocation as test.sh). |
| JaCoCo report | PASS | `mvn clean test jacoco:report` → `target/site/jacoco/index.html`; ~84.2% instruction / ~85.2% line coverage. |
| GitHub Actions workflow | CREATED | `.github/workflows/java-ci.yml` (push + PR, Temurin JDK 21, Maven cache, wrapper). |
| Generated files cleaned | PASS | `target/` removed after validation; no `.class`/`.DS_Store` remain. |

### Commands actually run (Phase 6)

`.\mvnw.cmd clean test` (real path), `.\mvnw.cmd clean test jacoco:report` (real path),
`bash scripts/test.sh` (real path), plus spaceless-copy runs of the modified `src`+`pom`. All reported
figures come from runs that actually executed; nothing is fabricated.

### Known Limitations

In-memory H2 only; alerting logs only (no real email/SMS); no REST API/UI/Docker; `checkMeasurement`
keeps a per-row sensor scan (required by the mock-based professor tests); see `docs/FINAL_REVIEW.md`.

---

Date: 2026-07-14 (updated in Phase 5 — CSV Import, Logging, Validation, Alerting)

## Phase 5 Validation

Environment unchanged (JDK 21.0.11, Maven 3.9.16, wrapper present, Windows 11).

| Check | Result | Notes |
|---|---:|---|
| Baseline `mvn clean test` before changes (spaceless) | PASS | 151 run, 0 failures, 0 errors — BUILD SUCCESS. |
| CSV parsing tests | PASS | `CustomCsvParsingTest` — 8, 0 failures/errors. |
| Data import tests | PASS | `CustomDataImportingServiceTest` — 14, 0 failures/errors. |
| Date parsing / validation tests | PASS | `CustomDateParsingValidationTest` — 8, 0 failures/errors. |
| Threshold alerting tests | PASS | `CustomThresholdAlertingTest` — 6, 0 failures/errors. |
| Full `mvn clean test` after changes (spaceless) | PASS | **187 run, 0 failures, 0 errors — BUILD SUCCESS** (115 base + 72 custom). |
| Full `.\mvnw.cmd clean test` in repo path `06 (WeatherSystem)` | PASS | **187 run, 0 failures, 0 errors — BUILD SUCCESS.** The 11 environmental CSV `%20` errors are now RESOLVED by the import path fix. |
| `scripts/test.sh` (repo path) | PASS | 187 run, 0 failures, 0 errors — BUILD SUCCESS. |
| `scripts/test.ps1` | NOT RUN | Not executed this session (same wrapper invocation as `test.sh`). |
| Generated files cleaned | PASS | `target/` removed after validation; no `.class`/`.DS_Store` remain. |

Custom test classes (72 total): CustomMeasurementRepositoryQueryTest 12, CustomRepositoryFilteringSmokeTest 3,
CustomSensorReportStatisticsTest 13, CustomReportLoadRatioAndFilteringTest 8, CustomCsvParsingTest 8,
CustomDateParsingValidationTest 8, CustomDataImportingServiceTest 14, CustomThresholdAlertingTest 6.

**Notable:** this is the first phase where the suite passes in the **real repo path** (with spaces),
not only in a spaceless copy — the CSV import now decodes `%20` in `URL.getPath()`-style paths.
Commands actually run: `mvn clean test` (baseline + modified, spaceless copies), `.\mvnw.cmd clean test`
(repo path), `bash scripts/test.sh` (repo path). No fake results.

---

Date: 2026-07-14 (updated in Phase 4 — Reports & Statistics)

## Phase 4 Validation

Environment unchanged (JDK 21.0.11, Maven 3.9.16, wrapper present, Windows 11).

| Check | Result | Notes |
|---|---:|---|
| Baseline `mvn clean test` before changes (spaceless path) | PASS | 130 run, 0 failures, 0 errors — BUILD SUCCESS. |
| Report/statistics custom tests | PASS | `CustomSensorReportStatisticsTest` 13; `CustomReportLoadRatioAndFilteringTest` 8 — 0 failures/errors. |
| Report smoke test | PASS | 100-row gateway dataset case inside `CustomReportLoadRatioAndFilteringTest`. |
| Full `mvn clean test` after changes (spaceless path) | PASS | **151 run, 0 failures, 0 errors — BUILD SUCCESS** (115 base + 36 custom). |
| Full run in repo path `06 (WeatherSystem)` | FAIL (environmental) | 151 run, 0 failures, **11 errors** — the pre-existing CSV `...%20...` errors only; all report/statistics + custom tests pass. Not a Phase 4 regression. |
| `scripts/test.sh` | NOT RUN | Not re-run this phase; `.\mvnw.cmd clean test` used instead (same wrapper). |
| `scripts/test.ps1` | NOT RUN | Not executed this session. |
| Generated files cleaned | PASS | `target/` removed after validation; no `.class`/`.DS_Store` remain. |

Per-class custom results (spaceless, after changes): CustomMeasurementRepositoryQueryTest 12,
CustomReportLoadRatioAndFilteringTest 8, CustomRepositoryFilteringSmokeTest 3,
CustomSensorReportStatisticsTest 13 → 36 custom; 115 base; **151 total, all green**.

Commands actually run: `mvn clean test` (baseline + modified, spaceless copies of committed src+pom),
`.\mvnw.cmd clean test` (repo path). No fake results: the suite "PASS" is the spaceless run that was
executed; the repo-path run is reported as FAIL with its exact (environmental) errors.

---

Date: 2026-07-14 (updated in Phase 3 — JPA & Repository Performance)

## Phase 3 Validation

Environment unchanged from Phase 2 (JDK 21.0.11, Maven 3.9.16, wrapper present, Windows 11).

| Check | Result | Notes |
|---|---:|---|
| Baseline `mvn clean test` before changes (spaceless path) | PASS | 115 run, 0 failures, 0 errors — BUILD SUCCESS. |
| Repository query tests | PASS | `CustomMeasurementRepositoryQueryTest` — 12 tests, 0 failures, 0 errors. |
| Repository smoke test | PASS | `CustomRepositoryFilteringSmokeTest` — 3 tests (200-row dataset), 0 failures. |
| Full `mvn clean test` after changes (spaceless path) | PASS | **130 run, 0 failures, 0 errors — BUILD SUCCESS** (115 base + 15 custom). |
| Full run in repo path `06 (WeatherSystem)` | FAIL (environmental) | 130 run, 0 failures, **11 errors** — all the pre-existing CSV `...%20...` errors; custom tests + non-CSV base tests all pass. Unchanged from Phase 2, not a Phase 3 regression. |
| `scripts/test.sh` | RAN (env FAIL) | Executes correctly via wrapper from root; surfaces the same 11 `%20` CSV errors. |
| `scripts/test.ps1` | NOT RUN | Not executed this session; identical wrapper invocation. |
| Generated files cleaned | PASS | `target/` removed after validation; no `.class`/`.DS_Store`/`.tmp` remain. |

Per-class (spaceless path, after changes):

| Test class | Tests | Failures | Errors |
|---|---:|---:|---:|
| Test_R0 / R1 / R2 / R3 / R4 / R4b (base) | 115 | 0 | 0 |
| CustomMeasurementRepositoryQueryTest | 12 | 0 | 0 |
| CustomRepositoryFilteringSmokeTest | 3 | 0 | 0 |
| **Total** | **130** | **0** | **0** |

Commands actually run this phase: `mvn clean test` (baseline + modified, spaceless copies of the
committed src+pom), `.\mvnw.cmd clean test` (repo path), `bash scripts/test.sh` (repo path).
No fake results: the only "PASS" for the suite is the spaceless run that was actually executed; the
repo-path run is reported as FAIL with its exact (environmental) errors.

---

## Environment

| Item | Result |
|---|---|
| Java version | openjdk 21.0.11 2026-04-21 LTS (Microsoft build 21.0.11+10-LTS) |
| Maven version | Apache Maven 3.9.16 |
| Maven wrapper present | YES (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`) |
| OS / shell | Windows 11; PowerShell 5.1 + Git Bash |

## Validation

| Check | Result | Notes |
|---|---:|---|
| Clean generated files | PASS | `target/` and `.DS_Store`/`*.class`/`*.tmp`/`*.log` removed; none remain. |
| Maven wrapper | PASS | Generated with `mvn -N wrapper:wrapper` (wrapper 3.3.4, Maven 3.9.16). |
| Java version compatibility | PASS | pom moved Java 25 → 21; compiles under the local JDK 21. |
| Persistence unit consistency | PASS | `weatherReportPU` (runtime) added; `weatherReportTestPU` (test) unchanged. |
| `mvn clean test` — spaceless path | PASS | **115 run, 0 failures, 0 errors** — BUILD SUCCESS (committed pom). |
| `mvn clean test` / `mvnw` — repo path with spaces | FAIL | **115 run, 0 failures, 11 errors** — all `Error reading CSV file: ...%20...` (environmental). |
| `scripts/test.sh` | RAN (env FAIL) | Script executes correctly (uses wrapper from root); surfaces the same 11 `%20` CSV errors. |
| `scripts/test.ps1` | NOT RUN | Not executed this session; identical wrapper invocation as `test.sh`. |

## Per-class results (spaceless path, committed pom)

| Test class | Tests | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| Test_R0 | 1 | 0 | 0 | 0 |
| Test_R1 | 29 | 0 | 0 | 0 |
| Test_R2 | 33 | 0 | 0 | 0 |
| Test_R3 | 31 | 0 | 0 | 0 |
| Test_R4 | 10 | 0 | 0 | 0 |
| Test_R4b | 11 | 0 | 0 | 0 |
| **Total** | **115** | **0** | **0** | **0** |

## Notes

- **The Java-25 build blocker is resolved.** Previously `mvn clean test` failed at compile
  (`error: release version 25 not supported`); it now compiles under JDK 21.
- **The 11 errors are environmental and pre-existing, not a Phase 2 regression.** The base/professor
  tests load CSV fixtures via `URL.getPath()`, which percent-encodes spaces. Because this portfolio's
  folder is named `06 (WeatherSystem)` (contains a space), the path reaches
  `DataImportingService` as `...06%20(WeatherSystem)...` and `FileReader` cannot open it. The same
  committed pom, built from a path **without** spaces, passes all 115 tests (see table above and
  `docs/PHASE_2_BUILD_STABILITY.md`).
- **How the spaceless run was performed honestly:** the committed `src/` + `pom.xml` were copied to a
  scratch directory outside the repo (no changes to the files) and `mvn clean test` was run there →
  BUILD SUCCESS, 115/115. This isolates the space-in-path issue from the build configuration.
- **Fix ownership:** the only fix is in `DataImportingService` (decode the path) or the folder name;
  the base tests must not be changed and CSV import is out of scope for Phase 2. Deferred to a later
  phase.
- A Mockito self-attach warning is emitted (harmless today; future-JDK concern). Noted for later.
- No fake results: the only "PASS" for the test suite is the clearly-labelled spaceless run that was
  actually executed; the repo-path run is reported as FAIL with its exact error.
