# Onboarding Guide — Self-Service Deployment

How a developer takes a new service from nothing to "GitOps is managing it,"
using the golden path. **Every command below is illustrative and was NOT
executed** — no service was generated, built, or deployed.

## The idea: self-service, not tickets

Without a platform, a new service means filing tickets and copy-pasting YAML from
an old repo. With this IDP the developer answers a few questions and the platform
produces a consistent, guardrail-compliant service. They stay the **owner**; the
platform just removes the repetitive setup.

## Step by step (NOT executed)

### 1. Provide inputs

Answer the parameters defined in
[../service-template/template.yaml](../service-template/template.yaml):

| Input | Example |
| --- | --- |
| service name | `payments-api` |
| owning team | `payments-team` |
| port | `8080` |
| image repo | `registry.example.invalid/payments-api` |

### 2. Generate the service (NOT executed)

```bash
# NOT executed — see the committed result in examples/new-service/
scripts/new-service.sh \
  --name payments-api --owner payments-team --port 8080 \
  --image registry.example.invalid/payments-api \
  --out examples/new-service
```

This scaffolds: the Java app + Dockerfile, `service.yaml` catalog metadata, a
Helm chart with `values-dev.yaml`/`values-prod.yaml`, and Argo CD Applications
for dev and prod.

### 3. Open a pull request

The generated folder is committed via a normal PR. Review applies the usual
gates (code review, policy checks). Nothing deploys yet.

### 4. CI builds the image (NOT executed)

On merge, a pipeline (see [../ci/pipeline-template.example.yml](../ci/pipeline-template.example.yml))
compiles, tests, builds the image, and publishes it to the registry. This
project does **not** run CI.

### 5. GitOps deploys it (NOT executed)

Argo CD sees the new `Application` manifests and reconciles the cluster to match:

- **dev** auto-syncs immediately (fast feedback),
- **prod** waits for a human to sync (a control gate).

```
answer inputs → generate → PR → merge → CI builds image → GitOps syncs
   (dev auto, prod manual)
```

## What "done" looks like

The service is registered in the catalog (`service.yaml`), has a repeatable
build, runs with platform guardrails (non-root, resource limits, probes), and
its desired state lives in Git. Ongoing changes follow the same PR → GitOps loop.

## What was NOT done

- No generator, CI, Helm, or Argo CD command ran.
- No image was built and no cluster was contacted.
- **Nothing was deployed.**
