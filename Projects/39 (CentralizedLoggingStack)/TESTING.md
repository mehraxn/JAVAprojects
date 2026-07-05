# Testing Centralized Logging Stack

No Java, Docker, Compose, Loki, Promtail, Grafana, ingestion, query, retention, or alert command was executed while preparing this project.

## Static validation checklist

- [ ] Review JSON escaping, field validation, and one-event-per-line output.
- [ ] Confirm synthetic example lines parse as JSON.
- [ ] Confirm Promtail timestamp format matches Java timestamps.
- [ ] Confirm label fields are bounded and trace/event fields remain unindexed.
- [ ] Confirm LogQL rule selector matches the Promtail job label.
- [ ] Review Loki storage, schema, retention, and ruler paths.

## File existence checks

- [ ] Java source, `Dockerfile`, and `docker-compose.yml` exist.
- [ ] `logging/loki-config.yml` exists.
- [ ] `logging/promtail-config.yml` exists.
- [ ] Example ruler and Grafana data-source files exist.
- [ ] Synthetic log example and format documentation exist.
- [ ] `docs/logging-pipeline.md`, `.env.example`, `README.md`, and `TESTING.md` exist.

## Configuration review checklist

- [ ] App and Promtail share the same log-volume destination.
- [ ] Promtail positions path uses its writable named volume.
- [ ] Promtail client URL matches Loki's service and port.
- [ ] Local rule directory matches the mounted tenant path.
- [ ] Grafana reaches Loki through the internal network.
- [ ] Retention duration and image versions are deliberate learning choices.

## Security checks

- [ ] No real secret, credential, token, private data, or production log is present.
- [ ] No production Loki, Grafana, application endpoint, or tenant is present.
- [ ] `.env` is ignored and the example password is a placeholder.
- [ ] Example messages contain no connection strings, headers, or request bodies.

## Commands normally used - NOT executed

```text
javac -d out src/centralizedloggingstack/*.java
docker compose config
docker compose build
docker compose up
```

Service-specific configuration checks and LogQL queries would also normally be performed with approved Loki, Promtail, and Grafana tooling. None were executed.

## Expected results in a proper environment

- Java appends valid structured events to the shared log file.
- Promtail resumes from persisted positions and attaches bounded labels.
- Loki stores searchable streams and honors the accepted retention configuration.
- Grafana provisions Loki and can query by service/environment/level plus parsed event/trace fields.
- More than five matching ERROR entries over the configured window moves the example rule toward firing.
- No notification is sent because Alertmanager is intentionally absent.
