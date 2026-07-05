# Centralized Logging Stack

Starter Java structured-logging application with a local Loki, Promtail, and Grafana configuration layout.

## Structure

```text
src/centralizedloggingstack/
Dockerfile
docker-compose.yml
logging/loki.yml
logging/promtail.yml
logging/grafana/provisioning/datasources/loki.yml
docs/LOGGING.md
TESTING.md
```

## Current assumption

Loki, Promtail, and Grafana are used only as a concrete starter choice. Confirm this stack before implementing the full logging pipeline.

## Safety and status

No container or logging service was started. The Java logger writes only locally, Grafana credentials are placeholders, and no production log data or secrets are included.

## Next implementation steps

- Confirm the logging stack and retention expectations.
- Define the structured log schema and redaction rules.
- Connect the shared application log volume to the collector.
- Add search examples and a dashboard only after logs are ingested.
