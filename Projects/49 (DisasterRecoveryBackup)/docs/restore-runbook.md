# Restore Runbook

Step-by-step recovery of the PostgreSQL database from a backup. **No restore was
performed; every command is marked NOT executed.** This is the canonical restore
procedure (the older `runbooks/restore.md` is a superseded stub).

## Before you start

- Confirm you have **incident authority** ([disaster-recovery-plan.md](disaster-recovery-plan.md)).
- Restore into a **disposable / DR target first**, never straight over a
  production DB. The example script refuses a host containing `prod`.
- Start a timer now — elapsed time is your measured **RTO**.

## Steps

### 1. Declare & isolate
Stop writes to the damaged database (scale the app to 0 or put it in maintenance)
so you are not racing the corruption.

```bash
# NOT executed:
kubectl scale deployment app --replicas=0
```

### 2. Select a backup
Pick the most recent **verified** backup at or before the last-known-good time.
The gap between "now" and that backup's timestamp is your **RPO** for this
incident.

```bash
# NOT executed — list available backups:
ls -l /backups/app-*.dump
```

### 3. Verify integrity BEFORE restoring
```bash
# NOT executed:
sha256sum --check /backups/app-<ts>.dump.sha256
```

### 4. Restore into the target
```bash
# NOT executed — see backup/restore-script.example.sh:
PGHOST=postgres-dr PGUSER=app PGDATABASE=app_restore \
  ./backup/restore-script.example.sh --file /backups/app-<ts>.dump --confirm
```

### 5. Validate the data (integrity + application)
```bash
# NOT executed — integrity: expected tables/rows present:
psql -h postgres-dr -U app -d app_restore -c "SELECT count(*) FROM app_data;"
```
Then run **application-level** checks: start the app against the restored DB and
exercise a critical read/write path. A restore that loads but fails app checks is
not a successful recovery.

### 6. Cut traffic over (controlled)
Only after validation, point the app at the restored DB and scale it back up.
Watch error rate and latency.

```bash
# NOT executed:
kubectl scale deployment app --replicas=3
```

### 7. Record evidence
Note: backup timestamp used (→ RPO), elapsed time (→ RTO), who did what, and any
surprises. Feed this into [incident-simulation.md](incident-simulation.md) and
the retrospective.

## If the restore fails

- Do **not** delete the damaged source until a restore has succeeded elsewhere.
- Try the next-older backup; a bad dump is exactly why we verify.
- Escalate per the DR plan; keep the timer running and comms flowing.

## What was NOT done

- No `kubectl`, `psql`, `pg_restore`, or script was executed.
- No database was restored or validated; **recovery was not tested.**
