# Prometheus Grafana Monitoring

Starter Java metrics application with Prometheus scrape configuration, alert placeholders, and Grafana provisioning files.

## Structure

```text
src/prometheusgrafanamonitoring/
Dockerfile
docker-compose.yml
monitoring/prometheus.yml
monitoring/alerts.yml
monitoring/grafana/provisioning/datasources/prometheus.yml
monitoring/grafana/provisioning/dashboards/dashboards.yml
monitoring/grafana/dashboards/starter-dashboard.json
docs/METRICS.md
TESTING.md
```

## Safety and status

The Java server and metrics endpoint are not implemented yet. No container, Prometheus server, Grafana server, scrape, or alert was started. Grafana credentials remain placeholders.

## Next implementation steps

- Implement `/health` and `/metrics` with Java `HttpServer`.
- Define metric names, types, labels, and cardinality limits.
- Confirm dashboard panels and meaningful alert thresholds.
- Replace placeholder credentials outside version control.
