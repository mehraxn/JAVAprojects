# Prometheus Grafana Monitoring

*A local Prometheus + Grafana monitoring lab for a Java service — real
Prometheus-format metrics emitted by the app, a Compose stack that scrapes
them, provisioned datasource/dashboard, alert rules over real metrics, and a
demo `/work` endpoint to drive traffic, latency, and errors.*

## What this project is

A hands-on lab for the complete metrics path: instrumentation → scrape →
time-series storage → PromQL → alert-rule evaluation → dashboards. The whole
stack runs locally with Docker Compose and was actually run and verified —
see [TEST_RESULTS.md](TEST_RESULTS.md).

## What it includes

- **Java 21 app** (built-in `HttpServer`, no dependencies) with `/`,
  `/health`, `/metrics`, and a **`/work` demo endpoint**:
  `/work` → 200, `/work?ms=100` simulates latency (capped at 5000ms),
  `/work?fail=1` → intentional 500 for alert testing
- **Real metrics**: `http_requests_total{method,route,status}` counter,
  `http_request_duration_seconds` histogram, JVM heap gauges,
  `process_uptime_seconds`, `app_info{version}` — with **bounded route
  labels** (unknown paths record `route="not_found"`, never the raw URL)
- **Prometheus** (`monitoring/prometheus.yml`) scraping the app every 15s
- **Alert rules** (`monitoring/alert-rules.yml`) that use only emitted
  metrics: target down, 5xx error ratio > 5%, p95 latency > 500ms, heap > 85%
- **Grafana provisioning**: Prometheus datasource (stable uid `prometheus`)
  and the dashboard `grafana/dashboards/java-app-dashboard.json` (target
  status, request rate by route, error ratio, p95 latency, heap, uptime,
  version)
- **Docker Compose stack** with healthchecks on all three services and
  health-gated startup (app → Prometheus → Grafana)

## Quick start

```bash
cp .env.example .env      # ships a local demo Grafana password; edit if you like
docker compose config     # sanity-check
docker compose up -d --wait

# generate traffic
curl "http://localhost:8080/work?ms=100"
curl "http://localhost:8080/work?fail=1"

# look around
open http://localhost:9090/targets    # java-app should be UP
open http://localhost:3000            # login from .env; dashboard is provisioned

docker compose down
```

The app image is the versioned local tag `prometheus-grafana-java-app:0.1.0`
(never `latest`); Prometheus and Grafana images are pinned versions.

## Route behavior (exact matching)

`HttpServer` contexts are prefix-matched, so handlers enforce exact paths:
`/health/test` and `/unknown` return 404 (recorded as `route="not_found"`),
wrong methods return 405, and `/work?fail=1` returns 500 on purpose.

## What is implemented (and verified)

Everything in [TEST_RESULTS.md](TEST_RESULTS.md) was actually run on
2026-07-10: compile, all endpoint behaviors, Docker build, `promtool check
config/rules`, the full Compose stack reaching all-healthy, Prometheus
scraping the app (`up == 1`, PromQL returning real counts), and Grafana's
provisioned datasource + dashboard confirmed via its API. Alert rules were
loaded and evaluating; no alert was driven all the way to firing.

## What is not production-grade

- **No Alertmanager** — rules evaluate, but nothing routes notifications.
- **No long-term storage** — a single local Prometheus volume.
- **No auth beyond the local Grafana password**; nothing is TLS-protected.
- **No cloud deployment, no SLO process** — thresholds are demo values, not
  measured SLOs.
- The hand-rolled metrics registry is educational; production Java should use
  a maintained instrumentation library (e.g. Micrometer or the Prometheus
  Java client).

## How to validate

Exact commands: [TESTING.md](TESTING.md). Recorded results:
[TEST_RESULTS.md](TEST_RESULTS.md). Metric semantics and PromQL guide:
[app-metrics-example.md](app-metrics-example.md).

## Resume Value

Instrumented a Java service with Prometheus metrics and assembled a local Docker Compose monitoring stack with scrape configuration, alerts, provisioned Grafana dashboards, and recorded runtime checks.

## Possible future improvements

- Alertmanager with reviewed routing/ownership.
- Recording rules for frequently used queries; GC/thread/CPU metrics.
- Configuration validation and dashboard-schema checks in CI.
- Measured SLO-based alerts after collecting representative data.
