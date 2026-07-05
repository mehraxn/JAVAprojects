# Traces

Covers the traces pillar. **No collector or trace backend was run.** This
explains distributed tracing and the role of the configs in
[`../monitoring`](../monitoring).

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

## Distributed tracing & context propagation

"Distributed" means the trace crosses process/service boundaries. That works
because the `trace_id` and parent `span_id` travel with the request in a
**W3C `traceparent` header**:

```
traceparent: 00-<32-hex trace_id>-<16-hex span_id>-01
```

Each service reads the incoming `traceparent`, creates a child span under the
same `trace_id`, and passes an updated header downstream. The app in this lab
does exactly this in `extractOrNewTraceId(...)`: it reuses an inbound trace id if
present, mints one otherwise, and returns a `traceparent` header — and it stamps
the same ids into every log line so **logs and traces correlate**.

## The pipeline: app → OTel Collector → Tempo → Grafana

```
app  ──OTLP──▶  OTel Collector  ──OTLP──▶  Tempo  ──▶  Grafana (search by trace_id)
```

- In production the **OpenTelemetry Java agent** auto-instruments the app and
  exports spans over **OTLP** (gRPC :4317 / HTTP :4318). This lab generates ids
  in-process to demonstrate correlation without that dependency.
- The **Collector** ([`../monitoring/otel-collector.example.yml`](../monitoring/otel-collector.example.yml))
  receives OTLP, batches, scrubs attributes, and exports traces to Tempo.
- **Tempo** ([`../monitoring/tempo.example.yml`](../monitoring/tempo.example.yml))
  stores spans and lets Grafana fetch a full trace by `trace_id`.

## OpenTelemetry Collector role

The Collector is the **vendor-neutral router** for all telemetry. The app speaks
only OTLP; the Collector decides where each signal goes (traces→Tempo,
metrics→Prometheus scrape endpoint, logs→Loki/debug). Swapping Tempo for Jaeger,
or adding redaction, is a change to the Collector config — **not** a code change.
It also protects backends via `batch` and `memory_limiter` processors.

## Sampling

Tracing every request is expensive at scale, so real deployments **sample**
(head-based at the SDK, or tail-based in the Collector to keep all error/slow
traces). No sampling policy is enforced here because nothing runs.

## What was NOT done

- No OTel agent, Collector, or Tempo was started; no span was exported or stored.
- No trace was searched or visualized.
