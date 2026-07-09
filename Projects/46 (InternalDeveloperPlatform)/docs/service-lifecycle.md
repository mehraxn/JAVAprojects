# Service Lifecycle

The golden path is not just day-one scaffolding; it defines how a service is
owned, changed, promoted, and eventually retired. **None of this was executed.**

## Stages

```
create → develop → promote (dev → prod) → operate → deprecate → retire
```

### 1. Create
Generated from the template (see [onboarding-guide.md](onboarding-guide.md)).
Lifecycle starts at `experimental` in `catalog-info.yaml`.

### 2. Develop
Changes go through PRs. Merges auto-deploy to **dev** via GitOps, giving fast
feedback without manual kubectl.

### 3. Promote
The **same artifact and chart** move to prod by syncing the prod `Application`.
Prod uses `values-prod.yaml` (more replicas, quieter logs) and **manual sync**,
so promotion is a deliberate, auditable act — not an accident. Update the
`lifecycle` field toward `production` as it matures.

### 4. Operate
Guardrails from the platform apply throughout: non-root containers, resource
requests/limits (see [../k8s/policies/resource-requirements.example.yaml](../k8s/policies/resource-requirements.example.yaml)),
and health/readiness probes. Ownership stays with the team named in
`catalog-info.yaml`.

### 5. Deprecate
Mark `lifecycle: deprecated`, announce a timeline, and stop accepting new
dependencies. GitOps still keeps it running until retirement.

### 6. Retire
Remove the Argo CD `Application` manifests from Git; the GitOps controller prunes
the resources (dev auto-prunes; prod removal is a deliberate change). The
`resources-finalizer.argocd.argoproj.io` finalizer makes Argo CD clean up the
resources it manages when an Application is deleted — a tidy cascade, not deletion
protection. Guarding production deletions is done with RBAC, review, and branch
protection on the config repo.

## Template versioning

The template itself evolves. A real platform versions it (e.g. `Chart.yaml`
`version`, `template.yaml` `apiVersion`) and offers an upgrade path
so existing services can adopt improvements without a rewrite. Breaking changes
ship with a migration note, not a silent bump.

## Ownership & exceptions

- Every service has an explicit **owner** in its catalog metadata.
- The golden path is the default, **not a cage**: teams with a justified need can
  diverge, ideally by contributing the new pattern back to the platform.

## What was NOT done

- No promotion, sync, prune, or retirement was performed.
- No lifecycle transition happened on any real system.
