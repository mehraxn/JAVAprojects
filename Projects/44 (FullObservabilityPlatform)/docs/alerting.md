# Alerting

Covers the alert rules in
[`../monitoring/alerts.example.yml`](../monitoring/alerts.example.yml).
**No rules were evaluated and no alert fired** — Prometheus was never run.

## What an alert rule is

A Prometheus alerting rule is a **PromQL expression that is evaluated on a
schedule** (`evaluation_interval`, 15s here). When the expression returns any
series for longer than the rule's `for:` duration, the alert transitions to
**firing**. Anatomy:

```yaml
- alert: HighHttpErrorRate          # name
  expr: <PromQL that is "true" when bad>   # condition
  for: 5m                            # must stay true this long (debounces blips)
  labels:  { severity: warning }     # used for routing/grouping
  annotations: { summary: "..." }    # human-readable, templated with $value/$labels
```

The `for:` duration is what separates a real problem from a momentary spike — it
prevents flapping alerts.

## The rules in this lab (SLI-based)

They map to the classic signals — availability, the **RED** method (Rate of
errors, Errors, Duration), and saturation:

| Alert | Signal | Fires when |
| --- | --- | --- |
| `JavaAppTargetDown` | availability | `up == 0` for 2m (scrape failing) |
| `HighHttpErrorRate` | errors | 5xx ratio > 5% for 5m |
| `HighRequestLatencyP95` | duration | p95 latency > 1s for 5m |
| `JvmHeapHigh` | saturation | heap > ~250MB for 10m |

Example expression (error ratio):

```promql
sum(rate(http_requests_total{status=~"5.."}[5m]))
  / sum(rate(http_requests_total[5m])) > 0.05
```

## Good alerting principles

- **Alert on symptoms, not causes.** "Users see errors / high latency" beats
  "CPU is 90%" — high CPU may be harmless.
- **Every alert should be actionable.** If nobody would do anything, it's noise.
- **Tie severity to urgency.** `critical` = wake someone; `warning` = look at it
  during the day.
- **Use `for:` to debounce.** Avoid firing on single-scrape spikes.

## Routing (out of scope here)

In production, firing alerts go to **Alertmanager**, which deduplicates, groups,
silences, and routes them to email/Slack/PagerDuty. The Alertmanager block in
`prometheus.yml` is intentionally commented out so **nothing tries to notify
anyone**.

## What was NOT done

- No rule was loaded or evaluated.
- No alert entered pending/firing state.
- No Alertmanager or notification channel exists.
