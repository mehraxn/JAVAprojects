# Restore runbook

This runbook separates the executable local drill from a real incident. Never
restore directly over a production database before validating the selected
backup in an isolated target.

## Local verification restore

1. Start a timer for the measured RTO.
2. Select the newest appropriate dump from `backups/`.
3. Run the guarded restore script:

   ```bash
   ./scripts/restore.sh backups/app-YYYYMMDDTHHMMSSZ.dump
   ```

4. Confirm the script reports a valid checksum and a non-zero `app_data` count.
5. Independently inspect the restored rows:

   ```bash
   docker compose exec -T restore-postgres \
     psql -U restore_user -d app_restore -c "TABLE app_data;"
   ```

6. Record the elapsed restore time and the selected backup's age. Do not claim
   an RTO or RPO measurement that was not observed.

The script always targets `app_restore` inside the local `restore-postgres`
container. It rejects a non-local `DR_TARGET_ENV`, external database variables,
missing checksums, unexpected filenames, and archives outside `backups/`.

## Production incident outline

The local script is intentionally not a production restore tool. During a real
incident:

1. obtain incident authority and stop or isolate application writes;
2. preserve the damaged source and relevant logs;
3. identify the last-known-good recovery point;
4. retrieve an immutable off-site backup through an audited process;
5. verify its signature/checksum;
6. restore into an isolated recovery environment;
7. run database integrity and application-level validation;
8. approve and execute a controlled traffic cutover;
9. monitor errors and latency, retaining a rollback path;
10. record the timeline, measured RPO/RTO, decisions, and follow-up actions.

If validation fails, keep the damaged source, try the next appropriate verified
backup, and escalate through the incident plan. Never weaken checksum or safety
guards merely to make a restore proceed.
