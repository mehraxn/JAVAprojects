# Dockerized Java PostgreSQL

A junior-friendly Java console application demonstrating a model/service/repository design backed by PostgreSQL through JDBC and prepared for local container execution.

## Features

- Validated `Task` model with ID, title, and completion status
- `TaskRepository` abstraction and JDBC implementation
- Service methods to create, list, find, complete, and delete tasks
- Prepared statements and try-with-resources
- Environment-based database configuration
- PostgreSQL schema initialization
- Multi-stage Java application image
- Compose configuration for the application and PostgreSQL

## Main classes

- `Task` — validated task model.
- `TaskRepository` — persistence contract.
- `JdbcTaskRepository` — PostgreSQL CRUD operations using standard `java.sql` APIs.
- `TaskService` — small business-logic layer.
- `DatabaseConfig` — reads and validates environment variables.
- `DatabaseConnection` — opens JDBC connections without logging passwords.
- `Main` — lists tasks and creates one starter task when the table is empty.

## Project structure

```text
src/dockerizedjavapostgres/
  DatabaseConfig.java
  DatabaseConnection.java
  JdbcTaskRepository.java
  Main.java
  Task.java
  TaskRepository.java
  TaskService.java
configs/init.sql
.dockerignore
.env.example
.gitignore
Dockerfile
docker-compose.yml
pom.xml
README.md
TESTING.md
```

## How the application works

1. PostgreSQL starts and executes `configs/init.sql` for a new database volume.
2. Compose waits for PostgreSQL's health check.
3. The Java container receives `DB_URL`, `DB_USER`, and `DB_PASSWORD`.
4. `Main` creates `DatabaseConfig`, `JdbcTaskRepository`, and `TaskService`.
5. JDBC opens a connection for each repository operation.
6. If the table is empty, one starter task is inserted.
7. Tasks are printed and the console application exits normally.

This is intentionally a short-lived console application, not an HTTP service.

## PostgreSQL configuration

Compose uses these environment values:

| Variable | Example value | Purpose |
|---|---|---|
| `POSTGRES_DB` | `taskdb` | Database created by the PostgreSQL image |
| `POSTGRES_USER` | `task_user` | Database user |
| `POSTGRES_PASSWORD` | `CHANGE_ME` | Placeholder password that must be replaced |
| `DB_URL` | `jdbc:postgresql://database:5432/taskdb` | JDBC URL supplied to Java |
| `DB_USER` | `task_user` | JDBC username |
| `DB_PASSWORD` | `CHANGE_ME` | JDBC password |

Copy `.env.example` to `.env` and replace `CHANGE_ME` before any future local run. `.env` is excluded from the application image by `.dockerignore`; it should also remain outside version control.

## Docker setup

The Dockerfile uses two stages:

1. A Maven/JDK stage compiles the source and downloads the PostgreSQL JDBC driver declared in `pom.xml`.
2. A smaller JRE stage copies the application JAR and runtime dependencies, then runs as non-root user `10001`.

`docker-compose.yml` defines:

- `database` — PostgreSQL with a health check, schema mount, and named data volume.
- `app` — the Java image with JDBC environment variables and a healthy-database dependency.

## Example usage

These commands document the intended future workflow; they were not executed while implementing this project.

```text
copy .env.example .env
```

Edit `.env`, replace the placeholder password, then:

```text
docker compose up --build
docker compose down
```

To remove the local database volume as well:

```text
docker compose down --volumes
```

Removing the volume deletes the local PostgreSQL data.

## Local Java execution

Plain `javac` can compile the source because it imports only standard `java.sql` APIs. Runtime database access still requires the PostgreSQL JDBC driver on the classpath and a reachable initialized database.

Maven can supply the driver when available:

```text
mvn package dependency:copy-dependencies -DincludeScope=runtime
java -cp "target/dockerized-java-postgres-1.0.0.jar;target/dependency/*" dockerizedjavapostgres.Main
```

The classpath separator shown above is for Windows. Other operating systems commonly use `:`.

## Limitations

- Nothing was compiled, built, connected, or executed in the current environment.
- Maven, Docker, PostgreSQL, Java, and the JDBC driver may not be installed locally.
- The PostgreSQL driver must be downloaded by Maven during an actual image or local build.
- The console process exits after displaying tasks.
- There is no HTTP API, migration framework, connection pool, authentication, or automated test suite.
- `init.sql` runs automatically only when PostgreSQL initializes a new empty volume.
- The pinned image and driver versions are starter choices and are not claimed to be the latest versions.

## Possible future improvements

- Add automated tests using an in-memory repository or test database.
- Add an HTTP interface and health endpoint.
- Introduce versioned schema migrations.
- Add connection pooling and transaction boundaries when complexity justifies them.
- Add container health checks for the Java process.
