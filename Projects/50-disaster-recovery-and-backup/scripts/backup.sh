#!/usr/bin/env bash
set -Eeuo pipefail

PROJECT_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd -P)"
readonly PROJECT_ROOT
readonly COMPOSE_FILE="${PROJECT_ROOT}/docker-compose.yml"
readonly BACKUP_DIR="${PROJECT_ROOT}/backups"

fail() {
  echo "ERROR: $*" >&2
  exit 1
}

command -v docker >/dev/null 2>&1 || fail "Docker is required."
docker compose version >/dev/null 2>&1 || fail "Docker Compose v2 is required."

mkdir -p "$BACKUP_DIR"

if ! docker compose -f "$COMPOSE_FILE" exec -T postgres \
  pg_isready --username=app_user --dbname=app >/dev/null 2>&1; then
  fail "The local PostgreSQL service is not ready. Run: docker compose up -d"
fi

timestamp="$(date -u +%Y%m%dT%H%M%SZ)"
backup_name="app-${timestamp}.dump"
artifact="${BACKUP_DIR}/${backup_name}"
checksum="${artifact}.sha256"
metric="${BACKUP_DIR}/backup_last_success.prom"

[[ ! -e "$artifact" ]] || fail "Backup already exists: $artifact"

tmp_dump="${artifact}.tmp"
tmp_checksum="${checksum}.tmp"
tmp_metric="${metric}.tmp"
trap 'rm -f -- "$tmp_dump" "$tmp_checksum" "$tmp_metric"' EXIT

echo "Creating a custom-format backup from the local Compose database..."
docker compose -f "$COMPOSE_FILE" run --rm --no-deps -T db-tools \
  pg_dump --format=custom --no-owner --no-privileges app >"$tmp_dump"

[[ -s "$tmp_dump" ]] || fail "pg_dump produced an empty backup."

# Confirm that pg_restore can read the archive catalogue before publishing it.
docker compose -f "$COMPOSE_FILE" run --rm --no-deps -T db-tools \
  pg_restore --list <"$tmp_dump" >/dev/null

# Checksum the still-unpublished temp dump, but record the final .dump name so
# restore.sh's in-container `sha256sum -c` resolves the published artifact. This
# guarantees a dump is never published without its matching .sha256.
docker compose -f "$COMPOSE_FILE" run --rm --no-deps -T \
  -e BACKUP_FILE="$backup_name" db-tools \
  sh -eu -c 'cd /backups && hash="$(sha256sum "${BACKUP_FILE}.tmp" | cut -d " " -f 1)" && printf "%s  %s\n" "$hash" "$BACKUP_FILE"' >"$tmp_checksum"

# Publish atomically once both the dump and its checksum exist: checksum first,
# then the dump, so a visible .dump always has its .sha256 sidecar.
mv -- "$tmp_checksum" "$checksum"
mv -- "$tmp_dump" "$artifact"

success_epoch="$(date -u +%s)"
{
  echo '# HELP disaster_recovery_backup_last_success_timestamp_seconds Unix timestamp of the last successful backup.'
  echo '# TYPE disaster_recovery_backup_last_success_timestamp_seconds gauge'
  echo "disaster_recovery_backup_last_success_timestamp_seconds ${success_epoch}"
} >"$tmp_metric"
mv -- "$tmp_metric" "$metric"

echo "Backup complete: $artifact"
echo "Checksum:       $checksum"
echo "Next step: ./scripts/restore.sh backups/$backup_name"
