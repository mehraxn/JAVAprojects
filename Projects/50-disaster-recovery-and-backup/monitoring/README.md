# Backup freshness monitoring

`scripts/backup.sh` writes `backups/backup_last_success.prom` after `pg_dump`,
archive inspection, and checksum creation all succeed. The file contains this
Prometheus textfile metric:

```text
disaster_recovery_backup_last_success_timestamp_seconds <unix_timestamp>
```

`backup-alerts.example.yml` is a valid example rule for a daily backup. It
alerts when the metric is missing or the latest success is more than 25 hours
old.

This monitoring integration is **demo-only**. The local Compose lab does not run
Prometheus or node_exporter. To make it executable, mount the generated `.prom`
file into node_exporter's textfile collector directory, configure Prometheus to
scrape node_exporter, and load the example rule. Test both alerts before using
them operationally.
