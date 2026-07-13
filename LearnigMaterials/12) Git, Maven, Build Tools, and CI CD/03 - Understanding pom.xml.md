# Understanding pom.xml

## Learning goals

- Understand the main parts of a Maven `pom.xml`.
- Learn `groupId`, `artifactId`, `version`, properties, dependencies, and plugins.

## Minimal example

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>order-app</artifactId>
    <version>1.0.0</version>

    <properties>
        <maven.compiler.release>21</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
</project>
```

## Key elements

| Element | Meaning |
|---|---|
| `groupId` | Organization or package-style owner |
| `artifactId` | Build artifact name |
| `version` | Current version |
| `properties` | Shared configuration values |
| `dependencies` | External libraries |
| `build/plugins` | Build tool configuration |

## Common mistakes

- Copying a `pom.xml` without understanding it.
- Using inconsistent Java versions.
- Forgetting source encoding.
- Adding dependencies that are not actually used.

## Mini exercise

Create a `pom.xml` for a `library-app` using Java 21 and UTF-8 source encoding.

## Quick summary

The `pom.xml` is the build definition. It tells Maven how to compile, test, package, and manage dependencies.
