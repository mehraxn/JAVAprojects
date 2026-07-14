# University Management System

## Overview

This educational Java application manages university identity, students, courses, registrations, exams, averages, ranking, and activity logging. The canonical Maven project is located in [`project/`](project); [`Raw File/`](Raw%20File) preserves the original course source and is not the implementation to build.

## Features

- University and rector details.
- Student enrollment and course activation with generated identifiers.
- Bidirectional course registration and study plans.
- Exam recording, student/course averages, and top-three ranking.
- Informative logging for important operations.
- Supplied professor tests and additional validation/edge-case tests.

## What This Project Demonstrates

- Object-oriented modeling behind a public facade.
- Fixed-array management, validation, and deterministic identifiers.
- Registration and exam business rules.
- Aggregation, averages, ranking, and logging.
- Maven-based automated testing while preserving the original educational API.

## Tech Stack

- Java 21.
- Maven and Maven Wrapper.
- JUnit 4, JUnit Jupiter, and the Vintage engine.
- JaCoCo for optional, non-gating local coverage reports.

## Project Structure

```text
.
├── project/                   # Canonical implementation and Maven build
│   ├── src/
│   ├── test/
│   ├── docs/
│   ├── scripts/
│   ├── pom.xml
│   └── TEST_RESULTS.md
├── Raw File/                  # Preserved original course source
└── ImplementationExplanation.md
```

## How to Run

Enter the canonical project first:

```bash
cd project
./mvnw clean test
```

Windows PowerShell:

```powershell
cd project
.\mvnw.cmd clean test
```

With a global Maven installation, use `mvn clean test`. See the [canonical README](project/README.md) for the example workflow and coverage command.

## Testing

Automated tests are under `project/test/`, with supplied tests and custom edge-case coverage. See [`project/TEST_RESULTS.md`](project/TEST_RESULTS.md) for the latest recorded validation results and [`project/docs/TESTING.md`](project/docs/TESTING.md) for the test layout.

## Known Limitations

- Educational, in-memory model with fixed capacities and linear lookup.
- No database, REST API, frontend, concurrency, or deployment layer.
- The nested layout and raw source are intentionally retained for course-history preservation.

## Resume Value

Built a Java university-management system supporting enrollment, course activation, registrations, exams, averages, ranking, logging, validation, and automated tests.
