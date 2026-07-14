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

# Turn a path into an absolute, tidy form without requiring it to exist.
normalize_dir() {
  local p="$1"
  [[ "$p" = /* ]] || p="$PWD/$p"
  # Collapse "/./", drop trailing "/.", squeeze slashes, drop a trailing slash.
  printf '%s' "$p" | sed -e 's|/\./|/|g' -e 's|/\.$|/|' -e 's|//*|/|g' -e 's|\(.\)/$|\1|'
}

# Guard --force from removing important locations (the repo, home, protected dirs).
is_dangerous_output_dir() {
  local abs root cwd home d
  abs="$(normalize_dir "$1")"
  root="$(normalize_dir "$ROOT")"
  cwd="$(normalize_dir "$PWD")"
  home="$(normalize_dir "${HOME:-/nonexistent-home}")"
  case "$abs" in "" | "/") return 0 ;; esac
  [[ "$abs" == "$root" || "$abs" == "$cwd" || "$abs" == "$home" ]] && return 0
  for d in scripts template templates examples helm gitops src; do
    [[ "$abs" == "$root/$d" ]] && return 0
  done
  return 1
}

usage() {
  cat >&2 <<'EOF'
Usage:
  new-service.sh --name NAME --owner TEAM --image REPO --out DIR [--port PORT] [--force]

Options:
  --name   Service name (DNS-safe: lowercase letters, digits, hyphens; starts with a letter, ends with a letter/digit, max 50 chars)
  --owner  Owning team (lowercase letters, digits, hyphens; starts/ends safely, max 63 chars)
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

# Name: Kubernetes/DNS-safe, short enough to leave room for -dev/-prod suffixes.
# Must start with a lowercase letter and end with a lowercase letter or digit.
if (( ${#SERVICE_NAME} > 50 )) || [[ ! "$SERVICE_NAME" =~ ^[a-z]([a-z0-9-]*[a-z0-9])?$ ]]; then
  die "--name '$SERVICE_NAME' is invalid: use lowercase letters, digits, and hyphens; start with a letter; end with a letter or digit; max 50 chars."
fi

# Owner: lowercase letters, digits, hyphens; must start with a lowercase letter and end with a letter or digit.
if (( ${#SERVICE_OWNER} > 63 )) || [[ ! "$SERVICE_OWNER" =~ ^[a-z]([a-z0-9-]*[a-z0-9])?$ ]]; then
  die "--owner '$SERVICE_OWNER' is invalid: use lowercase letters, digits, and hyphens; start with a letter; end with a letter or digit; max 63 chars."
fi

# Port: numeric and in range 1..65535.
if [[ ! "$SERVICE_PORT" =~ ^[0-9]+$ ]] || (( SERVICE_PORT < 1 || SERVICE_PORT > 65535 )); then
  die "--port '$SERVICE_PORT' is invalid: use a number between 1 and 65535."
fi

# Image: repository only (no tag). Keep validation simple and safe for template substitution.
# A colon is allowed before the last slash (registry port like localhost:5000), but
# a colon in the final path component means a tag (e.g. payments-api:latest), which
# the Helm chart controls instead.
if [[ "$SERVICE_IMAGE" =~ [[:space:]] ]] || [[ "$SERVICE_IMAGE" =~ [^a-z0-9._:/-] ]]; then
  die "--image '$SERVICE_IMAGE' is invalid: use lowercase repository characters only (letters, digits, '.', '_', '-', '/', optional registry port)."
fi
image_last="${SERVICE_IMAGE##*/}"
if [[ -z "$image_last" || "$SERVICE_IMAGE" == */ || "$image_last" == .* || "$image_last" == -* || "$image_last" == _* ]]; then
  die "--image '$SERVICE_IMAGE' is invalid: provide a non-empty repository name."
fi
if [[ "$image_last" == *:* ]]; then
  die "--image '$SERVICE_IMAGE' must not include a tag; provide the repository only (the Helm chart sets the tag)."
fi

[[ -d "$TEMPLATE_DIR" ]] || die "template directory not found at $TEMPLATE_DIR"

# Refuse to scaffold into (and potentially --force-delete) a dangerous location.
if is_dangerous_output_dir "$OUT_DIR"; then
  die "Refusing to remove dangerous output directory: $OUT_DIR"
fi

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
# Escape replacement values so special sed characters such as '&' cannot reinsert
# the matched placeholder. Input validation rejects unsafe values, but escaping is
# kept as a defensive guard.
sed_replacement_escape() {
  printf '%s' "$1" | sed -e 's/[\&|]/\\&/g'
}

substitute() {
  local file="$1"
  local esc_name esc_owner esc_port esc_image
  esc_name="$(sed_replacement_escape "$SERVICE_NAME")"
  esc_owner="$(sed_replacement_escape "$SERVICE_OWNER")"
  esc_port="$(sed_replacement_escape "$SERVICE_PORT")"
  esc_image="$(sed_replacement_escape "$SERVICE_IMAGE")"

  sed \
    -e "s|__SERVICE_NAME__|$esc_name|g" \
    -e "s|__SERVICE_OWNER__|$esc_owner|g" \
    -e "s|__SERVICE_PORT__|$esc_port|g" \
    -e "s|__SERVICE_IMAGE__|$esc_image|g" \
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
