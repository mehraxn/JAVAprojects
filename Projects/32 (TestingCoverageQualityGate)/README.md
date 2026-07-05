# Testing Coverage Quality Gate

A small Java project that separates testable pricing logic from its demo entry point and shows how automated tests, coverage measurement, and human code-review checks can form a quality gate.

## Features

- Subtotal and percentage-discount calculations using `BigDecimal`
- Validation for quantity, prices, discounts, and required objects
- JUnit 5 tests for normal, boundary, invalid, and rounding cases
- JaCoCo line-coverage reporting and an 80% minimum gate
- Maven `verify` lifecycle configuration
- Manual GitHub Actions workflow template

## Main classes

- `PriceCalculator` calculates subtotals and discounted totals.
- `DiscountPolicy` validates and applies a percentage discount.
- `Main` demonstrates a simple calculation and is excluded from the coverage gate.
- `PriceCalculatorTest` and `DiscountPolicyTest` contain the prepared JUnit tests.

## Project structure

```text
src/main/java/testingcoveragequalitygate/
src/test/java/testingcoveragequalitygate/
pom.xml
configs/quality-gate.properties
.github/workflows/quality.yml
docs/QUALITY_GATE.md
README.md
TESTING.md
```

## Quality gate

`mvn verify` is configured to compile the project, run JUnit tests, create the JaCoCo report, and fail if covered lines fall below 80%. The policy also requires manual review for duplicated logic, hardcoded secrets, and readable names.

The 80% value is a configured target, not a measured result from this implementation session.

## Example commands

These show the intended Maven workflow; they were not run during implementation.

```text
mvn test
mvn verify
```

If executed successfully, the HTML coverage report would be written to `target/site/jacoco/index.html`.

## Workflow location limitation

The workflow remains inside project 32 to respect the requested project boundary. GitHub Actions only discovers workflows in the repository-level `.github/workflows` directory, so this template must be reviewed and moved there before it can be triggered.

## Limitations

- Java, Maven, JUnit, JaCoCo, and the workflow were not executed.
- Maven must download the declared plugins and JUnit dependency when it is eventually run.
- Coverage indicates which lines ran; it does not prove that assertions or requirements are complete.
- No badge, passing build, or achieved coverage percentage is claimed.

## Possible improvements

- Add branch-coverage requirements after the line gate is established.
- Add mutation testing only if the added complexity is justified.
- Enable pull-request triggers after the workflow is placed and validated.
- Record agreed exclusions and threshold changes through code review.
