#!/usr/bin/env bash
# =============================================================================
# new-service.sh — golden-path service generator (REFERENCE / NOT RUN)
# =============================================================================
# Scaffolds a new service by copying the templates and replacing __TOKEN__
# placeholders with the developer's inputs. This script was NEVER executed in
# this repo. It is written to be readable and safe: it only writes into a fresh
# output directory and refuses to overwrite anything.
#
# Example (NOT executed):
#   ./new-service.sh \
#     --name payments-api \
#     --owner payments-team \
#     --port 8080 \
#     --image registry.example.invalid/payments-api \
#     --out ../examples/new-service
#
# The result of exactly that invocation is committed under examples/new-service/
# so you can see the output without running anything.
# =============================================================================
set -euo pipefail

SERVICE_NAME=""
SERVICE_OWNER=""
SERVICE_PORT="8080"
IMAGE_REPO=""
OUT_DIR=""

usage() {
  echo "Usage: $0 --name NAME --owner TEAM [--port PORT] --image REPO --out DIR"
  exit 2
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --name)  SERVICE_NAME="$2"; shift 2 ;;
    --owner) SERVICE_OWNER="$2"; shift 2 ;;
    --port)  SERVICE_PORT="$2"; shift 2 ;;
    --image) IMAGE_REPO="$2"; shift 2 ;;
    --out)   OUT_DIR="$2"; shift 2 ;;
    *) usage ;;
  esac
done

[[ -z "$SERVICE_NAME" || -z "$SERVICE_OWNER" || -z "$IMAGE_REPO" || -z "$OUT_DIR" ]] && usage

# 1) Validate the name so generated Kubernetes/DNS/Java identifiers are legal.
if [[ ! "$SERVICE_NAME" =~ ^[a-z][a-z0-9-]{1,38}[a-z0-9]$ ]]; then
  echo "ERROR: --name must be DNS-safe (lowercase, digits, hyphens)." >&2
  exit 1
fi

# 2) Never clobber an existing directory.
if [[ -e "$OUT_DIR" ]]; then
  echo "ERROR: output dir '$OUT_DIR' already exists; refusing to overwrite." >&2
  exit 1
fi

ROOT="$(cd "$(dirname "$0")/.." && pwd)"

# 3) Copy the template pieces into the new service layout.
mkdir -p "$OUT_DIR"
cp -R "$ROOT/service-template/app"        "$OUT_DIR/app"
cp    "$ROOT/service-template/service.yaml" "$OUT_DIR/service.yaml"
cp -R "$ROOT/helm-template"               "$OUT_DIR/helm"
cp -R "$ROOT/gitops-template/environments" "$OUT_DIR/gitops"

# 4) Substitute placeholders in the generated (non-Helm) files only. Helm
#    templates keep their own {{ }} syntax and are configured via values.
substitute() {
  local file="$1"
  sed -i \
    -e "s|__SERVICE_NAME__|$SERVICE_NAME|g" \
    -e "s|__SERVICE_OWNER__|$SERVICE_OWNER|g" \
    -e "s|__SERVICE_PORT__|$SERVICE_PORT|g" \
    -e "s|__IMAGE_REPO__|$IMAGE_REPO|g" \
    "$file"
}
export -f substitute
find "$OUT_DIR" -type f \
  \( -name "*.yaml" -o -name "Dockerfile" \) \
  -exec bash -c 'substitute "$0"' {} \;

# 5) Point the Helm image at the chosen repo (values, not template edits).
sed -i "s|repository: .*|repository: $IMAGE_REPO|" "$OUT_DIR/helm/values.yaml"

echo "Generated service '$SERVICE_NAME' at $OUT_DIR"
echo "Next: open a PR with this folder; GitOps takes over from there."
