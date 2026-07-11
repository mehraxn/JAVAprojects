# Testing — Docker Compose Full Stack

Exact commands to validate this stack. Results actually observed are recorded
honestly in [TEST_RESULTS.md](TEST_RESULTS.md). Commands use POSIX shell
syntax; on Windows use Git Bash or `curl.exe` from PowerShell. Run everything
from this project folder.

## A) Backend build (Maven)

The Docker build runs Maven for you, so no local JDK/Maven is required:

```bash
docker compose build
```

With a local Maven + JDK 21 you can also build directly:

```bash
cd backend
mvn -q -DskipTests package    # no unit tests exist yet; the jar lands in target/
```

Running the backend outside Compose requires a reachable PostgreSQL and the
`DB_URL`/`DB_USER`/`DB_PASSWORD` environment variables — inside Compose all
of that is wired automatically, so the compose workflow below is the primary
validation path.

## B) Start the stack

```bash
cp .env.example .env        # local demo values (password is demo-only)
docker compose config       # must exit 0
docker compose up -d --wait # health-gated: postgres -> backend -> frontend
docker compose ps           # expect all three services (healthy)
```

Useful logs if anything is off:

```bash
docker compose logs database
docker compose logs backend
docker compose logs frontend
```

## C) Verify the frontend

```bash
curl -i http://localhost:8080/
```

Expect 200 and the "Compose Notes" HTML page (or open it in a browser).

## D) Verify the API through the reverse proxy

```bash
curl -i http://localhost:8080/api/health   # 200 {"status":"UP"}
curl -i http://localhost:8080/api/notes    # 200 [] on first run
```

The backend is also reachable directly on its debug port
(`http://localhost:8081/health`) — same responses without Nginx in front.

## E) Create and list notes

```bash
curl -i -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -d '{"text":"first note created through compose"}'

curl -i http://localhost:8080/api/notes
```

Expect 201 with the created note (id, text, createdAt), then a 200 array
containing it.

## F) Verify route and error correctness

```bash
curl -i http://localhost:8080/api/notes/extra          # 404 JSON
curl -i http://localhost:8080/api/unknown              # 404 JSON
curl -i http://localhost:8081/health/test              # 404 JSON (direct backend)
curl -i -X POST http://localhost:8081/health           # 405, Allow: GET
curl -i -X DELETE http://localhost:8080/api/notes      # 405, Allow: GET, POST
curl -i -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" -d 'not json'    # 400 JSON
```

Every backend/API error is JSON (`{"error":"..."}`) — no HTML error pages, no
stack traces. Non-API frontend paths may return the frontend page because
Nginx intentionally falls back to `index.html`.

## G) Verify persistence

```bash
docker compose restart backend frontend
curl -i http://localhost:8080/api/notes    # note still there

docker compose down                        # containers removed, volume kept
docker compose up -d --wait
curl -i http://localhost:8080/api/notes    # note STILL there (named volume)
```

## H) Cleanup

```bash
docker compose down        # keeps the database volume
```

**Warning:** `docker compose down -v` also deletes the `postgres-data`
volume — all notes are gone. Use it only when you want a clean slate.
