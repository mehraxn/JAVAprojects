# Platform Architecture

How the pieces of this mini Internal Developer Platform (IDP) fit together. The
generator and Helm chart run locally; delivery (CI, Argo CD) is out of scope to
run here and nothing was deployed.

## The layers

```
        ┌─────────────────────────────────────────────────────────┐
        │  DEVELOPER EXPERIENCE                                    │
        │  scripts/new-service.sh  (+ template.yaml input contract)│
        └───────────────┬─────────────────────────────────────────┘
                        │ scaffolds
        ┌───────────────▼─────────────────────────────────────────┐
        │  TEMPLATE (the golden path)                             │
        │  template/  →  a complete, self-contained service       │
        └───────────────┬─────────────────────────────────────────┘
                        │ produces a per-service config repo
        ┌───────────────▼─────────────────────────────────────────┐
        │  DELIVERY (example only, not run here)                  │
        │  CI builds image → GitOps (Argo CD) syncs Helm release  │
        └─────────────────────────────────────────────────────────┘
```

## Components

| Component | Location | Responsibility |
| --- | --- | --- |
| Input contract | `template.yaml` | the questions a developer answers |
| Golden-path template | `template/` | Java app, Dockerfile, Helm chart, GitOps apps, catalog metadata |
| Generator | `scripts/new-service.sh` | validates inputs, substitutes `__TOKEN__`s into a new folder |
| Worked example | `examples/new-service/` | the rendered output for `payments-api` |
| GitOps project | `gitops/appproject.example.yaml` | Argo CD AppProject the services deploy into |
| Guardrails | `k8s/policies/`, `.github/workflows/` | resource defaults + example CI workflow |

## Golden path, not a cage

The platform's job is to make the **paved road** the easy road: a developer who
follows the template gets a chart, GitOps wiring, and CI-ready structure for free,
with sensible guardrails (non-root, resource limits, probes) already set. It does
**not** remove ownership — the generated `catalog-info.yaml` records the owning
team — and it leaves room for justified exceptions rather than blocking them.

## Separation of concerns

- **Code** varies little between services (the Java `package app` is fixed); what
  varies is **config** (name, port, image, resources) supplied through env vars
  and Helm values.
- **Provisioning/runtime** is Kubernetes + Helm; **delivery** is GitOps. The
  platform wires them but does not couple them — you can change one without the
  other.

See [onboarding-guide.md](onboarding-guide.md) for the developer flow and
[service-lifecycle.md](service-lifecycle.md) for what happens after day one.
