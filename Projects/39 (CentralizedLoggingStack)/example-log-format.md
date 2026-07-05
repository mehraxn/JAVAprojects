# Example Application Log Format

The application uses newline-delimited JSON: each physical line is one complete event.

## Fields

| Field | Example | Purpose |
|---|---|---|
| `timestamp` | `2026-01-15T10:00:00Z` | UTC event time in RFC 3339 format |
| `level` | `INFO` | One of DEBUG, INFO, WARN, or ERROR |
| `service` | `java-app` | Stable application name |
| `environment` | `learning` | Stable environment name |
| `event` | `request_completed` | Lowercase snake-case event category |
| `message` | `Example request completed` | Human-readable, non-sensitive context |
| `traceId` | `trace-example-001` | Correlation value retained as a field |

Example:

```json
{"timestamp":"2026-01-15T10:00:30Z","level":"INFO","service":"java-app","environment":"learning","event":"request_completed","message":"Example request completed","traceId":"trace-example-001"}
```

Additional safe sample lines are in `examples/java-app.log.example`. They are synthetic and were not ingested.

## Logging rules

- Use a stable event name rather than placing changing values in the event field.
- Put limited diagnostic context in the message, but never credentials or personal data.
- Preserve exception meaning without dumping secrets from request bodies, headers, or connection strings.
- Keep one event on one line so a file collector can identify boundaries.
- Use UTC timestamps with an explicit offset.
- Keep trace IDs as fields for parsing and filtering, not indexed labels.

## Example LogQL filters

```text
{service="java-app"} | json | event="request_completed"
{service="java-app", level="ERROR"} | json
{service="java-app"} | json | traceId="trace-example-001"
```

## Verification status

The format and synthetic examples were reviewed as text only. JSON generation, parsing, ingestion, and search were not executed.
