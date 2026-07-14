# Docker Compose Full Stack

*A Docker Compose full-stack lab — browser → Nginx frontend/reverse proxy →
Java backend API → PostgreSQL, with health-checked startup, named-volume
persistence, environment-based configuration, and honest validation results.*

## What this project is

A three-service Compose stack for a tiny notes application. Nginx serves a
static page and reverse-proxies `/api/*` to a dependency-light Java 21
backend (built with Maven, JDBC to PostgreSQL). Everything claimed here was
actually run — see [TEST_RESULTS.md](TEST_RESULTS.md).

## Architecture

```
browser ──▶ frontend (nginx:1.27, port 8080)
              ├── /            static index.html
              ├── /api/health  ──▶ backend /health
              └── /api/*       ──▶ backend (Java 21, HttpServer + JDBC)
                                      └──▶ database (postgres:16-alpine)
                                             └── named volume postgres-data
```

- **Startup is health-gated**: postgres must pass `pg_isready` before the
  backend starts; the backend must pass its own `/health` probe (a tiny Java
  HTTP check, no curl needed in the JRE image) before Nginx starts.
- **PostgreSQL is not exposed to the host** — only frontend (8080) and,
  for debugging, the backend (8081) are published.
- **Named volume** `postgres-data` keeps notes across `docker compose down`.

## API

| Route (public, via Nginx) | Backend route | Behavior |
| --- | --- | --- |
| `GET /api/health` | `GET /health` | 200 `{"status":"UP"}` or 503 if DB down |
| `GET /api/notes` | same | 200 JSON array of notes |
| `POST /api/notes` | same | body `{"text":"..."}` → 201 with created note |
| unknown API routes | — | JSON `{"error":"Not found"}` 404 |

Backend API routes are **exact-matched** (`/api/notes/extra` and `/health/test` are 404),
wrong methods get 405 with an `Allow` header, invalid JSON gets 400, and a
database outage returns a clean 503 JSON error — never an HTML error page or
a stack trace. Non-API frontend routes are handled by the Nginx single-page
app fallback (`try_files ... /index.html`), so the JSON 404 guarantee applies
to backend/API routes.

## Quick start

```bash
cp .env.example .env     # local demo values; edit if you like
docker compose config    # sanity check
docker compose up -d --wait

open http://localhost:8080          # the notes UI
curl http://localhost:8080/api/health
curl -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" -d '{"text":"first note"}'
curl http://localhost:8080/api/notes

docker compose down      # notes survive (named volume)
```

Destructive named-volume cleanup is intentionally omitted because it deletes the stored notes.

## Project structure

```text
docker-compose.yml            three services, healthchecks, named volume
.env.example                  local demo config (copy to gitignored .env)
frontend/index.html           notes UI (fetch-based)
frontend/nginx.conf           static files + /api/* reverse proxy
backend/                      Maven project (Java 21, PostgreSQL JDBC)
  src/main/java/dockercomposefullstack/
  Dockerfile                  multi-stage: Maven build → JRE runtime, non-root
database/init/001-schema.sql  schema, applied on first startup only
docs/architecture.md
README.md  TESTING.md  TEST_RESULTS.md
```

## What is implemented (and verified)

Everything in [TEST_RESULTS.md](TEST_RESULTS.md) was actually run on
2026-07-10: the Maven build (inside the Docker build), compose config/build,
health-gated startup to all-healthy, frontend page load, health/notes API
through the reverse proxy, exact-route 404s and 405s, note creation and
listing against real PostgreSQL, and **persistence across both
`compose restart` and a full `down`/`up` cycle**.

## What is not production-grade

- **No TLS** and **no authentication** — anyone who can reach the port can
  write notes.
- **No migrations tool** — the schema loads only when the data volume is
  first created.
- **Local demo credentials** via `.env` (never committed); a real deployment
  needs a secret manager.
- The hand-rolled JSON field extractor in the backend is deliberately minimal
  for a dependency-free demo; real projects should use a JSON library.
- No Kubernetes or cloud deployment.

## Resume Value

Built a local three-service stack combining Nginx, a Java HTTP backend, and PostgreSQL with container health checks, persistent storage, exact API behavior, and Compose-based validation.

## How to validate

Exact commands with expected results: [TESTING.md](TESTING.md). Honest
recorded results: [TEST_RESULTS.md](TEST_RESULTS.md).
