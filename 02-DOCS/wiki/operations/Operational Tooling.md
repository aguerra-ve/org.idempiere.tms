---
type: article
title: Operational Tooling
description: Rules for adding provider-specific operator tools without speculative integrations.
resource: ../../raw/operations/01-tools-readme.md
tags: [operations, credentials, tooling]
timestamp: 2026-09-21T18:54:40Z
aliases: [operational-tooling]
topic: operations
status: stable
sources: ["[Tools Catalog](../../raw/operations/01-tools-readme.md)"]
score: 4.0
---

# Operational Tooling

> Sources: workspace tooling catalog, 2026-09-21
> Raw: [Tools Catalog](../../raw/operations/01-tools-readme.md)

## Overview

`01-TOOLS/` is reserved for explicit operator workflows against external providers. No provider tool exists yet because no active integration has been detected.

## Rules

Use one provider per directory, keep real credentials in ignored `.env` files, begin with a smoke test, and require explicit confirmation for destructive commands. Runtime libraries remain in product code.

## Related

- [Workspace Control Plane](../meta/Workspace%20Control%20Plane.md)
- [Project Overview](../project/Project%20Overview.md)
