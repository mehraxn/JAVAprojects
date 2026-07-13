# Maven Dependency Scopes

## Learning goals

- Understand Maven dependency scopes.
- Choose the correct scope for common Java libraries.
- Avoid packaging test-only or container-provided libraries incorrectly.

## Why scopes matter

A dependency scope tells Maven when a library is needed: during compilation, tests, runtime, or only in a specific environment. Choosing the wrong scope can make builds larger, hide missing dependencies, or break CI.

## Common scopes

| Scope | Meaning | Example |
|---|---|---|
| `compile` | Needed to compile and run main code | A utility library used by application classes |
| `test` | Needed only for tests | JUnit |
| `runtime` | Needed when running, not compiling | Some database drivers |
| `provided` | Provided by the runtime environment | Servlet API in some web containers |
| `import` | Used with dependency management BOMs | A managed set of dependency versions |

## JUnit example

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>
```

JUnit should not be required by main application code.

## Runtime driver example

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.2.224</version>
    <scope>runtime</scope>
</dependency>
```

Code may compile against JDBC interfaces while the driver is used at runtime.

## Common mistakes

- Putting JUnit in compile scope.
- Marking a library as provided when no runtime provides it.
- Forgetting that test scope is not available to main code.
- Adding dependencies without checking whether they are actually used.

## Mini exercises

1. Choose a scope for JUnit.
2. Choose a scope for a development database driver.
3. Explain when `provided` would be appropriate.

## Quick summary

Scopes keep dependencies available only where they belong.
