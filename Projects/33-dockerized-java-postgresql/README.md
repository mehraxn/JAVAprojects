# Dockerized Java + PostgreSQL

## Description

A Dockerized Java CLI application that connects to PostgreSQL using JDBC. The app is a **one-shot container job**: it starts, connects to the database, ensures a starter task exists, lists all tasks, and exits with code 0. It is not a long-running API server.

## What it demonstrates

- Maven packaging of a plain-JDBC Java 21 application
- Docker multi-stage build (Maven build stage, slim JRE runtime stage)
- Docker Compose service networking (the app reaches the database via the `database` service name, never `localhost`)
- PostgreSQL 16 container with a `pg_isready` healthcheck
- The app waiting for a healthy database via `depends_on: condition: service_healthy`
- Environment-based database configuration (`DB_URL`, `DB_USER`, `DB_PASSWORD`)
- Database initialization with `configs/init.sql` mounted into `/docker-entrypoint-initdb.d`
- Named-volume persistence (`postgres_data`) that survives `docker compose down`
- Safe `.env` handling: `.env` is gitignored, `.env.example` holds placeholders only, and the app refuses to run with the placeholder password
- Non-root runtime container (`USER 10001`)
- Failure behavior CI can trust: configuration or database errors exit non-zero

## What is implemented

- Java CLI app (`Main`) with clear console output and correct exit codes
- JDBC connection handling (`DatabaseConfig`, `DatabaseConnection`)
- Task insert/list workflow (`Task`, `TaskService`, `TaskRepository`, `JdbcTaskRepository`)
- 32 JUnit 5 unit tests that run without a database (in-memory repository fake)
- `Dockerfile` (multi-stage), `docker-compose.yml`, `configs/init.sql`
- Maven Wrapper (`mvnw` / `mvnw.cmd`)
- Recorded validation evidence in `TEST_RESULTS.md`

## Project structure

```text
src/main/java/dockerizedjavapostgres/   Application source
src/test/java/dockerizedjavapostgres/   JUnit 5 unit tests (no database needed)
Dockerfile                              Multi-stage build, non-root runtime
docker-compose.yml                      app + database services, healthcheck, named volume
configs/init.sql                        Schema initialization (tasks table)
.env.example                            Placeholder credentials to copy to .env
mvnw, mvnw.cmd, .mvn/wrapper/           Maven Wrapper
README.md, TESTING.md, TEST_RESULTS.md  Documentation and validation evidence
```

## How it works

1. `docker compose up --build` starts PostgreSQL with the schema from `configs/init.sql` and builds the app image with Maven inside Docker (no local Maven needed).
2. Compose waits until the database healthcheck passes, then starts the app container.
3. The app reads `DB_URL`, `DB_USER`, `DB_PASSWORD` from its environment, connects over JDBC, inserts a starter task if the table is empty, prints all tasks, and exits 0.
4. The task data lives in the `postgres_data` named volume, so it survives a normal Compose stop and reappears on the next run. Removing that volume would delete the data, so destructive volume-cleanup commands are intentionally omitted.

The database service intentionally publishes no host port; only containers on the Compose network can reach it. Configuration and database failures exit non-zero — the exact behaviors are validated in `TEST_RESULTS.md`.

## Quick start

```bash
cp .env.example .env        # then set a real local password in .env
docker compose up --build
```

Windows PowerShell: `Copy-Item .env.example .env`. The app refuses to start while the placeholder password is unchanged.

To run the unit tests without Docker (JDK 21 required, Maven Wrapper included):

```bash
./mvnw test        # Windows: mvnw.cmd test
```

## What is not production-grade

- No API server — this is a CLI demo job.
- No authentication, no TLS.
- No migration tool (Flyway/Liquibase); the schema comes from a single init script that only runs on first volume creation.
- No connection pool; each operation opens a short-lived connection.
- No cloud database, no secret manager — local demo credentials in `.env` only.
- GitHub-ready does not mean production-ready.

## Resume Value

Containerized a tested Java/JDBC application with a multi-stage Docker build, PostgreSQL initialization, environment validation, Compose health-gated startup, and recorded end-to-end local evidence.

## How to validate

- `TESTING.md` — exact commands for Maven tests, the Compose stack, the persistence check, and the failure exit-code checks.
- `TEST_RESULTS.md` — the honest record of what was actually executed and the results.
