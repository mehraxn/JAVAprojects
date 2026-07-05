# Disaster Recovery Plan

The top-level DR plan for the Java/PostgreSQL/Kubernetes app. **This is a design
document — no backup, restore, or recovery was ever performed.**

## Scope

What we protect and how bad "gone" can get:

| Asset | Protection | Notes |
| --- | --- | --- |
| PostgreSQL data | logical backups (pg_dump) + off-site copies | the only truly irreplaceable asset |
| App/config (manifests, Helm) | Git | redeployable from source |
| Container images | registry (+ backup registry) | rebuildable from Git |
| Secrets | secrets manager | restored out-of-band, never from a plain backup |

The stateless app can be recreated from Git at any time; **the database is the
thing DR exists for.**

## Targets

- **RPO** (max data loss): see [rpo-rto.md](rpo-rto.md) — target ≤ 24h with daily
  logical backups (minutes with WAL archiving).
- **RTO** (max downtime): target ≤ 1h to restore into a disposable environment.

These are **targets**, not achieved measurements. Nothing here has been drilled.

## Failure scenarios covered

1. **Accidental data loss** (bad migration, `DELETE` without `WHERE`) → restore
   from the latest good backup.
2. **Volume / node loss** (PVC corruption, node failure) → the StatefulSet
   reschedules; if the volume is lost, restore from backup.
3. **Cluster / region loss** → rebuild the cluster from Git, restore the DB from
   **off-site** backups. (This is why on-cluster PVC backups alone are not DR.)

## Roles

| Role | Responsibility |
| --- | --- |
| Incident commander | declares the incident, owns decisions and comms |
| DB operator | runs the restore runbook |
| App owner | validates the application after restore, controls traffic |

## The recovery flow

```
detect → declare incident → isolate target → select good backup → restore
→ verify integrity → validate application → controlled traffic → retro
```

## Related documents

- [backup-strategy.md](backup-strategy.md) — how backups are taken/verified.
- [restore-runbook.md](restore-runbook.md) — the step-by-step restore.
- [rpo-rto.md](rpo-rto.md) — targets and how they relate to backup frequency.
- [incident-simulation.md](incident-simulation.md) — how we would drill this.

## What was NOT done

- No backup was taken; no restore or failover was performed.
- No cluster, volume, or database was created or touched.
- **Recovery has not been tested; no target is claimed as achieved.**
