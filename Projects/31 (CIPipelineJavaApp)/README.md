# CI Pipeline Java App

A small dependency-free Java application designed to demonstrate a clear continuous-integration pipeline without hiding the individual compilation, test, and packaging stages behind a build framework.

## Features

- Simple greeting business logic
- Input validation and trimming
- Separate application and test source trees
- Dependency-free test runner using assertions
- GitHub Actions workflow template
- Executable JAR packaging and artifact upload stages

## Project structure

```text
src/cipipelinejavaapp/
  GreetingService.java
  Main.java
test/cipipelinejavaapp/
  GreetingServiceTest.java
.github/workflows/ci.yml
.gitignore
docs/PIPELINE.md
README.md
TESTING.md
```

## Pipeline stages

| Stage | Purpose |
|---|---|
| Checkout | Makes repository files available to the runner |
| Set up Java | Selects Temurin JDK 21 |
| Compile | Compiles application and test classes with `javac` |
| Test | Runs `GreetingServiceTest` |
| Package | Creates an executable JAR and uploads the artifact |

The workflow is manual-only and was not executed.

## Example local commands

These commands document the expected process; they were not run during implementation.

```text
javac -d out src/cipipelinejavaapp/*.java
javac -cp out -d test-out test/cipipelinejavaapp/*.java
java -cp "out;test-out" cipipelinejavaapp.GreetingServiceTest
jar --create --file dist/ci-pipeline-java-app.jar --main-class cipipelinejavaapp.Main -C out cipipelinejavaapp
```

The example classpath uses the Windows separator (`;`). The CI workflow uses the Linux separator (`:`).

## Workflow location limitation

The workflow is stored inside this project because modifications are restricted to project 31. GitHub will not automatically discover a nested workflow. It must be reviewed and moved to the repository-level `.github/workflows` directory before it can be triggered.

## Limitations

- Java, tests, packaging, and CI were not executed.
- The test runner is intentionally small and does not use JUnit.
- There is no coverage report or quality gate in this project; those belong to project 32.
- No badge or successful build status is claimed.

## Possible future improvements

- Add push and pull-request triggers after enabling the workflow.
- Add more business cases and tests.
- Add artifact checksums and a release policy.
- Add caching only if a build tool is introduced.
