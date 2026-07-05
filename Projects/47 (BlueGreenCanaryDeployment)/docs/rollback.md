# Rollback

How to get back to a known-good version for both strategies, plus the risks and
safety checks that make rollback reliable. **No rollback was performed; every
command is marked NOT executed.**

## Why these strategies roll back well

Both keep the previous version **running** during the change, so rollback is
"redirect traffic," not "redeploy the old version." That is what makes it fast.

## Blue-green rollback

The old track is still live and idle. Roll back by pointing the Service back:

```bash
# NOT executed — re-select blue (v1); green keeps running, so this is instant:
kubectl apply -f k8s/blue-green/service.yaml
```

Because no pods are recreated, recovery is a single selector change — typically
seconds.

## Canary rollback

Take the canary's traffic share to zero:

```bash
# NOT executed — replica-ratio: scale the canary down to 0:
kubectl scale deployment java-app-canary --replicas=0

# NOT executed — ingress weight: set the canary weight to 0 (or delete it):
#   nginx.ingress.kubernetes.io/canary-weight: "0"
kubectl apply -f k8s/canary/ingress-canary-example.yaml
```

Stable (v1) was always carrying most of the traffic, so dropping the canary
returns to 100% stable with no cold start.

## Rollback steps (runbook)

1. **Stop the rollout.** Halt any automated promotion / pause the pipeline.
2. **Redirect traffic** to the last known-good version (commands above).
3. **Verify recovery.** `/version` returns the old version; error rate and
   latency return to baseline.
4. **Preserve evidence.** Capture logs, metrics, and the failing version's image
   digest before scaling it down.
5. **Communicate.** Note user impact and the rollback in the incident channel.
6. **Investigate** before retrying. Do not re-attempt the same release blindly.

## Risks

- **Schema / data compatibility.** Both versions may run at once (always in
  canary; during the overlap in blue-green). A migration that v2 needs but v1
  cannot tolerate makes rollback unsafe. Use **backward-compatible, expand/​
  contract migrations**: add columns before use, remove them only after the old
  version is gone.
- **In-flight state / sessions.** A hard switch can drop sticky sessions or
  in-progress work if the app isn't stateless. Prefer stateless services or
  shared session state.
- **Capacity.** Blue-green needs ~2× capacity during overlap; a canary needs
  spare room for the extra track. Rolling back into insufficient capacity fails.
- **Rollback not rehearsed.** A rollback path that has never been tried is a
  guess. Rehearse it in a disposable environment.

## Safety checks before promoting

- Readiness probes green on the new track; `/version` confirms the expected
  version is actually serving.
- Canary error rate and latency within threshold vs stable over a bake period
  (see `../monitoring/rollout-alerts.example.yml`).
- A clear, **tested** rollback command and a named person with authority to call
  it.
- Migrations verified compatible with both versions.

## What was NOT done

- No `kubectl`/`helm` command was run; no cluster exists.
- No traffic was switched, shifted, or rolled back.
- No metrics were observed; **no claim is made that switching was tested.**
