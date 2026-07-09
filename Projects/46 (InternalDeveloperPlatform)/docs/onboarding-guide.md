# Onboarding Guide — Self-Service Deployment

How a developer takes a new service from nothing to "GitOps is managing it,"
using the golden path. The generator step is real and runnable; the CI and Argo
CD steps are illustrative — no cluster or controller runs in this repo.

## The idea: self-service, not tickets

Without a platform, a new service means filing tickets and copy-pasting YAML from
an old repo. With this IDP the developer answers a few questions and the platform
produces a consistent, guardrail-compliant service. They stay the **owner**; the
platform just removes the repetitive setup.

## Step by step

### 1. Provide inputs

Answer the parameters defined in [../template.yaml](../template.yaml):

| Input | Example |
| --- | --- |
| service name | `payments-api` |
| owning team | `payments-team` |
| port | `8080` |
| image repo | `registry.example.invalid/payments-api` |

### 2. Generate the service (runnable)

```bash
./scripts/new-service.sh \
  --name payments-api --owner payments-team --port 8080 \
  --image registry.example.invalid/payments-api \
  --out examples/new-service --force
```

This scaffolds: the Java app + Dockerfile, `catalog-info.yaml` metadata, a Helm
chart with `values-dev.yaml`/`values-prod.yaml` and templates, and Argo CD
Applications for dev and prod. See the result in
[../examples/new-service/](../examples/new-service/).

### 3. Open a pull request

The generated folder is committed via a normal PR. Review applies the usual gates
(code review, policy checks). Nothing deploys yet.

### 4. CI builds the image (example only)

On merge, a pipeline (see the example workflow
[../.github/workflows/golden-path-validation.example.yml](../.github/workflows/golden-path-validation.example.yml))
compiles, tests, builds the image, and publishes it. This repo does **not** run CI.

### 5. GitOps deploys it (example only)

Argo CD sees the new `Application` manifests and reconciles the cluster to match:

- **dev** auto-syncs immediately (fast feedback),
- **prod** waits for a human to sync (a control gate).

```
answer inputs → generate → PR → merge → CI builds image → GitOps syncs
   (dev auto, prod manual)
```

## What "done" looks like

The service is registered in the catalog (`catalog-info.yaml`), has a repeatable
build, runs with platform guardrails (non-root, resource limits, probes), and its
desired state lives in Git. Ongoing changes follow the same PR → GitOps loop.

## What was NOT done here

- No CI, Helm apply, or Argo CD command ran against real infrastructure.
- No image was built and no cluster was contacted.
- **Nothing was deployed.**
