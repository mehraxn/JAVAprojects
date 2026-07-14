# Internal Developer Platform (mini)

A small **Internal Developer Platform** built around one idea: a **golden path**.
A developer runs a self-service generator, and out comes a complete, consistent,
locally testable Java service — source, Dockerfile, Helm chart, Argo CD
Applications, catalog metadata, and per-environment values — instead of
copy-pasting YAML from an old repo.

## The story

```
developer runs the generator
        │  scripts/new-service.sh  (substitutes __TOKEN__ placeholders)
        ▼
template/  (the golden path)
        ▼
examples/new-service/  — a complete, self-contained service:
   Java source · Dockerfile · Helm chart · Argo CD apps · catalog · env values
```

## What problem it solves

Every new service should not mean filing tickets and hand-writing a Dockerfile,
Helm chart, Argo CD Application, catalog entry, and config from scratch. This
project shows the platform-engineering answer: developers answer a few questions
and get a guardrail-compliant service they still own.

## What is implemented (and actually works)

- **Working generator** — [scripts/new-service.sh](scripts/new-service.sh):
  validates inputs, supports `--force`, substitutes placeholders, prints a summary.
- **Working golden-path template** — [template/](template/): Java service,
  Dockerfile, Helm chart (with `templates/`), Argo CD apps, catalog metadata.
- **A generated example** — [examples/new-service/](examples/new-service/) is the
  real output of the generator (not hand-written).
- **Helm chart** that renders valid Kubernetes manifests (`helm template` / `helm lint`).
- **Java service** exposing `/`, `/health`, `/ready`, configured via env vars.
- **Platform guardrail examples** — [k8s/policies/](k8s/policies/) and the
  Argo CD [gitops/appproject.example.yaml](gitops/appproject.example.yaml).
- **A testing guide** — [TESTING.md](TESTING.md) with exact commands.

## What is example-only (not executed here)

These are illustrative and were **not** run against real infrastructure:

- Argo CD deployment / sync (manifests only; no controller here)
- Kubernetes cluster deployment
- The CI workflow ([.github/workflows/golden-path-validation.example.yml](.github/workflows/golden-path-validation.example.yml)) — a valid example, triggered manually, not run in this repo
- The policy / AppProject examples
- Any cloud infrastructure

All repo URLs and registries are `example.invalid` placeholders. No real secrets.

## Project structure

```text
template/               the golden path (mirrors a generated service 1:1)
template.yaml           input contract (parameters the generator accepts)
scripts/new-service.sh  the generator (+ scripts/README.md)
examples/new-service/   generated output for "payments-api"
gitops/                 AppProject example + GitOps explainer
k8s/policies/           example resource guardrail (LimitRange)
.github/workflows/      example CI workflow (manual trigger)
docs/                   golden path, onboarding, architecture, lifecycle
README.md  TESTING.md  TEST_RESULTS.md
```

## Generate a service

```bash
chmod +x scripts/new-service.sh
./scripts/new-service.sh \
  --name payments-api \
  --owner payments-team \
  --port 8080 \
  --image registry.example.invalid/payments-api \
  --out examples/new-service \
  --force
```

## Compile and run the generated service

Requires a JDK 21 (for `com.sun.net.httpserver`, bundled with the JDK).

```bash
# Compile
javac -d out examples/new-service/src/app/*.java

# Run (reads SERVICE_NAME and SERVICE_PORT from the environment)
SERVICE_NAME=payments-api SERVICE_PORT=8080 java -cp out app.Main
```

## Test the endpoints

```bash
curl http://localhost:8080/
curl http://localhost:8080/health
curl http://localhost:8080/ready
```

Expected responses:

```json
{"service":"payments-api","message":"hello from payments-api"}
{"status":"ok","service":"payments-api"}
{"status":"ready","service":"payments-api"}
```

## Optional: Helm and Docker

```bash
# Render the Kubernetes manifests (no cluster needed)
helm template payments-api examples/new-service/helm

# Build the image (requires a running Docker daemon)
docker build -t registry.example.invalid/payments-api:0.1.0 examples/new-service
```

## Honesty notes

- No real cluster deployment happened. The `gitops/` manifests are desired state,
  not a live sync.
- CI is an example workflow; it was not run in this repo.
- Argo CD was not installed and nothing was synced.
- See [TEST_RESULTS.md](TEST_RESULTS.md) to record your own run's real output.

## What I learned

- What an **Internal Developer Platform / golden path** is, and why it reduces
  toil without removing ownership.
- Designing a **template + generator** that stays collision-free with Helm's
  `{{ }}` syntax by using `__TOKEN__` placeholders.
- Wiring **GitOps per environment** with sane dev-auto / prod-manual defaults.
- Committing a **generated example** so reviewers see real output.

## Future improvements

- Back the golden path with Backstage software templates and a live catalog.
- Add template versioning and an upgrade path for existing services.
- Add policy-as-code gates (e.g. OPA/Kyverno) to the CI workflow.
