# Testing — Prometheus Grafana Monitoring

Exact commands to validate this lab. Results actually observed with these
commands are recorded honestly in [TEST_RESULTS.md](TEST_RESULTS.md).
Commands use POSIX shell syntax; on Windows use Git Bash (or `curl.exe` from
PowerShell). Run everything from this project folder.

## A) Java-only validation

Requires JDK 21 (or run inside an `eclipse-temurin:21-jdk` container):

```bash
javac -Xlint:all -Werror --add-modules jdk.httpserver -d out src/prometheusgrafanamonitoring/*.java

APP_PORT=8080 APP_VERSION=0.1.0 \
  java --add-modules jdk.httpserver -cp out prometheusgrafanamonitoring.Main
```

In another terminal:

```bash
curl -i http://localhost:8080/
curl -i http://localhost:8080/health
curl -i http://localhost:8080/metrics
curl -i http://localhost:8080/work
curl -i "http://localhost:8080/work?ms=100"
curl -i "http://localhost:8080/work?fail=1"
curl -i http://localhost:8080/unknown
curl -i http://localhost:8080/health/test
curl -i -X POST http://localhost:8080/health
```

Expected:

- `/` → 200; `/health` → 200; `/work` → 200 `{"worked_ms":0}`
- `/work?ms=100` → 200 after ~100ms (`ms` capped at 5000)
- `/work?fail=1` → **500 intentionally**
- `/metrics` → Prometheus text: `http_requests_total{method,route,status}`,
  `http_request_duration_seconds_*`, `jvm_memory_*`,
  `process_uptime_seconds`, `app_info{version="0.1.0"}`
- `/unknown` and `/health/test` → 404 (metrics record `route="not_found"`)
- `POST /health` (and `POST /metrics`) → 405

## B) Docker build

```bash
docker build -t prometheus-grafana-java-app:0.1.0 .
```

## C) Docker Compose validation

```bash
cp .env.example .env
# .env.example ships a local demo password (local-demo-password); edit if needed
docker compose config
docker compose up -d --wait     # healthchecks gate app -> prometheus -> grafana
docker compose ps               # expect all three (healthy)
```

Then verify:

```bash
curl http://localhost:8080/health
curl http://localhost:8080/metrics
curl http://localhost:9090/-/ready
curl http://localhost:3000/api/health
```

## D) Prometheus validation

Open http://localhost:9090/targets and confirm `java-app` is UP, or via API:

```bash
curl -G http://localhost:9090/api/v1/query \
  --data-urlencode 'query=up{job="java-app"}'

curl -G http://localhost:9090/api/v1/query \
  --data-urlencode 'query=http_requests_total'

# alert rules loaded and evaluating:
curl http://localhost:9090/api/v1/rules
```

## E) Generate traffic

```bash
curl http://localhost:8080/work
curl "http://localhost:8080/work?ms=100"
curl "http://localhost:8080/work?fail=1"
```

Repeat a few times; give Prometheus a scrape interval (~15s) to pick it up.
Sustained `fail=1` traffic for 5+ minutes drives the `JavaAppHighErrorRatio`
alert toward firing; sustained `ms=600` traffic drives `JavaAppHighLatency`.

## F) Grafana validation

Open http://localhost:3000 and log in with the credentials from `.env`.

- **Datasource**: Connections → Data sources → "Prometheus" is provisioned
  (uid `prometheus`, read-only).
- **Dashboard**: Dashboards → Learning folder → "Java Application
  Monitoring" — after traffic, the request-rate-by-route, error-ratio, p95,
  heap, uptime, and version panels show data.

Or via API:

```bash
curl -u admin:$GRAFANA_ADMIN_PASSWORD http://localhost:3000/api/datasources
curl -u admin:$GRAFANA_ADMIN_PASSWORD "http://localhost:3000/api/search?query="
```

## G) promtool validation

With promtool installed:

```bash
promtool check config monitoring/prometheus.yml
promtool check rules monitoring/alert-rules.yml
```

Without it, use the Prometheus image (mount so the rule-file path in the
config resolves):

```bash
docker run --rm -v "$PWD/monitoring:/etc/prometheus:ro" \
  --entrypoint promtool prom/prometheus:v2.54.1 \
  check config /etc/prometheus/prometheus.yml
```

## H) Cleanup

```bash
docker compose down
rm -rf out
```

Add `-v` to `docker compose down` to also delete the Prometheus/Grafana data
volumes.
