# Testing Centralized Logging Stack

This guide separates Java-only validation from the full Docker Compose logging stack. Record real results in `TEST_RESULTS.md`; do not invent Loki/Grafana results.

## 1. Java-only validation

Compile the app:

```bash
javac -d out src/centralizedloggingstack/*.java
```

Run a short local demo that exits after a few events:

```bash
LOG_FILE=/tmp/java-app.log \
APP_SERVICE=java-app \
APP_ENVIRONMENT=learning \
APP_MAX_EVENTS=5 \
HEARTBEAT_INTERVAL_MS=200 \
ERROR_EVERY_N=2 \
java -cp out centralizedloggingstack.Main
```

Validate that every generated line is JSON:

```bash
python3 - <<'PY'
import json
from pathlib import Path
for line in Path('/tmp/java-app.log').read_text().splitlines():
    json.loads(line)
print('all generated log lines are valid JSON')
PY
```

Cleanup:

```bash
rm -rf out
rm -f /tmp/java-app.log
```

## 2. Docker Compose setup

Create the local `.env` file before running Compose:

```bash
cp .env.example .env
# edit GRAFANA_ADMIN_PASSWORD in .env
```

For normal INFO-only logs:

```bash
docker compose config
docker compose build
docker compose up -d
```

For faster demo logs and intentional ERROR entries:

```bash
ERROR_EVERY_N=2 HEARTBEAT_INTERVAL_MS=1000 docker compose up -d --build
```

## 3. Service checks

Loki readiness:

```bash
curl http://localhost:3100/ready
```

Grafana health:

```bash
curl http://localhost:3000/api/health
```

Promtail/app runtime logs:

```bash
docker compose logs app --tail=20
docker compose logs promtail --tail=20
```

## 4. Verify Loki ingestion

Generate or wait for a few app log lines, then query Loki. Include the tenant header:

```bash
curl -G "http://localhost:3100/loki/api/v1/query_range" \
  -H "X-Scope-OrgID: learning" \
  --data-urlencode 'query={service="java-app", environment="learning"}' \
  --data-urlencode 'limit=5'
```

If `ERROR_EVERY_N` is set, verify ERROR logs:

```bash
curl -G "http://localhost:3100/loki/api/v1/query_range" \
  -H "X-Scope-OrgID: learning" \
  --data-urlencode 'query={service="java-app", level="ERROR"}' \
  --data-urlencode 'limit=5'
```

## 5. Verify Grafana provisioning

Open:

```text
http://localhost:3000
```

Use the credentials from `.env`.

Check:

- the `Loki` datasource exists;
- the `Java App Centralized Logs` dashboard is present;
- the dashboard shows recent logs after the app emits data;
- Grafana Explore can query `{service="java-app", environment="learning"}`.

## 6. Alert/ruler verification

The example rule is:

```text
sum(count_over_time({job="java-app", service="java-app", environment="learning", level="ERROR"}[5m])) > 5
```

To exercise it, run the stack with a small `ERROR_EVERY_N` and enough events. Rule evaluation can lag; record real status only after checking Loki's ruler API or Grafana alerting view.

## 7. Cleanup

```bash
docker compose down -v
rm -rf out
rm -f /tmp/java-app.log
```

## Expected limitations

- This lab does not configure Alertmanager notification delivery.
- The stack is local and single-node.
- Retention, deletion, access control, TLS, and durable storage are learning examples only.
- Do not use production logs or real secrets in this project.
