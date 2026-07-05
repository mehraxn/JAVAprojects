# Logs

Covers the logs pillar. **Loki and Promtail were not run.** This explains the
pipeline the app + [`../logging`](../logging) configs describe.

## What logs are

Logs are **discrete, timestamped event records**. Unlike metrics (aggregated
numbers), each log line preserves the detail of one event — exactly what you
need to understand *why* something happened. Their cost is volume: logs are the
most expensive pillar to store and search, so structure and labels matter.

## Structured logging

The app writes **one JSON object per line** to stdout:

```json
{"timestamp":"2026-07-05T12:00:00Z","level":"INFO","service":"observable-java-app",
 "env":"dev","event":"http_request","message":"GET /work -> 200",
 "trace_id":"4bf92f...","span_id":"00f067..."}
```

Structured (vs free-text) logs are machine-parseable: Loki can filter on fields,
and — crucially — the `trace_id` field links each line back to its distributed
trace (see [traces.md](traces.md)). That correlation is the whole point of a
"full" observability stack.

## The pipeline: Promtail → Loki → Grafana

```
app stdout (JSON)  ─▶  Promtail  ─▶  Loki  ─▶  Grafana (LogQL)
                        │ parse JSON, promote labels
                        └ ship the rest as the log body
```

- **Promtail** ([`../logging/promtail-config.example.yml`](../logging/promtail-config.example.yml))
  tails the log files, parses the JSON, and promotes a few **low-cardinality**
  fields (`level`, `env`, `event`) to Loki labels.
- **Loki** ([`../logging/loki-config.example.yml`](../logging/loki-config.example.yml))
  indexes **only those labels**, not the full text — that is why it is cheap
  compared to a full-text engine. You then grep the body with LogQL at query
  time.

## LogQL examples (NOT executed)

```logql
# all error logs for the service
{service="observable-java-app", level="ERROR"}

# parse JSON and show only failed requests, formatted with the trace id
{service="observable-java-app"} | json | status >= 500
  | line_format "{{.event}} trace={{.trace_id}} {{.message}}"

# error rate as a metric derived from logs
sum(rate({service="observable-java-app", level="ERROR"}[5m]))
```

## Label cardinality warning

Same rule as metrics: never promote `trace_id`, `span_id`, user IDs, or request
IDs to Loki **labels** — they are high-cardinality and will blow up the index.
Keep them in the log **body** (searchable) instead. Promtail's config does
exactly this: it labels on `level`/`env`/`event` and leaves `trace_id` in the
body.

## What was NOT done

- No log was shipped; Promtail and Loki were not started.
- No LogQL query ran and no logs panel was rendered.
