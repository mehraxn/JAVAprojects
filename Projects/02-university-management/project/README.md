# University Management System

A compact Java OOP application for managing a university's students, courses, registrations, exams, averages, and merit ranking. The project preserves the original educational API while adding validation, deterministic behavior, automated tests, and reproducible Maven tooling.

## Features

- University identity and rector management.
- Progressive student enrollment beginning at ID `10000`.
- Progressive course activation beginning at code `10`.
- Bidirectional course registration and study plans.
- Duplicate-safe registration.
- Exam recording with grades from 0 through 30.
- One updatable grade per student and course.
- Student and course grade averages.
- Top-three ranking with attendance-completion bonus.
- Activity logging for enrollment, activation, registration, and exams.
- Professor/base tests plus custom edge-case tests.

## Technology and requirements

- Java 21
- Maven 3.9.x or the included Maven Wrapper
- JUnit 4, JUnit Jupiter, and the Vintage engine
- JaCoCo for non-gating coverage reports

The original course layout is retained: production code is under `src/`, supplied tests are under `test/example` and `test/it`, and added tests are under `test/custom`.

## Build and test

Linux/macOS:

```bash
./mvnw clean test
bash scripts/test.sh
```

Windows PowerShell:

```powershell
.\mvnw.cmd clean test
.\scripts\test.ps1
```

With a global Maven installation, run `mvn clean test`. Generate coverage with `./mvnw clean test jacoco:report`; the HTML report is written to `target/site/jacoco/index.html`.

## Typical workflow

```java
University university = new University("Politecnico di Torino");
university.setRector("Guido", "Saracco");

int student = university.enroll("Mario", "Rossi");
int course = university.activate("Object Oriented Programming", "James Gosling");

university.register(student, course);
university.exam(student, course, 28);

System.out.println(university.studentAvg(student));
System.out.println(university.topThreeStudents());
```

Registration updates both attendees and study plans. Repeating a registration is idempotent. An exam requires registration; recording the same exam again updates its grade instead of creating duplicate average entries.

## Architecture and validation

`University` is the public facade. Compact private `Student` and `Course` models retain the assignment's fixed capacities while checked operations prevent array overflow. Required names are trimmed and validated, IDs and course codes are checked for state-changing operations, and public lookup formats remain professor-compatible.

See [Architecture](docs/ARCHITECTURE.md), [Design Decisions](docs/DESIGN_DECISIONS.md), and [Testing](docs/TESTING.md).

## Logging

The public `University.logger` uses the original logger name `University`. Successful enrollment, activation, first registration, and exam recording generate informative log messages without using debug output in the domain implementation.

## Known limitations

- Educational, in-memory model with fixed capacities.
- Linear lookup is used at the assignment's small scale.
- No database, REST API, frontend, concurrency, or deployment layer.

## Resume value

Built and validated a Java university-management system with student enrollment, course activation, registrations, exam recording, grade averages, top-student ranking, logging, automated tests, and clean Maven project documentation.
