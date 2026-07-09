# Test Results — Full Observability Platform

Real results from running the stack locally on **2026-07-09**. Commands are in
[TESTING.md](TESTING.md). This is a **local demo**: no real secrets, no cloud, no
`terraform`-style external infra. `docker compose down` was run afterward.

- Host: Windows 11, Docker 29.4.2, Docker Compose v5.1.3.
- **No local JDK** — Java is compiled and run inside the Docker image (the build
  proves the source compiles). Endpoint tests hit the containerized app.

## Java compile

- **PASS.** `docker build` compiles `src/fullobservabilityplatform/*.java` with
  `javac` in the `eclipse-temurin:21-jdk` stage; the image
  `observable-java-app:0.1.0` built successfully. (No standalone local `javac`
  run — no JDK on the host.)

## Docker build

- **PASS.** `docker build -t observable-java-app:0.1.0 -f docker/Dockerfile.example .`
  succeeded (multi-stage: JDK build → JRE-alpine runtime, non-root user 10001).

## docker compose config

- **PASS.** `docker compose config` validated and printed the merged config with
  no errors.

## docker compose up

- **PASS.** All 7 services reached `Up`: app, prometheus, loki, promtail, tempo,
  otel-collector, grafana.

## Java endpoint results (containerized app, port 8080)

Observed HTTP status codes:

| Path | Status | Notes |
| --- | --- | --- |
| `/` | **200** | `{"message":"observability example"}` |
| `/health` | **200** | `{"status":"UP"}` |
| `/work` | **200** | `{"worked":true}` |
| `/work?fail=1` | **500** | forced failure (intentional) |
| `/metrics` | **200** | Prometheus text |
| `/unknown` | **404** | unknown route (fixed — no longer 200) |

Every response also carried a `traceparent` header, e.g.
`00-d7a71a9174ba772c9607f9714cdffd17-f596daf89a99bc8b-01`.

## /metrics result

- **PASS.** Real series present, e.g.
  `http_requests_total{method="GET",route="/work",status="200"} 41`,
  `http_requests_total{method="GET",route="/work",status="500"} 4`,
  `http_requests_total{method="GET",route="/unknown",status="404"} 1`, plus the
  `http_request_duration_seconds` histogram, `jvm_memory_used_bytes`,
  `process_uptime_seconds`, and `app_info`.

## Prometheus target verification

- **PASS.** `GET /api/v1/targets`: `observable-java-app -> up`,
  `otel-collector -> up`, `prometheus -> up`.
- `up{job="observable-java-app"} == 1`.
- Alert rules loaded (`GET /api/v1/rules`): `JavaAppTargetDown`,
  `HighHttpErrorRate`, `HighRequestLatencyP95`, `JvmHeapHigh` (evaluated by
  Prometheus; not routed — no Alertmanager, by design).
- Dashboard PromQL returned real values under load, e.g. 5xx ratio ≈ `0.038`,
  p95 latency ≈ `0.34s`.

## Loki log query verification

- **PASS.** `{service="observable-java-app"}` returned a log stream with labels
  `{service, job, level, env, event, filename=/var/log/app/app.log}`. Each line
  is the app's JSON with `method`, `path`, `status`, `duration_ms`, `trace_id`,
  `span_id`. `trace_id`/`span_id` are **in the body, not labels** (verified — no
  high-cardinality label).

## Tempo trace verification

- **N/A by design — no real span export.** Traces are correlation-only: the app
  produces `trace_id`/`span_id` in logs and the `traceparent` header but does not
  export OpenTelemetry spans. A Tempo search for `service.name=observable-java-app`
  correctly returned `{"traces":[]}`. Tempo itself is **running and healthy** and
  is provisioned as a Grafana datasource. See [docs/traces.md](docs/traces.md).
  (The OpenTelemetry Java agent was tested and does **not** instrument this JDK
  `com.sun.net.httpserver` app, so it was not shipped; real export would need
  manual OTel SDK instrumentation.)

## Grafana datasource/dashboard provisioning result

- **PASS.** `GET /api/datasources` (admin/admin) returned provisioned
  **Prometheus** (uid `prometheus`), **Loki** (uid `loki`), and **Tempo** (uid
  `tempo`) — the same UIDs the dashboard JSON references.
- `GET /api/search?type=dash-db` shows **"Full Observability Platform - Example"**
  in the **Observability Lab** folder (auto-provisioned).
- `GET /api/health` → `{"database":"ok",...}`.

## Cleanup

- `docker compose down` removed all containers and the network. Generated
  `logs/app/*.log` and `out/` were removed. `logs/**/*.log` is git-ignored; only
  the `.gitkeep` placeholders are tracked.

## Known limitations

- **Traces are correlation-only** — no spans are exported to Tempo (see above).
- Local demo: signals are in-container and **discarded on `docker compose down`**
  (no persistence, no retention tuning, no long-term storage).
- **No alert routing** — rules are evaluated but there is no Alertmanager /
  notifications.
- **No access control** beyond a placeholder Grafana password (`admin/admin`),
  no TLS, no multi-tenancy, no cloud deployment.
- Loki/Tempo `/ready` shows a brief "waiting 15s after being ready" grace window
  right after start; both were functional (Loki served queries) once warm.
