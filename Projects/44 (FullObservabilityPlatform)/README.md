# Full Observability Platform

*A complete observability stack for a Java service — metrics, logs, and traces correlated through Prometheus, Loki, Tempo, an OpenTelemetry Collector, and Grafana.*

## Problem this project solves

When something breaks in production, one signal is rarely enough: metrics tell
you *that* it's slow, logs tell you *what* happened, traces tell you *where* the
time went. This project wires up **all three pillars** for one Java app and — the
hard part — **correlates** them through a shared `trace_id`, so you can pivot from
a latency spike to the exact failing request.

## Technologies & concepts

- **Java 21** app emitting all three signals from one place
- **Prometheus** (metrics, pull-based scraping) + **Grafana** (single pane of glass)
- **Loki + Promtail** (logs); **Tempo** (traces)
- **OpenTelemetry Collector** (vendor-neutral receive → process → export)
- **W3C trace context**, PromQL/LogQL, RED-method alerting

## Architecture overview

```
app ──/metrics──────────────▶ Prometheus ─┐
app ──stdout JSON──▶ Promtail ▶ Loki ──────┤
app ──OTLP──▶ OTel Collector ▶ Tempo ──────┼─▶ Grafana (metrics + logs + traces)
                       └──/metrics :8889──▶ Prometheus
```

## Project structure

```text
src/fullobservabilityplatform/Main.java   emits metrics + JSON logs + trace IDs
docker/Dockerfile.example  docker-compose.yml   full stack (NOT run)
monitoring/prometheus.yml  alerts.example.yml
monitoring/otel-collector.example.yml  tempo.example.yml
monitoring/grafana-dashboard.example.json
logging/loki-config.example.yml  promtail-config.example.yml
docs/metrics.md  docs/logs.md  docs/traces.md  docs/alerting.md
diagrams/observability-flow.md   README.md  TESTING.md
```

## Important files explained

- **Main.java** — `/metrics` (Prometheus), one structured JSON log per request, and a generated W3C `trace_id`/`span_id` returned in a `traceparent` header **and** stamped into every log line (that's the correlation).
- **prometheus.yml / alerts.example.yml** — 15s scrape + SLI/RED alerts (target-down, 5xx>5%, p95>1s, heap).
- **otel-collector.example.yml** — OTLP in → memory_limiter/resource/batch → Tempo / Prometheus / debug.
- **grafana-dashboard.example.json** — request rate, 5xx-ratio, latency p50/p95/p99 (`histogram_quantile`), JVM heap, and a Loki logs panel.
- **loki + promtail configs** — label-only indexing; `trace_id` kept in the body (not a label) to avoid cardinality blow-ups.

## How it would work in a real environment

`docker compose up` starts app + collector + Prometheus + Loki + Promtail +
Tempo + Grafana. Generate load against `/work`; Prometheus scrapes metrics,
Promtail ships logs to Loki, spans flow through the Collector to Tempo. In
Grafana you pivot: latency spike (metric) → error logs at that moment (Loki via
`trace_id`) → the exact span tree (Tempo).

## What was prepared but NOT executed

Prepared: the app and every config. **Not executed:** no image built, no
`docker compose up`, no Prometheus scrape, no alert evaluation, no logs shipped,
no spans exported, **no dashboard imported**. All configs are static examples;
**observability was not tested.**

## Security notes

- **No real secrets/credentials** — Grafana admin password is a placeholder only.
- No external endpoints — the Collector exports only in-stack (Tempo/Prometheus/debug).
- **Label-cardinality discipline**: high-cardinality fields (`trace_id`, user IDs) never become metric/log labels.
- Image tags are pinned for realism; nothing is pushed.

## Limitations

- The stack was never run; no signal was collected, stored, or rendered.
- No sampling policy enforced; the app's `/metrics` is illustrative (not the HPA/OTLP path).
- Java compilation not run (no JDK on the authoring machine).

## Future improvements

- Add the OpenTelemetry Java agent for real auto-instrumentation + tail-based sampling.
- Alertmanager routing (Slack/PagerDuty) and SLO burn-rate alerts.
- Exemplars linking metrics directly to traces; long-term metric storage.

## What I learned

- The distinct jobs of **metrics vs logs vs traces** and when each answers the question.
- How **trace-context propagation** correlates logs and traces.
- The **OpenTelemetry Collector** as a swappable router so the app only speaks OTLP.
- Why **label cardinality** is the make-or-break cost decision in metrics/logs.
