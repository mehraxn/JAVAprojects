# Disaster Recovery & Backup

*A disaster-recovery and backup design for a Java/PostgreSQL/Kubernetes app — scheduled logical backups, a verified restore procedure, RPO/RTO targets, and a game-day simulation plan.*

## Problem this project solves

Backups that are never restored are just hope. When data is lost — a bad
migration, a deleted volume, a lost region — you need a **tested** path back
within known limits. This project designs that path: automated `pg_dump`
backups, a **restore runbook that verifies integrity**, explicit **RPO/RTO**
targets, and an **incident simulation** to prove it all before a real outage.

## Technologies & concepts

- **PostgreSQL** logical backups (`pg_dump -Fc`) + `pg_restore` verification
- **Kubernetes** — StatefulSet with persistent volumes, backup CronJob, PVC, Secret
- **RPO / RTO**, **3-2-1** + **GFS** retention, checksums, off-site immutability
- **Failure simulation (game day)**, incident runbook, backup verification

## Architecture overview

```
 StatefulSet (Postgres) ── volumeClaimTemplates ─▶ DB PVC (durable)
        │
   CronJob: pg_dump (suspended) ─▶ backup PVC ─▶ [off-site object storage]
        │
   restore: verify checksum ─▶ pg_restore into DISPOSABLE target ─▶ validate
        │
   RPO = age of backup used   |   RTO = time to service restored
```

## Project structure

```text
backup/
  backup-script.example.sh   restore-script.example.sh   pg-dump-example.md
k8s/
  postgres-statefulset.yaml  pvc.yaml  backup-cronjob.yaml  secret.example.yaml
docs/
  disaster-recovery-plan.md  backup-strategy.md  restore-runbook.md
  incident-simulation.md  rpo-rto.md
.gitignore  README.md  TESTING.md
```

## Important files explained

- **backup/backup-script.example.sh** — `pg_dump -Fc` → SHA-256 checksum → retention prune → success marker; refuses a placeholder/unset host, never hardcodes creds.
- **backup/restore-script.example.sh** — verifies checksum → `pg_restore` → validation query; requires `--confirm` and **refuses a host containing `prod`**.
- **k8s/postgres-statefulset.yaml** — StatefulSet with per-pod durable volume (`volumeClaimTemplates`).
- **k8s/pvc.yaml** — a **separate** backup-storage volume so losing the DB pod doesn't lose the backups.
- **k8s/backup-cronjob.yaml** — nightly `pg_dump`, ships **suspended** by default, creds from the Secret.
- **docs/** — DR plan, backup strategy (3-2-1/GFS/verification), restore runbook, game-day simulation, RPO/RTO.

## How it would work in a real environment

The CronJob dumps the DB nightly to the backup PVC (and, in a real setup, to
off-region object storage with versioning + immutability). To recover: pick a
verified backup, check its checksum, restore into a **disposable** target,
validate data + application, then cut traffic over — timing it to measure RTO and
noting the backup age as RPO. Game days rehearse this so the runbook is proven.

## What was prepared but NOT executed

Prepared: backup/restore scripts + pg_dump reference, the K8s StatefulSet/PVC/
CronJob/Secret, and five DR docs. **Not executed:** no script ran, no `kubectl`,
no Kubernetes resource created, no database backed up or restored. **Recovery was
not tested and no RPO/RTO is claimed as achieved.**

## Security notes

- **No real secrets** — `secret.example.yaml` holds `REPLACE_ME` placeholders; `.gitignore` blocks real `secret.yaml`, `*.dump`, `backups/`.
- **No real credentials** — scripts read creds only from env (a Secret); never hardcoded.
- **No production endpoints** — restore refuses a `prod`-looking host; the CronJob is suspended.
- Backups should be encrypted at rest/in transit and shipped off-site with object-lock immutability.

## Limitations

- No cluster/database; no backup, restore, or drill was performed.
- A single-cluster PVC is **not** true DR — off-site copies are described, not created.
- Scripts were syntax-checked (`bash -n`) only; PostgreSQL/kubectl tooling was not run.

## Future improvements

- Add WAL archiving / PITR for a minutes-scale RPO; automate off-site sync.
- Automate periodic restore-verification jobs and backup-freshness alerting.
- Warm standby / read replica to shrink RTO; expand/contract migration checks in CI.

## What I learned

- **A dump isn't a backup until it restores and validates** — verification is the whole point.
- How **RPO** (backup frequency) and **RTO** (restore speed) drive the design.
- Why a **StatefulSet + separate backup PVC** and **off-site immutability** matter.
- Rehearsing recovery with **game days** so the runbook is proven, not hoped-for.
