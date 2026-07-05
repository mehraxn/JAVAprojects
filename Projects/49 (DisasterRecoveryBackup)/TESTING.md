# Testing — Disaster Recovery & Backup

> **Nothing was executed.** No backup, restore, script, `kubectl`, database, or
> cloud command ran; no Kubernetes resource was created and **recovery was not
> tested.** This documents static review and expected behavior. (Scripts were
> parse-checked with `bash -n` only — that does not run them.)

## 1. Static validation checklist

- [ ] `backup-script.example.sh` refuses a placeholder/unset host; no hardcoded creds.
- [ ] `restore-script.example.sh` requires `--confirm` and refuses a host containing `prod`.
- [ ] CronJob has `suspend: true` and pulls creds from the Secret.
- [ ] StatefulSet uses `volumeClaimTemplates`; `pvc.yaml` is a separate backup volume.
- [ ] CronJob schedule (`0 2 * * *`) matches the ≤24h RPO target.

## 2. File existence checks

- [ ] `backup/`: `backup-script.example.sh`, `restore-script.example.sh`, `pg-dump-example.md`
- [ ] `k8s/`: `postgres-statefulset.yaml`, `pvc.yaml`, `backup-cronjob.yaml`, `secret.example.yaml`
- [ ] `docs/`: `disaster-recovery-plan.md`, `backup-strategy.md`, `restore-runbook.md`, `incident-simulation.md`, `rpo-rto.md`
- [ ] `.gitignore`, `README.md`, `TESTING.md`

## 3. YAML / config review checklist

- [ ] All YAML well-formed; StatefulSet/CronJob/PVC/Secret shapes valid.
- [ ] Checksums written on backup and checked on restore.
- [ ] Backup ↔ RPO alignment (daily schedule ↔ ≤24h RPO).
- [ ] Docs separate backup success from restore success; RPO/RTO are targets, not claims.

## 4. Security checks

- [ ] **No real secrets** — `secret.example.yaml` holds `REPLACE_ME` placeholders.
- [ ] **No real credentials** — scripts read creds from env only.
- [ ] **No production endpoints** — restore refuses `prod`; CronJob suspended.
- [ ] `.gitignore` excludes real `secret.yaml`, `*.dump`, `backups/`.

## 5. Commands normally used — NOT executed

```bash
# NOT executed
kubectl apply -f k8s/secret.yaml   # from secret.example.yaml, filled securely
kubectl apply -f k8s/postgres-statefulset.yaml -f k8s/pvc.yaml -f k8s/backup-cronjob.yaml
./backup/backup-script.example.sh
./backup/restore-script.example.sh --file backups/app-<ts>.dump --confirm
```

## 6. Expected results in a proper environment

- The CronJob (once unsuspended) writes a checksummed dump nightly to the backup PVC.
- The restore script verifies the checksum, restores into a disposable target, and the validation query returns a row count.
- A game-day scenario recovers within the RTO target; data loss is within the RPO target.
- Freshness alert fires if the newest backup is too old.

## 7. Manual review checklist (portfolio quality)

- [ ] README explains backup strategy, restore, RPO/RTO, and verification clearly.
- [ ] Scripts are safe by construction (guards, no hardcoded creds).
- [ ] Backups-are-not-DR-until-restored point is made honestly.
- [ ] Every command marked NOT executed; no fake badges/screenshots.
- [ ] RPO/RTO framed as targets, never as achieved results.
