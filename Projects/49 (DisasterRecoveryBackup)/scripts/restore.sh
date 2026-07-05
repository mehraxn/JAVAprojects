#!/usr/bin/env bash
set -Eeuo pipefail

readonly PROJECT_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd -P)"
readonly COMPOSE_FILE="${PROJECT_ROOT}/docker-compose.yml"
readonly BACKUP_DIR="${PROJECT_ROOT}/backups"
readonly RESTORE_SERVICE="restore-postgres"
readonly RESTORE_DATABASE="app_restore"

fail() {
  echo "RESTORE FAILED: $*" >&2
  exit 1
}

usage() {
  echo "Usage: $0 backups/<timestamped-backup>.dump" >&2
}

[[ $# -eq 1 ]] || { usage; exit 2; }
command -v docker >/dev/null 2>&1 || fail "Docker is required."
docker compose version >/dev/null 2>&1 || fail "Docker Compose v2 is required."

# This workflow intentionally supports only the fixed, disposable local target.
[[ "${DR_TARGET_ENV:-local}" == "local" ]] || \
  fail "DR_TARGET_ENV must be 'local'; production-like restores are refused."
[[ -z "${PGHOST:-}" && -z "${DATABASE_URL:-}" ]] || \
  fail "External database variables are set. Unset PGHOST and DATABASE_URL."
[[ "$RESTORE_SERVICE" == "restore-postgres" && "$RESTORE_DATABASE" == *_restore ]] || \
  fail "The restore target did not pass the disposable-target guard."

backup_input="$1"
[[ -f "$backup_input" ]] || fail "Backup not found: $backup_input"
[[ -f "${backup_input}.sha256" ]] || fail "Checksum file not found: ${backup_input}.sha256"

backup_directory="$(cd -- "$(dirname -- "$backup_input")" && pwd -P)"
expected_directory="$(cd -- "$BACKUP_DIR" && pwd -P)"
[[ "$backup_directory" == "$expected_directory" ]] || \
  fail "The selected backup must be inside ${BACKUP_DIR}."

backup_name="$(basename -- "$backup_input")"
[[ "$backup_name" =~ ^app-[0-9]{8}T[0-9]{6}Z\.dump$ ]] || \
  fail "Unexpected backup filename: $backup_name"

if ! docker compose -f "$COMPOSE_FILE" exec -T "$RESTORE_SERVICE" \
  pg_isready --username=restore_user --dbname=postgres >/dev/null 2>&1; then
  fail "The local restore database is not ready. Run: docker compose up -d"
fi

echo "Verifying SHA-256 checksum before restore..."
docker compose -f "$COMPOSE_FILE" exec -T \
  -e BACKUP_FILE="$backup_name" "$RESTORE_SERVICE" \
  sh -eu -c 'cd /backups && sha256sum -c "${BACKUP_FILE}.sha256"'

echo "Recreating disposable database ${RESTORE_DATABASE}..."
docker compose -f "$COMPOSE_FILE" exec -T \
  -e RESTORE_DATABASE="$RESTORE_DATABASE" \
  -e BACKUP_FILE="$backup_name" "$RESTORE_SERVICE" sh -eu -c '
    dropdb --host=127.0.0.1 --username="$POSTGRES_USER" \
      --if-exists --force "$RESTORE_DATABASE"
    createdb --host=127.0.0.1 --username="$POSTGRES_USER" "$RESTORE_DATABASE"
    pg_restore --host=127.0.0.1 --username="$POSTGRES_USER" \
      --dbname="$RESTORE_DATABASE" --exit-on-error \
      --no-owner --no-privileges "/backups/$BACKUP_FILE"
  '

row_count="$(docker compose -f "$COMPOSE_FILE" exec -T \
  -e RESTORE_DATABASE="$RESTORE_DATABASE" "$RESTORE_SERVICE" \
  sh -eu -c 'psql --host=127.0.0.1 --username="$POSTGRES_USER" \
    --dbname="$RESTORE_DATABASE" --no-align --tuples-only \
    --set=ON_ERROR_STOP=1 --command="SELECT count(*) FROM app_data;"' \
  | tr -d '[:space:]')"

[[ "$row_count" =~ ^[0-9]+$ ]] || fail "app_data validation returned an invalid count."
(( row_count > 0 )) || fail "app_data exists but contains no rows."

echo "RESTORE SUCCEEDED: checksum valid and app_data contains ${row_count} row(s)."
echo "Target: local Compose service ${RESTORE_SERVICE}, database ${RESTORE_DATABASE}."
