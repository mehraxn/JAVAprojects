# Testing Dockerized Java + PostgreSQL

This guide lists the exact commands used to validate the project. The results of actually running them are recorded in `TEST_RESULTS.md`.

Reminder: the app is a one-shot CLI container that performs a database operation and exits. It is not a long-running API server, so the `app` service finishing with exit code 0 is the success condition.

## A) Prerequisites

```bash
java -version            # JDK 21
./mvnw -version          # Maven Wrapper (downloads Maven 3.9.9 itself)
docker --version
docker compose version
```

Windows: use `mvnw.cmd` instead of `./mvnw` everywhere. A locally installed Maven (`mvn -version`) works as a fallback for every wrapper command. On Linux/macOS run `chmod +x mvnw` once if needed.

## B) Maven test and package (no Docker or database needed)

```bash
./mvnw test        # 32 JUnit 5 unit tests, in-memory fake repository
./mvnw package     # builds target/dockerized-java-postgres-1.0.0.jar
```

Windows: `mvnw.cmd test` and `mvnw.cmd package`. Fallback: `mvn test` / `mvn package`.

## C) Docker Compose setup

```bash
cp .env.example .env
```

Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Then edit `.env` and replace the placeholder password with any local value. This is enforced: the app exits with a configuration error while the placeholder is unchanged. Never commit `.env` (it is gitignored).

## D) Compose validation

```bash
docker compose config     # resolves and validates the stack
docker compose up --build
```

Expected behavior:

1. PostgreSQL 16 starts and runs `configs/init.sql` (first start only).
2. PostgreSQL becomes healthy (`pg_isready` healthcheck).
3. The app container starts only after the database is healthy.
4. The app connects using JDBC via the `database` service name.
5. The app inserts a starter task (first run) or reports existing tasks.
6. The app lists all tasks and exits with code 0 (`exited (0)` in `docker compose ps -a`).

## E) Check containers and logs

```bash
docker compose ps -a
docker compose logs database
docker compose logs app
```

## F) Persistence test

Run the app again without removing the volume — the previous task must still be there and no duplicate is inserted:

```bash
docker compose run --rm app
```

Then prove the data survives a full stack shutdown:

```bash
docker compose down
docker compose up app
```

Expected: the previously inserted task is listed again, because the `postgres_data` named volume remains.

Destructive cleanup (only when you want to delete all task data):

```bash
docker compose down -v
```

Warning: `docker compose down -v` removes the database volume and deletes stored tasks. The next `up` re-runs `init.sql` from scratch.

## G) Failure exit-code tests

The app must exit non-zero on bad configuration so Compose/CI can detect failures.

Placeholder password (safe — connects nowhere):

```bash
docker compose run --rm -e DB_PASSWORD=replace_with_a_local_learning_password app
echo $?        # non-zero; PowerShell: $LASTEXITCODE
```

Missing configuration, without Docker (after `./mvnw package`):

```bash
java -cp target/classes dockerizedjavapostgres.Main
```

Expected: `Configuration error: Database URL is required.` and exit code 1. No password is ever printed, and no stack trace appears in normal CLI output.

## H) Cleanup

```bash
docker compose down
rm -rf target
```

Windows PowerShell:

```powershell
docker compose down
Remove-Item -Recurse -Force target -ErrorAction SilentlyContinue
```

Add `-v` to `docker compose down` only if you also want to delete the database volume.
