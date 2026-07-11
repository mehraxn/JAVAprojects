# Quality Gate Design

The automated part of this example runs through Maven's `verify` phase:

1. Compile the main and test sources.
2. Run the JUnit 5 tests.
3. Generate a JaCoCo line-coverage report.
4. Fail verification when bundle line coverage is below the configured minimum (`coverage.minimum`, default 80%).

`Main` is excluded because it is a console demonstration rather than business logic. Any future exclusion should have a documented reason.

## Deliberate coverage decisions

`DiscountPolicy.getPercentage()` intentionally has no dedicated test. A test that only asserts a getter returns its constructor argument inflates coverage without verifying real behavior — exactly the kind of test a coverage gate can tempt people to write. Leaving it uncovered keeps measured line coverage at 95.8% (23 of 24 checked lines), which:

- comfortably passes the default 80% gate, and
- allows the negative gate demonstration (`mvn verify -Dcoverage.minimum=0.99`) to fail for real, proving the gate actually blocks builds.

## Negative validation

A gate that can only pass proves nothing. Running `mvn verify -Dcoverage.minimum=0.99` (or `./mvnw verify -Dcoverage.minimum=0.99`) temporarily raises the threshold above actual coverage; the resulting BUILD FAILURE is the expected, successful outcome of that check. The default in `pom.xml` stays at 80%.

## Manual review criteria

Coverage cannot assess design quality on its own. A reviewer must also confirm:

- tests pass and contain meaningful assertions;
- there is no obvious duplicated logic;
- no credentials, tokens, or other secrets are hardcoded;
- names describe the intent of classes, methods, variables, and tests; and
- boundary and validation behavior is tested.

## Interpretation

An 80% line threshold is a learning-project policy, not a universal quality standard. High coverage can still accompany weak tests, while an uncovered defensive branch may have a valid explanation. The report is evidence for review, not a substitute for review.

## Honest status

The gate was executed locally on 2026-07-11: `mvn verify` passed with 29 tests and 95.8% line coverage of the checked classes, and the negative check `-Dcoverage.minimum=0.99` failed as expected. See `TEST_RESULTS.md` for the full record. GitHub Actions has not been run; the workflow remains a template.
