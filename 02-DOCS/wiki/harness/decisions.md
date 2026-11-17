---
type: decision
title: Harness Decisions
description: Append-only record of significant workspace-control decisions.
timestamp: 2026-09-21T18:54:40Z
topic: harness
status: stable
sources: []
score: 1.0
---

# Harness decisions

- Accepted plan `7ae12ee1e3efa03488c0a78b30c9905c2a4ea75660b278766e2ce8f4df5cbad9`.
- Project kind: mixed.
- SDD: selected.

- Accepted plan `41c76be72590c172aa2ad64c28f19d3a59e3efc632be0b12e00cba8dc4c953c0`.
- Project kind: mixed.
- SDD: selected.

## 2026-09-21 — Previous-work import strategy

- Requirements: preserve complete V1/V2 context, keep the future plugin tree clean, copy no secrets, and leave no reference or link to the previous directory.
- Option 1: isolated complete snapshot under `02-DOCS/raw/` — selected.
- Option 2: reduced documentation-only snapshot — rejected because it would omit replay evidence and code context.
- Option 3: merge the previous application into the repository root — rejected because it would conflict with the target plugin.
- Why: isolation preserves evidence without confusing historical prototypes with active product code.

## 2026-09-21 — Harness scaffold

- Requirements: organize the recovered context, preserve existing rsc instructions, avoid speculative providers, and perform no destructive operation.
- Option 1: apply the updated non-destructive harness audit — selected by literal consent `yes, proceed`.
- Option 2: adjust the audit — not selected.
- Option 3: leave the scaffold incomplete — not selected.
- Why: the knowledge layer can be completed independently of fetching or modifying product code.

## 2026-09-29 — RSC 2.0.15

- Requirements: update only this repository's declared harness, preserve Codex and Cursor, retain local work and the machine's `context7` exclusion, and create no commit.
- Decision: the user authorized upgrading from 2.0.5 to the exact published version 2.0.15 after reviewing package differences and expected impact.
- Why: the release fixes repeated memory compaction notices seen here, worktree memory isolation and linked-skill detection. The 33 installed skill bodies and four agent definitions did not change between the published packages.
- Evidence: `02-DOCS/wiki/ftd/rsc-upgrade-2-0-15.md`; sync and both doctor checks passed, with two recoverable RSC backups.
