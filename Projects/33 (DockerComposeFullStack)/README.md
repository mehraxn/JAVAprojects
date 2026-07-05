# Docker Compose Full Stack

## Description

An educational full-stack project connecting a static Nginx frontend, a small Java HTTP/JDBC backend, and PostgreSQL with Docker Compose. The application stores short notes to make service-to-service communication visible.

## Goal

The goal is to understand how a browser-facing frontend, backend API, and persistent database are built and configured as separate services that communicate over a private Compose network.

## Technologies and concepts used

- Java 21 built-in `HttpServer`
- Standard JDBC APIs and PostgreSQL driver packaging
- Nginx static-file serving and reverse proxying
- PostgreSQL schema initialization
- Docker multi-stage builds
- Docker Compose services, DNS, networks, volumes, dependencies, and health checks
- Environment-variable configuration and placeholder secret handling

## Project structure

```text
backend/                         Java source, Maven file, and Dockerfile
database/init/001-schema.sql     Initial notes table
frontend/index.html              Static browser interface
frontend/nginx.conf              Static serving and /api reverse proxy
docker-compose.yml               Full-stack service topology
.env.example                     Placeholder local configuration
docs/architecture.md             Request and startup flow
README.md                        Project documentation
TESTING.md                       Validation guide
```

## Important files explained

- `backend/src/` contains configuration, model, repository, JSON, health-check, API-server, and entry-point classes.
- `backend/pom.xml` declares the PostgreSQL JDBC runtime dependency.
- `backend/Dockerfile` builds the backend and creates a non-root runtime image.
- `frontend/nginx.conf` proxies browser `/api` requests to `backend:8080` on the Compose network.
- `database/init/001-schema.sql` creates the notes table only when PostgreSQL initializes an empty data directory.
- `docker-compose.yml` defines frontend, backend, database, health checks, the bridge network, and persistent volume.

## Intended real-environment workflow

For an approved local exercise, copy `.env.example` to ignored `.env`, replace the password placeholder, review `docker compose config`, build the backend image, and start the stack. A browser would open the frontend host port. Nginx would forward API calls to the backend service, and the backend would connect to PostgreSQL through the `database` service name.

The database is intentionally not published to the host. Frontend and backend host ports are configurable for local use.

## Prepared but not executed

- Java API, JDBC repository, static frontend, Nginx proxy, schema, Compose networking, volumes, and health checks were prepared.
- Docker, Compose, Java, Maven, Nginx, PostgreSQL, HTTP requests, and JDBC operations were not executed.
- No image was built, service started, note saved, or health state observed.
- No working-stack claim is made.

## Manual validation checklist

- [ ] Confirm `.env` is ignored and placeholders are replaced locally.
- [ ] Review resolved ports, mounts, service names, and environment variables.
- [ ] Confirm Nginx proxies `/api` to `backend:8080`.
- [ ] Confirm JDBC uses `database:5432`, not container-local `localhost`.
- [ ] Confirm database initialization and named-volume behavior.
- [ ] Exercise health, list-notes, and create-note endpoints.
- [ ] Recreate containers without deleting the volume and verify persistence.

## Common mistakes avoided

- No real password is committed.
- PostgreSQL is not unnecessarily exposed to the host.
- Browser requests do not attempt to resolve Compose hostnames.
- Startup health is distinguished from simple container creation order.
- Database initialization is not described as a migration system.
- The root filesystem and service-user boundaries are documented honestly.

## Possible future improvements

- Add backend unit and integration tests.
- Add bounded database retries and connection pooling.
- Replace initialization SQL with versioned migrations.
- Add update/delete operations and stronger HTTP request limits.
- Add TLS, authentication, and an approved secret mechanism only with a clear learning scope.
