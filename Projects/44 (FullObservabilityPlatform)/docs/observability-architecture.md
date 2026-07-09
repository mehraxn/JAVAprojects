# Observability Architecture

This is a **local, runnable** observability lab for one Java service. Metrics
answer aggregate questions ("how many requests/sec, what's p95?"), logs preserve
discrete events ("why did request X fail?"), and traces show where in a request
path the time or error went. A shared service name, environment, and trace
context tie the signals together without creating high-cardinality indexes.

`docker compose up -d` builds the app image and starts Prometheus, Loki,
Promtail, Grafana, and — as a tracing-backend extension — the OpenTelemetry
Collector and Tempo. Metrics and logs are fully exercised; traces are
**correlation-only** (see below). Everything is validated locally (see
[../TEST_RESULTS.md](../TEST_RESULTS.md)).

## The signals

```
app ──/metrics──────────────▶ Prometheus ─┐
app ──JSON log file──▶ Promtail ▶ Loki ────┼─▶ Grafana (metrics + logs)
app ──trace_id in logs/headers (correlation)┘
   (OTel Collector + Tempo run as a ready-but-unexercised tracing backend)
```

### Metrics
The app exposes a Prometheus text endpoint at `GET /metrics`
(`http_requests_total`, `http_request_duration_seconds` histogram,
`jvm_memory_used_bytes`, `process_uptime_seconds`, `app_info`). Prometheus
scrapes `app:8080/metrics` every 15s. Labels are kept low-cardinality: requests
are labelled by `method`, `route` (template, not raw path), and `status` — never
by IDs. See [metrics.md](metrics.md).

### Logs
The app writes **one JSON object per request** to both stdout and
`/var/log/app/app.log`. Each line carries `timestamp, level, service, env,
method, path, status, duration_ms, trace_id, span_id`. Promtail tails the file,
promotes only low-cardinality fields (`level`, `env`, `event`) to Loki labels,
and ships the full line as the body. `trace_id`/`span_id` stay in the body
(searchable) and are **never** labels. See [logs.md](logs.md).

### Traces (correlation-only)
The app generates W3C `trace_id`/`span_id` per request, returns them in a
`traceparent` header, and stamps them into every log line — so logs carry trace
context and one request's `trace_id` links its log lines together. The app does
**not** export OpenTelemetry spans (the OTel Java agent does not instrument the
JDK `com.sun.net.httpserver` this app uses — verified). The OTel Collector and
Tempo run as a ready tracing backend that the app does not feed. See
[traces.md](traces.md) for the honest boundary and how to make traces real.

## Trace-context correlation

Every request gets a W3C `traceparent` (`00-<trace_id>-<span_id>-01`) returned in
the response header and written into every log line. An inbound `traceparent` is
reused so context propagates across hops. This lets Grafana pivot from a log line
to a trace (a Loki→Tempo derived-field link is provisioned on the `trace_id`
field).

## Sampling

Tracing every request is expensive at scale. This lab exports every span (no
sampling) because the volume is tiny; a real deployment would sample —
head-based at the SDK/agent, or **tail-based in the Collector** to keep all
error and slow traces while dropping most successful ones.

## Redaction

Sensitive data must not reach a backend. The Collector's `resource` processor
demonstrates governance by stamping `deployment.environment` and **deleting a
`user.email` attribute** as an example scrub. Logs contain no secrets, and
high-cardinality/PII fields are never promoted to labels.

## Retention

This lab keeps signals in-container and disposable — `docker compose down`
discards them; there are no persistent volumes and no long-term store. In
production each backend gets a retention policy sized to cost/compliance: short
hot retention for logs/traces (e.g. days) with cheaper object storage, and
longer downsampled retention for metrics.

## Dashboarding

Grafana is auto-provisioned (datasources with stable UIDs `prometheus`, `loki`,
`tempo`, plus one dashboard) so the "Full Observability Platform" dashboard
appears on first start. Panels: request rate by route, 5xx error ratio, latency
p50/p95/p99 (`histogram_quantile`), JVM heap, and a Loki logs panel.

## Alerting

Prometheus loads SLI/RED alert rules ([alerting.md](alerting.md)): target-down
(`up == 0`), 5xx error ratio > 5%, p95 latency > 1s, and high JVM heap. These are
**local-demo examples** and are evaluated by Prometheus but **not routed** —
Alertmanager is intentionally omitted so nothing tries to notify anyone.

## Incident workflow (how you'd use it)

1. An alert (or a dashboard glance) shows a symptom — e.g. rising 5xx ratio.
2. **Metrics** localize it: which route, since when, how bad (rate/latency panels).
3. **Logs** explain it: open the Loki panel, filter `level="ERROR"`, read the
   failing request lines (method/path/status/duration).
4. **Trace context**: use the failing line's `trace_id` to gather every log line
   for that request (and, once real span export is added, jump to its Tempo
   trace via the provisioned Loki→Tempo link).
5. Fix, then confirm the metric recovers and the alert clears.

## What is implemented locally

- Java app emitting metrics, JSON file logs, and W3C trace context.
- Prometheus scraping + SLI alert rules; Grafana dashboard + datasource provisioning.
- Loki + Promtail collecting the app's JSON logs.
- OTel Collector + Tempo running as a tracing backend, provisioned in Grafana.

## What is example-only / not production-ready

- **Traces are correlation-only** — the app does not export spans, so Tempo
  receives none from it (see [traces.md](traces.md) to make it real).
- No persistence, no access control beyond a placeholder Grafana password.
- No alert routing (no Alertmanager), no sampling policy, no long-term storage.
- No TLS, no multi-tenant isolation, no cloud deployment.
