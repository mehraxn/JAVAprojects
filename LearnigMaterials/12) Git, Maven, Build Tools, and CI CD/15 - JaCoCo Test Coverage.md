# JaCoCo Test Coverage

## Learning goals

- Understand test coverage.
- Learn line and branch coverage.
- Use JaCoCo without chasing meaningless percentages.

## What coverage means

Coverage measures which parts of code were executed by tests.

- Line coverage: which lines ran.
- Branch coverage: which decision paths ran.

High coverage can be useful, but 100% coverage does not automatically mean good tests.

## Maven plugin example

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Run:

```bash
mvn clean test
```

Then inspect the generated report under `target/site/jacoco`.

## Common mistakes

- Chasing coverage instead of meaningful assertions.
- Ignoring edge cases because coverage looks high.
- Testing getters only to increase numbers.
- Treating coverage as proof that code is correct.

## Mini exercises

1. Add JaCoCo to a Maven app.
2. Compare line coverage and branch coverage for an `if` statement.
3. Add a test for an exception path.

## Quick summary

Coverage is a useful signal, not a guarantee. Good tests assert behavior and edge cases.
