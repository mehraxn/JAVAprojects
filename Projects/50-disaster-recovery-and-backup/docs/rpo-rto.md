# RPO and RTO

RPO and RTO define how much data loss and downtime the recovery strategy is meant
to tolerate. This project provides a local restore drill that can be used to
measure them, but the repository does not include invented measurements.

## Definitions

- **RPO — Recovery Point Objective**: the maximum acceptable data-loss window,
  measured backwards from the failure time to the recovery point. In this project
  it is mainly controlled by backup frequency.
- **RTO — Recovery Time Objective**: the maximum acceptable recovery duration,
  measured from the restore decision until the restored system is validated.

```text
        data loss / recovery point window          recovery duration
   |<------------------------------------>|<------------------------>|
 last good backup                     failure                 validated restore
```

## Targets used in this project

| Metric | Target | Main driver |
| --- | --- | --- |
| RPO | <= 24h | daily logical backup schedule |
| RTO | <= 1h | `pg_restore` into an isolated target plus validation |

These are design targets. To make them real claims, run the drill in
`TESTING.md`, time the restore, calculate the age of the selected backup, and
write the result in `TEST_RESULTS.md`.

## Relationship to backup frequency

RPO is approximately bounded by the backup interval:

- daily backups -> up to about 24 hours of possible data loss;
- hourly backups -> up to about 1 hour;
- continuous WAL archiving/PITR -> seconds or minutes, depending on the setup.

The Kubernetes CronJob example is scheduled at `0 2 * * *`, matching the 24-hour
RPO target. If the schedule changes, update the RPO target and alert threshold
with it.

## How to measure in the local lab

1. Create a backup with `./scripts/backup.sh`.
2. Simulate local data loss as shown in `TESTING.md`.
3. Start a timer before running `./scripts/restore.sh <backup>`.
4. Stop the timer when the script reports successful checksum and row-count
   validation.
5. Calculate observed RPO as `restore decision time - selected backup timestamp`.
6. Record both values in `TEST_RESULTS.md`.

## What this project does not prove automatically

- It does not prove production traffic cutover time.
- It does not prove Kubernetes scheduling, storage, DNS, or permissions.
- It does not prove off-site recovery from a lost host or region.
- It does not prove point-in-time recovery between backup runs.

Those require a production-like environment, off-site storage, and repeated
restore drills.
