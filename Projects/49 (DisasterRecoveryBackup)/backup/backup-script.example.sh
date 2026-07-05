#!/usr/bin/env bash
# =============================================================================
# backup-script.example.sh — logical PostgreSQL backup (EXAMPLE / NOT EXECUTED)
# =============================================================================
# Takes a consistent pg_dump, compresses it, records a checksum, prunes old
# backups, and writes a success marker for monitoring. THIS SCRIPT WAS NEVER RUN.
# It is written to be safe: it reads credentials only from the environment (never
# hardcoded), and it REFUSES to run against a placeholder/unset host.
#
# Credentials come from env (as a real CronJob would inject from a Secret):
#   PGHOST PGPORT PGUSER PGDATABASE  and  PGPASSWORD (or ~/.pgpass)
# Config:
#   BACKUP_DIR (default ./backups)   RETENTION_DAYS (default 7)
#
# Example (NOT executed):
#   PGHOST=db.internal PGUSER=app PGDATABASE=app ./backup-script.example.sh
# =============================================================================
set -euo pipefail

BACKUP_DIR="${BACKUP_DIR:-./backups}"
RETENTION_DAYS="${RETENTION_DAYS:-7}"
PGHOST="${PGHOST:-}"
PGPORT="${PGPORT:-5432}"
PGUSER="${PGUSER:-}"
PGDATABASE="${PGDATABASE:-}"

# --- safety guards -----------------------------------------------------------
if [[ -z "$PGHOST" || -z "$PGUSER" || -z "$PGDATABASE" ]]; then
  echo "ERROR: PGHOST, PGUSER, and PGDATABASE must be set (from a Secret)." >&2
  exit 1
fi
if [[ "$PGHOST" == *"example.invalid"* || "$PGHOST" == *"placeholder"* ]]; then
  echo "ERROR: PGHOST is a placeholder. This example refuses to run." >&2
  exit 1
fi

timestamp="$(date -u +%Y%m%dT%H%M%SZ)"
artifact="${BACKUP_DIR}/${PGDATABASE}-${timestamp}.dump"

mkdir -p "$BACKUP_DIR"

echo "[$(date -u)] starting backup of ${PGDATABASE} on ${PGHOST}"

# --- 1. consistent dump in custom format (-Fc) -------------------------------
# Custom format supports selective, parallel restore and is compressed.
# --no-owner/--no-privileges make the dump portable to a fresh restore target.
pg_dump \
  --host="$PGHOST" --port="$PGPORT" --username="$PGUSER" \
  --format=custom --no-owner --no-privileges \
  --file="$artifact" \
  "$PGDATABASE"

# --- 2. checksum for integrity verification ----------------------------------
sha256sum "$artifact" > "${artifact}.sha256"

# --- 3. retention: delete backups older than RETENTION_DAYS ------------------
find "$BACKUP_DIR" -name "${PGDATABASE}-*.dump*" -type f \
  -mtime +"$RETENTION_DAYS" -print -delete

# --- 4. success marker (scraped by monitoring/backup-alerts) -----------------
date -u +%s > "${BACKUP_DIR}/last_success_epoch"

echo "[$(date -u)] backup complete: ${artifact}"
echo "Verify separately with restore-script.example.sh into a DISPOSABLE target."
