# Testing Docker Compose Full Stack

This project was reviewed statically only. Docker, Compose, Java, Maven, Nginx, PostgreSQL, and HTTP requests were not executed.

## Configuration checks

- [ ] Copy `.env.example` to `.env` and replace the password placeholder.
- [ ] Confirm `.env` remains ignored by Git.
- [ ] Run `docker compose config` and inspect interpolated values before starting services.
- [ ] Confirm the database has no published host port.
- [ ] Confirm all three services use `app-network`.
- [ ] Confirm the PostgreSQL volume and initialization-script mount are present.
- [ ] Confirm no real credential is stored in tracked files.

## Manual service checks

| Check | Expected result |
|---|---|
| Build and start the stack | Database becomes healthy, followed by backend and frontend |
| Open `http://localhost:8081` | Notes page loads from Nginx |
| Request `http://localhost:8080/health` | `200` with `{"status":"UP"}` when PostgreSQL is reachable |
| Submit a normal note | Backend returns `201`; note appears in the list |
| Refresh the page | Previously created notes are loaded from PostgreSQL |
| Recreate containers without deleting the volume | Notes remain available |
| Stop or make PostgreSQL unavailable | Backend health endpoint returns `503` |

## Validation checks

| Input | Expected result |
|---|---|
| Empty or whitespace-only note | Frontend blocks it; direct backend request returns `400` |
| Note longer than 500 characters | Backend returns `400` |
| Unsupported method on `/api/notes` | Backend returns `405` with an `Allow` header |
| Database unavailable during note request | Backend returns `503` without exposing SQL details |
| Missing `POSTGRES_PASSWORD` during interpolation | Compose reports the explanatory required-variable error |
| Invalid `APP_PORT` | Backend stops during configuration validation |

## Networking checks

- [ ] From the browser, verify API calls use `/api/notes`, not a container hostname.
- [ ] Confirm Nginx proxies `/api/` to `backend:8080`.
- [ ] Confirm the backend JDBC URL uses `database:5432`.
- [ ] Confirm frontend and backend host-port overrides still map to container ports 80 and 8080.
- [ ] Confirm PostgreSQL cannot be reached through an accidentally published host port.

## Health-check checks

- [ ] Confirm `pg_isready` controls the database health state.
- [ ] Confirm the backend health probe requires both HTTP availability and a database query.
- [ ] Confirm the frontend health probe requests its local Nginx page.
- [ ] Confirm `depends_on` health conditions establish startup order but are not treated as a complete recovery strategy.

## Cleanup caution

`docker compose down` normally keeps the named database volume. Adding `--volumes` removes it and deletes the learning data. Review the target project and volume before using destructive cleanup options.

## Current status

All checklist items requiring containers remain unverified. There is no claim that images build, services start, health checks pass, or data persists at runtime.
