# Architecture

## Request flow

```text
Browser
  | http://localhost:8081
  v
frontend (Nginx :80)
  | /api/* -> http://backend:8080
  v
backend (Java HttpServer :8080)
  | jdbc:postgresql://database:5432/fullstackdb
  v
database (PostgreSQL :5432)
  |
  v
postgres-data named volume
```

The browser cannot resolve Compose service names. It reaches Nginx through the frontend's published host port. Nginx and the Java backend can use `backend` and `database` because Compose provides DNS for services attached to `app-network`.

## Service responsibilities

### Frontend

Nginx serves `index.html`. Its reverse-proxy rule sends `/api/` requests to the backend, keeping browser requests same-origin and avoiding unnecessary cross-origin configuration.

### Backend

The Java application uses `com.sun.net.httpserver.HttpServer` and exposes health and notes endpoints. `NoteRepository` opens JDBC connections with `DB_URL`, `DB_USER`, and `DB_PASSWORD`. It returns generic database errors to clients instead of SQL details.

### Database

PostgreSQL initializes the `notes` table from `database/init/001-schema.sql` when the named volume is empty. The database is available to other containers on port 5432 but is not published to the host.

## Startup and health

1. PostgreSQL starts and `pg_isready` checks the configured database and user.
2. The backend starts after the database reports healthy.
3. The backend health probe calls `/health`, which also performs a small database query.
4. The frontend starts after the backend reports healthy and checks its local Nginx page.

Health-based dependencies make the demonstration easier to follow, but they do not replace application retries, monitoring, or operational recovery design.

## Ports

| Direction | Mapping or destination |
|---|---|
| Host to frontend | `${FRONTEND_HOST_PORT:-8081}` to container port 80 |
| Host to backend | `${BACKEND_HOST_PORT:-8080}` to container port 8080 |
| Frontend to backend | `backend:8080` on `app-network` |
| Backend to database | `database:5432` on `app-network` |

The direct backend host port is useful for learning and manual API checks. It could be removed in a stricter setup where all requests must pass through Nginx.

## Persistence and initialization

`postgres-data` is a Docker-managed named volume. Removing a container does not normally remove the volume. The initialization script is not a migration system and will not rerun against an existing initialized volume.

## Deliberate omissions

This project does not include pgAdmin, TLS, login handling, production secrets, orchestration beyond Compose, or production deployment guidance. These omissions keep the example focused on service composition and networking.

## Verification status

This architecture was prepared and reviewed as configuration and source files only. No image, container, network, volume, endpoint, JDBC connection, or health check was executed.
