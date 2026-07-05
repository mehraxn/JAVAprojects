# Testing Prometheus Grafana Monitoring

No Java, Docker, Compose, Prometheus, Grafana, scrape, PromQL, alert, or dashboard process was executed while preparing this project.

## Static validation checklist

- [ ] Review Java metric HELP/TYPE lines and sample formatting.
- [ ] Confirm histogram buckets are cumulative and include count/sum.
- [ ] Confirm request labels are bounded.
- [ ] Confirm dashboard JSON parses and panel queries use emitted metrics.
- [ ] Confirm alert expressions have deliberate thresholds and `for` durations.
- [ ] Confirm provisioning UIDs and paths agree.

## File existence checks

- [ ] Java source, `Dockerfile`, and `docker-compose.yml` exist.
- [ ] `monitoring/prometheus.yml` exists.
- [ ] `monitoring/alert-rules.yml` exists.
- [ ] Dashboard JSON and both provisioning files exist.
- [ ] `app-metrics-example.md`, `.env.example`, `README.md`, and `TESTING.md` exist.

## Configuration review checklist

- [ ] Prometheus rule-file path matches the Compose mount.
- [ ] Java target hostname/port matches the Compose app service.
- [ ] Grafana reaches `prometheus:9090` on the internal network.
- [ ] Dashboard provider path matches its mounted directory.
- [ ] Persistent volumes and host-port overrides are intentional.
- [ ] Image versions are explicit and reviewed before use.

## Security checks

- [ ] No real secret, credential, API key, or token is present.
- [ ] No production Prometheus, Grafana, or application endpoint is present.
- [ ] `.env` is ignored and the example password is an obvious placeholder.
- [ ] Dashboard JSON contains no credentials or production data.

## Commands normally used - NOT executed

```text
javac --add-modules jdk.httpserver ...
docker compose config
docker compose build
docker compose up
promtool check config monitoring/prometheus.yml
promtool check rules monitoring/alert-rules.yml
```

These commands require installed tooling and an approved local environment. None were executed.

## Expected results in a proper environment

- Java exposes valid Prometheus text at `/metrics`.
- Prometheus reports both configured targets as up and stores samples.
- Request counters/rates, duration quantiles, heap ratio, and uptime queries return sensible values.
- Target failure moves the down alert through pending to firing after its duration.
- Grafana provisions the data source and example dashboard without embedded fake data.
- No notification is delivered until a separate Alertmanager path is configured.
