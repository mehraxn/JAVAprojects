# Testing the Testing Coverage Quality Gate project

This guide lists the exact commands used to validate the project. The results of actually running them are recorded in `TEST_RESULTS.md`.

## A) Prerequisites

A JDK 21 is required. The Maven Wrapper downloads Maven 3.9.9 automatically, so a local Maven installation is optional.

Check your tools:

```bash
java -version
./mvnw -version
```

Or with a locally installed Maven:

```bash
mvn -version
```

On Windows, use `mvnw.cmd` instead of `./mvnw` in all commands below. On Linux/macOS, if the wrapper is not executable after checkout, run `chmod +x mvnw` once.

## B) Run tests

```bash
./mvnw test
```

Fallback with local Maven:

```bash
mvn test
```

This compiles the sources and runs all JUnit 5 tests through Maven Surefire. Expected: `BUILD SUCCESS` with 29 tests passing.

## C) Run the full quality gate

```bash
./mvnw verify
```

Fallback:

```bash
mvn verify
```

This runs the tests, generates the JaCoCo coverage report, and enforces the line-coverage gate (default minimum: 80%, configured as `<coverage.minimum>0.80</coverage.minimum>` in `pom.xml`). Expected: `BUILD SUCCESS` with "All coverage checks have been met."

## D) View the JaCoCo report

After `verify`, open:

```text
target/site/jacoco/index.html
```

Machine-readable numbers are in `target/site/jacoco/jacoco.csv`. These files are generated locally and must not be committed (`target/` is in `.gitignore`).

## E) Run the negative coverage-gate test

```bash
./mvnw verify -Dcoverage.minimum=0.99
```

Fallback:

```bash
mvn verify -Dcoverage.minimum=0.99
```

Expected: this command should FAIL, because actual line coverage of the checked classes is below 99% (the trivial accessor `DiscountPolicy.getPercentage()` is deliberately left uncovered — see `docs/QUALITY_GATE.md`). Maven prints:

```text
Rule violated for bundle testing-coverage-quality-gate: lines covered ratio is 0.95, but expected minimum is 0.99
BUILD FAILURE
```

For this negative test, failure is the expected successful outcome: it proves the gate really blocks builds below the threshold. If you ever raise actual coverage above 99%, use `-Dcoverage.minimum=1.00` instead. The default threshold in `pom.xml` stays at 80%.

## F) CI workflow template

The workflow file `.github/workflows/quality.yml` is included under the project for reference. GitHub only discovers workflows in the repository-level `.github/workflows` directory, so to activate it, copy it there and adjust the `working-directory` and `cache-dependency-path` values to the real repo layout. It has not been executed in GitHub for this project.

## G) Cleanup

Remove generated build files before committing:

```bash
rm -rf target
```

On Windows PowerShell:

```powershell
Remove-Item -Recurse -Force target
```
