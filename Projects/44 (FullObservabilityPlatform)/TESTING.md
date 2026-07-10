# Testing — Full Observability Platform

This is a **runnable** local lab. Commands below are exact; recorded real results
are in [TEST_RESULTS.md](TEST_RESULTS.md). Run from the project root
(`44 (FullObservabilityPlatform)/`). `curl`/URLs assume the default ports.

> Nothing here contacts a cloud or uses real secrets. `docker compose down -v`
> leaves a clean machine.

## 1. Java-only validation (needs a JDK 21)

```bash
javac -d out src/fullobservabilityplatform/*.java
APP_PORT=8080 APP_LOG_FILE=/tmp/observable-java-app.log \
  java -cp out fullobservabilityplatform.Main
```

`APP_LOG_FILE` points the JSON log file at a locally writable path (the default,
`/var/log/app/app.log`, is for the Docker setup). If the path is unwritable the
app logs to stdout only and keeps running.

In another terminal:

```bash
curl -i http://localhost:8080/            # 200 {"message":"observability example"}
curl -i http://localhost:8080/health      # 200 {"status":"UP"}
curl -i http://localhost:8080/work        # 200 {"worked":true}
curl -i "http://localhost:8080/work?fail=1"  # 500 {"error":"internal"}
curl -i http://localhost:8080/metrics     # 200 Prometheus text
curl -i http://localhost:8080/unknown     # 404 {"error":"not found"}
```

Expected: `/unknown` returns **404**, `/work?fail=1` returns **500**, normal
responses carry a `traceparent` header, and JSON log lines land in
`/tmp/observable-java-app.log`. Without a JDK, use the Docker build below (it
compiles Java inside the container).

## 2. Docker image build

```bash
docker build -t observable-java-app:0.1.0 -f docker/Dockerfile.example .
```

This compiles the dependency-free app (multi-stage build) into a non-root JRE
image — no local JDK required.

## 3. Compose validation

```bash
docker compose config      # prints the merged, validated config
docker compose up -d        # builds app + starts the 7-service stack
docker compose ps           # all services running
```

## 4. Verify the signals

```bash
# App is up and emitting metrics
curl http://localhost:8080/health
curl http://localhost:8080/metrics

# Generate some load so panels/alerts/logs have data
for i in $(seq 1 30); do curl -s http://localhost:8080/work >/dev/null; done
curl -s "http://localhost:8080/work?fail=1" >/dev/null   # one error

# Core services healthy
curl http://localhost:9090/-/ready        # Prometheus: "Prometheus Server is Ready."
curl http://localhost:3000/api/health     # Grafana: {"database":"ok",...}
curl http://localhost:3100/ready          # Loki: "ready"
curl http://localhost:3200/ready          # Tempo: "ready"
```

### Prometheus target check

Open `http://localhost:9090` → **Status → Targets**: `observable-java-app` should
be **UP**. Or via API:

```bash
curl -s http://localhost:9090/api/v1/targets \
  | python -c "import sys,json;[print(t['labels']['job'],t['health']) for t in json.load(sys.stdin)['data']['activeTargets']]"
```

### Loki log check

In Docker Compose the app's logs live in the **`app-logs` named volume** (shared
with Promtail) — there is no host log file, so validate through Loki/Grafana:

- **Grafana UI:** **Explore → Loki datasource** → query
  `{service="observable-java-app"}`, or search the log body for a `trace_id`.
- **Loki API** (labels below are the ones Promtail really creates —
  `service`, `job`, `level`, `env`, `event`):

```bash
curl -s -G "http://localhost:3100/loki/api/v1/query_range" \
  --data-urlencode 'query={service="observable-java-app"}' \
  --data-urlencode 'limit=5'
```

- **Container stdout** (the app logs to stdout too):

```bash
docker compose logs app | tail -5
docker compose logs promtail | tail -5
```

### Tempo / trace-context check

Trace context is **correlation-only**: the app does not export spans, so Tempo
has no app traces (see [docs/traces.md](docs/traces.md)). What you can verify:

```bash
# Tempo is running and provisioned as a Grafana datasource:
curl http://localhost:3200/ready                      # "ready"
# A search returns no app spans by design (empty result is expected):
curl -s -G http://localhost:3200/api/search \
  --data-urlencode 'tags=service.name=observable-java-app'   # {"traces":[]}

# The correlation itself IS real — every response carries a traceparent header:
curl -si http://localhost:8080/work | grep -i traceparent
# and every log line carries the same trace_id (see the Loki check above).
```

### Grafana provisioning check

Open `http://localhost:3000` (admin/admin):
- **Connections → Data sources** shows Prometheus, Loki, Tempo (provisioned).
- **Dashboards** shows "Full Observability Platform - Example" under the
  *Observability Lab* folder.

Or via API:

```bash
curl -s -u admin:admin http://localhost:3000/api/datasources \
  | python -c "import sys,json;[print(d['name'],d['uid']) for d in json.load(sys.stdin)]"
```

## 5. Cleanup

```bash
docker compose down -v      # -v also removes the app-logs named volume
rm -rf out
rm -f /tmp/observable-java-app.log
```

## 6. Notes

- App logs in Docker live in the `app-logs` named volume (no host file);
  `docker compose down -v` removes it. Local `*.log` files are git-ignored.
- This is a **local demo**: no persistence, no alert routing, no access control
  beyond a placeholder Grafana password, no cloud deployment.
- **Tempo honesty:** no app spans are expected in Tempo by default — the app does
  not export OTLP spans. Tempo is included as an extension-ready backend.
