# Maven and CI Exercises

## Exercise 1: Create a Maven application

Difficulty: Easy

## Goal

Create a standard Maven folder structure.

## Background

Maven expects source and tests in predictable locations.

## Starter Code

```text
pom.xml
src/main/java
src/test/java
```

## Requirements

- Add `src/main/resources`.
- Add `src/test/resources`.
- Add Java package `com.example.app`.

## Expected Behavior

`mvn test` should compile source and tests.

## Test Cases

- Main class compiles.
- Test class compiles.
- Resources are in correct folders.

## Hints

Follow Maven conventions exactly.

## Common Mistakes

- Putting tests under `src/main/java`.
- Committing `target`.

## Bonus Challenge

Add a README with build commands.

## Solution Outline

Create standard folders and a minimal `pom.xml`.

## Exercise 2: Add JUnit dependency

Difficulty: Easy

## Goal

Run unit tests with Maven.

## Background

JUnit should be a test-scoped dependency.

## Starter Code

```xml
<dependencies>
</dependencies>
```

## Requirements

- Add JUnit Jupiter.
- Use test scope.
- Add one test class.

## Expected Behavior

`mvn test` runs the test.

## Test Cases

- Passing assertion.
- Failing assertion shows useful output.

## Hints

Use Surefire if needed.

## Common Mistakes

- Adding JUnit to compile scope.
- Wrong test class name.

## Bonus Challenge

Add parameterized test dependency if desired.

## Solution Outline

Add dependency, write test, run Maven.

## Exercise 3: Configure compiler release

Difficulty: Easy

## Goal

Set Java release explicitly.

## Background

Local and CI builds should use the same Java version.

## Starter Code

```xml
<properties>
</properties>
```

## Requirements

- Add `maven.compiler.release`.
- Use Java 21 or your chosen version.
- Document the expected JDK.

## Expected Behavior

Compilation uses the configured release.

## Test Cases

- `mvn clean compile` passes.
- Wrong JDK produces understandable error.

## Hints

Check `java -version`.

## Common Mistakes

- Configuring release newer than installed JDK.
- CI using different Java version.

## Bonus Challenge

Add GitHub Actions Java version to match.

## Solution Outline

Set release property and align CI setup.

## Exercise 4: Add Maven Wrapper

Difficulty: Medium

## Goal

Make Maven version reproducible.

## Background

Wrapper files let users build without installing Maven globally.

## Starter Code

```bash
mvn wrapper:wrapper
```

## Requirements

- Add wrapper files.
- Run wrapper command.
- Document Windows and Unix commands.

## Expected Behavior

`./mvnw test` or `.\mvnw.cmd test` works.

## Test Cases

- Wrapper runs tests.
- Wrapper files are committed.

## Hints

On Unix-like systems, executable permission may matter.

## Common Mistakes

- Forgetting `.mvn/wrapper`.
- Using wrapper in docs but not committing it.

## Bonus Challenge

Pin a Maven wrapper version.

## Solution Outline

Generate wrapper, commit files, update README.

## Exercise 5: Add JaCoCo

Difficulty: Medium

## Goal

Generate coverage reports.

## Background

Coverage shows which code tests execute.

## Starter Code

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
</plugin>
```

## Requirements

- Add JaCoCo plugin.
- Run tests.
- Generate report.

## Expected Behavior

Coverage report appears under `target/site/jacoco`.

## Test Cases

- Report generated.
- Line coverage visible.
- Branch coverage considered.

## Hints

Use `prepare-agent` and `report` goals.

## Common Mistakes

- Chasing 100% coverage without meaningful assertions.
- Ignoring edge cases.

## Bonus Challenge

Add a minimum coverage check.

## Solution Outline

Add plugin executions and inspect generated report.

## Exercise 6: Add GitHub Actions

Difficulty: Medium

## Goal

Run tests automatically in CI.

## Background

CI verifies the build outside your local machine.

## Starter Code

```yaml
name: Java CI
```

## Requirements

- Check out source.
- Set up Java.
- Run Maven tests.
- Upload test reports on failure.

## Expected Behavior

Pushes and pull requests run the workflow.

## Test Cases

- CI passes for green tests.
- CI fails for broken tests.
- Reports are uploaded.

## Hints

Use `actions/setup-java`.

## Common Mistakes

- YAML indentation errors.
- CI Java version differs from Maven release.

## Bonus Challenge

Add a build status badge.

## Solution Outline

Create `.github/workflows/java-ci.yml` with checkout, setup Java, test, and artifact steps.

## Exercise 7: Separate unit and integration tests

Difficulty: Hard

## Goal

Use Surefire and Failsafe appropriately.

## Background

Unit tests should be fast. Integration tests may use a database or file setup.

## Starter Code

```text
ProductServiceTest
ProductRepositoryIT
```

## Requirements

- Unit tests end with `Test`.
- Integration tests end with `IT`.
- Configure Failsafe.

## Expected Behavior

`mvn test` runs unit tests; `mvn verify` runs integration tests.

## Test Cases

- Unit test runs in test phase.
- Integration test runs in verify phase.

## Hints

Check plugin naming conventions.

## Common Mistakes

- Naming slow integration tests `*Test`.
- Skipping integration tests in CI.

## Bonus Challenge

Activate integration tests with a profile.

## Solution Outline

Configure Surefire and Failsafe with clear naming.

## Exercise 8: Fix broken Maven build from error output

Difficulty: Medium

## Goal

Learn to read Maven failures.

## Background

The first real error usually identifies the category.

## Starter Code

```text
[ERROR] COMPILATION ERROR
[ERROR] cannot find symbol
```

## Requirements

- Classify error type.
- Find first real cause.
- Fix one issue at a time.

## Expected Behavior

Build passes after the root cause is fixed.

## Test Cases

- Dependency resolution failure.
- Compilation failure.
- Test failure.
- Plugin failure.

## Hints

Do not read only the last line.

## Common Mistakes

- Changing random files.
- Ignoring Java version mismatch.

## Bonus Challenge

Write a troubleshooting checklist.

## Solution Outline

Identify phase, read first cause, fix, rerun.
