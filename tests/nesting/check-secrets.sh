#!/usr/bin/env bash
set -euo pipefail

script_dir=$(CDPATH= cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
repo_root=$(CDPATH= cd -- "$script_dir/../.." && pwd)

high_confidence_pattern='-----BEGIN ([A-Z0-9]+ )?PRIVATE KEY-----|AKIA[0-9A-Z]{16}|gh[pousr]_[A-Za-z0-9]{20,}|sk-(proj-)?[A-Za-z0-9_-]{20,}'
credential_pattern="(password|passwd|api[_-]?key|client[_-]?secret|access[_-]?token)[[:space:]]*[:=][[:space:]]*[\"'][^\"']{12,}[\"']"

contains_secret() {
	local file=$1
	LC_ALL=C grep -Eiq -- "$high_confidence_pattern" "$file" \
		|| LC_ALL=C grep -Eiq -- "$credential_pattern" "$file"
}

self_test() {
	local fixture
	fixture=$(mktemp)
	trap 'rm -f -- "$fixture"' RETURN

	printf '%s%s\n' 'password=' '"0123456789abcdef"' > "$fixture"
	if ! contains_secret "$fixture"; then
		printf 'check-secrets self-test failed: known fixture was not detected\n' >&2
		return 1
	fi

	printf '%s\n' 'password=${TEST_PASSWORD}' > "$fixture"
	if contains_secret "$fixture"; then
		printf 'check-secrets self-test failed: environment reference was rejected\n' >&2
		return 1
	fi
}

if [[ ${1-} == "--self-test" ]]; then
	self_test
	printf 'check-secrets self-test: pass and fail paths verified\n'
	exit 0
fi

cd "$repo_root"
found=0
while IFS= read -r -d '' file; do
	case "$file" in
		02-DOCS/raw/contexto-v2/*|*/target/*|*/node_modules/*|*/build/*|*/dist/*)
			continue
			;;
	esac

	[[ -f $file ]] || continue
	case "$(basename -- "$file")" in
		.env.example)
			;;
		.env|.env.*|*.pem|*.key|credentials.json|service-account.json)
			printf 'possible secret-bearing file: %s\n' "$file" >&2
			found=1
			continue
			;;
	esac

	LC_ALL=C grep -Iq . "$file" || continue
	if contains_secret "$file"; then
		printf 'possible secret content: %s\n' "$file" >&2
		found=1
	fi
done < <(
	{
		git diff --name-only --diff-filter=ACMR -z
		git diff --cached --name-only --diff-filter=ACMR -z
		git ls-files --others --exclude-standard -z
	} | sort -zu
)

if (( found != 0 )); then
	printf 'secret scan failed; values were intentionally not printed\n' >&2
	exit 1
fi

printf 'secret scan passed for changed and new active-workspace files\n'
