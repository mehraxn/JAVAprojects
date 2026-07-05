# Recovery Flow

## Local implemented flow

```text
start Docker Compose lab
  -> load sample app_data rows
  -> run scripts/backup.sh
  -> create backups/app-<timestamp>.dump
  -> create backups/app-<timestamp>.dump.sha256
  -> simulate local data loss
  -> run scripts/restore.sh <backup>
  -> verify checksum
  -> recreate disposable app_restore database
  -> pg_restore into restore-postgres
  -> validate app_data row count
```

This flow is executable with the commands in `TESTING.md`.

## Production incident flow

```text
failure detection
  -> incident decision
  -> isolate unsafe writes
  -> select known-good backup
  -> verify backup integrity
  -> restore into isolated target
  -> database checks
  -> application checks
  -> controlled traffic cutover
  -> monitoring, evidence, and retrospective
```

The production flow is a runbook model. Traffic cutover, failover automation,
off-site object storage, and production validation are not implemented in this
local lab.
