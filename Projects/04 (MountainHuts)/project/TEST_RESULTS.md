# Test Results

Date: 2026-07-14

## Environment

| Item | Result |
|---|---|
| Java version | openjdk 21.0.11 2026-04-21 LTS (Microsoft) |
| Maven version | Apache Maven 3.9.16 (via Maven wrapper) |
| Maven wrapper present | YES (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/`) |
| OS / shell | Windows 11; PowerShell 5.1 + Git Bash |

## Validation

| Check | Result | Notes |
|---|---:|---|
| Clean generated files | PASS | `target/` removed after validation; no `.class`/`.DS_Store`. |
| Java version compatibility | PASS | pom moved from Java 25 → 21; compiles and runs on JDK 21. |
| Maven wrapper | PASS | Generated with `mvn -N wrapper:wrapper` (wrapper 3.3.4, Maven 3.9.16). |
| Full Maven test suite | PASS | **47 run, 0 failures, 0 errors — BUILD SUCCESS.** |
| AltitudeRange tests | PASS | `CustomAltitudeRangeTest` — 6. |
| Municipality tests | PASS | `CustomMunicipalityAndHutTest` (municipality + hut) — 6. |
| MountainHut tests | PASS | included in `CustomMunicipalityAndHutTest`. |
| Region tests | PASS | `CustomRegionTest` — 9 (behavior, defensive/deterministic, end-to-end). |
| CSV import tests | PASS | `CustomCsvImportTest` — 7 (real dataset 94/167, missing/malformed/invalid/blank). |
| Query/report tests | PASS | professor `TestR4_Queries` (6) + `CustomRegionTest.endToEndQueries`. |
| Professor/example tests | PASS | example (4) + TestR1..R4 (15) = 19. |
| scripts/test.sh | PASS | 47 run, 0 failures, 0 errors — BUILD SUCCESS. |
| scripts/test.ps1 | NOT RUN | Not executed this session (identical wrapper invocation as test.sh). |
| JaCoCo report | PASS | `mvn clean test jacoco:report` → `target/site/jacoco/index.html`; ~95.2% line coverage. |
| GitHub Actions workflow | CREATED | `.github/workflows/java-ci.yml` (push + PR, Temurin JDK 21, Maven cache, wrapper). |

## Baseline (before changes)

The committed `pom.xml` targeted Java 25 (`<release>25</release>`), which is not supported by the local
JDK 21 — `mvn clean test` failed at compile with `release version 25 not supported`. After lowering the
target to Java 21 (no Java-25 feature was used), the full suite compiles and passes.

## Commands actually run

`mvn clean test` (after Java-21 change — PASS 19/19 professor+example, then PASS 47/47 with custom),
`mvn clean test jacoco:report` (PASS), `mvn -N wrapper:wrapper`, `bash scripts/test.sh` (PASS 47/47).

## Known Limitations

- Educational/local project; file/CSV based.
- Format-specific CSV importer (no quoted fields).
- No database, no REST API, no frontend, no production deployment, no external services.

All results above come from commands that were actually executed; nothing is fabricated.
