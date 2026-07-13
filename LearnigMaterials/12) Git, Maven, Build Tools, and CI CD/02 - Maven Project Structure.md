# Maven Project Structure

## Learning goals

- Understand the standard Maven folder layout.
- Know where Java source, tests, and resources belong.
- Recognize the generated `target` folder.

## Standard layout

```text
my-app/
  pom.xml
  src/
    main/
      java/
      resources/
    test/
      java/
      resources/
  target/
```

## Meaning of each folder

| Path | Purpose |
|---|---|
| `src/main/java` | Application source code |
| `src/main/resources` | Files packaged with the application |
| `src/test/java` | Test source code |
| `src/test/resources` | Files used by tests |
| `target` | Generated build output |

## Package path example

If the package is:

```java
package com.example.orders;
```

Then the file usually lives under:

```text
src/main/java/com/example/orders/
```

## Common mistakes

- Placing source directly beside `pom.xml`.
- Committing the `target` folder.
- Putting tests under `src/main/java`.
- Using a package name that does not match the folder path.

## Mini exercise

Create a Maven-style folder layout for a `bookstore` package and place one `Book` class and one `BookTest` class in the correct folders.

## Quick summary

Maven expects a standard layout. Following it makes builds, tests, and CI simpler.
