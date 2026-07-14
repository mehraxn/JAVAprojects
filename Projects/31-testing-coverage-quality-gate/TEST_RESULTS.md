# Test Results

Date: 2026-07-11

All commands below were actually executed on a Windows 11 machine from this project directory. No system-wide JDK or Maven was installed, so validation used a portable Eclipse Temurin JDK 21.0.11 and Apache Maven 3.9.9 (and, after generation, the project's own Maven Wrapper, which downloaded Maven 3.9.9 itself). Nothing in this file is estimated or assumed.

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK Temurin 21.0.11+10 (portable; no system JDK installed) |
| Maven version | PASS | Apache Maven 3.9.9 via `mvnw.cmd -version` |
| Maven test | PASS | `mvnw.cmd test` (and `mvn test`): 29 tests run, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS |
| Maven verify | PASS | `mvnw.cmd clean verify` (and `mvn clean verify`): "All coverage checks have been met", BUILD SUCCESS |
| JUnit test count | 29 | DiscountPolicyTest: 13, PriceCalculatorTest: 16 (parameterized cases counted individually) |
| JaCoCo report | PASS | Generated at `target/site/jacoco/index.html` and `jacoco.csv` (local only, not committed) |
| Coverage threshold (gate) | PASS | Default `coverage.minimum=0.80` line-coverage check passed during `verify` |
| Actual coverage (checked classes, `Main` excluded) | Line 95.8% (23/24), Instruction 97.2% (103/106), Branch 100% (18/18) | From `target/site/jacoco/jacoco.csv` |
| Actual coverage (whole bundle incl. `Main`) | Line 76.7% (23/30) | `Main` is a demo entry point excluded from the gate |

Note: line coverage of the checked classes is deliberately not 100%. The trivial accessor `DiscountPolicy.getPercentage()` has no dedicated test, because a getter-returns-constructor-argument test adds coverage without real verification, and the uncovered line keeps measured coverage below 100% so the negative gate test can fail meaningfully. See `docs/QUALITY_GATE.md`.

## Negative quality-gate validation

| Check | Expected | Result | Notes |
|---|---|---:|---|
| `mvnw.cmd verify -Dcoverage.minimum=0.99` | FAIL | PASS | Build failed as expected (exit code 1): "Rule violated for bundle testing-coverage-quality-gate: lines covered ratio is 0.95, but expected minimum is 0.99" |
| `mvn verify -Dcoverage.minimum=0.99` | FAIL | PASS | Same expected failure with standalone Maven (exit code 1) |

PASS here means the gate correctly failed: `mvn verify -Dcoverage.minimum=0.99` failed as expected because actual line coverage (95.8%) was below the temporary 99% threshold. The default threshold in `pom.xml` remains 80%.

## Maven Wrapper

| Check | Result | Notes |
|---|---:|---|
| Wrapper generation | PASS | `mvn -N wrapper:wrapper -Dmaven=3.9.9` created `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties` |
| Wrapper execution | PASS | `mvnw.cmd -version`, `mvnw.cmd test`, and `mvnw.cmd clean verify` all succeeded |

Note: the wrapper was generated on Windows. On Linux/macOS run `chmod +x mvnw` once if the executable bit was not preserved.

## CI validation

| Check | Result | Notes |
|---|---:|---|
| Workflow YAML syntax | PASS | `.github/workflows/quality.yml` parsed successfully with a YAML parser |
| GitHub Actions workflow | NOT RUN | Template only. It sits inside the project folder; GitHub discovers workflows only at the repository-level `.github/workflows` directory, and it was not executed in GitHub |

## Tools unavailable

- No system-wide JDK or Maven was installed on the validation machine; a portable JDK 21 and Maven 3.9.9 were downloaded solely for this validation. Anyone reproducing the results only needs a JDK 21 — the committed Maven Wrapper handles Maven.

## Known limitations

- Demo codebase only (two small business classes).
- No mutation testing.
- No integration tests.
- CI workflow is a template unless moved to the repository-level `.github/workflows` directory; GitHub Actions was not run.
- `target/` and coverage reports are generated locally and not committed.
