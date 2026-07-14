# Test Results — Full Observability Platform

Real results from running the stack locally on **2026-07-09** (re-validated the
same day after switching app logs to the `app-logs` **named Docker volume**).
Commands are in [TESTING.md](TESTING.md). This is a **local demo**: no real
secrets, no cloud deployment, no real alert routing. `docker compose down -v`
was run afterward.

- Host: Windows 11, Docker 29.4.2, Docker Compose v5.1.3.
- **No local JDK** — Java is compiled and run inside the Docker image (the build
  proves the source compiles). Endpoint tests hit the containerized app. The
  Java-only path in TESTING.md (with `APP_LOG_FILE=/tmp/...`) was therefore not
  run on this host.

## Java compile

- **PASS.** `docker build` compiles `src/fullobservabilityplatform/*.java` with
  `javac` in the `eclipse-temurin:21-jdk` stage; the image
  `observable-java-app:0.1.0` built successfully. (No standalone local `javac`
  run — no JDK on the host.)

## Docker build

- **PASS.** `docker build -t observable-java-app:0.1.0 -f docker/Dockerfile.example .`
  succeeded (multi-stage: JDK build → JRE-alpine runtime, non-root user 10001).

## docker compose config

- **PASS.** Validated cleanly after the named-volume change.

## docker compose up

- **PASS (re-run with the named volume).** All 7 services reached `Up`: app,
  prometheus, loki, promtail, tempo, otel-collector, grafana.

## Java endpoint results (containerized app, port 8080)

| Path | Status | Notes |
| --- | --- | --- |
| `/` | **200** | `{"message":"observability example"}` |
| `/health` | **200** | `{"status":"UP"}` |
| `/work` | **200** | `{"worked":true}` |
| `/work?fail=1` | **500** | forced failure (intentional) |
| `/metrics` | **200** | Prometheus text |
| `/unknown` | **404** | unknown route |

Every normal response carried a **`traceparent` header**, e.g.
`00-4a480a596de810a4c4b4201a50906661-7e0038cd17ff7d8e-01`.

## /metrics result

- **PASS.** Real series present: `http_requests_total{method,route,status}`
  (including `status="500"` and `status="404"` entries), the
  `http_request_duration_seconds` histogram, `jvm_memory_used_bytes`,
  `process_uptime_seconds`, and `app_info`.

## JSON logs written (named volume)

- **PASS.** Inside the app container, `/var/log/app/app.log` (on the `app-logs`
  named volume) contains one JSON object per request with `timestamp, level,
  service, env, event, method, path, status, duration_ms, trace_id, span_id`.
  The same lines go to stdout (`docker compose logs app`).

## Prometheus target verification

- **PASS.** `GET /api/v1/targets`: `observable-java-app -> up`,
  `otel-collector -> up`, `prometheus -> up`; and
  `up{job="observable-java-app"} == 1`.
- Alert rules loaded and evaluated (`JavaAppTargetDown`, `HighHttpErrorRate`,
  `HighRequestLatencyP95`, `JvmHeapHigh`) — local-demo examples, **not routed**
  (no Alertmanager, by design).

## Loki log shipping verification

- **PASS (via the named volume).** The exact documented query —
  `{service="observable-java-app"}` with `limit=5` against
  `/loki/api/v1/query_range` — returned streams whose labels are
  `service, job, level, env, event` and whose log bodies **contain `trace_id`
  and `span_id`** (verified). `trace_id`/`span_id` are not labels
  (high-cardinality discipline).

## Grafana datasource/dashboard provisioning result

- **PASS.** `GET /api/datasources` (admin/admin) returned provisioned
  **Prometheus** (uid `prometheus`), **Loki** (uid `loki`), **Tempo** (uid
  `tempo`). `GET /api/search?type=dash-db` shows **"Full Observability Platform -
  Example"** in the **Observability Lab** folder.

## Tempo app spans

- **Not implemented / not expected by default.** The app performs trace-context
  correlation only and does not export OTLP spans; a Tempo search for
  `service.name=observable-java-app` correctly returned `{"traces":[]}`. Tempo
  and the OTel Collector run healthy as **extension-ready** backends. (The OTel
  Java agent was tested earlier and does not instrument this JDK
  `com.sun.net.httpserver` app; real export would need OTel SDK instrumentation —
  see [docs/traces.md](docs/traces.md).)

## Cleanup

- **PASS.** `docker compose down -v` removed all containers, the network, and
  the `app-logs` volume (verified gone). No project containers left running.

## Known limitations

- **No real span export** — traces are correlation-only (see above).
- **No real cloud deployment**; local demo only.
- **No real alert routing** — rules evaluated, no Alertmanager/notifications.
- No persistence or retention tuning; signals are discarded on
  `docker compose down -v`.
- No access control beyond the placeholder Grafana password (`admin/admin`);
  no TLS, no multi-tenancy.
