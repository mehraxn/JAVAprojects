# Incident Simulation / Game Day

This document explains how to rehearse disaster recovery. The repository already
contains an executable **local accidental-data-loss drill** in `TESTING.md`. The
other scenarios below are planned game-day exercises for Kubernetes or production-
like environments and are not executed by this local lab.

## Why simulate

"Backups are working" is only a claim until a restore succeeds. A game day finds
problems such as missing checksums, wrong credentials, unrestorable dumps, slow
restore steps, unclear ownership, or missing validation before a real incident.
An untested backup should be treated as suspicious.

## Ground rules

- Run drills in a disposable environment unless an approved incident commander
  explicitly authorizes a production action.
- Never restore directly over the only production database before validating the
  backup somewhere isolated.
- Use a copy of the backup, not the only copy.
- Time each stage and record evidence in `TEST_RESULTS.md` or an incident log.
- Turn every delay or unclear step into a runbook fix.

## Scenario matrix

| # | Scenario | Where it can be tested here | Expected result |
| --- | --- | --- | --- |
| 1 | Accidental data loss | Local Compose lab | delete rows from primary, restore backup into disposable DB, validate rows |
| 2 | Corrupt backup | Local Compose lab | checksum check fails before restore |
| 3 | Database volume loss | Kubernetes or a disposable cluster | recreate database target and restore from backup |
| 4 | Cluster or region loss | Future production-like lab | rebuild from Git, retrieve off-site backup, restore and cut over |

## Executable local drill

The local drill is documented in [../TESTING.md](../TESTING.md). It tests the most
important safety property: a backup can be restored and validated **without
modifying the primary database**.

High-level flow:

```text
start Compose lab
  -> confirm sample data
  -> run scripts/backup.sh
  -> delete rows from local primary
  -> run scripts/restore.sh <backup>
  -> verify rows in app_restore
  -> confirm production guard refuses DR_TARGET_ENV=production
```

## Production-style drill procedure

1. Pick a scenario and announce the game-day window.
2. Start the clock and record the initial state.
3. Inject the failure in the disposable environment.
4. Follow the relevant runbook exactly.
5. Record detection time, decision time, restore duration, validation duration,
   and selected backup age.
6. Compare measured RPO/RTO against the targets.
7. Write a short retrospective with fixes.

## Example failure-injection commands

These commands are examples only. Run them only in a disposable lab where losing
state is acceptable.

```bash
kubectl delete pod postgres-0
kubectl delete pvc data-postgres-0
psql -h postgres-dr -d app_test -c "DROP TABLE app_data;"
```

## Success criteria

A drill passes when the selected backup is verified, restore completes without
bypassing safeguards, `app_data` validates successfully, the observed RPO/RTO are
recorded, and the retrospective produces concrete follow-up work.

## Current evidence boundary

The repository provides the commands needed to run scenario 1 locally. It does
not commit generated backup files or fake test results. After running the drill,
add your real command output and measurements to `TEST_RESULTS.md`.
