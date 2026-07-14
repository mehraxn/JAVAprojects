# Test Results — Prometheus Grafana Monitoring

Validation performed on **2026-07-10** on Windows 11 with Docker Desktop
(engine 29.4.2). No JDK or promtool was installed on the host, so compilation
ran in the official `eclipse-temurin:21-jdk` container and promtool ran via
the `prom/prometheus:v2.54.1` image. Everything below was actually run.

## Java compile result — PASS

```
javac -Xlint:all -Werror --add-modules jdk.httpserver -d out src/prometheusgrafanamonitoring/*.java
```

Compiled cleanly (all lints, warnings as errors).

## Java endpoint tests — PASS

Tested against the built image (`docker run -p 18080:8080`):

| Request | Result |
| --- | --- |
| `GET /` | 200 `{"message":"Monitoring example"}` |
| `GET /health` | 200 `{"status":"UP"}` |
| `GET /metrics` | 200, Prometheus text format |
| `GET /work` | 200 `{"worked_ms":0}` |
| `GET /work?ms=100` | 200 `{"worked_ms":100}` |
| `GET /work?fail=1` | **500** `{"error":"intentional failure for alert testing"}` |
| `GET /unknown` | 404 |
| `GET /health/test` | 404 (exact routes) |
| `POST /health`, `POST /metrics` | 405 |

## /metrics result — PASS

Real Prometheus exposition confirmed, with **bounded route labels**:

```
http_requests_total{method="GET",route="/work",status="200"} 4
http_requests_total{method="GET",route="/work",status="500"} 2
http_requests_total{method="GET",route="not_found",status="404"} 4
http_requests_total{method="POST",route="/health",status="405"} 1
app_info{version="0.1.0"} 1
```

Plus `http_request_duration_seconds` histogram (buckets/sum/count),
`jvm_memory_used_bytes` / `jvm_memory_max_bytes`, `process_uptime_seconds`.
Unknown paths record `route="not_found"` — never the raw URL.

## Docker build result — PASS

`docker build -t prometheus-grafana-java-app:0.1.0 .` succeeded (multi-stage,
non-root).

## docker compose config result — PASS

`docker compose config --quiet` exited 0 (with `.env` copied from
`.env.example`).

## docker compose up result — PASS

`docker compose up -d --wait`: all three services reached **healthy** — the
wget healthchecks work in all three images, and startup was health-gated in
order app → Prometheus → Grafana.

## Prometheus target status — PASS

`curl http://localhost:9090/-/ready` → `Prometheus Server is Ready.`

```
up{environment="learning",instance="app:8080",job="java-app"} = 1
```

## PromQL query result — PASS

After sending 30 requests (10× `/work`, 10× `/work?ms=100`, 10×
`/work?fail=1`) and waiting one scrape interval:

```
sum(http_requests_total{job="java-app",status="500"}) = 10
```

— exactly the 10 intentional failures.

## Grafana datasource provisioning result — PASS

`GET /api/health` → `database: ok, version 11.2.0`. `GET /api/datasources`
shows the provisioned **Prometheus** datasource with uid `prometheus`
(matching every dashboard panel), read-only, default.

## Grafana dashboard provisioning result — PASS

`GET /api/search` shows **"Java Application Monitoring"** (uid
`java-app-monitoring`) provisioned in the *Learning* folder. The dashboard
JSON was also parsed programmatically before deployment.

## Alert rules / promtool result — PASS

- promtool (via the `prom/prometheus:v2.54.1` image):
  `check config` → valid, 1 rule file found; `check rules` → **4 rules found**.
- Live Prometheus `/api/v1/rules`: all four rules (`JavaAppTargetDown`,
  `JavaAppHighErrorRatio`, `JavaAppHighLatency`, `JavaAppHighHeapUsage`)
  loaded and evaluating (state `inactive` under normal traffic).
- **No alert was driven to firing** — that requires sustained (5m+) error or
  latency traffic; the commands to do so are in TESTING.md section E.

## Tools unavailable

- JDK on the host — compile ran in the Temurin container.
- promtool on the host — ran via the Prometheus image.

## Known limitations

- No Alertmanager: rules evaluate, but nothing routes notifications.
- Alert thresholds are demo values, not measured SLOs; no alert firing was
  observed end-to-end.
- Single local Prometheus volume; no long-term storage, auth, or TLS.
- The hand-rolled metrics registry is educational — production Java should
  use a maintained instrumentation library.
- Results are a point-in-time snapshot of one validation run; the stack was
  torn down afterward (`docker compose down`).
