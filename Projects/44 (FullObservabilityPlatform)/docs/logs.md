# Logs

Covers the logs pillar: the app's JSON logs are collected by Promtail and stored
in Loki, queryable in Grafana. This explains the pipeline the app +
[`../logging`](../logging) configs implement.

## What logs are

Logs are **discrete, timestamped event records**. Unlike metrics (aggregated
numbers), each log line preserves the detail of one event — exactly what you
need to understand *why* something happened. Their cost is volume: logs are the
most expensive pillar to store and search, so structure and labels matter.

## Structured logging

The app writes **one JSON object per request** to both stdout and
`/var/log/app/app.log`. Each line carries the request's method, path, status,
duration, and trace context:

```json
{"timestamp":"2026-07-05T12:00:00Z","level":"INFO","service":"observable-java-app",
 "env":"dev","event":"http_request","method":"GET","path":"/work","status":200,
 "duration_ms":212.4,"trace_id":"4bf92f...","span_id":"00f067..."}
```

Structured (vs free-text) logs are machine-parseable: Loki can filter on fields,
and — crucially — the `trace_id` field links each line back to a trace (see
[traces.md](traces.md)). That correlation is the whole point of a "full"
observability stack.

## The pipeline: Promtail → Loki → Grafana

```
app JSON log file  ─▶  Promtail  ─▶  Loki  ─▶  Grafana (LogQL)
(/var/log/app/*.log)    │ parse JSON, promote labels
                        └ ship the rest as the log body
```

The app writes to `./logs/app/app.log` (bind-mounted to `/var/log/app` in the
container); Promtail reads the same directory read-only.

- **Promtail** ([`../logging/promtail-config.example.yml`](../logging/promtail-config.example.yml))
  tails `/var/log/app/*.log`, parses the JSON, and promotes a few
  **low-cardinality** fields (`level`, `env`, `event`) to Loki labels.
- **Loki** ([`../logging/loki-config.example.yml`](../logging/loki-config.example.yml))
  indexes **only those labels**, not the full text — that is why it is cheap
  compared to a full-text engine. You then grep the body with LogQL at query
  time.

## LogQL examples

```logql
# all error logs for the service
{service="observable-java-app", level="ERROR"}

# parse JSON and show only failed requests, formatted with the trace id
{service="observable-java-app"} | json | status >= 500
  | line_format "{{.method}} {{.path}} -> {{.status}} trace={{.trace_id}}"

# error rate as a metric derived from logs
sum(rate({service="observable-java-app", level="ERROR"}[5m]))
```

## Label cardinality warning

Same rule as metrics: never promote `trace_id`, `span_id`, user IDs, or request
IDs to Loki **labels** — they are high-cardinality and will blow up the index.
Keep them in the log **body** (searchable) instead. Promtail's config does
exactly this: it labels on `level`/`env`/`event` and leaves `trace_id`/`span_id`
in the body.

## Local-only caveats

- Logs live in Loki's local disk and are discarded on `docker compose down`.
- No retention tuning, multi-tenancy, or object storage — this is a local demo.
