# Task Manager JDBC

## Overview

An educational Java task manager demonstrating repository-pattern architecture with two interchangeable implementations: a behavior-tested in-memory repository and a JDBC repository built entirely with standard `java.sql` APIs. No framework, database, driver, or external dependency is required to compile and exercise the in-memory workflow with plain `javac`/`java`.

## What This Project Demonstrates

- A validated `Task` domain model (final class) with a `Status` enum (`OPEN`, `IN_PROGRESS`, `DONE`)
- A `TaskRepository` interface with in-memory and JDBC implementations
- `PreparedStatement` for every dynamic SQL value (no string-concatenated SQL)
- Generated-key handling with `Statement.RETURN_GENERATED_KEYS` and validity checks
- try-with-resources for `Connection`, `PreparedStatement`, and `ResultSet`
- Service-layer validation independent of the storage implementation
- Status filtering and defensive copies/unmodifiable lists
- Dependency-free automated tests (custom assertion helper + test runner)
- Strict compilation with `-Xlint:all -Werror`

## Features

- Create tasks with a title, optional description, optional due date, and initial `OPEN` status
- List all tasks, update task details, change task status
- Filter tasks by status and delete tasks
- Command-based CLI demos of the in-memory stack and validation behavior
- A complete JDBC repository implementation for learning (see the JDBC note below)

## Main classes

- `Task` — validated, final task model with the `Status` enum.
- `TaskRepository` — common CRUD and query contract.
- `InMemoryTaskRepository` — `LinkedHashMap` storage with generated IDs and defensive copies.
- `JdbcTaskRepository` — JDBC implementation using `Connection`, `PreparedStatement`, and `ResultSet`.
- `DatabaseConnection` — validated JDBC configuration and `DriverManager` connection boundary.
- `TaskService` — application operations independent of repository implementation.
- `Main` — command-based CLI (`help`, `demo`, `in-memory-demo`, `validation-demo`, `jdbc-info`).

```text
TaskService -> TaskRepository -> InMemoryTaskRepository
                              -> JdbcTaskRepository -> DatabaseConnection
```

A task ID of `0` means "not yet saved" — the repository assigns the next sequential positive ID on `add`. Stored-task lookups require positive IDs.

## Tech Stack

- Java 21 standard library.
- JDBC API and prepared statements.
- Plain `javac`/`java`; no bundled database driver or build framework.
- Dependency-free tests plus Bash and PowerShell scripts.

## Architecture / Design

```text
CLI → TaskService → TaskRepository → InMemoryTaskRepository or JdbcTaskRepository → SQL database
```

The repository interface keeps workflows independent of storage. Automated behavior tests use the in-memory implementation; the JDBC repository accepts a caller-supplied `Connection` and uses prepared statements.

## Project Structure

```text
.
├── src/taskmanagerjdbc/     # Model, service, repository interface/implementations, CLI
├── tests/taskmanagerjdbc/   # In-memory behavior and JDBC static checks
├── scripts/                 # Cross-platform validation scripts
├── TESTING.md
└── TEST_RESULTS.md
```

## How to Run

```text
javac -Xlint:all -Werror -d out src/taskmanagerjdbc/*.java

java -cp out taskmanagerjdbc.Main help
java -cp out taskmanagerjdbc.Main demo
java -cp out taskmanagerjdbc.Main in-memory-demo
java -cp out taskmanagerjdbc.Main validation-demo
java -cp out taskmanagerjdbc.Main jdbc-info
```

Running with no command prints the usage text. `demo`/`in-memory-demo` walk through create → update details → change status → filter → delete using `InMemoryTaskRepository` (no database is contacted). `validation-demo` intentionally triggers validation failures and exits 0 because the rejections are the point. `jdbc-info` explains the JDBC repository without connecting to anything. `Main.run(args, out, err)` returns an exit code (0 for valid commands, non-zero for unknown ones) and only `main` calls `System.exit`.

## JDBC note

- `JdbcTaskRepository` is fully implemented using `java.sql` APIs and compiles without any driver.
- Running it against a real database requires a JDBC driver on the classpath, a database, a matching `tasks` table, and a valid JDBC URL — none of which are bundled.
- **Real database integration tests are not included.** The automated tests cover the in-memory repository, service layer, domain validation, CLI, `DatabaseConnection` failure handling, and a static safety review of the JDBC source (PreparedStatement usage, try-with-resources, generated-key checks).
- There are no hardcoded credentials anywhere in the project.

A representative schema (identity-column syntax varies by database product):

```sql
CREATE TABLE tasks (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    due_date DATE,
    status VARCHAR(30) NOT NULL
);
```

The repository expects status text matching the Java enum names (`OPEN`, `IN_PROGRESS`, `DONE`).

## Testing

The project ships with dependency-free automated tests (custom `Assert` helper + `TestRunner`) covering the model, in-memory repository, service layer, `DatabaseConnection` failure paths, JDBC static safety review, and CLI exit codes:

```text
javac -Xlint:all -Werror -cp out -d test-out tests/taskmanagerjdbc/*.java
java -cp "out;test-out" taskmanagerjdbc.TestRunner   # Windows (use out:test-out on Linux/macOS)
```

Or run everything with one script: `./scripts/test.sh` (Linux/macOS/Git Bash) or `.\scripts\test.ps1` (Windows PowerShell). See [TESTING.md](TESTING.md) for the full procedure and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest recorded results.

## Java concepts practiced

- Interfaces, polymorphism, encapsulation, and dependency injection through constructors
- `List`, `Map`, `Optional`, and enum types
- `LocalDate` and nullable due dates
- JDBC connections, prepared statements, generated keys, result sets, and SQL exceptions
- Try-with-resources and input validation
- Exit codes and testable CLI entry points

## Known Limitations

- No JDBC driver, database, schema migration tool, or demo database is bundled
- Real JDBC integration tests are not included (the JDBC code is compiled and statically reviewed only)
- No HTTP API, authentication, or users
- No connection pool and no transaction manager beyond simple single-statement JDBC usage
- In-memory records disappear when the process exits
- Educational project, not production task-management software

## Resume Value

Built a Java task manager with service/repository separation, validated CRUD workflows, an in-memory test implementation, and a JDBC repository using prepared statements and caller-managed connections.

## Possible future improvements

- Transaction support for multi-step operations
- Pagination and title searching
- Configurable database-specific schema scripts
- Interactive console input
- Optional integration tests against a user-provided test database
