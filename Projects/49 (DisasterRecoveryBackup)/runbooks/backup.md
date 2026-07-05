# Backup Runbook

## Local lab procedure

1. Confirm Docker is running and the primary service is healthy with
   `docker compose ps`.
2. Run `./scripts/backup.sh` from the project directory.
3. Confirm the script prints both a `.dump` path and its `.sha256` path.
4. Restore the archive into the disposable target with `scripts/restore.sh`.
5. Record the backup timestamp, restore result, and measured duration.

Do not copy local credentials or unencrypted lab backups into a shared system.
Production backups additionally require capacity checks, encryption, off-site
immutable storage, retention enforcement, access auditing, and scheduled restore
verification.
