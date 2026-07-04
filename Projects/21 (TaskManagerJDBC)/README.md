# Task Manager JDBC

## Status

Java and JDBC skeleton created. SQL operations and schema setup are not implemented.

## Planned features

- Create, list, update, and delete tasks.
- Mark tasks as done.
- Filter tasks by status.
- Store due dates.
- Use prepared JDBC statements.

## Current classes

- Task: task model and status enum.
- DatabaseConnection: connection configuration boundary.
- JdbcTaskRepository: planned JDBC CRUD operations.
- TaskService: validation and application operations.
- Main: safe runner that does not connect automatically.

## Runtime requirement

The source uses standard java.sql APIs. Running database operations requires an existing JDBC driver, JDBC URL, and database. This project must not be run until those are supplied.

## Source layout

Source files are under src/taskmanagerjdbc.
