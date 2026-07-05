# Logging Pipeline

## End-to-end flow

```text
Java application
  | newline-delimited JSON
  v
app-logs named volume
  | file tail + positions
  v
Promtail
  | parse timestamp and bounded labels
  | push HTTP batches
  v
Loki
  | index labels and store log chunks
  +--> LogQL queries from Grafana
  +--> example ruler evaluation
```

## 1. Log production

The Java app writes one complete JSON object per line. Stable field names make downstream parsing predictable. The logger validates levels and event names and escapes JSON control characters. Application teams still own redaction: a structurally valid event can be unsafe if its message contains a credential or personal data.

## 2. Collection

Promtail watches `/var/log/java-app/*.log` on the shared read-only volume. Its positions file records offsets so a restart can resume near the last-read location. Positions reduce duplicates but do not provide exactly-once delivery.

The pipeline parses the JSON timestamp and selects three bounded labels:

- `service` identifies the application;
- `environment` identifies the small approved environment set; and
- `level` identifies DEBUG, INFO, WARN, or ERROR.

`event`, `message`, and `traceId` stay in the log body. This avoids high-cardinality label indexes.

## 3. Aggregation and retention

Loki groups entries into streams based on their complete label sets. It indexes labels rather than every word of every message, then stores compressed log chunks on the local filesystem volume. The example uses one Loki process, in-memory ring coordination, filesystem storage, and seven-day retention.

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

The example ruler expression counts entries containing the structured ERROR marker over five minutes. If the count remains above five for two minutes, the rule becomes firing. This can indicate an error burst but does not measure every failure mode and can be affected by duplicate, delayed, or missing logs.

Alert notifications require an Alertmanager or another supported delivery path. Neither is configured, so the example demonstrates evaluation only.

## Operational boundaries

- Logs are evidence, not a complete health signal; combine them with metrics and traces.
- A trace ID supports correlation but should not become a Loki label.
- Rotation and retention are separate: application-file rotation controls local disk usage, while Loki retention controls stored aggregated data.
- Never rely on log deletion as the first protection for secrets; prevent sensitive data from being logged.

## Verification status

No pipeline component was started. File tailing, parsing, delivery, storage, retention, queries, ruler discovery, and alert evaluation are intended behavior based on static configuration only.
