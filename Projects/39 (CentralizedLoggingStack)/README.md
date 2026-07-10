# Centralized Logging Stack

## Description

A local, runnable centralized logging lab for a Java service. The app writes newline-delimited structured JSON logs to stdout or to a configured log file; Docker Compose configures a shared log volume, Promtail tails and parses those logs, Loki stores them, and Grafana is provisioned with a Loki datasource and dashboard for LogQL exploration.

## Goal

The goal is to understand how structured application events move from a local service log file into a centralized logging backend where they can be searched, filtered, retained, visualized, and used for simple log-derived alerts.

## Technologies and concepts used

- Java structured JSON logging and validation
- Newline-delimited JSON event format
- Docker Compose shared named volumes
- Promtail file discovery, positions, JSON parsing, timestamps, and labels
- Loki streams, filesystem storage, retention, LogQL, and ruler concepts
- Grafana Loki datasource and dashboard provisioning
- Demo error-log generation for alert testing
- Label-cardinality, redaction, tenant headers, and correlation-ID practices

## Project structure

```text
src/centralizedloggingstack/                 Java logger and demo process
Dockerfile                                   Non-root Java image
docker-compose.yml                           App, Loki, Promtail, and Grafana
logging/loki-config.yml                      Local single-process Loki settings
logging/promtail-config.yml                  File collection and JSON pipeline
logging/rules/learning/app-log-alerts.yml    Example ERROR burst rule
logging/grafana/provisioning/datasources/    Loki datasource provisioning
logging/grafana/provisioning/dashboards/     Dashboard provider provisioning
logging/grafana/dashboards/java-app-logs.json
examples/java-app.log.example                Synthetic JSON log lines
example-log-format.md                         Field contract
docs/logging-pipeline.md                      End-to-end explanation
.env.example                                  Local Compose settings template
README.md                                     Project documentation
TESTING.md                                    Validation guide
TEST_RESULTS.md                               Real validation status and limitations
```

## What is implemented

- `StructuredLogger.java` validates levels/events, escapes JSON, and writes one event per line.
- `Main.java` emits startup, heartbeat, and optional demo ERROR events.
- `promtail-config.yml` tails `/var/log/java-app/*.log`, tracks offsets, parses JSON time, and promotes only bounded fields to labels.
- `loki-config.yml` defines local filesystem storage, seven-day learning retention, and a local ruler directory.
- `app-log-alerts.yml` counts bursts of logs with `level="ERROR"`; no Alertmanager destination is configured.
- Grafana provisioning creates a Loki datasource and imports a basic Java app logs dashboard.
- Docker Compose uses a named `app-logs` volume so the non-root app can write logs and Promtail can read the same files.

## Demo error logs

By default, the app writes INFO startup and heartbeat events. To produce controlled ERROR logs for the Loki rule and dashboard, set:

```bash
ERROR_EVERY_N=3
```

For faster local testing, also set:

```bash
HEARTBEAT_INTERVAL_MS=1000
```

These settings are for a learning/demo run only.

## Running the stack locally

Create a local `.env` first because Grafana requires a password:

```bash
cp .env.example .env
# edit GRAFANA_ADMIN_PASSWORD before running the stack
```

Then validate and run:

```bash
docker compose config
docker compose build
docker compose up -d
```

Useful local URLs:

```text
Loki:    http://localhost:3100
Grafana: http://localhost:3000
```

Grafana uses the credentials from `.env`.

## LogQL examples

Because Loki is configured with a learning tenant header, API queries should include `X-Scope-OrgID: learning`.

```text
{service="java-app", environment="learning"}
{service="java-app", level="ERROR"}
{service="java-app"} | json | event="heartbeat"
{service="java-app"} | json | traceId="heartbeat-1"
```

Example API query:

```bash
curl -G "http://localhost:3100/loki/api/v1/query_range" \
  -H "X-Scope-OrgID: learning" \
  --data-urlencode 'query={service="java-app", environment="learning"}' \
  --data-urlencode 'limit=5'
```

## Security and operational notes

- No real Grafana credential or production log is committed.
- `.env` is ignored and `.env.example` contains placeholders only.
- Trace IDs, event values, messages, users, and URLs are not Loki labels.
- `traceId` remains in the structured log body to avoid high-cardinality indexing.
- Logs must not contain passwords, tokens, connection strings, private data, raw request bodies, or headers.
- Alert rules are local examples only; no Alertmanager notification routing is configured.
- The stack is a single-node learning lab, not a production logging platform.

## Validation status

Java structured-log generation and static config parsing are validated in `TEST_RESULTS.md`. Docker Compose, Loki ingestion, Grafana dashboard loading, and alert evaluation should be recorded there only after a real local run.

## Possible future improvements

- Add reviewed log rotation for the application file.
- Add authentication/TLS between collectors, Loki, and Grafana.
- Use durable object storage and a resilient topology for non-local use.
- Add parsing-failure metrics and ingestion dashboards.
- Add trace-system links without promoting trace IDs to labels.
- Add Alertmanager only with reviewed ownership and routing.
