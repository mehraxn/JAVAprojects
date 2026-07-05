# RPO and RTO

The two numbers that define a recovery strategy. **These are proposed targets,
not measured or achieved results — nothing has been drilled.**

## Definitions

- **RPO — Recovery Point Objective**: the maximum acceptable **data loss**,
  measured *backwards* from the moment of failure. "How much recent data can we
  afford to lose?" It is bounded by **how often you back up**.
- **RTO — Recovery Time Objective**: the maximum acceptable **downtime**, measured
  *forwards* from failure to service restored. "How long can we be down?" It is
  bounded by **how fast you can restore**.

```
        data loss (RPO)            downtime (RTO)
   |<---------------------->|<----------------------->|
 last good              FAILURE                    service
  backup                                            restored
```

## Proposed targets (this project)

| Metric | Target | Driven by |
| --- | --- | --- |
| RPO | ≤ 24h | daily logical backup (CronJob at 02:00) |
| RTO | ≤ 1h | restore a pg_dump into a disposable target + validate |

Want a smaller **RPO**? Back up more often, or add **WAL archiving / PITR**
(RPO → minutes). Want a smaller **RTO**? Pre-provision a warm standby, use
parallel `pg_restore -j`, or use physical restore instead of logical.

## Relationship to backup frequency

RPO ≈ backup interval. Daily backups ⇒ up to ~24h of loss. Hourly ⇒ ~1h.
Continuous WAL archiving ⇒ seconds. The schedule in
[../k8s/backup-cronjob.yaml](../k8s/backup-cronjob.yaml) (`0 2 * * *`) is chosen to
match the 24h RPO target — change both together.

## How the targets are measured (in a drill, NOT executed)

During an [incident simulation](incident-simulation.md):

- **RPO measured** = failure time − timestamp of the backup you restored.
- **RTO measured** = time service restored − failure time.

Compare measured vs target; if measured > target, adjust frequency (RPO) or
restore mechanism (RTO). Until a drill runs, **no target is verified**.

## What was NOT done

- No backup interval was exercised; no restore was timed.
- RPO/RTO remain **targets only** — none has been achieved or measured.
