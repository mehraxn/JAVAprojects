# Testing Docker Compose Full Stack

No Docker, Compose, Java, Maven, Nginx, PostgreSQL, browser, HTTP, or JDBC command was executed while preparing this project.

## Static validation checklist

- [ ] Review Java package paths, validation, SQL resource handling, and JSON escaping.
- [ ] Confirm frontend API paths match backend contexts.
- [ ] Confirm Nginx proxy behavior preserves `/api` paths.
- [ ] Confirm Deployment ordering relies on health conditions where documented.
- [ ] Review schema constraints and note-length limits.

## File existence checks

- [ ] Backend Java source, `pom.xml`, and `Dockerfile` exist.
- [ ] `frontend/index.html` and `frontend/nginx.conf` exist.
- [ ] `database/init/001-schema.sql` exists.
- [ ] `docker-compose.yml` and `.env.example` exist.
- [ ] `docs/architecture.md`, `README.md`, and `TESTING.md` exist.

## Configuration review checklist

- [ ] All services share the intended bridge network.
- [ ] Frontend, backend, and database ports match their consumers.
- [ ] Database credentials and names agree across services.
- [ ] Named volume and initialization mounts target correct paths.
- [ ] Health checks use commands/endpoints available inside each image.
- [ ] PostgreSQL has no accidental host-port publication.

## Security checks

- [ ] No real secret or credential is present.
- [ ] No production host, database, or endpoint is present.
- [ ] `.env` is ignored.
- [ ] Database errors returned to clients do not reveal SQL or credentials.
- [ ] Backend runs as a non-root user.

## Commands normally used - NOT executed

```text
docker compose config
docker compose build
docker compose up
docker compose ps
docker compose logs
docker compose down
```

These commands require installed tooling, reviewed local values, and an approved disposable environment.

## Expected results in a proper environment

- PostgreSQL becomes healthy before the backend reports healthy.
- Nginx serves the frontend after the backend is available.
- `/health` reports database-aware backend status.
- Notes can be created and listed through the frontend/API path.
- Recreated containers retain notes while the named volume remains.
- Invalid notes receive controlled client errors, and unavailable PostgreSQL receives a controlled service-unavailable response.
