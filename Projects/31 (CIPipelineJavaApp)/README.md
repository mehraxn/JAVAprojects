# CI Pipeline Java App

## Description

A Java CI pipeline starter that demonstrates the compile, test, package, and artifact stages of continuous integration without hiding them behind a build framework. A small greeting service provides validated business logic, and a dependency-free test runner supplies a clear pass/fail process exit status that CI can act on.

## What it demonstrates

- `javac` compilation with separate output directories for application and test classes
- A dependency-free test runner that exits 0 on success and 1 on the first failed check
- Test separation: test classes are never packaged into the final JAR
- Executable JAR packaging with `jar --create --main-class`
- A JAR smoke test as a pipeline stage
- A GitHub Actions workflow template with an artifact-upload step
- A local validation workflow you can run with nothing but a JDK

## What is implemented

- Java 21 application source (`GreetingService`, `Main`)
- Java test source (`GreetingServiceTest`, plain `main`-method runner, 7 checks)
- Local build commands documented in `TESTING.md`
- CI workflow YAML template at `.github/workflows/ci.yml`
- Recorded validation evidence in `TEST_RESULTS.md`

## Project structure

```text
src/cipipelinejavaapp/           Application source
test/cipipelinejavaapp/          Dependency-free test runner
.github/workflows/ci.yml         CI workflow template
docs/PIPELINE.md                 Pipeline design explanation
.gitignore                       Generated-output exclusions
README.md                        Project documentation
TESTING.md                       Validation guide
TEST_RESULTS.md                  Actual recorded validation results
```

## Important files explained

- `GreetingService.java` contains the testable greeting logic: it trims input, rejects null/blank names, and enforces an 80-character limit.
- `Main.java` is a minimal console entry point (greets `CI learner` by default, or the first argument).
- `GreetingServiceTest.java` checks normal, trimmed, boundary (80 vs 81 characters), null, empty, and blank inputs. It prints `All tests passed (7 checks).` and exits 0 on success, or prints the failed check and exits 1.
- `.github/workflows/ci.yml` defines checkout, JDK setup, application compilation, test compilation, test execution, JAR packaging, a JAR smoke test, and artifact upload.
- `docs/PIPELINE.md` explains the stage ordering and repository-placement decisions.

## Quick start

From the project root (JDK 21 required, no build tool needed):

```bash
javac -d out src/cipipelinejavaapp/*.java
javac -cp out -d test-out test/cipipelinejavaapp/*.java
java -cp "out:test-out" cipipelinejavaapp.GreetingServiceTest   # Windows: "out;test-out"
```

See `TESTING.md` for the full pipeline including packaging and the smoke test.

## CI workflow template

The workflow file is included as a template. GitHub discovers workflows only in the repository-level `.github/workflows` directory, so to activate it in a portfolio repository, copy or move it there and adjust the `working-directory` and artifact paths to the real repo layout. In this repository the project lives at `Projects/31 (CIPipelineJavaApp)`, and the template already uses that path.

GitHub Actions has not been executed for this project, so no pipeline run or artifact upload is claimed; see `TEST_RESULTS.md` for what was actually validated locally.

## What is not production-grade

- No Maven/Gradle build system — the point is to see the raw pipeline stages.
- No external dependencies and no test framework.
- No production deployment.
- The GitHub Actions workflow does not run unless activated at the repository level.
- Artifact upload is unproven until the workflow actually runs in GitHub.

## How to validate

- `TESTING.md` — exact commands for each pipeline stage, for both Windows and Linux/macOS.
- `TEST_RESULTS.md` — the honest record of what was actually executed and the results.

## Possible future improvements

- Add more service behavior and test cases.
- Publish checksums with retained artifacts.
- Add a test framework or build tool only when its value outweighs the added complexity.
- Enable pull-request checks after the workflow is moved to repository scope.
