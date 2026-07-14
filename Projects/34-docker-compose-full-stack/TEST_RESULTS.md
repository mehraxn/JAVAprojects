# Test Results — Docker Compose Full Stack

Date: 2026-07-10. Host: Windows 11, Docker Desktop (engine 29.4.2). No local
JDK/Maven — the backend was compiled by Maven **inside** the multi-stage
Docker build (that is the project's build path). Everything below was
actually run; the stack was torn down afterward (`docker compose down -v`,
removing the test data volume).

## Static/backend validation

| Check | Result | Notes |
|---|---:|---|
| Maven package (via Docker build) | PASS | `mvn package` in the build stage compiled cleanly and produced the jar + JDBC driver |
| Backend route matching | PASS | `/health/test` → 404, `/api/notes/extra` → 404 JSON (exact-path checks) |
| JSON error handling | PASS | unknown backend/API routes return `{"error":"Not found"}`; 405 carries `Allow`; invalid body → 400 JSON; no backend HTML error pages |

## Docker Compose validation

| Check | Result | Notes |
|---|---:|---|
| `docker compose config` | PASS | exit 0 with `.env` from `.env.example` |
| `docker compose build` | PASS | image `compose-fullstack-backend:0.1.0` (one transient base-image download error on first attempt; clean retry succeeded) |
| `docker compose up -d --wait` | PASS | health-gated startup: database → backend → frontend, all `(healthy)` |
| PostgreSQL health | PASS | `pg_isready` healthcheck green; schema auto-applied on first start |
| Backend health | PASS | `GET /api/health` → 200 `{"status":"UP"}` (Java-based container healthcheck also green) |
| Frontend load | PASS | `GET http://localhost:8080/` → 200 notes page via Nginx |

## Application workflow (through the Nginx reverse proxy)

| Check | Result | Notes |
|---|---:|---|
| List (empty) | PASS | `GET /api/notes` → 200 `[]` |
| Create note | PASS | `POST /api/notes` with `{"text":"..."}` → 201 with id/text/createdAt |
| List notes | PASS | created note returned |
| `/api/notes/extra` | PASS | 404 JSON |
| `/api/unknown` | PASS | 404 |
| `POST /health` (direct backend) | PASS | 405, `Allow: GET` |
| `DELETE /api/notes` | PASS | 405, `Allow: GET, POST` |
| invalid JSON body | PASS | 400 `{"error":"Request body must be JSON like {\"text\":\"your note\"}."}` |

## Persistence

| Check | Result | Notes |
|---|---:|---|
| After `docker compose restart backend frontend` | PASS | note still listed |
| After full `docker compose down` + `up -d` | PASS | note still listed — the `postgres-data` named volume survived container removal |

## Tools unavailable

- JDK/Maven on the host — the containerized Maven build stage was used
  instead (the intended workflow).

## Known limitations

- Local demo only: no TLS, no authentication, no rate limiting.
- No database migrations tool — the schema applies only when the volume is
  first created.
- Credentials come from a local `.env` (never committed); `.env.example`
  holds a clearly-labeled demo password.
- The minimal hand-rolled JSON field extractor is demo-grade; real projects
  should use a JSON library.
- Results are a point-in-time snapshot of one validation run.
