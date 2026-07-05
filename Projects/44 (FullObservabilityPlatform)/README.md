# Full Observability Platform

Starter structure for correlating Java application metrics, structured logs, and distributed traces in one educational observability design.

## Structure

```text
src/fullobservabilityplatform/
docker/docker-compose.example.yml
monitoring/prometheus.yml
monitoring/otel-collector.example.yml
monitoring/alerts.example.yml
monitoring/grafana-dashboard.example.json
logging/loki-config.example.yml
docs/observability-architecture.md
diagrams/observability-flow.md
README.md
TESTING.md
```

## Status

Skeleton only. No instrumentation library, collector, metrics target, log pipeline, trace backend, dashboard, alert, container, or deployment was executed.

## Required confirmations

- Approved Java OpenTelemetry instrumentation approach
- Metrics, logs, and trace backends and versions
- Correlation-field and label-cardinality policy
- Retention, access, redaction, sampling, and alert ownership
