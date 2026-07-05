# Backup Policy

## Local lab policy

- Protected data: the `app` PostgreSQL database.
- Method: consistent custom-format logical dump with `pg_dump -Fc`.
- Integrity: required SHA-256 sidecar and archive-catalogue inspection.
- Verification: restore into the separate `app_restore` database and validate
  that `app_data` contains rows.
- Retention: manual; generated files are Git-ignored and are never deleted by
  the local backup script.
- Ownership: the person running the lab records and removes local artifacts.

## Production requirements

Before adapting this lab, define an approved schedule, RPO/RTO targets, encrypted
off-site immutable storage, retention/deletion rules, least-privilege access,
monitoring ownership, incident escalation, and recurring restore drills. Local
Compose credentials and unencrypted files are not approved production controls.
