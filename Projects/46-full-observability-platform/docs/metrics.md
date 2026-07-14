# Metrics

Covers the metrics pillar: Prometheus scrapes the app's `/metrics` endpoint using
[`../monitoring/prometheus.yml`](../monitoring/prometheus.yml). This explains what
the config and the endpoint do.

## What metrics are

Metrics are **numeric measurements aggregated over time** — cheap to store,
cheap to query, ideal for dashboards and alerts. They answer *aggregate*
questions ("how many requests per second?", "what's p95 latency?") but not
*per-event* ones ("why did request X fail?"). For the latter you use logs and
traces.

Metric types the app exposes:

| Metric | Type | Meaning |
| --- | --- | --- |
| `http_requests_total` | counter | monotonically increasing request count, labeled by `method`, `route`, `status` |
| `http_request_duration_seconds` | histogram | request latency bucketed by `le` (≤ threshold) |
| `jvm_memory_used_bytes` | gauge | current heap usage (goes up and down) |
| `process_uptime_seconds` | gauge | time since start |
| `app_info` | gauge | constant `1` carrying `service`/`env` labels |

## Prometheus scraping

Prometheus is **pull-based**: on a fixed `scrape_interval` (15s here) it makes an
HTTP `GET /metrics` to each configured target and stores the returned samples as
time series.

```yaml
scrape_configs:
  - job_name: observable-java-app
    metrics_path: /metrics
    static_configs:
      - targets: ["app:8080"]
```

Each sample becomes a time series keyed by its name **plus its labels**, e.g.
`http_requests_total{method="GET",route="/work",status="200"}`. Prometheus also
records a synthetic `up` series per target (1 = scrape succeeded), which the
`JavaAppTargetDown` alert uses.

### Counters vs rates

A counter only ever increases, so you almost always wrap it in `rate()`:

```promql
# requests per second per route, averaged over 5 minutes
sum(rate(http_requests_total[5m])) by (route)
```

### Histograms and quantiles

The histogram's `_bucket{le="..."}` series let you compute percentiles at query
time without storing every observation:

```promql
histogram_quantile(0.95,
  sum(rate(http_request_duration_seconds_bucket[5m])) by (le))
```

## Label cardinality (the main footgun)

Every unique label-value combination is a separate time series. High-cardinality
labels (user IDs, `trace_id`, raw URLs with IDs) explode memory. The app labels
by **route template** (`/work`), never the raw path — keep IDs out of labels and
put them in logs/traces instead.

## Local-only caveats

- Prometheus stores series on local disk, discarded on `docker compose down`.
- No long-term/remote storage, downsampling, or federation — this is a local demo.
