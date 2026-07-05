# Application Metrics Example

## Metric types

- **Counter:** increases over time and is useful for totals such as handled requests. Query its rate rather than graphing the raw total for traffic volume.
- **Gauge:** can increase or decrease, such as current JVM heap usage.
- **Histogram:** counts observations in cumulative buckets and also publishes a total count and sum. Prometheus can estimate latency percentiles across those buckets.
- **Info metric:** a gauge fixed at `1` with stable descriptive labels such as an application version.

## Metrics emitted by the Java example

| Metric | Type | Meaning |
|---|---|---|
| `http_requests_total` | Counter | Requests grouped by bounded method, path, and status labels |
| `http_request_duration_seconds` | Histogram | Duration of handled requests |
| `jvm_memory_used_bytes` | Gauge | Current heap usage from Java management APIs |
| `jvm_memory_max_bytes` | Gauge | Maximum heap size reported by the JVM |
| `process_uptime_seconds` | Gauge | Time since the example registry started |
| `app_info` | Gauge | Static learning version information |

The registry uses only three fixed HTTP contexts, which keeps the `path` label bounded. Do not place user IDs, raw URLs, request IDs, email addresses, or other unbounded values in metric labels; each unique label combination creates another time series.

## Target and scrape flow

```text
Java app /metrics
       ^
       | HTTP scrape every 15 seconds
       |
Prometheus target: app:8080
       |
       +--> stored time series
       +--> alert-rule evaluation
       +--> Grafana PromQL queries
```

A target is one network endpoint that exposes metrics. A scrape is one Prometheus request to that endpoint. Failed scrapes set the automatically generated `up` metric to `0` for that target.

## Example PromQL

```text
up{job="java-app"}
sum(rate(http_requests_total{job="java-app"}[5m]))
histogram_quantile(0.95, sum by (le) (rate(http_request_duration_seconds_bucket{job="java-app"}[5m])))
sum(jvm_memory_used_bytes{job="java-app",area="heap"}) / sum(jvm_memory_max_bytes{job="java-app",area="heap"})
```

`rate` calculates per-second counter change over a window. `histogram_quantile` estimates a percentile and depends on suitable bucket boundaries. Ratios need protection against empty denominators, as shown in the alert and dashboard error-ratio queries.

## JVM metrics concept

This example reads only heap memory through standard Java management APIs. A real observability library or exporter can expose garbage-collection pauses, thread counts, class loading, buffer pools, process CPU, file descriptors, and other JVM measurements. More metrics are not automatically better: select metrics tied to user impact, capacity, and actionable diagnosis.

## Verification status

The metric contract and queries were reviewed statically. The endpoint, scrape behavior, queries, alerts, and dashboard panels were not executed or tested.
