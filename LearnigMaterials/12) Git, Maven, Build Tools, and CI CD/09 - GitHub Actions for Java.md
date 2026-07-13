# GitHub Actions for Java

## Learning goals

- Understand a basic GitHub Actions Java workflow.
- Run Maven tests automatically.
- Know what each YAML section means.

## Example workflow

Create:

```text
.github/workflows/java-ci.yml
```

Example:

```yaml
name: Java CI

on:
  push:
  pull_request:

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Check out source
        uses: actions/checkout@v4

      - name: Set up Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'

      - name: Run tests
        run: mvn clean test
```

## What the workflow does

- Runs on pushes and pull requests.
- Uses an Ubuntu runner.
- Checks out the repository.
- Installs Java 21.
- Runs Maven tests.

## Maven Wrapper version

If the repository has Maven Wrapper:

```yaml
- name: Run tests
  run: ./mvnw clean test
```

## Common mistakes

- YAML indentation errors.
- Java version mismatch.
- Using `mvn` when only wrapper files are intended.
- Tests depend on files not present in the repository.

## Mini exercise

Create a GitHub Actions workflow that runs `mvn clean test` on every push.

## Quick summary

GitHub Actions can compile and test Java code automatically with a small YAML file.
