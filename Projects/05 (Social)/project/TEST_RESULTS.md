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
| Java version compatibility | PASS | pom moved from Java 25 → 21; `IO.println` removed; compiles on JDK 21. |
| Maven wrapper | PASS | Generated with `mvn -N wrapper:wrapper` (wrapper 3.3.4, Maven 3.9.16). |
| Full Maven test suite | PASS | **41 run, 0 failures, 0 errors — BUILD SUCCESS.** |
| Base test (professor) | PASS | `example.TestExample` — 5 (R1–R5). |
| Custom tests | PASS | 36 across 4 classes. |
| Person tests | PASS | `CustomPersonAndFriendshipTest` (person + friendship) — 9. |
| Friendship tests | PASS | included in `CustomPersonAndFriendshipTest`. |
| Group tests | PASS | `CustomGroupTest` — 12. |
| Post tests | PASS | `CustomPostPaginationTest` — 9. |
| Pagination tests | PASS | included in `CustomPostPaginationTest` (user + friend feeds). |
| Statistics tests | PASS | `CustomStatisticsAndEndToEndTest` — 6 (incl. end-to-end + tie-break). |
| scripts/test.sh | PASS | 41 run, 0 failures, 0 errors — BUILD SUCCESS. |
| scripts/test.ps1 | NOT RUN | Not executed this session (identical wrapper invocation as test.sh). |
| JaCoCo report | PASS | `mvn clean test jacoco:report` → `target/site/jacoco/index.html`; ~96.0% instruction / ~96.6% line coverage. |
| GitHub Actions workflow | CREATED | `.github/workflows/java-ci.yml` (push + PR, Temurin JDK 21, Maven cache, wrapper). |

## Commands actually run

`mvn clean test` (baseline — FAILED before changes with `release version 25 not supported`),
`mvn clean test jacoco:report` (after changes — PASS 41/0/0), `mvn -N wrapper:wrapper`,
`bash scripts/test.sh` (PASS 41/0/0).

## Known Limitations

- Educational/local project; H2 database only.
- No REST API, no authentication, no frontend.
- No production deployment; no external services.
- Statistics computed in Java (small dataset) rather than via JPQL.

All results above come from commands that were actually executed; nothing is fabricated.
