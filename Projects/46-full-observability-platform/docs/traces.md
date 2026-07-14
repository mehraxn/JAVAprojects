# Traces

Covers the traces pillar. This lab demonstrates **trace-context correlation** in
logs and headers. It does **not** export real OpenTelemetry spans — the OTel
Collector and Tempo are included and running as an example tracing backend, ready
for real instrumentation (see *Future improvements* below).

## What a trace is

A **trace** follows a single request as it flows through a system. It is a tree
of **spans**, where each span is one unit of work (an HTTP handler, a DB call, an
outbound request) with a start time, duration, and attributes. Every span shares
one `trace_id`; each has its own `span_id` and a `parent_span_id` linking it to
its caller.

```
trace_id = 4bf92f...
└─ span: GET /work           (root, span_id=00f067..., 220ms)
   ├─ span: db.query users   (span_id=a1b2..., 40ms)
   └─ span: http GET billing (span_id=c3d4..., 120ms)  ← latency lives here
```

Traces answer the question metrics and logs cannot: **where in the request path
did the time (or the error) actually go?**

## What this lab implements: trace-context correlation

Every request gets a W3C trace context, propagated in the standard header:

```
traceparent: 00-<32-hex trace_id>-<16-hex span_id>-01
```

`extractOrNewTraceId(...)` reuses an inbound `traceparent` if present, mints one
otherwise, returns it as a response header, and stamps the same `trace_id`/
`span_id` into every JSON log line. So you can:

- follow one request's `trace_id` across log lines, and
- propagate context to a downstream service via the header.

This is **real correlation**, and it is what the Loki→Tempo derived-field link in
the Grafana datasource provisioning is wired to use.

## What this lab does NOT do (honest boundary)

The app does **not** export OpenTelemetry spans, so **Tempo receives no traces
from the app** and a Tempo search for the service returns nothing. Why not
auto-magically?

- The OpenTelemetry **Java agent** auto-instruments many HTTP servers (Servlet,
  Netty, etc.) but **not** the JDK built-in `com.sun.net.httpserver` this app
  uses — verified: attaching the agent produced no spans for these requests.
- Exporting real spans would therefore require **manual instrumentation** with
  the OpenTelemetry SDK/API (create a span per request, export via OTLP). That
  adds a real dependency to this deliberately dependency-free app.

Rather than pretend, this lab keeps the app dependency-free and correlation-only,
and ships the tracing backend ready to receive spans.

## The tracing backend (included, ready, not exercised)

```
[ app would export OTLP ] ──▶ OTel Collector ──OTLP──▶ Tempo ──▶ Grafana
```

- The **Collector**
  ([`../monitoring/otel-collector.example.yml`](../monitoring/otel-collector.example.yml))
  runs and listens for OTLP (gRPC :4317 / HTTP :4318), ready to route spans to
  Tempo.
- **Tempo** ([`../monitoring/tempo.example.yml`](../monitoring/tempo.example.yml))
  runs and is provisioned as a Grafana datasource (uid `tempo`).

## Sampling

Tracing every request is expensive at scale, so real deployments **sample**
(head-based at the SDK, or tail-based in the Collector to keep all error/slow
traces). No sampling policy is exercised here because the app exports no spans.

## Future improvements (to make traces real)

1. Add the OpenTelemetry SDK/API to the app and create a SERVER span per request
   in the `observed(...)` wrapper, using the span's own ids for the logs so the
   in-log `trace_id` equals the exported span id.
2. Export via OTLP to `http://otel-collector:4318` (already running); the
   Collector forwards to Tempo, and the Grafana Tempo datasource makes traces
   searchable. Then this doc and the README can claim real Tempo export.
