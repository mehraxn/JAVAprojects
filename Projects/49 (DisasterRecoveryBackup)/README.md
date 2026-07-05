# Disaster Recovery and Backup Lab

This project is a locally executable PostgreSQL disaster-recovery lab. It starts
a primary database with sample data, creates custom-format `pg_dump` archives,
adds SHA-256 checksums, and restores a selected archive into a separate disposable
database. The restore is considered successful only when the checksum passes and
the restored `app_data` table contains rows.

## Architecture

```text
db/init.sql
    |
    v
postgres:5432 (primary) --pg_dump--> backups/app-<UTC timestamp>.dump
                                            |
                                            +--> .sha256 checksum
                                            +--> Prometheus textfile metric
                                            |
                                            v
restore-postgres:5433 (disposable) <-- pg_restore + app_data validation
```

Both databases are local Docker containers with separate named volumes. Restore
scripts cannot target an arbitrary host or database: they always recreate
`app_restore` inside the `restore-postgres` service.

## Prerequisites

- Docker Engine or Docker Desktop with Docker Compose v2
- Bash (Linux, macOS, WSL, or Git Bash on Windows)
- Ports 5432 and 5433 available, or override `POSTGRES_PORT` and
  `RESTORE_POSTGRES_PORT`

The Compose credentials are obvious local-only development values. They are not
real secrets and must never be reused in a shared or production environment.

## Run locally

From this project directory:

```bash
docker compose up -d
docker compose ps
```

The primary database initializes `app_data` automatically the first time its
volume is created. Verify the sample records:

```bash
docker compose exec -T postgres \
  psql -U app_user -d app -c "TABLE app_data;"
```

Initialization scripts run only for a new PostgreSQL volume. To reset this lab
completely, use `docker compose down -v` and then start it again. This deletes
only the two local Compose database volumes; backup files remain in `backups/`.

## Create a backup

```bash
./scripts/backup.sh
```

On Windows PowerShell, invoke the same script through Bash:

```powershell
bash ./scripts/backup.sh
```

The script:

1. checks that the Compose primary database is ready;
2. streams `pg_dump -Fc` to a timestamped file in `backups/`;
3. confirms that `pg_restore` can read the archive catalogue;
4. creates a SHA-256 sidecar file;
5. writes a Prometheus textfile metric only after backup success.

Generated backups, checksums, and metrics are ignored by Git.

## Restore and validate

Select the backup printed by the backup command:

```bash
./scripts/restore.sh backups/app-20260101T120000Z.dump
```

The restore script requires the matching `.sha256` file, verifies it before any
database change, recreates only the fixed `app_restore` database in the isolated
restore container, runs `pg_restore --exit-on-error`, and checks that `app_data`
contains at least one row. It refuses non-local `DR_TARGET_ENV` values and refuses
to run when external `PGHOST` or `DATABASE_URL` variables are present.

The primary database is never overwritten by the restore workflow. This makes
restore drills repeatable without risking the source data.

## RPO and RTO

- **Recovery Point Objective (RPO)** is the maximum acceptable data-loss window.
  A daily successful backup gives a theoretical RPO of up to 24 hours. The age of
  the backup actually selected during a drill is the observed recovery point.
- **Recovery Time Objective (RTO)** is the maximum acceptable recovery duration.
  Measure from the decision to restore until checksum verification, restore, and
  validation finish.

This repository does not claim measured RPO or RTO results. Follow `TESTING.md`
and record your own local measurements.

## Implemented versus demo-only

Implemented and locally executable:

- two isolated PostgreSQL 16 Compose services;
- automatic sample schema/data initialization;
- timestamped custom-format backups and SHA-256 checksums;
- safe restore into a disposable database with data validation;
- a generated backup-success metric file;
- an optional local Ansible checksum-validation playbook.

Examples that are not part of the local Compose execution:

- `k8s/`: StatefulSet, headless Service, backup PVC, suspended CronJob, and
  placeholder Secret. These require a Kubernetes cluster and have not been
  deployed by this project.
- `monitoring/backup-alerts.example.yml`: realistic Prometheus alert rules, but
  the local lab does not run Prometheus or node_exporter.
- `terraform/`: documented extension point only; no cloud resources are created.
- off-site copies, immutable object storage, WAL archiving, and point-in-time
  recovery are design/future work.

## Project structure

```text
docker-compose.yml       local primary and disposable restore databases
db/init.sql              app_data schema and sample rows
scripts/backup.sh        executable backup/checksum workflow
scripts/restore.sh       guarded restore and validation workflow
backups/                 ignored runtime artifacts
k8s/                     suspended Kubernetes deployment examples
monitoring/              backup freshness rule and integration notes
docs/                    DR strategy, policy, incident, and RPO/RTO documents
runbooks/                operational backup, restore, and failover checklists
ansible/                  optional local checksum validation
terraform/                documented future cloud extension point
```

## Security and limitations

- No real secrets are committed. Kubernetes credentials remain placeholders.
- Local backup files are not encrypted. Real backups require encryption,
  access controls, off-site copies, retention, and immutability.
- A local bind-mounted backup directory is not protection from host loss.
- Logical dumps do not provide point-in-time recovery between backup runs.
- Kubernetes and monitoring examples must be tested in their target environment
  before operational use.

## Future improvements

- upload encrypted backups to versioned, immutable object storage;
- add WAL archiving and point-in-time recovery;
- run scheduled automated restore drills in CI or a disposable environment;
- add Prometheus and node_exporter to the Compose lab;
- sign backup metadata and record restore audit events;
- test larger datasets and document measured RTO trends.

See [TESTING.md](TESTING.md) for the exact end-to-end recovery drill.
