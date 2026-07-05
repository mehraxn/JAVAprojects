# Centralized Logging Stack

## Description

An educational centralized-logging stack using a small Java newline-delimited JSON logger, Promtail file collection, Loki aggregation and LogQL, and a provisioned Grafana data source.

## Goal

The goal is to understand how structured application events move from a local file into a centralized store where they can be searched, correlated, retained, and evaluated by basic log-derived alert rules.

## Technologies and concepts used

- Java structured JSON logging and validation
- Newline-delimited JSON event format
- Docker Compose shared and persistent volumes
- Promtail file discovery, positions, parsing pipelines, timestamps, and labels
- Loki streams, filesystem storage, retention, LogQL, and ruler concepts
- Grafana Loki data-source provisioning
- Label-cardinality, redaction, and correlation-ID practices

## Project structure

```text
src/centralizedloggingstack/               Java logger and demo process
Dockerfile                                 Non-root Java image
docker-compose.yml                         App, Loki, Promtail, and Grafana
logging/loki-config.yml                    Local single-process Loki settings
logging/promtail-config.yml                File collection and JSON pipeline
logging/rules/fake/app-log-alerts.yml       Example error-burst rule
logging/grafana/provisioning/datasources/loki.yml
examples/java-app.log.example              Synthetic JSON log lines
example-log-format.md                       Field contract
docs/logging-pipeline.md                    End-to-end explanation
.env.example                               Placeholder local settings
README.md                                  Project documentation
TESTING.md                                 Validation guide
```

## Important files explained

- `StructuredLogger.java` validates levels/events, escapes JSON, and writes one event per line.
- `Main.java` emits startup and heartbeat events for a future learning run.
- `promtail-config.yml` tails `/var/log/java-app/*.log`, tracks offsets, parses JSON time, and promotes only bounded fields to labels.
- `loki-config.yml` defines single-process filesystem storage and seven-day learning retention.
- `app-log-alerts.yml` counts bursts of ERROR entries; no notification destination is configured.
- The Grafana provisioning file makes Loki available for Explore/LogQL queries.

## Intended real-environment workflow

For an approved local exercise, copy `.env.example` to ignored `.env`, replace the Grafana password placeholder, review `docker compose config`, build the Java app, and start the stack. Promtail would read the shared app-log volume, persist positions, parse each JSON line, and push entries to Loki. Grafana would query Loki by bounded labels and parsed fields.

Before using real logs, define redaction, retention, access, deletion, and incident-handling rules. The stack must never ingest passwords, tokens, connection strings, personal data, or raw request bodies.

## Prepared but not executed

- Structured logger, sample logs, Compose services, Loki/Promtail configs, Grafana provisioning, retention, and an example rule were prepared.
- Java, Docker, Compose, Loki, Promtail, Grafana, ingestion, LogQL, ruler evaluation, and retention were not executed.
- No log was written, collected, indexed, queried, alerted on, or displayed.
- No working centralized-logging or deployed-service claim is made.

## Manual validation checklist

- [ ] Confirm every sample/application line is valid one-line JSON.
- [ ] Confirm Promtail path matches the app's shared volume path.
- [ ] Confirm Loki URL and Compose service name agree.
- [ ] Confirm positions use a persistent writable volume.
- [ ] Confirm only level, service, and environment become labels.
- [ ] Query by event/trace ID as parsed fields, not labels.
- [ ] Review retention and alert thresholds against approved data.

## Common mistakes avoided

- No real Grafana credential or production log is committed.
- Trace IDs, event values, messages, users, and URLs are not high-cardinality labels.
- Base64/JSON structure is not confused with secret protection.
- Positions are persistent and separate from read-only app logs.
- Log-derived alerts are not treated as complete health monitoring.
- Ruler evaluation is distinguished from Alertmanager notification delivery.

## Possible future improvements

- Add reviewed log rotation for the application file.
- Add authentication/TLS between collectors, Loki, and Grafana.
- Use durable object storage and a resilient topology for non-local use.
- Add parsing-failure metrics and ingestion dashboards.
- Add trace-system links without promoting trace IDs to labels.
- Add Alertmanager only with reviewed ownership and routing.
