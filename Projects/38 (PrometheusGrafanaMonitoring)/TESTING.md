# Testing Prometheus Grafana Monitoring

## Static checks

- Review scrape targets, rule paths, and provisioning paths.
- Confirm metrics follow Prometheus naming conventions.
- Confirm dashboards do not claim data that is not emitted.
- Confirm credentials are placeholders.

## Deferred checks

- Compile and test the Java metrics endpoint.
- Validate Prometheus configuration and rules.
- Provision Grafana and inspect the starter dashboard.
- Trigger alerts only in an approved local environment.

No monitoring or container command was executed.
