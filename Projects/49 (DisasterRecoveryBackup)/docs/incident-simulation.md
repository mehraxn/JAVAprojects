# Incident Simulation (Game Day)

How you would **rehearse** disaster recovery so the runbooks are proven, not
hoped-for. **No simulation was run** — this describes the drill.

## Why simulate

"Backups are working" is a claim; a **restore drill** is proof. Game days find
the gaps (a missing checksum, a wrong credential, an unrestorable dump, an RTO
that's really 4 hours) *before* a real incident does. An untested backup should
be assumed broken.

## Ground rules

- Run in a **disposable** environment only — never against production.
- Use a **copy** of a backup; never the only copy.
- Time everything; compare to the [rpo-rto.md](rpo-rto.md) targets.
- Write a blameless retro; turn every gap into a runbook fix.

## Scenarios

| # | Scenario | Inject (in a lab) | Expected recovery |
| --- | --- | --- | --- |
| 1 | Accidental data loss | `DELETE FROM app_data;` on a test DB | restore latest backup; RPO = age of that backup |
| 2 | Volume loss | delete the DB PVC / pod on a test cluster | StatefulSet reschedules; if data gone, restore |
| 3 | Corrupt backup | point restore at a truncated dump | checksum check fails → fall back to older backup |
| 4 | Region loss | rebuild cluster from Git in a lab | redeploy manifests; restore from off-site copy |

## Drill procedure (NOT executed)

1. Pick a scenario; announce start; start the clock.
2. Inject the failure in the disposable environment.
3. Follow [restore-runbook.md](restore-runbook.md) exactly — no shortcuts (that's
   the point).
4. Record: detection time, decision time, restore time (**RTO**), and the
   data-loss window (**RPO**).
5. Validate data + application.
6. Retro: what was slow, unclear, or missing? File fixes.

## Chaos-style failure injection (examples, NOT executed)

```bash
# NOT executed — all against a DISPOSABLE lab only:
kubectl delete pod postgres-0                 # pod loss (StatefulSet reschedules)
kubectl delete pvc data-postgres-0            # volume loss (forces a restore)
psql -h postgres-dr -d app_test -c "DROP TABLE app_data;"   # data loss
```

## Success criteria

A drill passes when: the correct backup was found and verified, the restore
completed within the **RTO target**, data loss was within the **RPO target**, the
application validated green, and the retro produced concrete improvements.

## What was NOT done

- No failure was injected; no drill was run.
- No recovery time or data-loss window was measured.
- **No claim is made that recovery works.**
