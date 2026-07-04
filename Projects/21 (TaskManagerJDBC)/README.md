# Task Manager JDBC

A small task manager demonstrating repository-based design with both a database-free in-memory implementation and a JDBC implementation built entirely with standard `java.sql` APIs.

## Features

- Add tasks with a title, optional description, optional due date, and initial `OPEN` status.
- Update task status to `OPEN`, `IN_PROGRESS`, or `DONE`.
- Delete tasks and list all tasks.
- Search tasks by status.
- Reject blank titles, invalid IDs, null statuses, and duplicate in-memory IDs.
- Run the console demonstration without a database or JDBC driver.
- Study or configure JDBC CRUD operations using prepared statements and generated keys.

## Main classes and repository structure

- `Task` — validated task model and status enum.
- `TaskRepository` — common CRUD and query contract.
- `InMemoryTaskRepository` — `LinkedHashMap` storage with generated IDs and defensive copies.
- `JdbcTaskRepository` — JDBC implementation using `Connection`, `PreparedStatement`, and `ResultSet`.
- `DatabaseConnection` — validated JDBC configuration and `DriverManager` connection boundary.
- `TaskService` — application operations independent of repository implementation.
- `Main` — safe in-memory demonstration; it never contacts a database.

The service depends on `TaskRepository`, so switching storage does not change task-management logic:

```text
TaskService -> TaskRepository -> InMemoryTaskRepository
                              -> JdbcTaskRepository -> DatabaseConnection
```

## How the program works

`TaskService` validates application operations and talks only to `TaskRepository`. The default `Main` supplies `InMemoryTaskRepository`, which assigns IDs and stores defensive task copies. `JdbcTaskRepository` provides the same operations with prepared SQL statements and maps database rows back into `Task` objects.

## Runnable without a database

`Main` uses `InMemoryTaskRepository`. It demonstrates adding, updating, filtering, listing, and deleting tasks without a driver, JDBC URL, schema, or database server.

```text
javac -d out src/taskmanagerjdbc/*.java
java -cp out taskmanagerjdbc.Main
```

The in-memory repository keeps data only for the life of the process.

## JDBC learning example

`JdbcTaskRepository` contains complete SQL operations, parameter binding, generated-key handling, row mapping, and try-with-resources cleanup. Real execution additionally requires:

1. A database server or embedded database.
2. Its JDBC driver on the runtime classpath.
3. A matching `tasks` table.
4. A valid JDBC URL and credentials where required.

No JDBC driver is included or installed by this project. A representative schema is:

```sql
CREATE TABLE tasks (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    due_date DATE,
    status VARCHAR(30) NOT NULL
);
```

Identity-column syntax differs between database products, so adjust the schema for the selected database. The repository expects status text matching the Java enum names.

## Java concepts practiced

- Interfaces, polymorphism, encapsulation, and dependency injection through constructors
- `List`, `Map`, `Optional`, and enum types
- `LocalDate` and nullable due dates
- JDBC connections, prepared statements, generated keys, result sets, and SQL exceptions
- Try-with-resources and input validation

## Backend and database concepts practiced

- Repository interfaces and interchangeable storage implementations
- CRUD operations, generated keys, parameter binding, and row mapping
- JDBC connection failures and checked `SQLException` propagation
- Database-independent service logic and a database-free fallback

## Example usage

Compile and run the in-memory version:

```text
javac -d out src/taskmanagerjdbc/*.java
java -cp out taskmanagerjdbc.Main
```

JDBC execution is intentionally not started by `Main`.

## Storage approach

- `InMemoryTaskRepository`: runnable storage that lasts only for the current process.
- `JdbcTaskRepository`: JDBC-style learning implementation requiring a user-supplied driver, JDBC URL, database, and compatible `tasks` table.

## Limitations

- No JDBC driver, database, or schema migration tool is included
- SQL identity syntax and generated-key behavior can vary by database product
- In-memory records disappear when the process exits
- No transactions are needed for the current single-operation service methods

## Possible future improvements

- Transaction support for multi-step operations
- Pagination and title searching
- Configurable database-specific schema scripts
- Interactive console input
- Automated tests with a user-provided test database
