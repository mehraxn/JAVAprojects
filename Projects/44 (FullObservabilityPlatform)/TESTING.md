# Testing — Full Observability Platform

> **Nothing was executed.** No image build, `docker compose up`, Prometheus,
> Grafana, Loki, Tempo, or Collector ran; **no dashboard was imported and
> observability was not tested.** This documents static review and expected
> runtime behavior.

## 1. Static validation checklist

- [ ] Metric names in the app match PromQL in `alerts.example.yml` + the dashboard.
- [ ] Log field names in the app match Promtail's `json` stage (`level`, `env`, `trace_id`).
- [ ] `service`/`env`/`trace_id` consistent across metrics, logs, traces.
- [ ] Scrape target matches the app Service/port (`app:8080`, `/metrics`).
- [ ] Collector exports only in-stack destinations (Tempo/Prometheus/debug).

## 2. File existence checks

- [ ] `src/fullobservabilityplatform/Main.java`, `docker/Dockerfile.example`, `docker-compose.yml`
- [ ] `monitoring/`: `prometheus.yml`, `alerts.example.yml`, `otel-collector.example.yml`, `tempo.example.yml`, `grafana-dashboard.example.json`
- [ ] `logging/`: `loki-config.example.yml`, `promtail-config.example.yml`
- [ ] `docs/metrics.md`, `logs.md`, `traces.md`, `alerting.md`
- [ ] `README.md`, `TESTING.md`

## 3. YAML / config review checklist

- [ ] All `*.yml` well-formed; dashboard JSON parses.
- [ ] Prometheus scrape interval + rule_files reference present.
- [ ] Collector pipelines: receivers → memory_limiter/resource/batch → exporters.
- [ ] Compose mounts each config into the right container.

## 4. Security checks

- [ ] **No real secrets** — Grafana admin password is a placeholder.
- [ ] **No real credentials** — no external service creds.
- [ ] **No production endpoints** — all targets in-stack/localhost.
- [ ] No high-cardinality label used in Prometheus/Loki.

## 5. Commands normally used — NOT executed

```bash
# NOT executed
javac -d out src/fullobservabilityplatform/*.java && java -cp out fullobservabilityplatform.Main
docker compose up -d
#   Grafana http://localhost:3000 | Prometheus http://localhost:9090
curl "localhost:8080/work" ; curl "localhost:8080/work?fail=1"
```

## 6. Expected results in a proper environment

- Prometheus scrapes the app (`up == 1`); metrics appear.
- Load makes rate/error/latency panels move; JVM heap tracks usage.
- Logs reach Loki and render; spans reach Tempo and are searchable by `trace_id`.
- One request is traceable across all three signals via its `trace_id`.
- An induced error/latency moves an alert to pending/firing.

## 7. Manual review checklist (portfolio quality)

- [ ] README explains the three pillars and correlation clearly.
- [ ] Dashboard uses proper PromQL (`rate`, `histogram_quantile`) + a LogQL panel.
- [ ] Cardinality discipline is visible in the configs.
- [ ] Every command marked NOT executed; no fake dashboards/screenshots/badges.
- [ ] Honest that nothing was collected or rendered.
