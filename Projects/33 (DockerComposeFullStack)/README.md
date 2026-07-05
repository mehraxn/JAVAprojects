# Docker Compose Full Stack

An educational full-stack project that connects a static Nginx frontend, a small Java HTTP backend, and PostgreSQL with Docker Compose. The application stores and lists short notes so the communication path is visible without adding a large framework.

## Services

| Service | Purpose | Container port | Host access |
|---|---|---:|---|
| `frontend` | Serves the HTML page and proxies `/api` requests | 80 | `http://localhost:8081` by default |
| `backend` | Runs Java `HttpServer` endpoints and JDBC code | 8080 | `http://localhost:8080` by default |
| `database` | Stores notes in PostgreSQL | 5432 | Internal network only |

Host ports can be changed with `FRONTEND_HOST_PORT` and `BACKEND_HOST_PORT` in `.env`. PostgreSQL is deliberately not published to the host because only the backend needs it.

## Features

- Java 21 backend using the built-in `HttpServer`
- JDBC repository using the standard `java.sql` API
- PostgreSQL runtime driver supplied through Maven
- Static HTML and JavaScript frontend served by Nginx
- Nginx reverse proxy from `/api` to the backend
- Explicit Compose bridge network
- Named volume for PostgreSQL data
- Database, backend, and frontend health-check examples
- Environment-based ports and database configuration
- Placeholder-only environment example

## Project structure

```text
backend/
  Dockerfile
  pom.xml
  src/main/java/dockercomposefullstack/
database/init/001-schema.sql
frontend/
  index.html
  nginx.conf
docs/architecture.md
.env.example
docker-compose.yml
README.md
TESTING.md
```

## API

| Method and path | Behavior |
|---|---|
| `GET /health` | Returns `200` when the backend can query PostgreSQL, otherwise `503` |
| `GET /api/notes` | Returns all notes as a JSON array |
| `POST /api/notes` | Accepts a plain-text body and creates a note |

Notes must contain 1 to 500 non-whitespace characters. JSON is produced manually to keep the Java example small.

## Intended local workflow

These commands are documentation only and were not executed while implementing the project.

1. Copy `.env.example` to `.env`.
2. Replace the password placeholder with a local development password.
3. Review the resolved configuration with `docker compose config`.
4. Start the learning stack with `docker compose up --build`.
5. Open `http://localhost:8081`.

The first backend image build would use Maven to obtain the PostgreSQL JDBC driver. Compose would also need to pull the declared Maven, Java, PostgreSQL, and Nginx images if they are not already available.

## Communication flow

The browser calls `/api/notes` on the frontend origin. Nginx forwards that request to `http://backend:8080` using Compose DNS. The Java backend connects to `jdbc:postgresql://database:5432/...`. Service names work only inside the Compose network; the browser uses published host ports instead.

See [docs/architecture.md](docs/architecture.md) for the full request and startup flow.

## Data persistence

The named volume `postgres-data` stores PostgreSQL files outside the container lifecycle. Recreating the database container should retain data while the volume exists. The initialization SQL runs only when PostgreSQL initializes an empty data directory.

## Security boundaries

- `.env.example` contains a conspicuous placeholder, not a usable production secret.
- `.env` is ignored by Git.
- The setup is for local learning and is not production hardened.
- TLS, authentication, authorization, secret management, backups, and database migrations are outside this example.
- pgAdmin is intentionally omitted to keep the stack focused and reduce exposed services.

## Limitations

- Docker, Docker Compose, Java, Maven, the HTTP endpoints, and PostgreSQL were not executed.
- Images were not built or pulled, and no container health status was observed.
- Runtime behavior depends on Docker availability, image access, and a user-created `.env` file.
- No successful local test or working-stack claim is made.

## Possible improvements

- Add automated backend tests and a disposable integration-test database.
- Add database migrations instead of relying on one initialization script.
- Add update and delete operations for notes.
- Add authentication only after defining a clear learning goal.
- Pin images by digest when reproducible builds become a requirement.
