# Platform Architecture

How the pieces of this mini Internal Developer Platform (IDP) fit together.
**Nothing here was generated, built, synced, or deployed.**

## The layers

```
        ┌─────────────────────────────────────────────────────────┐
        │  DEVELOPER EXPERIENCE                                    │
        │  scripts/new-service.sh  (+ template.yaml input contract)│
        └───────────────┬─────────────────────────────────────────┘
                        │ scaffolds
        ┌───────────────▼─────────────────────────────────────────┐
        │  TEMPLATES (the golden path)                            │
        │  service-template/   helm-template/   gitops-template/  │
        └───────────────┬─────────────────────────────────────────┘
                        │ produces a per-service config repo
        ┌───────────────▼─────────────────────────────────────────┐
        │  DELIVERY (out of scope to run here)                    │
        │  CI builds image → GitOps (Argo CD) syncs Helm release  │
        └─────────────────────────────────────────────────────────┘
```

## Components

| Component | Folder | Responsibility |
| --- | --- | --- |
| Input contract | `service-template/template.yaml` | the questions a developer answers |
| Service template | `service-template/` | Java app + Dockerfile + catalog metadata |
| Chart template | `helm-template/` | how the service runs on Kubernetes (values-driven) |
| GitOps template | `gitops-template/` | Argo CD Applications per environment |
| Generator | `scripts/new-service.sh` | substitutes `__TOKEN__`s into a new folder |
| Worked example | `examples/new-service/` | the rendered output for `payments-api` |
| Guardrails | `k8s/policies/`, `ci/` | resource defaults + pipeline template (supporting) |

## Golden path, not a cage

The platform's job is to make the **paved road** the easy road: a developer who
follows the template gets CI, a chart, and GitOps wiring for free, with sensible
guardrails (non-root, resource limits, probes) already set. It does **not**
remove ownership — the generated `service.yaml` records the owning team — and it
should leave room for justified exceptions rather than blocking them.

## Separation of concerns

- **Code** varies little between services (the Java `package` is fixed); what
  varies is **config** (name, port, image, resources) supplied through values.
- **Provisioning/runtime** is Kubernetes + Helm; **delivery** is GitOps. The
  platform wires them but does not couple them — you can change one without the
  other.

See [onboarding-guide.md](onboarding-guide.md) for the developer flow and
[service-lifecycle.md](service-lifecycle.md) for what happens after day one.
