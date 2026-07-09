# Observability Flow

```text
Java app ──/metrics──────────────▶ Prometheus ─┐
Java app ──JSON log file──▶ Promtail ▶ Loki ────┼─▶ Grafana (metrics + logs)
Java app ──trace_id in logs/headers (correlation)┘

  Tracing backend extension (running, not fed by the app):
  [ future OTLP exporter ] ──▶ OTel Collector ──▶ Tempo ──▶ Grafana
```

Ports (host): app 8080, Prometheus 9090, Loki 3100, Tempo 3200,
OTel Collector OTLP-HTTP 4318 / metrics 8889, Grafana 3000.

The stack is a local demo brought up with `docker compose up -d`. Signals are
in-container and disposable; `docker compose down` leaves a clean machine.
Traces are correlation-only — the app produces `trace_id`/`span_id` for log and
header correlation but does not export spans (see ../docs/traces.md).
