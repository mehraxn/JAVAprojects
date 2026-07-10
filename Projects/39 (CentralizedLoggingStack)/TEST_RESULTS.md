# Test Results — CentralizedLoggingStack

## Summary

This file records the validation performed for this portfolio version. Results are intentionally honest: Java and static configuration checks were run here; Docker Compose and full Loki/Grafana ingestion were not run in this environment because Docker is unavailable.

## Java compile result

Status: PASSED

Command:

```bash
javac -d out src/centralizedloggingstack/*.java
```

Result: compilation completed successfully.

## Structured log generation result

Status: PASSED

Command:

```bash
LOG_FILE=/tmp/java-app.log \
APP_SERVICE=java-app \
APP_ENVIRONMENT=learning \
APP_MAX_EVENTS=5 \
HEARTBEAT_INTERVAL_MS=200 \
ERROR_EVERY_N=2 \
java -cp out centralizedloggingstack.Main
```

Result: generated newline-delimited JSON logs including INFO and intentional demo ERROR events.

## JSON parsing result

Status: PASSED

Checks performed:

- generated `/tmp/java-app.log` lines parsed as JSON;
- `examples/java-app.log.example` lines parsed as JSON;
- `logging/grafana/dashboards/java-app-logs.json` parsed as JSON.

## YAML parsing result

Status: PASSED

Parsed successfully:

- `docker-compose.yml`
- `logging/loki-config.yml`
- `logging/promtail-config.yml`
- `logging/rules/learning/app-log-alerts.yml`
- `logging/grafana/provisioning/datasources/loki.yml`
- `logging/grafana/provisioning/dashboards/java-app-logs.yml`

## Docker Compose config result

Status: NOT RUN

Reason: Docker is unavailable in this environment.

Command to run locally:

```bash
cp .env.example .env
# edit GRAFANA_ADMIN_PASSWORD
docker compose config
```

## Docker Compose runtime result

Status: NOT RUN

Reason: Docker is unavailable in this environment.

Command to run locally:

```bash
docker compose up -d --build
```

## Promtail to Loki ingestion result

Status: NOT RUN

Reason: the full Docker Compose stack was not started in this environment.

Expected verification command after local stack startup:

```bash
curl -G "http://localhost:3100/loki/api/v1/query_range" \
  -H "X-Scope-OrgID: learning" \
  --data-urlencode 'query={service="java-app", environment="learning"}' \
  --data-urlencode 'limit=5'
```

## Grafana provisioning result

Status: STATIC FILES PRESENT / RUNTIME NOT RUN

Provisioning files exist for:

- Loki datasource;
- Java app logs dashboard provider;
- Java app logs dashboard JSON.

Runtime verification still requires a local Docker Compose run.

## Loki alert rule result

Status: STATIC YAML VALID / RULE EVALUATION NOT RUN

The rule uses the bounded `level="ERROR"` label:

```text
sum(count_over_time({job="java-app", service="java-app", environment="learning", level="ERROR"}[5m])) > 5
```

Ruler evaluation was not run in this environment.

## Known limitations

- No production logs are included.
- No Alertmanager notification delivery is configured.
- Loki/Grafana are local single-node learning services.
- Docker Compose, Loki ingestion, Grafana dashboard loading, and alert evaluation must be verified locally before claiming full-stack runtime success.
