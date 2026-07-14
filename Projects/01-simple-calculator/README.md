# Simple Calculator

## Overview

This introductory console calculator performs addition, subtraction, multiplication, and division. It also serves as a small Git workflow lab covering cloning, commits, pulls, rebasing, and conflict resolution.

## Features

- Menu-driven console interaction.
- Addition, subtraction, multiplication, and division for `double` values.
- Division-by-zero and invalid-operation error states.
- A reusable `Calculator.compute(...)` entry point.
- JUnit tests for each operation and error case.
- Supporting diagrams and notes for the original Git exercise.

## What This Project Demonstrates

- Basic Java methods, constants, control flow, and console input/output.
- Separating arithmetic operations from the interactive entry point.
- Simple error-state handling.
- Introductory JUnit assertions.
- Core Git workflows, including divergent histories, rebase, and conflict resolution.

## Tech Stack

- Java 25 as currently configured in `pom.xml`.
- Maven.
- JUnit Jupiter, with JUnit 4 and Vintage dependencies retained by the course build.

## Project Structure

```text
.
├── src/calc/Calculator.java
├── test/calc/TestCalculator.java
├── img/                       # Git workflow diagrams and UI images
├── pom.xml
├── CodeExplanation.md
└── README_it.md               # Original Italian lab instructions
```

## How to Run

The source uses the Java 25 `IO` API and the Maven compiler is configured for release 25. With a compatible JDK installed, run from this folder:

```bash
mvn compile
java -cp target/classes calc.Calculator
```

Running `calc.Calculator` directly from an IDE configured with JDK 25 is an alternative.

## Testing

The JUnit test class is located at `test/calc/TestCalculator.java`. With JDK 25 and Maven available, run:

```bash
mvn test
```

No `TEST_RESULTS.md` or retained Surefire report currently records a verified test run, so this README does not claim a passing result.

## Original Lab Focus

The original assignment used this calculator to practice cloning a repository, staging and committing changes, synchronizing remote changes, rebasing divergent commits, and resolving conflicts. The diagrams remain under `img/`, and the Italian lab handout remains in `README_it.md`.

## Known Limitations

- Educational command-line application with no persistence or graphical interface.
- Input parsing assumes numeric values and does not recover from malformed console input.
- Error state is stored in static mutable fields.
- The Java 25 build requirement may not work in environments standardized on Java 21.
- A generated `target/` directory is currently retained and should be reviewed in a later cleanup prompt.

## Resume Value

Built a Java console calculator with reusable arithmetic operations, explicit error handling, JUnit tests, and supporting Git rebase and conflict-resolution exercises.
