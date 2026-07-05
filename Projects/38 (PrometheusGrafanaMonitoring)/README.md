# Prometheus Grafana Monitoring

## Description

An educational monitoring stack for a small Java HTTP application. The app exposes Prometheus-format metrics, Prometheus scrapes and evaluates them, and Grafana is provisioned with a Prometheus data source and an example dashboard.

## Goal

The goal is to understand the complete metrics path—from instrumentation and target discovery through scraping, time-series storage, PromQL queries, alert-rule evaluation, and dashboard visualization.

## Technologies and concepts used

- Java 21 built-in `HttpServer`
- Prometheus text exposition format
- Counters, gauges, histograms, labels, and cardinality
- Prometheus targets, scraping, storage, PromQL, and alert rules
- Grafana data-source and dashboard provisioning
- Docker Compose service networking and persistent volumes
- JVM memory and process-uptime metrics concepts

## Project structure

```text
src/prometheusgrafanamonitoring/         Java app and metric registry
Dockerfile                               Java application image
docker-compose.yml                       App, Prometheus, and Grafana services
monitoring/prometheus.yml                Targets and scrape settings
monitoring/alert-rules.yml               Example alert expressions
grafana/provisioning/                    Data-source/dashboard providers
grafana/dashboards/java-app-dashboard.example.json
app-metrics-example.md                   Metric contract and PromQL guide
.env.example                             Placeholder local settings
README.md                                Project documentation
TESTING.md                               Validation guide
```

## Important files explained

- `Main.java` exposes `/`, `/health`, and `/metrics` and records bounded request labels.
- `MetricsRegistry.java` emits request counters, request-duration histogram data, JVM heap gauges, uptime, and app information.
- `prometheus.yml` scrapes Prometheus itself and `app:8080` every 15 seconds.
- `alert-rules.yml` demonstrates target-down, HTTP error-ratio, and heap-usage alerts.
- Grafana provisioning creates a Prometheus data source and loads the example dashboard.
- The dashboard JSON defines target, traffic, errors, p95 duration, and heap panels without embedded data.

## Intended real-environment workflow

For an approved local lab, copy `.env.example` to ignored `.env`, replace the Grafana password placeholder, inspect `docker compose config`, build the Java image, and start the services. A developer would inspect `/metrics`, check Prometheus targets and rules, run PromQL queries, and then review each provisioned Grafana panel and empty-data state.

Alert thresholds must be tuned against real behavior and ownership. No Alertmanager is included, so rule evaluation would not deliver notifications.

## Prepared but not executed

- Java instrumentation, Dockerfile, Compose stack, Prometheus target/rules, Grafana provisioning, and example dashboard were prepared.
- The dashboard JSON and metric/query relationships were prepared as source only.
- Java, Docker, Compose, Prometheus, Grafana, scraping, querying, alerting, and dashboard import were not executed.
- No sample was stored, rule fired, panel rendered, or service deployed.

## Manual validation checklist

- [ ] Confirm metric names/types follow Prometheus conventions.
- [ ] Confirm label values remain bounded and exclude user/request identifiers.
- [ ] Confirm Prometheus target and Compose app hostname/port agree.
- [ ] Confirm alert queries reference emitted metrics.
- [ ] Confirm Grafana data-source UID matches dashboard references.
- [ ] Inspect dashboard units, legends, time ranges, and no-data behavior.
- [ ] Review thresholds and notification ownership before enabling alerts.

## Common mistakes avoided

- No real Grafana password is committed.
- Dashboard JSON contains no fabricated data or success screenshot.
- High-cardinality identifiers are not metric labels.
- Alert rules include duration windows instead of firing on one brief sample.
- Example thresholds are not described as production SLOs.
- Alert evaluation is distinguished from notification delivery.

## Possible future improvements

- Replace the educational registry with maintained Java instrumentation.
- Add garbage-collection, thread, CPU, and dependency metrics selectively.
- Add Alertmanager only with reviewed routing and ownership.
- Add recording rules for frequently used queries.
- Add configuration validation and dashboard-schema checks to CI.
- Add measured SLO-based alerts after collecting representative data.
