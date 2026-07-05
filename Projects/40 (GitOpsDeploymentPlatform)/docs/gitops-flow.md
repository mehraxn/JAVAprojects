# GitOps Flow

## Meaning

GitOps treats reviewed Git content as the source of truth for desired deployment state. A controller observes a repository revision, renders the selected environment path, compares desired and live state, and reports or reconciles drift according to policy.

## Planned flow

```text
application change -> build/test/image publication -> reviewed image-tag change
-> environment overlay commit -> Argo CD comparison -> approved/automatic sync
-> Kubernetes rollout -> health observation
```

Dev and prod use separate overlays and separate Argo CD Applications. Dev demonstrates automatic self-healing without pruning. The production-design Application has no automated sync policy.

The Helm chart is an alternative packaging example. The Argo CD Applications currently select Kustomize overlays so one delivery path remains unambiguous.

No controller, repository connection, rendering, comparison, sync, or rollout was executed.
