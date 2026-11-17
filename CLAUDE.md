# CLAUDE.md — IQP TMS iDempiere

This file is the root instruction source for Claude Code in this workspace. The root is a single Git repository intended for the `org.idempiere.tms` plugin; recovered V1/V2 material is reference context, not active product code.

## What this project is

Continuation of the iDempiere TMS plugin from `feature/dd-product-nesting`, preserving its Java/OSGi, SQL, and documentation conventions. The workspace also contains an isolated snapshot of prior predispatch research and prototypes under `02-DOCS/raw/contexto-v2/`.

## Knowledge map

The full catalog lives in `02-DOCS/wiki/index.md`. Read the relevant article before changing its area, and update the wiki when durable conventions change.

Read first:

| Area | Article |
|------|---------|
| User profile | `02-DOCS/wiki/harness/user-profile.md` |
| SDD constitution | `02-DOCS/wiki/sdd/constitution.md` (only after SDD is explicitly entered) |
| Everything else | `02-DOCS/wiki/index.md` |

## Workspace map

| Path | Role |
|------|------|
| `01-TOOLS/` | Operational tooling for explicitly detected external providers. |
| `02-DOCS/raw/contexto-v2/` | Immutable recovered V1/V2 source context; never treat it as the active plugin tree. |
| `02-DOCS/wiki/` | Curated, cross-linked project knowledge. |
| `.rsc/` | Local rsc harness implementation and memory. |

## Working rules

- Before editing, run `git status --short` and preserve unrelated user changes.
- Do not commit, configure remotes, fetch, or push without explicit authorization.
- Do not modify recovered raw context; distill updates into `02-DOCS/wiki/`.
- Never publish secrets. Real `.env` files, keys, service accounts, and tokens stay local and ignored.
- Ignore generated outputs such as `node_modules/`, `target/`, `build/`, `dist/`, and IDE metadata.
- When active plugin code is added, its own build and test instructions supersede historical prototype commands.

## Main commands

No product build command is active yet. The target plugin has not been fetched into the repository root.

## Operational tooling

Manual external-service operations belong in `01-TOOLS/`. Do not create provider tooling until the active code or an explicit recurring operation requires it.

## Documentation

- `02-DOCS/inbox/` — drop zone for new source material.
- `02-DOCS/raw/` — immutable sources and worklogs.
- `02-DOCS/wiki/index.md` — machine-readable knowledge catalog.
- `02-DOCS/wiki/log.md` — newest-first operation log.
