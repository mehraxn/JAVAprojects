# Test Results

Real output captured after running the lab locally. Generated backup files are
ignored by Git, so this file is the portfolio evidence.

## Environment

- Date/time of test: 2026-07-05, ~15:21 UTC
- Machine/OS: Windows 11 Home (WSL2 backend), Bash shell
- Docker version: 29.4.2
- Docker Compose version: v5.1.3
- PostgreSQL image: postgres:16-alpine (PostgreSQL 16.14)

## Commands run

```bash
docker compose config --quiet
docker compose up -d
./scripts/backup.sh
./scripts/restore.sh backups/app-20260705T152153Z.dump
DR_TARGET_ENV=production ./scripts/restore.sh backups/app-20260705T152153Z.dump
PGHOST=prod.example.com ./scripts/restore.sh backups/app-20260705T152153Z.dump
```

Both database services reported `healthy` before backup/restore:

```text
NAME                                              SERVICE            STATUS
disaster-recovery-backup-lab-postgres-1           postgres           Up (healthy)
disaster-recovery-backup-lab-restore-postgres-1   restore-postgres   Up (healthy)
```

## Evidence to paste

### Sample data before data-loss simulation

```text
 id |  name   |          created_at
----+---------+-------------------------------
  1 | alpha   | 2026-07-05 15:21:20.593923+00
  2 | bravo   | 2026-07-05 15:21:20.593923+00
  3 | charlie | 2026-07-05 15:21:20.593923+00
(3 rows)
```

### Backup output

```text
Creating a custom-format backup from the local Compose database...
Backup complete: backups/app-20260705T152153Z.dump
Checksum:       backups/app-20260705T152153Z.dump.sha256
Next step: ./scripts/restore.sh backups/app-20260705T152153Z.dump
```

Artifacts generated in `backups/`:

```text
app-20260705T152153Z.dump          (2830 bytes)
app-20260705T152153Z.dump.sha256
backup_last_success.prom
```

Checksum file contents:

```text
862bd94a5c964d16c06d638bd976a9b5a3f97accd80e665e034c5185a65df3b9  app-20260705T152153Z.dump
```

### Simulated data loss on the primary (disposable local container only)

```text
DELETE 3
0
```

### Restore output

```text
Verifying SHA-256 checksum before restore...
app-20260705T152153Z.dump: OK
Recreating disposable database app_restore...
NOTICE:  database "app_restore" does not exist, skipping
RESTORE SUCCEEDED: checksum valid and app_data contains 3 row(s).
Target: local Compose service restore-postgres, database app_restore.

real    0m2.285s
```

Restored data in `app_restore` (on the separate `restore-postgres` container):

```text
 id |  name   |          created_at
----+---------+-------------------------------
  1 | alpha   | 2026-07-05 15:21:20.593923+00
  2 | bravo   | 2026-07-05 15:21:20.593923+00
  3 | charlie | 2026-07-05 15:21:20.593923+00
(3 rows)
```

Primary database after restore (proves it was **not** overwritten):

```text
0
```

### Production guard output

```text
$ DR_TARGET_ENV=production ./scripts/restore.sh backups/app-20260705T152153Z.dump
RESTORE FAILED: DR_TARGET_ENV must be 'local'; production-like restores are refused.
(exit 1, no database touched)

$ PGHOST=prod.example.com ./scripts/restore.sh backups/app-20260705T152153Z.dump
RESTORE FAILED: External database variables are set. Unset PGHOST and DATABASE_URL.
(exit 1, no database touched)
```

## Measurements

| Measurement | Value |
| --- | --- |
| Sample rows before data-loss simulation | 3 |
| Restored rows after restore | 3 |
| Selected backup timestamp | 2026-07-05T15:21:53Z |
| Observed backup age / RPO window | < 1 minute (backup taken immediately before the drill) |
| Restore duration / RTO measurement | ~2.3 s (`real 0m2.285s`) |

## Result

- Backup created successfully: yes
- Checksum verified successfully: yes
- Restore completed into disposable database: yes
- Primary database was not overwritten by restore: yes (primary stayed at 0 rows; restore landed in `app_restore`)
- Production guard refused unsafe target: yes (both `DR_TARGET_ENV=production` and a set `PGHOST` were refused before any database change)

## Notes / follow-up fixes

- Full flow (start -> backup -> simulated loss -> restore -> guard checks)
  passed with no code changes required. The restore verified the SHA-256
  checksum, rebuilt the disposable `app_restore` database, and validated a
  non-zero `app_data` row count before reporting success.
- Docker Engine must be running for every command; all work happens against
  disposable local Compose containers with lab-only credentials.
