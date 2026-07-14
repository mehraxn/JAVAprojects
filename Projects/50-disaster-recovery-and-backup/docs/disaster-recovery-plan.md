# Disaster Recovery Plan

This is the top-level disaster-recovery plan for a PostgreSQL-backed application.
The repository includes a **local Docker Compose lab** that can create a backup,
verify its checksum, and restore it into a disposable database. The Kubernetes
manifests and production incident process remain examples and must be tested in a
real target environment before operational use.

## Scope

What we protect and how bad "gone" can get:

| Asset | Protection | Notes |
| --- | --- | --- |
| PostgreSQL data | logical backups with `pg_dump`, checksum validation, restore drill | the only truly irreplaceable asset |
| Application/configuration | Git-managed manifests and source code | redeployable from source |
| Container images | container registry, rebuild path from Git | should be rebuildable and reproducible |
| Secrets | external secret manager or Kubernetes Secret | restored out-of-band; never stored in plain backups |

The stateless application can be recreated from Git. The database is the main
reason the DR plan exists.

## Recovery targets

| Metric | Target used in the design | Current status |
| --- | --- | --- |
| RPO | <= 24h with daily logical backups | target only; measure during a real drill |
| RTO | <= 1h for restore + validation | target only; measure with `TESTING.md` |

A local backup/restore workflow is implemented, but this repository intentionally
does not include fake RPO/RTO results. Record real timings in `TEST_RESULTS.md`
after running the commands yourself.

## Failure scenarios covered

1. **Accidental data loss**: restore the latest verified backup into a disposable
   database, validate the data, then decide how to recover the primary system.
2. **Corrupted or lost database volume**: recreate the database environment and
   restore from the latest valid backup.
3. **Broken backup artifact**: checksum verification fails before restore; choose
   an older known-good backup.
4. **Cluster or region loss**: rebuild infrastructure from Git and restore from
   off-site storage. This is a design requirement, not implemented in the local
   lab.

## Roles in a real incident

| Role | Responsibility |
| --- | --- |
| Incident commander | declares the incident, owns timeline, decisions, and communication |
| Database operator | selects backup, verifies integrity, performs restore, records evidence |
| Application owner | validates application behavior and approves traffic cutover |
| Security/operator reviewer | confirms access, secrets, audit trail, and rollback plan |

## Recovery flow

```text
detect failure
  -> declare incident
  -> stop or isolate unsafe writes
  -> select latest known-good backup
  -> verify checksum/signature
  -> restore into isolated target
  -> validate database and application
  -> approve controlled cutover
  -> monitor and document timeline
  -> retrospective and fixes
```

## What is implemented here

- Local PostgreSQL primary database with sample `app_data` records.
- Timestamped `pg_dump -Fc` backup creation.
- SHA-256 checksum sidecar file.
- Safe restore into a separate disposable `restore-postgres` container.
- Restore validation using the `app_data` table.
- Example Prometheus backup freshness metric and alert rules.
- Kubernetes StatefulSet, Service, backup PVC, Secret example, and suspended
  CronJob manifests for review.

## What is not implemented here

- Production restore, traffic cutover, or failover automation.
- Off-site immutable object storage.
- Encrypted backup artifacts.
- WAL archiving or point-in-time recovery.
- A deployed Kubernetes cluster or deployed monitoring stack.
- Real measured RPO/RTO results.

## Related documents

- [backup-strategy.md](backup-strategy.md) — how backups are taken and verified.
- [restore-runbook.md](restore-runbook.md) — local and production restore steps.
- [rpo-rto.md](rpo-rto.md) — target definitions and measurement method.
- [incident-simulation.md](incident-simulation.md) — game-day scenarios.
- [../TESTING.md](../TESTING.md) — executable local recovery drill.
- [../TEST_RESULTS.md](../TEST_RESULTS.md) — place for real measured evidence.
