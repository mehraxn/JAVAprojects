# Quality Gate Design

The automated part of this example runs through Maven's `verify` phase:

1. Compile the main and test sources.
2. Run the JUnit 5 tests.
3. Generate a JaCoCo line-coverage report.
4. Fail verification when bundle line coverage is below 80%.

`Main` is excluded because it is a console demonstration rather than business logic. Any future exclusion should have a documented reason.

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

The gate is configured but was not run. The project does not claim that tests pass or that the 80% threshold is currently achieved.
