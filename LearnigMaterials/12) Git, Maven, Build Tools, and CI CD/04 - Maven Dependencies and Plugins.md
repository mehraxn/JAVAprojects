# Maven Dependencies and Plugins

## Learning goals

- Understand Maven dependencies.
- Understand Maven plugins.
- Know the difference between using a library and configuring the build.

## Dependencies

A dependency is a library your code or tests use.

```xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

`test` scope means the dependency is used for tests, not for main application code.

## Plugins

A plugin changes what Maven does during the build.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.13.0</version>
            <configuration>
                <release>21</release>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## Maven lifecycle

Common phases:

- `validate`
- `compile`
- `test`
- `package`
- `clean`

Examples:

```bash
mvn clean test
mvn clean package
```

## Common mistakes

- Adding a plugin when a dependency is needed.
- Adding a dependency when a plugin is needed.
- Leaving old dependency versions forever.
- Using dependencies in main code that are marked as test-only.

## Mini exercise

Add JUnit as a test dependency and configure the compiler plugin for Java 21.

## Quick summary

Dependencies are libraries. Plugins configure Maven behavior.
