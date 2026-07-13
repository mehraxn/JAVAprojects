# Maven and CI Exercises

## 1. Create a Maven application

Create the standard structure:

```text
src/main/java
src/main/resources
src/test/java
src/test/resources
```

Add a `pom.xml` with Java 21 compiler release.

## 2. Add JUnit

Add JUnit Jupiter and write one test for a `Calculator`.

## 3. Add Maven Wrapper

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

- checks out source;
- sets up Java 21;
- runs tests.

## 5. Fix common build issues

Practice fixing:

- wrong Java version;
- missing test dependency;
- generated `target` folder shown in `git status`;
- tests that pass locally but fail in CI because a file is missing.
