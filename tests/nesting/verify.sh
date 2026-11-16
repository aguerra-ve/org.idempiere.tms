#!/usr/bin/env bash
set -euo pipefail

script_dir=$(CDPATH= cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
repo_root=$(CDPATH= cd -- "$script_dir/../.." && pwd)

cd "$repo_root"
mvn -q -f "$script_dir/pom.xml" test
sha256sum --quiet -c "$script_dir/raw-baseline.sha256"
"$script_dir/check-secrets.sh" --self-test
"$script_dir/check-secrets.sh"

printf 'nesting verification passed\n'
