#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

docker compose \
  -f "$script_dir/compose.yml" \
  -f "$script_dir/compose.local.yml" \
  exec -T db psql -v ON_ERROR_STOP=1 -U postgres -d assurance_db \
  < "$script_dir/reset-cross-linking-demo-data.sql"

docker compose \
  -f "$script_dir/compose.yml" \
  -f "$script_dir/compose.local.yml" \
  exec -T db psql -v ON_ERROR_STOP=1 -U postgres -d assurance_db \
  < "$script_dir/seed-cross-linking-fixtures.sql"

echo "Reset complete: local demo projects are back to seeded Proposal 2 data."
