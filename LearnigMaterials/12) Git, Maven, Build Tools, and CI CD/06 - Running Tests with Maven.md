# Running Tests with Maven

## Learning goals

- Run unit tests with Maven.
- Understand the Surefire plugin.
- Know common test command patterns.

## Basic commands

```bash
mvn test
mvn clean test
```

With Maven Wrapper:

```bash
./mvnw clean test
```

Windows:

```powershell
.\mvnw.cmd clean test
```

## Surefire plugin

Maven uses the Surefire plugin to run unit tests.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
</plugin>
```

## Test naming

Common test class names:

- `CalculatorTest`
- `OrderServiceTest`
- `ProductRepositoryTest`

## Common mistakes

- Tests are not under `src/test/java`.
- Test class names do not match Maven's default patterns.
- JUnit dependency is missing.
- Tests depend on local files that CI does not have.

## Mini exercise

Create a `Calculator` class and a `CalculatorTest` class. Run `mvn test` and intentionally break one assertion to see the failure output.

## Quick summary

Maven test commands make automated testing repeatable locally and in CI.
