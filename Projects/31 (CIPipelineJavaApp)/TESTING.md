# Testing CI Pipeline Java App

Nothing described here was executed during implementation.

## Manual Java checks

| Test | Input | Expected result |
|---|---|---|
| Normal greeting | `Java` | `Hello, Java!` |
| Trimmed greeting | `  CI  ` | `Hello, CI!` |
| Null name | `null` | `IllegalArgumentException` |
| Blank name | whitespace | `IllegalArgumentException` |
| Oversized name | 81 characters | `IllegalArgumentException` |

## Manual build checklist

- [ ] Compile application classes into `out`.
- [ ] Compile test classes with `out` on the classpath.
- [ ] Run `GreetingServiceTest` and confirm a zero exit status.
- [ ] Run `Main` with and without a name argument.
- [ ] Package the application JAR.
- [ ] Execute the JAR and verify its main class.

## CI configuration checks

- [ ] Confirm checkout and Java setup action versions.
- [ ] Confirm every working directory points to project 31.
- [ ] Confirm compile, test, and package stages are separate.
- [ ] Confirm failed tests prevent packaging.
- [ ] Confirm artifact upload requires the JAR to exist.
- [ ] Move the workflow to repository scope only after approval.
- [ ] Trigger it manually before adding automatic triggers.

## Current limitations

- Java was not installed or run.
- The test runner was not executed.
- The JAR was not created.
- The GitHub Actions workflow was not enabled or run.
