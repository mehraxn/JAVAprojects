# Full Observability Platform

*A local, runnable observability lab for one Java service — Prometheus metrics,
structured JSON logs shipped through Promtail to Loki, Grafana dashboards and
alert rules, plus W3C trace-context correlation across headers and logs. Tempo
and the OpenTelemetry Collector are included as extension-ready tracing
backends, but the app does not export real OTLP spans by default.*

**Implemented and validated:** Java service · Prometheus metrics · JSON logs ·
Promtail → Loki · Grafana datasource + dashboard provisioning · Prometheus alert
rules · `trace_id`/`span_id` correlation · `traceparent` response header.
**Extension-ready (not exercised):** Tempo · OpenTelemetry Collector · real OTLP
span export.

## Problem this project solves

When something breaks, one signal is rarely enough: metrics tell you *that* it's
slow, logs tell you *what* happened, traces tell you *where* the time went. This
project wires up **metrics and logs end-to-end** for one Java app and adds
**trace-context correlation** (a shared `trace_id` across headers and log lines),
so you can pivot from a latency spike to the exact failing request — all on a
laptop with `docker compose up`. The tracing backend (Collector + Tempo) is
included extension-ready; the app does not export spans to it by default.

## What it includes

- A dependency-free **Java 21** app: Prometheus `/metrics`, structured **JSON
  logs** (to file), and W3C **trace IDs** in headers + logs.
- **Prometheus** (metrics + SLI alert rules), **Loki + Promtail** (logs),
  **Tempo** (traces), the **OpenTelemetry Collector** (OTLP router).
- **Grafana**, auto-provisioned with datasources (stable UIDs `prometheus`,
  `loki`, `tempo`) and a dashboard.

## What is implemented (be specific)

- **Metrics** — real: Prometheus scrapes `app:8080/metrics` every 15s.
- **Logs** — real: the app writes JSON to `/var/log/app/app.log`; Promtail ships
  it to Loki; queryable in Grafana with LogQL.
- **Traces** — **correlation-only (honest)**: the app generates W3C
  `trace_id`/`span_id`, returns them in a `traceparent` header, and stamps them
  into every log line. It does **not** export OpenTelemetry spans — the OTel
  Java agent does not instrument the JDK `com.sun.net.httpserver` this app uses
  (verified). The OTel **Collector + Tempo** run as a ready tracing backend that
  the app does not feed; see [docs/traces.md](docs/traces.md) for the boundary
  and how to make traces real.
- **Dashboards + alerts** — Grafana dashboard and Prometheus SLI/RED alert rules
  are provisioned/loaded (alerts are evaluated but **not routed** — no
  Alertmanager).

## Architecture

```
app ──/metrics──────────────▶ Prometheus ─┐
app ──JSON log file──▶ Promtail ▶ Loki ────┼─▶ Grafana (metrics + logs)
app ──trace_id in logs/headers (correlation)┘
   (OTel Collector + Tempo run as a ready-but-unexercised tracing backend)
```

See [docs/observability-architecture.md](docs/observability-architecture.md) for
the full design (metrics, logs, traces, sampling, redaction, retention,
dashboarding, alerting, and the incident workflow).

## Project structure

```text
src/fullobservabilityplatform/Main.java     metrics + JSON file logs + trace IDs
docker/Dockerfile.example                    app image (dependency-free, non-root)
docker-compose.yml                           the full stack (runnable)
monitoring/prometheus.yml  alerts.example.yml
monitoring/otel-collector.example.yml  tempo.example.yml
monitoring/grafana-dashboard.example.json
grafana/provisioning/{datasources,dashboards}/   auto-provision Grafana
logging/loki-config.example.yml  promtail-config.example.yml
(app-logs named volume)                      app writes app.log; Promtail reads it
docs/*.md   diagrams/observability-flow.md   README.md  TESTING.md  TEST_RESULTS.md
```

## How to run locally

**Java only** (needs a JDK 21) — quick endpoint check without the stack:

```bash
javac -d out src/fullobservabilityplatform/*.java
APP_PORT=8080 APP_LOG_FILE=/tmp/observable-java-app.log \
  java -cp out fullobservabilityplatform.Main
# APP_LOG_FILE picks a writable log path for local runs (default is
# /var/log/app/app.log, used inside Docker). If the path is unwritable the app
# logs to stdout only and keeps running.
```

**Docker image** (compiles Java in-container; no local JDK needed):

```bash
docker build -t observable-java-app:0.1.0 -f docker/Dockerfile.example .
```

**Full stack:**

```bash
docker compose up -d
# app http://localhost:8080 | Prometheus http://localhost:9090
# Grafana http://localhost:3000 (admin/admin)
```

## How to validate

Full command list is in [TESTING.md](TESTING.md); recorded real results are in
[TEST_RESULTS.md](TEST_RESULTS.md). In short:

- **Endpoints:** `curl localhost:8080/` `/health` `/work` `/work?fail=1`
  (returns 500) `/metrics` `/unknown` (returns 404).
- **Metrics:** Prometheus → Status → Targets shows `observable-java-app` **UP**;
  `http_requests_total` is queryable at `http://localhost:9090`.
- **Logs:** Grafana Explore → Loki → `{service="observable-java-app"}` shows the
  JSON request logs (with `trace_id` in the body).
- **Trace context:** every response carries a `traceparent` header and every log
  line a `trace_id`; the app exports no spans, so Tempo has no app traces (by
  design — see [docs/traces.md](docs/traces.md)).
- **Grafana:** the dashboard and the Prometheus/Loki/Tempo datasources appear
  automatically after `docker compose up`.

## Security notes

- **No real secrets** — the Grafana admin password is a placeholder (`admin`).
- No external endpoints — the Collector exports only in-stack (Tempo).
- **Label-cardinality discipline** — `trace_id`/`span_id`/user IDs are never
  promoted to Prometheus or Loki labels.
- The app image runs as a non-root user; the app image tag is pinned (`0.1.0`,
  not `latest`), as are all stack images.

## Example-Only Scope and Limitations

- **Real span export** — traces are correlation-only; adding it is a documented
  next step ([docs/traces.md](docs/traces.md)).
- Production **retention** and long-term storage (signals are in-container,
  discarded on `docker compose down`).
- **Access control** (only a placeholder Grafana password; no TLS/SSO).
- **Alert routing** (rules are evaluated but no Alertmanager / notifications).
- **Sampling** policy, multi-tenancy, and **real incident response** tooling.
- **Cloud deployment** — this runs locally only.

## Resume Value

Assembled and locally validated an observability stack combining Java metrics and structured logs with Prometheus, Loki, Promtail, Grafana provisioning, dashboards, alerts, and documented trace correlation boundaries.

## Future improvements

- **Make traces real:** add the OpenTelemetry SDK/API to the app, create a span
  per request, and export via OTLP to the already-running Collector → Tempo
  (see [docs/traces.md](docs/traces.md)).
- Tail-based sampling in the Collector; exemplars linking metrics→traces.
- Alertmanager routing (Slack/PagerDuty) and SLO burn-rate alerts.

## What I learned

- The distinct jobs of **metrics vs logs vs traces** and when each answers.
- How **trace-context propagation** correlates logs via a shared `trace_id`.
- The **OpenTelemetry Collector + Tempo** as the tracing backend, and honestly
  what the OTel Java agent does and does not auto-instrument.
- Why **label cardinality** is the make-or-break cost decision in metrics/logs.
