# Phase 2 Build and Repository Stability

Date: 2026-07-14
Scope: `Projects/06 (WeatherSystem)/project` only.

## Goal

Make the project clean, reproducible, runnable, and stable **before** any performance work.
This phase touches only build/config/repository-hygiene concerns — no repository query
optimization, no report-formula changes, no CSV-import rewrite, no public-API renames.

## Changes Made

- **Generated files removed:** `target/` and OS/editor junk (`.DS_Store`, `*.class`, `*.tmp`, `*.log`).
- **.gitignore updates:** added H2 local database files (`*.mv.db`, `*.trace.db`) on top of the
  Phase 1 entries (`target/`, `data/`, `.vscode/`, `.idea/`, `*.iml`, `*.class`, `*.log`, `*.tmp`,
  `.DS_Store`, `Thumbs.db`).
- **Maven wrapper added:** generated with `mvn -N wrapper:wrapper` (wrapper plugin 3.3.4, Maven 3.9.16).
  Created `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`.
- **Java version:** `pom.xml` changed from Java **25** to Java **21** (details below).
- **pom.xml:** `maven-compiler-plugin` upgraded `3.8.1 → 3.13.0`; added
  `<maven.compiler.release>21</maven.compiler.release>` property and referenced it from the plugin.
- **persistence.xml:** added the missing runtime unit `weatherReportPU`; kept `weatherReportTestPU`
  unchanged (details below).
- **scripts:** `scripts/test.sh` and `scripts/test.ps1` updated to `cd` to the project root before
  running and to prefer the wrapper.
- **README:** added `Requirements`, `Build and Test`, and `Project Notes` sections at the top
  (original course spec left intact below them).
- **TEST_RESULTS.md:** updated with actual Phase 2 results.

## Maven Wrapper

Present (created this phase):

```
mvnw
mvnw.cmd
.mvn/wrapper/maven-wrapper.properties
```

Usage: `./mvnw clean test` (Unix) or `.\mvnw.cmd clean test` (Windows). The wrapper is the
"only-script" distribution (no committed jar); it downloads Maven 3.9.16 on first use.

## Java Version

- **Previous:** Java 25 (`<java.version>25</java.version>`, compiler `<release>25</release>`,
  `maven-compiler-plugin` 3.8.1).
- **Final:** Java 21 (`<maven.compiler.release>21</maven.compiler.release>`, `<java.version>21</java.version>`,
  `maven-compiler-plugin` 3.13.0).
- **Reason:** The environment provides OpenJDK 21 (LTS). On the committed pom, `mvn clean test`
  previously failed at compile with `error: release version 25 not supported` — a JDK 21 toolchain
  cannot emit release-25 bytecode, and plugin 3.8.1 predates Java 25 support entirely. Java 21 is the
  portable LTS baseline and matches the local (and likely CI) JDK. No source code required Java 25
  features. This decision can be revisited if the grading/CI environment is confirmed to provide JDK 25.

## Persistence Units

`src/main/resources/META-INF/persistence.xml` now defines **two** units:

| Unit | Role | JDBC URL | `hbm2ddl.auto` |
|---|---|---|---|
| `weatherReportPU` | runtime / dev (default) | `jdbc:h2:mem:wrdb_runtime;DB_CLOSE_DELAY=-1` | `update` |
| `weatherReportTestPU` | tests | `jdbc:h2:mem:wrdb;DB_CLOSE_DELAY=0` | `create` |

Why this fixes the mismatch:
- `PersistenceManager.PU_NAME` defaults to `weatherReportPU`. Before Phase 2 that unit **did not
  exist**, so any runtime path that did not call `setTestMode()` would fail with "no persistence unit
  named weatherReportPU". The runtime unit is now defined.
- `PersistenceManager.setTestMode()` (called by `BasePersistenceTest`) selects `weatherReportTestPU`,
  which is **unchanged** — so professor/base tests behave exactly as before.
- Both units list the same seven entity classes and use the same H2 in-memory provider. The runtime
  unit uses a separate in-memory database name (`wrdb_runtime`) and quieter logging so running the
  app locally does not interfere with tests and leaves no files on disk.

## Test Commands

```bash
./mvnw clean test          # Unix / Git Bash
mvn clean test             # fallback
bash scripts/test.sh       # convenience script
```

```powershell
.\mvnw.cmd clean test      # Windows PowerShell
mvn clean test             # fallback
.\scripts\test.ps1         # convenience script
```

## Test Result

Actual results from this phase (see `TEST_RESULTS.md` for the full table):

- **Compilation:** now succeeds under JDK 21 (the Java-25 blocker is resolved).
- **Committed pom, run from the repo's actual path `06 (WeatherSystem)`:** `mvn clean test` →
  **115 tests run, 0 failures, 11 errors** — all 11 are `Error reading CSV file: ...%20...`.
- **Committed pom, run from a path without spaces:** `mvn clean test` →
  **115 tests run, 0 failures, 0 errors — BUILD SUCCESS.**

### About the 11 errors (environmental, pre-existing, not a Phase 2 regression)

The professor/base tests load CSV fixtures with
`getClass().getClassLoader().getResource("csv/S_111.csv").getPath()`. `URL.getPath()` returns a
**percent-encoded** path, so a space in the checkout directory becomes `%20`. `DataImportingService`
opens that raw path with `new FileReader(...)`, which cannot find `...06%20(WeatherSystem)...`.

- This affects **only** environments whose absolute path contains spaces (this portfolio uses the
  folder name `06 (WeatherSystem)`). It is why the Phase 1 diagnostic (spaceless scratch path) and the
  Phase 2 spaceless re-run both pass all 115 tests.
- It is **not** caused by any Phase 2 change (Java version, persistence, wrapper). It surfaced only
  now because before Phase 2 the build failed at compile, so tests never ran.
- The only code that could fix it is `DataImportingService` (decode/normalize the incoming path), and
  the base tests cannot be modified. CSV-import changes are explicitly **out of scope for Phase 2** and
  deferred. Recommended fix (early in a later phase): resolve the path via `Paths.get(new URI(url))`
  or `URLDecoder.decode(...)` instead of `URL.getPath()` consumption — a small, low-risk change.

## What Was Not Changed

- **No repository performance refactoring** (still `read()`-all + Java filtering in reports).
- **No report/statistics formula changes** (zero-std outliers, load-ratio units, etc. untouched).
- **No CSV import rewrite** (charset, try-with-resources, path decoding, ImportResult — all deferred).
- **No public API renames** or architectural refactoring.
- **No JPA mapping changes** (indexes, EAGER/LAZY, enum storage — all deferred).
- The `weatherReportTestPU` unit and all base tests are behaviourally unchanged.

## CI

- `.gitlab-ci.yml` exists and runs `mvn ... test`; left unchanged this phase (it relies on the runner
  image's JDK). No `.github/workflows` present. Adding a GitHub Actions workflow (pinned to JDK 21,
  using the wrapper) is deferred to Phase 6 per the fix plan.

## Next Phase

**Phase 3 — JPA and Repository Performance:** add targeted `MeasurementRepository` query methods
(gateway/network date-range) mirroring `findBySensorAndDateRange`, add indexes on `Measurement` query
columns, and review EntityManager lifecycle — without changing report formulas yet.
