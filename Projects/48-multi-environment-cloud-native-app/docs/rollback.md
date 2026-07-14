# Rollback

How to return an environment to a known-good state. **No rollback was performed;
every command is marked NOT executed.**

## Rollback = redeploy a previously verified revision

Because desired state lives in Git and images are immutable + pinned by digest,
rollback is deterministic: point the environment back at the previous digest /
Git revision. You are redeploying something that already worked, not hot-fixing.

## Options (fastest first)

### 1. Git revert (the GitOps way)

```bash
# NOT executed — revert the promotion commit; Argo CD syncs the old digest back:
git revert <promotion-commit-sha>
git push
# prod: then perform the manual Argo CD sync.
```

### 2. Argo CD rollback to a prior synced revision

```bash
# NOT executed:
argocd app rollback cloud-native-app-prod <previous-revision>
```

### 3. Kustomize/kubectl (break-glass)

```bash
# NOT executed — set the overlay image back to the last-good digest and re-apply:
kubectl apply -k "Projects/48-multi-environment-cloud-native-app/k8s/overlays/prod"
```

## Runbook

1. **Stop the rollout / pause promotion.**
2. **Identify the last-good digest** (the one running before this release).
3. **Revert** to it (option 1 or 2 above).
4. **Verify**: `/config` reports the expected environment and version; error rate
   and latency return to baseline.
5. **Preserve evidence** (logs, metrics, the bad digest) before cleanup.
6. **Communicate** impact and investigate before retrying.

## Risks & safety checks

- **Database migrations.** Rolling the image back does not roll back a schema
  change. Use backward-compatible **expand/contract** migrations so the previous
  version still runs against the new schema. This is the #1 thing that makes a
  rollback unsafe.
- **Config vs image.** If an incident was caused by a config change, revert the
  config, not the image (or both). Keep them separately revertible — they are.
- **Rehearse it.** A rollback path that has never been exercised is a guess; test
  it in dev/staging.
- **Prod is manual on purpose** so both promotion and rollback to prod are
  deliberate, audited actions.

## What was NOT done

- No rollback or Argo CD synchronization command was run.
- No environment was rolled back; no cluster exists.
