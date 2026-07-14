# Restore Runbook

The executable local restore command is:

```bash
./scripts/restore.sh backups/app-YYYYMMDDTHHMMSSZ.dump
```

It verifies the checksum and restores only into the disposable `app_restore`
database. Follow the detailed [restore runbook](../docs/restore-runbook.md) for
validation, production incident boundaries, and failure handling.
