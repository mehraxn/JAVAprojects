# CI Pipeline Java App

## Description

A small dependency-free Java application prepared with a GitHub Actions workflow that makes compilation, testing, packaging, and artifact handling visible as separate continuous-integration steps.

## Goal

The goal is to learn what a CI pipeline actually performs rather than hiding every operation behind a large build framework. A greeting service provides simple validated business logic, and a dependency-free test runner supplies a clear pass/fail process exit status.

## Technologies and concepts used

- Java 21 source and package structure
- Input validation and focused service logic
- Separate application and test output directories
- `javac`, `java`, and `jar` command concepts
- GitHub Actions checkout and Java setup actions
- Build artifacts and failure-driven pipeline control

## Project structure

```text
src/cipipelinejavaapp/           Application source
test/cipipelinejavaapp/          Dependency-free test runner
.github/workflows/ci.yml         CI workflow template
docs/PIPELINE.md                 Pipeline design explanation
.gitignore                       Generated-output exclusions
README.md                        Project documentation
TESTING.md                       Validation guide
```

## Important files explained

- `GreetingService.java` contains the testable greeting and validation logic.
- `Main.java` provides a minimal console demonstration.
- `GreetingServiceTest.java` performs normal and invalid-input checks without an external test library.
- `.github/workflows/ci.yml` defines checkout, Java setup, application compilation, test compilation, test execution, JAR packaging, and artifact upload.
- `docs/PIPELINE.md` explains trigger and repository-placement decisions.

## Intended real-environment workflow

A developer would first compile application classes, compile tests against those classes, run the test runner, and package only application classes into an executable JAR. In CI, the same stages would run on a clean hosted runner, and any failed compilation or test would stop later stages.

GitHub discovers workflows only in the repository-level `.github/workflows` directory. The workflow remains inside project 31 to respect project isolation, so it must be reviewed and moved to repository scope before GitHub Actions can discover it.

## Prepared but not executed

- Java source, test source, workflow stages, packaging commands, and artifact configuration were prepared.
- The workflow uses a manual trigger and contains no fake status badge.
- Java compilation, tests, JAR creation, artifact upload, and GitHub Actions execution were not performed.
- No passing pipeline or working artifact is claimed.

## Manual validation checklist

- [ ] Confirm source and package paths agree.
- [ ] Confirm tests compile into `test-out`, separate from application classes.
- [ ] Confirm the test class exits non-zero on an assertion failure.
- [ ] Confirm packaging includes application classes but excludes tests.
- [ ] Review action versions and Java distribution/version.
- [ ] Move the workflow only after repository-level approval.
- [ ] Trigger manually before adding push or pull-request events.

## Common mistakes avoided

- Test classes are not packaged into the application JAR.
- CI stages are explicit and ordered.
- The workflow does not claim it ran successfully.
- No fake badge or generated artifact is committed.
- The nested-workflow discovery limitation is documented.
- Linux and Windows classpath separators are not treated as interchangeable.

## Possible future improvements

- Add more service behavior and test cases.
- Add pull-request triggers after the workflow is enabled and verified.
- Publish checksums with retained artifacts.
- Add a test framework only when its value outweighs the added build complexity.
- Record pipeline permissions and artifact-retention policy explicitly.
