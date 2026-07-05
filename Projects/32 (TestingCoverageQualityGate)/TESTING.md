# Testing Testing Coverage Quality Gate

No commands, tests, coverage tools, or workflows were run during implementation.

## Prepared JUnit cases

| Area | Case | Expected result |
|---|---|---|
| Subtotal | 3 items at 10.00 | `30.00` |
| Discount | 10% off 100.00 | `90.00` |
| Boundaries | 0% and 100% discounts | Full subtotal and zero total |
| Rounding | 2 items at 1.668 | `3.34` using half-up rounding |
| Quantity validation | Zero quantity | `IllegalArgumentException` |
| Price validation | Negative or null price | `IllegalArgumentException` |
| Discount validation | Below 0, above 100, or null | `IllegalArgumentException` |
| Policy validation | Null policy | `IllegalArgumentException` |

## Manual Maven checklist

- [ ] Confirm a JDK 21 and Maven are available before running anything.
- [ ] Run `mvn test` and verify every JUnit test passes.
- [ ] Run `mvn verify` and verify the JaCoCo check executes.
- [ ] Open `target/site/jacoco/index.html` and inspect uncovered lines.
- [ ] Temporarily lower coverage in a review branch and confirm the gate fails below 80%.
- [ ] Confirm `Main` is the only intentional coverage exclusion.

## Quality review checklist

- [ ] All tests pass.
- [ ] Line coverage meets or exceeds 80%.
- [ ] No obvious duplicated logic exists.
- [ ] No hardcoded secrets exist.
- [ ] Classes, methods, variables, and tests use readable names.
- [ ] Assertions verify behavior rather than merely execute lines.
- [ ] Validation and boundary cases have tests.

## CI checklist

- [ ] Move the workflow to the repository-level workflow directory after approval.
- [ ] Confirm its Maven cache points to this project's `pom.xml`.
- [ ] Trigger the workflow manually.
- [ ] Verify test or coverage failure stops `mvn verify`.
- [ ] Verify the JaCoCo report artifact is retained when available.

## Current status

The test suite and gate are configured but unexecuted. No passing tests, successful workflow, generated report, or achieved coverage level is claimed.
