#!/usr/bin/env bash
set -euo pipefail

# Mirrors publish.yml version logic: release version comes from tag name `vX.Y.Z`.
# For non-tag runs, fallback to latest release tag, then version.properties.
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
OUTPUT_FILE="$ROOT_DIR/content/wizard/data/version.json"
VERSION_FILE="$ROOT_DIR/version.properties"

extract_from_tag() {
  local tag="$1"
  if [[ "$tag" =~ ^v[0-9]+\.[0-9]+\.[0-9]+([.-][0-9A-Za-z.-]+)?$ ]]; then
    echo "${tag#v}"
    return 0
  fi
  return 1
}

version=""

if [[ -n "${GITHUB_REF_NAME:-}" ]]; then
  if version_from_ref="$(extract_from_tag "$GITHUB_REF_NAME")"; then
    version="$version_from_ref"
  fi
fi

if [[ -z "$version" ]]; then
  latest_tag="$(git -C "$ROOT_DIR" tag --list 'v*.*.*' --sort=-v:refname | head -n 1 || true)"
  if [[ -n "$latest_tag" ]]; then
    if version_from_tag="$(extract_from_tag "$latest_tag")"; then
      version="$version_from_tag"
    fi
  fi
fi

if [[ -z "$version" ]] && [[ -f "$VERSION_FILE" ]]; then
  version_from_properties="$(awk -F'=' '/^libraryVersionName=/{print $2}' "$VERSION_FILE" | tr -d '[:space:]')"
  if [[ -n "$version_from_properties" ]]; then
    version="$version_from_properties"
  fi
fi

if [[ -z "$version" ]]; then
  version="1.0.0"
fi

mkdir -p "$(dirname "$OUTPUT_FILE")"
cat > "$OUTPUT_FILE" <<JSON
{
  "kickVersion": "$version"
}
JSON

echo "Generated $OUTPUT_FILE"
cat "$OUTPUT_FILE"
