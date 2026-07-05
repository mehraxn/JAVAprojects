# End-to-end local recovery test

Run these commands from the project directory in Bash. Docker Engine or Docker
Desktop must be running. Do not paste claimed timings into this document unless
you measured them yourself.

## 1. Validate and start the lab

```bash
docker info >/dev/null
docker compose config --quiet
docker compose up -d
docker compose ps
```

Wait until both database services report `healthy`.

## 2. Check automatically initialized sample data

```bash
docker compose exec -T postgres \
  psql -U app_user -d app -c "SELECT id, name, created_at FROM app_data ORDER BY id;"

docker compose exec -T postgres \
  psql -U app_user -d app -tA -c "SELECT count(*) FROM app_data;"
```

The second command should report a non-zero count. Record the actual count; do
not hardcode a result into a portfolio claim.

## 3. Create and inspect a backup

```bash
./scripts/backup.sh
BACKUP_FILE="$(find backups -maxdepth 1 -type f -name 'app-*.dump' -printf '%T@ %p\n' \
  | sort -nr | head -n1 | cut -d' ' -f2-)"
echo "$BACKUP_FILE"
test -f "$BACKUP_FILE"
test -f "${BACKUP_FILE}.sha256"
```

If your `find` implementation does not support `-printf`, copy the exact backup
path printed by `backup.sh` instead:

```bash
BACKUP_FILE="backups/app-YYYYMMDDTHHMMSSZ.dump"
```

## 4. Simulate data loss after the backup

This changes only the disposable local primary container:

```bash
docker compose exec -T postgres \
  psql -U app_user -d app -v ON_ERROR_STOP=1 -c "DELETE FROM app_data;"

docker compose exec -T postgres \
  psql -U app_user -d app -tA -c "SELECT count(*) FROM app_data;"
```

Confirm that the source table now has zero rows before continuing.

## 5. Restore the selected backup safely

The script restores into `app_restore` on the separate `restore-postgres`
container. It never overwrites the primary database.

```bash
time ./scripts/restore.sh "$BACKUP_FILE"
```

Measurement placeholder:

> Run this command and paste your measured RTO here: __________

Record the selected backup's age at recovery time as the observed recovery
point/data-loss window:

> Run the drill and paste your measured backup age (observed RPO) here: __________

## 6. Verify that the data exists again

```bash
docker compose exec -T restore-postgres \
  psql -U restore_user -d app_restore \
  -c "SELECT id, name, created_at FROM app_data ORDER BY id;"

docker compose exec -T restore-postgres \
  psql -U restore_user -d app_restore -tA \
  -c "SELECT count(*) FROM app_data;"
```

The restored table must exist and contain a non-zero row count. Compare it with
the count recorded before simulated data loss.

## 7. Verify the production guard

This command must fail before changing any database:

```bash
DR_TARGET_ENV=production ./scripts/restore.sh "$BACKUP_FILE"
```

Do not continue if it succeeds.

## 8. Optional Kubernetes static checks

These examples are not deployed by the Compose test. If `kubectl` is installed,
client-side parsing can be checked without contacting a cluster:

```bash
kubectl create --dry-run=client -f k8s/postgres-service.yaml -o yaml >/dev/null
kubectl create --dry-run=client -f k8s/postgres-statefulset.yaml -o yaml >/dev/null
kubectl create --dry-run=client -f k8s/pvc.yaml -o yaml >/dev/null
kubectl create --dry-run=client -f k8s/backup-cronjob.yaml -o yaml >/dev/null
```

This validates parsing only; it does not prove scheduling, storage, DNS, backup,
or restore behavior in a Kubernetes cluster.

## 9. Clean up

```bash
docker compose down
```

To delete both local database volumes and re-run `db/init.sql` on the next start:

```bash
docker compose down -v
```

Backup artifacts remain in `backups/` until you remove them deliberately.
