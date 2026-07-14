# pg_dump / pg_restore — Reference

Reference notes for the PostgreSQL tools used by the executable local scripts.
The standalone commands below are learning examples; use `../scripts/` for the
guarded Compose workflow.

## Logical vs physical backups

- **Logical** (`pg_dump`) — exports SQL/data for a database. Portable across
  versions and architectures, restores selectively, ideal for a single app DB.
  Used by this project.
- **Physical** (`pg_basebackup` + WAL archiving) — byte-level copy of the whole
  cluster. Enables point-in-time recovery (PITR) and tiny RPO, but is tied to the
  same major version/platform. Mentioned in [../docs/backup-strategy.md](../docs/backup-strategy.md).

## Dump formats

| Flag | Format | Restore with | Notes |
| --- | --- | --- | --- |
| `-Fp` | plain SQL | `psql` | human-readable, no selective restore |
| `-Fc` | custom (compressed) | `pg_restore` | **recommended**: selective + parallel |
| `-Fd` | directory | `pg_restore -j` | parallel dump *and* restore |

## Common commands

```bash
# Full custom-format dump (scripts/backup.sh performs this through Compose):
pg_dump -h "$PGHOST" -U "$PGUSER" -Fc --no-owner --no-privileges \
  -f app-$(date -u +%Y%m%dT%H%M%SZ).dump app

# NOT executed — restore into a DISPOSABLE database:
pg_restore -h "$PGHOST" -U "$PGUSER" -d app_restore \
  --clean --if-exists --no-owner --no-privileges app-<ts>.dump

# NOT executed — parallel restore (4 jobs) for a large DB:
pg_restore -j 4 -h "$PGHOST" -U "$PGUSER" -d app_restore app-<ts>.dump

# NOT executed — restore just one table:
pg_restore -h "$PGHOST" -U "$PGUSER" -d app_restore -t app_data app-<ts>.dump
```

## Consistency

`pg_dump` runs inside a single transaction snapshot, so the dump is internally
consistent even while the DB takes writes — no downtime needed. For multiple
databases or cluster-wide objects/roles, `pg_dumpall` is the equivalent.

## Credentials

Never put the password on the command line or in a file in Git. Use `PGPASSWORD`
from a Kubernetes Secret (see [../k8s/secret.example.yaml](../k8s/secret.example.yaml))
or a `~/.pgpass` file with `0600` permissions. All examples here use placeholder
credentials only.

## Verification

A dump is not a backup until it has been **restored and validated**. The
[maintained restore script](../scripts/restore.sh) requires and verifies the
SHA-256 checksum, restores into a disposable target, and runs a row-count query. See
[../docs/backup-strategy.md](../docs/backup-strategy.md#verification).
