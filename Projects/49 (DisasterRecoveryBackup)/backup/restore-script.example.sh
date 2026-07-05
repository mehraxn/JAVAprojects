#!/usr/bin/env bash
# =============================================================================
# restore-script.example.sh — restore a pg_dump into a DISPOSABLE target
# (EXAMPLE / NOT EXECUTED)
# =============================================================================
# Verifies the backup checksum, restores it into a target database, and runs a
# validation query. THIS SCRIPT WAS NEVER RUN. Restoring is destructive, so this
# example has extra guards: it requires --confirm AND refuses to touch a host
# that looks like production. A backup is only proven good once it restores AND
# validates here — never assume a dump is restorable.
#
# Credentials from env (target DB): PGHOST PGPORT PGUSER PGDATABASE PGPASSWORD
# Usage (NOT executed):
#   ./restore-script.example.sh --file backups/app-<ts>.dump --confirm
# =============================================================================
set -euo pipefail

ARTIFACT=""
CONFIRM="false"
while [[ $# -gt 0 ]]; do
  case "$1" in
    --file) ARTIFACT="$2"; shift 2 ;;
    --confirm) CONFIRM="true"; shift ;;
    *) echo "Usage: $0 --file <dump> --confirm" >&2; exit 2 ;;
  esac
done

PGHOST="${PGHOST:-}"
PGPORT="${PGPORT:-5432}"
PGUSER="${PGUSER:-}"
PGDATABASE="${PGDATABASE:-}"

# --- safety guards -----------------------------------------------------------
[[ -z "$ARTIFACT" ]] && { echo "ERROR: --file is required." >&2; exit 1; }
[[ "$CONFIRM" != "true" ]] && {
  echo "ERROR: restore is destructive; re-run with --confirm." >&2; exit 1; }
if [[ -z "$PGHOST" || -z "$PGUSER" || -z "$PGDATABASE" ]]; then
  echo "ERROR: target PGHOST, PGUSER, PGDATABASE must be set." >&2; exit 1
fi
# Refuse anything that looks like production — restore ONLY into disposable DR.
if [[ "$PGHOST" == *"prod"* ]]; then
  echo "ERROR: PGHOST looks like production. Restore into a disposable target." >&2
  exit 1
fi

# --- 1. verify integrity BEFORE restoring ------------------------------------
if [[ -f "${ARTIFACT}.sha256" ]]; then
  echo "verifying checksum..."
  sha256sum --check "${ARTIFACT}.sha256"
else
  echo "WARNING: no .sha256 sidecar found; cannot verify integrity." >&2
fi

echo "restoring ${ARTIFACT} into ${PGDATABASE} on ${PGHOST} (disposable target)"

# --- 2. restore (custom format) ----------------------------------------------
# --clean --if-exists drops objects first so the restore is repeatable.
pg_restore \
  --host="$PGHOST" --port="$PGPORT" --username="$PGUSER" \
  --dbname="$PGDATABASE" \
  --clean --if-exists --no-owner --no-privileges \
  "$ARTIFACT"

# --- 3. validation query (prove data is actually there) ----------------------
echo "running post-restore validation..."
psql --host="$PGHOST" --port="$PGPORT" --username="$PGUSER" --dbname="$PGDATABASE" \
  --tuples-only --command="SELECT 'row_count=' || count(*) FROM app_data;" \
  || { echo "ERROR: validation query failed." >&2; exit 1; }

echo "restore + validation complete. Record elapsed time (RTO) and the backup"
echo "timestamp used (data-loss window = RPO)."
