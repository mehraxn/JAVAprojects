# Testing Coverage Quality Gate

## Description

A focused Java project demonstrating testable business logic, JUnit tests, JaCoCo coverage measurement, and a Maven verification gate without claiming that coverage alone proves software quality.

## Goal

The goal is to show how automated tests, a measurable line-coverage threshold, and human review criteria can work together as a quality gate for a small Java codebase.

## Technologies and concepts used

- Java 21 and `BigDecimal` money calculations
- Small classes with validation and deterministic behavior
- JUnit 5 unit tests
- Maven build lifecycle
- JaCoCo report generation and line-coverage enforcement
- GitHub Actions workflow template
- Manual checks for naming, duplication, and secrets

## Project structure

```text
src/main/java/testingcoveragequalitygate/   Application source
src/test/java/testingcoveragequalitygate/   JUnit tests
pom.xml                                     Maven, JUnit, and JaCoCo configuration
configs/quality-gate.properties             Human-readable policy mirror
.github/workflows/quality.yml               Verification workflow template
docs/QUALITY_GATE.md                        Gate interpretation
README.md                                   Project documentation
TESTING.md                                  Validation guide
```

## Important files explained

- `PriceCalculator.java` calculates subtotals and discounted totals with explicit rounding.
- `DiscountPolicy.java` validates and applies percentage discounts.
- Test classes cover normal behavior, boundaries, invalid inputs, and rounding.
- `pom.xml` binds JUnit execution, JaCoCo reporting, and an 80% line-coverage check to Maven verification.
- `quality.yml` demonstrates automated `mvn verify` and report artifact handling.
- `docs/QUALITY_GATE.md` explains why coverage evidence still requires code review.

## Intended real-environment workflow

A developer would review dependencies, run unit tests, inspect failures, run Maven verification, and open the generated JaCoCo HTML report. The build should fail when tests fail or covered lines fall below the configured threshold. Reviewers would separately assess duplicated logic, hardcoded secrets, names, edge cases, and assertion quality.

The workflow is stored inside the project boundary. It must be reviewed and moved to the repository-level `.github/workflows` directory before GitHub can discover it.

## Prepared but not executed

- Maven, JUnit, JaCoCo, test cases, the 80% line threshold, and workflow steps were configured.
- Java, Maven, tests, coverage instrumentation, report generation, gate enforcement, and CI were not executed.
- The 80% value is a policy target, not a measured result.
- No badge, passing build, or achieved coverage percentage is claimed.

## Manual validation checklist

- [ ] Review every calculation and validation branch against a test.
- [ ] Confirm monetary assertions use the intended scale and rounding.
- [ ] Confirm `Main` is the only deliberate coverage exclusion.
- [ ] Inspect the JaCoCo report instead of relying only on the percentage.
- [ ] Confirm the gate fails below the configured threshold.
- [ ] Review names, duplicated logic, secrets, and assertion meaning manually.
- [ ] Enable CI only after moving and reviewing the workflow.

## Common mistakes avoided

- Coverage is not described as proof of correctness.
- The threshold is not presented as already achieved.
- Demo entry-point code is separated from tested business logic.
- Tests include invalid and boundary cases, not only happy paths.
- No fake report or success badge is included.
- Workflow placement limitations are explicit.

## Possible future improvements

- Add branch-coverage criteria after establishing stable line coverage.
- Add parameterized tests for broader boundary combinations.
- Add mutation testing only when the additional complexity is justified.
- Establish a reviewed policy for exclusions and threshold changes.
- Enable pull-request checks after the workflow is validated.
