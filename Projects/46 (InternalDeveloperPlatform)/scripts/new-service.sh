#!/usr/bin/env bash
# =============================================================================
# new-service.sh — golden-path service generator
# =============================================================================
# Scaffolds a new, self-contained Java service by copying the golden-path
# template (../template) and replacing __TOKEN__ placeholders with the
# developer's inputs.
#
# Example:
#   ./scripts/new-service.sh \
#     --name payments-api \
#     --owner payments-team \
#     --port 8080 \
#     --image registry.example.invalid/payments-api \
#     --out examples/new-service \
#     --force
#
# The generated folder contains Java source, a Dockerfile, a Helm chart with
# templates, Argo CD Applications, catalog metadata, and environment values.
# =============================================================================
set -euo pipefail

SERVICE_NAME=""
SERVICE_OWNER=""
SERVICE_PORT="8080"
SERVICE_IMAGE=""
OUT_DIR=""
FORCE="false"

# Resolve the project root (this script lives in <root>/scripts).
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
TEMPLATE_DIR="$ROOT/template"

log()  { echo "[new-service] $*"; }
die()  { echo "ERROR: $*" >&2; exit 1; }

usage() {
  cat >&2 <<'EOF'
Usage:
  new-service.sh --name NAME --owner TEAM --image REPO --out DIR [--port PORT] [--force]

Options:
  --name   Service name (DNS-safe: lowercase letters, digits, hyphens; starts with a letter)
  --owner  Owning team (lowercase letters, digits, hyphens)
  --port   Container port (1-65535). Default: 8080
  --image  Container image repository, no tag (e.g. registry.example.invalid/payments-api)
  --out    Output directory to create
  --force  Overwrite the output directory if it already exists
  -h, --help  Show this help
EOF
  exit 2
}

# --- Parse arguments ---------------------------------------------------------
while [[ $# -gt 0 ]]; do
  case "$1" in
    --name)  [[ $# -ge 2 ]] || die "--name needs a value";  SERVICE_NAME="$2";  shift 2 ;;
    --owner) [[ $# -ge 2 ]] || die "--owner needs a value"; SERVICE_OWNER="$2"; shift 2 ;;
    --port)  [[ $# -ge 2 ]] || die "--port needs a value";  SERVICE_PORT="$2";  shift 2 ;;
    --image) [[ $# -ge 2 ]] || die "--image needs a value"; SERVICE_IMAGE="$2"; shift 2 ;;
    --out)   [[ $# -ge 2 ]] || die "--out needs a value";   OUT_DIR="$2";       shift 2 ;;
    --force) FORCE="true"; shift ;;
    -h|--help) usage ;;
    *) echo "Unknown argument: $1" >&2; usage ;;
  esac
done

# --- Validate required inputs ------------------------------------------------
[[ -n "$SERVICE_NAME"  ]] || die "--name is required"
[[ -n "$SERVICE_OWNER" ]] || die "--owner is required"
[[ -n "$SERVICE_IMAGE" ]] || die "--image is required"
[[ -n "$OUT_DIR"       ]] || die "--out is required"

# Name: lowercase letters, digits, hyphens; must start with a lowercase letter.
if [[ ! "$SERVICE_NAME" =~ ^[a-z][a-z0-9-]*$ ]]; then
  die "--name '$SERVICE_NAME' is invalid: use lowercase letters, digits, and hyphens, starting with a letter."
fi

# Owner: lowercase letters, digits, hyphens; must start with a lowercase letter.
if [[ ! "$SERVICE_OWNER" =~ ^[a-z][a-z0-9-]*$ ]]; then
  die "--owner '$SERVICE_OWNER' is invalid: use lowercase letters, digits, and hyphens, starting with a letter."
fi

# Port: numeric and in range 1..65535.
if [[ ! "$SERVICE_PORT" =~ ^[0-9]+$ ]] || (( SERVICE_PORT < 1 || SERVICE_PORT > 65535 )); then
  die "--port '$SERVICE_PORT' is invalid: use a number between 1 and 65535."
fi

# Image: must be non-empty (validated above) and must not include a tag.
if [[ "$SERVICE_IMAGE" == *:* ]]; then
  die "--image '$SERVICE_IMAGE' must not include a tag; provide the repository only."
fi

[[ -d "$TEMPLATE_DIR" ]] || die "template directory not found at $TEMPLATE_DIR"

# --- Prepare the output directory --------------------------------------------
if [[ -e "$OUT_DIR" ]]; then
  if [[ "$FORCE" == "true" ]]; then
    log "output '$OUT_DIR' exists; removing it (--force)."
    rm -rf "$OUT_DIR"
  else
    die "output dir '$OUT_DIR' already exists; pass --force to overwrite."
  fi
fi

# --- Copy the template -------------------------------------------------------
mkdir -p "$OUT_DIR"
# Copy the template contents (including dotfiles like .helmignore).
cp -R "$TEMPLATE_DIR/." "$OUT_DIR/"

# --- Substitute placeholders -------------------------------------------------
# Portable approach: sed to a temp file, then move back (no in-place -i needed).
# Helm templates use {{ }} and contain no __TOKEN__, so they are untouched.
substitute() {
  local file="$1"
  sed \
    -e "s|__SERVICE_NAME__|$SERVICE_NAME|g" \
    -e "s|__SERVICE_OWNER__|$SERVICE_OWNER|g" \
    -e "s|__SERVICE_PORT__|$SERVICE_PORT|g" \
    -e "s|__SERVICE_IMAGE__|$SERVICE_IMAGE|g" \
    "$file" > "$file.tmp"
  mv "$file.tmp" "$file"
}

while IFS= read -r -d '' file; do
  substitute "$file"
done < <(find "$OUT_DIR" -type f -print0)

# --- Summary -----------------------------------------------------------------
echo
echo "Generated service: $SERVICE_NAME"
echo "Owner: $SERVICE_OWNER"
echo "Port: $SERVICE_PORT"
echo "Image: $SERVICE_IMAGE"
echo "Output: $OUT_DIR"
