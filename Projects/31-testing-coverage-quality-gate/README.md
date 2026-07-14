# Testing Coverage Quality Gate

## Description

A Java testing and coverage quality-gate lab: small, deterministic business logic covered by JUnit 5 tests, with Maven Surefire test execution, JaCoCo coverage reporting, and an enforced coverage threshold that fails the build when it is not met.

## What it demonstrates

- JUnit 5 unit testing, including parameterized tests
- The Maven test lifecycle (`test` and `verify`)
- Maven Surefire test execution
- JaCoCo coverage report generation
- JaCoCo coverage enforcement (`jacoco:check` bound to `verify`)
- A positive quality gate: the default 80% line-coverage threshold passes
- A negative quality-gate validation: a temporarily stricter threshold fails the build on purpose
- A GitHub Actions workflow template for the same gate in CI

## What is implemented

- Java 21 business logic (`PriceCalculator`, `DiscountPolicy`) using `BigDecimal` money arithmetic
- 29 JUnit 5 tests covering normal calculations, invalid input, boundaries, zero values, null handling, and rounding
- Maven build with a configurable coverage threshold: `<coverage.minimum>0.80</coverage.minimum>`
- Maven Wrapper (`mvnw` / `mvnw.cmd`), so no local Maven installation is required
- CI workflow template at `.github/workflows/quality.yml`

## Project structure

```text
src/main/java/testingcoveragequalitygate/   Application source
src/test/java/testingcoveragequalitygate/   JUnit 5 tests
pom.xml                                     Maven, JUnit, Surefire, and JaCoCo configuration
mvnw, mvnw.cmd, .mvn/wrapper/               Maven Wrapper
configs/quality-gate.properties             Human-readable policy mirror
.github/workflows/quality.yml               CI workflow template
docs/QUALITY_GATE.md                        Gate design and interpretation
TESTING.md                                  How to validate the project yourself
TEST_RESULTS.md                             Actual recorded validation results
```

## Important files explained

- `PriceCalculator.java` calculates subtotals and discounted totals with explicit HALF_UP rounding to two decimal places.
- `DiscountPolicy.java` validates and applies percentage discounts between 0 and 100.
- The test classes cover normal behavior, boundaries, invalid inputs, zero values, and rounding, several of them as parameterized tests.
- `pom.xml` binds JUnit execution, JaCoCo reporting, and the line-coverage check to `mvn verify`. The threshold is a property (`coverage.minimum`, default `0.80`) so it can be overridden per run.
- `Main` is excluded from the coverage check because it is only a console demo entry point.

## Quick start

```bash
./mvnw test      # compile and run the JUnit tests
./mvnw verify    # tests + JaCoCo report + 80% line-coverage gate
```

On Windows use `mvnw.cmd`. If you prefer a locally installed Maven, `mvn test` and `mvn verify` work the same way.

After `verify`, the coverage report is at `target/site/jacoco/index.html` (generated locally, not committed).

## Negative quality-gate validation

A quality gate is only trustworthy if it can also fail. To prove the gate actually blocks low coverage:

```bash
./mvnw verify -Dcoverage.minimum=0.99
```

This is expected to FAIL, because actual line coverage of the checked classes is below 99%. For this negative test, a build failure is the successful outcome. The default threshold in `pom.xml` remains 80%. See `TESTING.md` for details.

## CI workflow template

This workflow is included as a template. To activate it in a portfolio repository, copy or move it to the repository-level `.github/workflows` directory and adjust `working-directory` paths to match the real repo layout. In this repository the project lives at `Projects/31-testing-coverage-quality-gate`, and the template already uses that path. GitHub Actions has not been executed for this project; see `TEST_RESULTS.md`.

## What is not production-grade

- Small demo codebase with two business classes.
- The CI workflow must be moved to the repository-level `.github/workflows` directory before it can run in a portfolio monorepo.
- No mutation testing.
- No integration tests.
- No external services, databases, or network calls.
- An 80% line threshold is a learning-project policy, not a universal quality standard; see `docs/QUALITY_GATE.md`.

## How to validate

- `TESTING.md` — exact commands to run the tests, the gate, and the negative gate check yourself.
- `TEST_RESULTS.md` — the honest record of what was actually executed and the real coverage numbers.

## Resume Value

Implemented a Maven/JUnit quality-gate project with JaCoCo reporting, an enforced coverage threshold, negative gate validation, and a reusable GitHub Actions workflow template.

## Possible future improvements

- Add a branch-coverage limit next to the line-coverage limit.
- Add mutation testing (for example PIT) once the extra complexity is justified.
- Enable pull-request checks after moving the workflow to the repository level.
