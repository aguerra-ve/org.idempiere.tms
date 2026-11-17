---
type: article
title: Workspace Control Plane
description: Root instructions, repository boundaries, and the relationship between raw context and active code.
resource: ../../raw/meta/claude-md.md
tags: [harness, workflow, repository]
timestamp: 2026-09-21T18:54:40Z
aliases: [workspace-control-plane]
topic: meta
status: stable
sources: ["[Claude Instructions](../../raw/meta/claude-md.md)", "[Agent Instructions](../../raw/meta/agents-md.md)"]
score: 5.0
---

# Workspace Control Plane

> Sources: root workspace instructions, 2026-09-21
> Raw: [Claude Instructions](../../raw/meta/claude-md.md); [Agent Instructions](../../raw/meta/agents-md.md)

## Overview

The root is a Git repository for the future active TMS plugin. rsc supplies the workflow and knowledge layer; the recovered V1/V2 tree is immutable evidence under `raw/`.

## Working boundary

Check Git state before editing, preserve unrelated work, do not commit or configure remotes without authorization, keep secrets local, and never treat generated artifacts as source. Durable knowledge belongs in the wiki rather than in raw evidence.

## Related

- [Operational Tooling](../operations/Operational%20Tooling.md)
- [Project Overview](../project/Project%20Overview.md)
