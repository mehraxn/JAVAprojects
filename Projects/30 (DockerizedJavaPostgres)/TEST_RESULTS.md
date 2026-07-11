# Test Results

Date: 2026-07-11

All commands below were actually executed on a Windows 11 machine (Docker Desktop, Docker 29.4.2, Compose v5.1.3) from this project directory. No system-wide JDK or Maven was installed, so local Maven validation used a portable Eclipse Temurin JDK 21.0.11 plus the project's Maven Wrapper. Nothing in this file is estimated or assumed. A throwaway local password was used in an uncommitted `.env`, which was deleted after validation.

## Local Java/Maven validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK Temurin 21.0.11+10 (portable; no system JDK installed) |
| mvn test / mvnw test | PASS | 32 JUnit 5 tests run, 0 failures (DatabaseConfigTest 15, TaskTest 10, TaskServiceTest 7), BUILD SUCCESS |
| mvn package | PASS | Built `target/dockerized-java-postgres-1.0.0.jar`, BUILD SUCCESS |
| Maven Wrapper | PASS | Generated with `mvn -N wrapper:wrapper -Dmaven=3.9.9`; `mvnw.cmd -version` and `mvnw.cmd test` succeeded |
| Failure exit code: missing DB_URL | PASS | `java -cp target/classes dockerizedjavapostgres.Main` with no env printed `Configuration error: Database URL is required.` and exited 1 |
| Failure exit code: placeholder password (local) | PASS | Placeholder `DB_PASSWORD` printed `Configuration error: Database password placeholder must be replaced.` and exited 1 |

## Docker Compose validation

| Check | Result | Notes |
|---|---:|---|
| docker compose config | PASS | Stack resolved: `app` + `database` services, healthcheck, named volume, init.sql bind mount |
| docker compose build | PASS | Multi-stage image built; Maven ran inside Docker (no host Maven needed) |
| docker compose up | PASS | `database` started first, became healthy, then `app` started |
| PostgreSQL health | PASS | `docker inspect` reported `healthy` (pg_isready healthcheck) |
| App JDBC connection | PASS | Log: `Connecting to PostgreSQL using DatabaseConfig{url='jdbc:postgresql://database:5432/taskdb', username='task_user'}` — via Compose service name, no password printed |
| Task insert/list | PASS | First run: `Table was empty - inserted starter task with ID 1`, listed 1 task, app exited 0 (`Exited (0)` in `docker compose ps -a`) |
| Idempotent re-run | PASS | `docker compose run --rm app`: `Found existing tasks - skipping starter insert.`, still 1 task, exit 0 |
| Persistence after down/up | PASS | `docker compose down` (containers and network removed) then `docker compose up app`: task ID 1 still listed from the `postgres_data` named volume, exit 0 |
| Failure exit code: placeholder password (container) | PASS | `docker compose run --rm -e DB_PASSWORD=replace_with_a_local_learning_password app` exited 1 with a configuration error |
| Failure exit code: wrong password (container) | PASS | `docker compose run --rm -e DB_PASSWORD=wrong_password_on_purpose app` exited 1 with `Database operation failed: Could not connect to PostgreSQL...` — no stack trace, no password in output |
| Cleanup | PASS | `docker compose down -v` removed containers, network, and the `postgres_data` volume |

## Tools unavailable

- No system-wide JDK or Maven was installed on the validation machine; a portable JDK 21 was downloaded solely for this validation. Anyone reproducing the results needs a JDK 21 for the unit tests (the wrapper handles Maven) — or just Docker, since the image builds with Maven inside Docker.

## Known limitations

- Local demo only; not deployed anywhere.
- CLI one-shot container job, not a web service.
- No migration tool — `init.sql` only runs when the volume is first created.
- No connection pooling.
- No production secret manager — local `.env` only, and `.env` is never committed.
- No cloud deployment.
- `target/` and `.env` were removed after validation and are not committed.
