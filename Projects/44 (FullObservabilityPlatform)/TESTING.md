# Testing — Full Observability Platform

This is a **runnable** local lab. Commands below are exact; recorded real results
are in [TEST_RESULTS.md](TEST_RESULTS.md). Run from the project root
(`44 (FullObservabilityPlatform)/`). `curl`/URLs assume the default ports.

> Nothing here contacts a cloud or uses real secrets. `docker compose down`
> leaves a clean machine.

## 1. Java-only validation (needs a JDK 21)

```bash
javac -d out src/fullobservabilityplatform/*.java
APP_PORT=8080 java -cp out fullobservabilityplatform.Main
```

In another terminal:

```bash
curl -i http://localhost:8080/            # 200 {"message":"observability example"}
curl -i http://localhost:8080/health      # 200 {"status":"UP"}
curl -i http://localhost:8080/work        # 200 {"worked":true}
curl -i "http://localhost:8080/work?fail=1"  # 500 {"error":"internal"}
curl -i http://localhost:8080/metrics     # 200 Prometheus text
curl -i http://localhost:8080/unknown     # 404 {"error":"not found"}
```

Expected: `/unknown` returns **404**, `/work?fail=1` returns **500**. Without a
JDK, use the Docker build below (it compiles Java inside the container).

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

In Grafana **Explore → Loki**, run `{service="observable-java-app"}`. Or via API:

```bash
curl -s -G http://localhost:3100/loki/api/v1/query_range \
  --data-urlencode 'query={service="observable-java-app"}' | head -c 400
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
docker compose down
rm -rf out
rm -f logs/app/*.log
```

## 6. Notes

- Do not commit `logs/app/*.log` — it is git-ignored (only the `.gitkeep`
  placeholders are tracked).
- This is a **local demo**: no persistence, no alert routing, no access control
  beyond a placeholder Grafana password, no cloud deployment.
