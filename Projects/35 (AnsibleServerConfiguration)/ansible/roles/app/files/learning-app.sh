#!/usr/bin/env sh
set -eu

# This is a harmless stand-in for a real application artifact.
CONFIG_FILE="${APP_CONFIG_FILE:-/etc/learning-app/application.conf}"
. "${CONFIG_FILE}"

case "${HEARTBEAT_SECONDS}" in
  ''|*[!0-9]*)
    echo "HEARTBEAT_SECONDS must be a positive whole number." >&2
    exit 1
    ;;
esac

if [ "${HEARTBEAT_SECONDS}" -lt 1 ]; then
  echo "HEARTBEAT_SECONDS must be greater than zero." >&2
  exit 1
fi

while true; do
  echo "${APP_NAME} is running in ${APP_ENVIRONMENT}."
  sleep "${HEARTBEAT_SECONDS}"
done
