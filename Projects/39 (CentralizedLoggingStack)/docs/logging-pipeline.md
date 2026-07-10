# Logging Pipeline

## End-to-end flow

```text
Java application
  | newline-delimited JSON
  v
app-logs named Docker volume
  | file tail + positions
  v
Promtail
  | parse timestamp and bounded labels
  | push HTTP batches with tenant header: learning
  v
Loki
  | index labels and store log chunks
  +--> LogQL queries from Grafana
  +--> example ruler evaluation
Grafana
  | provisioned Loki datasource and dashboard
  v
Search, dashboards, and local alert review
```

## 1. Log production

The Java app writes one complete JSON object per line. Stable field names make downstream parsing predictable. The logger validates levels and event names and escapes JSON control characters. Application teams still own redaction: a structurally valid event can be unsafe if its message contains a credential or personal data.

The demo app normally emits startup and heartbeat events. `ERROR_EVERY_N` can intentionally create demo ERROR events so the LogQL rule and dashboard can be tested without a failing real service.

## 2. Collection

Promtail watches `/var/log/java-app/*.log` on the shared read-only app-log volume. Its positions file records offsets so a restart can resume near the last-read location. Positions reduce duplicates but do not provide exactly-once delivery.

The pipeline parses the JSON timestamp and selects three bounded labels:

- `service` identifies the application;
- `environment` identifies the small approved environment set; and
- `level` identifies DEBUG, INFO, WARN, or ERROR.

`event`, `message`, and `traceId` stay in the log body. This avoids high-cardinality label indexes. A trace ID is useful for search/correlation, but it should not become a Loki label.

## 3. Aggregation and retention

Loki groups entries into streams based on their complete label sets. It indexes labels rather than every word of every message, then stores compressed log chunks on the local filesystem volume. The example uses one Loki process, in-memory ring coordination, filesystem storage, and seven-day retention.

The lab uses tenant name `learning` for Promtail and Grafana requests. This is a local demonstration of tenant headers, not a security boundary by itself.

These settings favor understandability over availability. Production designs require authentication, TLS, durable object storage, capacity planning, backups, tenant isolation, and tested retention/deletion controls.

## 4. Searching

Start with a narrow label selector, then parse and filter content:

```text
{service="java-app", environment="learning"}
{service="java-app", level="ERROR"}
{service="java-app"} | json | event="heartbeat"
{service="java-app"} | json | traceId="heartbeat-1"
```

Broad searches over long time ranges can be expensive. Prefer known service/environment labels, short time windows, and exact parsed-field filters.

## 5. Log-derived alerting

The example ruler expression counts entries with the bounded `level="ERROR"` label over five minutes. If the count remains above five for two minutes, the rule becomes firing. This can indicate an error burst but does not measure every failure mode and can be affected by duplicate, delayed, or missing logs.

Alert notifications require Alertmanager or another supported delivery path. Neither is configured, so the example demonstrates evaluation only.

## 6. Grafana dashboarding

Grafana is provisioned with a Loki datasource and a dashboard containing:

- recent application logs;
- log volume grouped by level;
- recent ERROR logs.

This is intentionally small so the dashboard remains understandable for a local lab.

## Operational boundaries

- Logs are evidence, not a complete health signal; combine them with metrics and traces.
- A trace ID supports correlation but should not become a Loki label.
- Rotation and retention are separate: application-file rotation controls local disk usage, while Loki retention controls stored aggregated data.
- Never rely on log deletion as the first protection for secrets; prevent sensitive data from being logged.

## Verification status

Java structured-log generation and static configuration parsing are validated in `TEST_RESULTS.md`. Full Promtail-to-Loki ingestion, Grafana dashboard loading, and alert evaluation require a local Docker Compose run and should only be marked passed after real verification.
