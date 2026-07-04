# Maven Basics for Java Projects - Complete Guide

## Overview
Maven is a build tool. It manages dependencies, compiles code, runs tests, and packages projects.

---

## 1. Standard Maven structure

```text
project/
  pom.xml
  src/
    main/
      java/
        com/example/App.java
      resources/
    test/
      java/
        com/example/AppTest.java
```

Important folders:

| Folder | Meaning |
|---|---|
| `src/main/java` | production Java code |
| `src/main/resources` | config/resources |
| `src/test/java` | test code |
| `target` | generated build output |

---

## 2. `pom.xml`

The `pom.xml` file describes the project.

Example:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>demo</artifactId>
    <version>1.0-SNAPSHOT</version>
</project>
```

---

## 2b. Project coordinates: groupId, artifactId, version

Every Maven project (and every dependency) is identified by three **coordinates**.
Together they uniquely name a library, like a postal address.

| Coordinate | Meaning | Example |
|---|---|---|
| `groupId` | who/what organization owns it (usually a reversed domain) | `com.example`, `org.junit.jupiter` |
| `artifactId` | the specific project/library name | `demo`, `junit-jupiter` |
| `version` | which release of it | `1.0-SNAPSHOT`, `5.10.0` |

Read together, `com.example : demo : 1.0-SNAPSHOT` means
"version 1.0-SNAPSHOT of the *demo* project made by *com.example*."

`-SNAPSHOT` marks a version that is still **in development** and may change. A version
without `-SNAPSHOT` (like `1.0.0`) is treated as a fixed, released version.

When you add a dependency, you are just naming **its** three coordinates so Maven can find
and download it.

---

## 3. Dependencies

A dependency is an external library.

Example JUnit dependency:

```xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

Maven downloads dependencies automatically.

### Dependency scope (main code vs test code)

The `<scope>` tag says **when** a dependency is needed:

| Scope | Available during | Typical use |
|---|---|---|
| `compile` (default) | main code, tests, and the final package | normal libraries your app uses |
| `test` | test code only | testing tools like JUnit |

A **test dependency** (`<scope>test</scope>`) is available only in `src/test/java` and is
**not** bundled into the final package. That is why JUnit uses `test` scope — your users
do not need your tests to run your program.

---

## 4. Common Maven commands

```bash
mvn compile   # compile main code
mvn test      # run tests
mvn package   # create jar/war package
mvn clean     # remove target folder
mvn clean test
```

---

## 5. Maven lifecycle simplified

```text
validate -> compile -> test -> package -> verify -> install -> deploy
```

When you run a later phase, Maven runs previous phases too.

Example:

```bash
mvn package
```

This also compiles and tests the project first.

---

## 6. `target` folder

Maven outputs generated files into `target/`.

Usually `target/` should be in `.gitignore`.

---

## 7. Maven and IDE

Most IDEs can import a Maven project by reading `pom.xml`. If dependencies are missing, refresh/reload the Maven project.

---

## Common mistakes

### Mistake 1: editing files in `target`
They are generated and can be deleted/recreated.

### Mistake 2: forgetting dependency scope
JUnit should usually have `test` scope.

### Mistake 3: putting code outside Maven folder structure
Maven expects standard folders unless configured otherwise.

---

## 8. Lifecycle phases explained (conceptually)

You do not run these by hand here — this is only to understand what each phase *means*.
Because phases are ordered, running a later phase automatically runs the earlier ones.

| Phase | What it does | What runs before it |
|---|---|---|
| `compile` | turns `src/main/java` `.java` files into `.class` files in `target/` | validate |
| `test` | compiles and runs the tests in `src/test/java` | validate, compile |
| `package` | bundles the compiled code into a `.jar` (or `.war`) | validate, compile, test |

So asking for `package` conceptually means: *"compile my code, run my tests, and if they
pass, build the jar."* If the tests fail, packaging stops.

---

## Exam Notes

- A Maven project is described by **`pom.xml`**.
- The three **coordinates** are `groupId`, `artifactId`, `version`.
- `src/main/java` = production code; `src/test/java` = test code; `target/` = generated output.
- A dependency = an external library named by its own coordinates.
- `test` scope = available only for tests, not shipped in the final package (e.g. JUnit).
- Lifecycle order: **compile → test → package**; a later phase runs the earlier ones first.

---

## Mini quiz

### Q1. What file defines a Maven project?
Answer: `pom.xml`.

### Q2. Where is production Java code placed?
Answer: `src/main/java`.

### Q3. What command runs tests?
Answer: `mvn test`.

---

## More Practice Questions

1. Name the three coordinates that uniquely identify a Maven project or dependency.

2. What does the `-SNAPSHOT` suffix on a version mean?

3. Why is JUnit usually declared with `<scope>test</scope>` instead of the default scope?

4. In the lifecycle, if you ask Maven to `package`, which earlier phases run first, and
   what happens if a test fails?

5. Which folder holds generated build output, and should it be committed to Git?
   (Answer: `target/`; no — put it in `.gitignore`.)

6. Where should production code and test code each be placed in the standard structure?

7. In your own words, what problem do dependency coordinates solve?
