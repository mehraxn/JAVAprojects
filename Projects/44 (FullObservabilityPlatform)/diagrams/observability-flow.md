# Observability Flow

```text
Java app -> metrics -> Prometheus -> Grafana
Java app -> logs -----------------> Loki -> Grafana
Java app -> OTLP -> Collector ----> trace backend -> Grafana
```

Diagram is conceptual only. No backend or connection was created.
