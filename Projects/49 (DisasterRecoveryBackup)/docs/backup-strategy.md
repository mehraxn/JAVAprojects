# Backup Strategy

How backups are taken, stored, retained, and — critically — verified. **No
backup was taken and nothing was stored.**

## What to back up

The **PostgreSQL database** is the irreplaceable asset. Everything else (app,
manifests, images) is redeployable from Git/registry.

## Backup types

| Type | Tool | RPO | Trade-off |
| --- | --- | --- | --- |
| Logical (used here) | `pg_dump -Fc` | = backup interval (e.g. 24h) | portable, simple, selective restore; slower for huge DBs |
| Physical + WAL (PITR) | `pg_basebackup` + WAL archive | seconds–minutes | tiny RPO, point-in-time; version/platform-locked, more moving parts |

This project uses **daily logical backups** (see
[../backup/backup-script.example.sh](../backup/backup-script.example.sh) and
[../k8s/backup-cronjob.yaml](../k8s/backup-cronjob.yaml)). For a smaller RPO you
would add WAL archiving on top.

## Schedule & retention (3-2-1 + GFS)

- **Schedule:** nightly at 02:00 (CronJob), consistent snapshot via `pg_dump`'s
  transaction — no downtime.
- **3-2-1 rule:** ≥ **3** copies, on ≥ **2** media/locations, with ≥ **1**
  **off-site**. The on-cluster PVC is one copy; a real setup also ships to
  off-region object storage.
- **GFS retention (Grandfather-Father-Son):** keep e.g. 7 daily, 4 weekly, 12
  monthly. The example script prunes by age (`RETENTION_DAYS`).

## Storage & durability

- Backups land on the **backup PVC** ([../k8s/pvc.yaml](../k8s/pvc.yaml)), which
  is separate from the database's own volume so losing the DB pod does not lose
  the backups.
- **A single-cluster PVC is not DR.** Copy backups **off-cluster / off-region**
  to object storage with **versioning + immutability (object lock)** so
  ransomware or a fat-fingered delete cannot destroy them.

## Security

- **Encryption** in transit (TLS to storage) and at rest (server-side or
  client-side before upload).
- Credentials come only from a **Secret** ([../k8s/secret.example.yaml](../k8s/secret.example.yaml));
  never in scripts, images, or Git. All values in this repo are placeholders.

## Verification — a dump is not a backup until it restores

Backups fail silently more often than anyone expects. Verify at multiple levels:

1. **Existence & freshness** — a success marker + alert if the newest backup is
   too old (`../monitoring/backup-alerts.example.yml`).
2. **Integrity** — a SHA-256 checksum stored beside each dump; checked before
   restore.
3. **Restore test** — periodically restore into a **disposable** target and run a
   validation query. This is the only proof that matters.

The [restore-runbook.md](restore-runbook.md) does 2 and 3.

## What was NOT done

- No dump was created; no checksum, marker, or off-site copy exists.
- No retention pruning ran; no verification restore was performed.
