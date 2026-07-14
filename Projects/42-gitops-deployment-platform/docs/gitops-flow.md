# GitOps Flow

## Meaning

GitOps treats reviewed Git content as the source of truth for desired
deployment state. A controller (Argo CD here) observes a repository revision,
renders the selected environment path, compares desired and live state, and
reports or reconciles drift according to policy.

## The flow in this lab

```text
application change -> build/test/image publication -> reviewed image reference change
-> environment overlay commit -> Argo CD comparison -> approved/automatic sync
-> Kubernetes rollout -> health observation
```

- **Dev** (`gitops/argocd/dev-application.example.yaml`): auto-sync with
  self-heal enabled and **prune disabled** — drift is corrected
  automatically, but a bad commit cannot mass-delete resources without a
  human look.
- **Prod** (`gitops/argocd/prod-application.example.yaml`): **no `automated:`
  block**, so Argo CD only reports differences; a human clicking Sync is the
  release gate.
- Both environments' **namespaces are declared in Git** (each overlay carries
  a `namespace.yaml`), so `CreateNamespace=false` is accurate — nothing is
  created implicitly.
- An **AppProject** (`gitops/argocd/appproject.example.yaml`) restricts the
  Applications to the lab's repo, destinations, namespaces, and resource
  kinds. Production should restrict further (per-team RBAC, sync windows,
  tighter kind allowlists).

The Helm chart is an alternative packaging example. The Argo CD Applications
select the Kustomize overlays so one delivery path remains unambiguous.

## Image references

The lab pins the versioned local tag `gitops-java-app:0.1.0`. Tags are
traceable, not inherently immutable — in production, CI should publish an
image **digest** and the promotion commit should change a `newDigest` entry
in the overlay, so every environment runs exactly the bytes that were built.

## Honesty boundary

The Kustomize overlays, Helm chart, Docker image, and app endpoints in this
flow were validated locally (see [../TEST_RESULTS.md](../TEST_RESULTS.md)).
**Argo CD itself was not run**: no repository connection, comparison, sync,
drift correction, or rollout happened. This is a local portfolio lab, not a
production platform.
