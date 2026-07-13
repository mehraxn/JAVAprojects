# Mini Exercises

## 1. Create a Maven application

Create a Maven project with:

- `src/main/java`
- `src/test/java`
- `pom.xml`
- Java 21 compiler release

## 2. Add JUnit

Add JUnit Jupiter as a test dependency and write one test for a `Calculator` class.

## 3. Configure Maven Wrapper

Add Maven Wrapper and run:

```bash
./mvnw clean test
```

Windows:

```powershell
.\mvnw.cmd clean test
```

## 4. Add GitHub Actions

Create a workflow that:

- checks out the repository;
- sets up Java 21;
- runs Maven tests.

## 5. Fix a build problem

Intentionally configure the wrong Java release, observe the error, then fix the configuration.

## 6. Clean Git history

Add `target/` to `.gitignore`, run a build, and confirm `git status` does not show generated files.

## Quick summary

Build-tool exercises are about making compile and test steps repeatable on every machine.
