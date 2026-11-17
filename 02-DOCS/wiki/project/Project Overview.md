---
type: article
title: Project Overview
description: Current boundary between the future TMS plugin and recovered V1/V2 reference material.
resource: ../../raw/contexto-v2/docs/v2/V2_KICKOFF_CONTEXT.md
tags: [idempiere, tms, predispatch]
timestamp: 2026-09-21T18:54:40Z
aliases: [project-overview]
topic: project
status: draft
sources: ["[V2 Kickoff Context](../../raw/contexto-v2/docs/v2/V2_KICKOFF_CONTEXT.md)", "[Previous Project Instructions](../../raw/contexto-v2/AGENTS.md)"]
score: 13.0
---

# Project Overview

> Sources: recovered V1/V2 workspace, 2026-09-21
> Raw: [V2 Kickoff Context](../../raw/contexto-v2/docs/v2/V2_KICKOFF_CONTEXT.md); [Previous Project Instructions](../../raw/contexto-v2/AGENTS.md)

## Overview

This workspace is intended to continue `org.idempiere.tms` from `feature/dd-product-nesting`. Product code is not present yet. The recovered snapshot is evidence and design context from a related predispatch initiative; it must not be mistaken for the active plugin tree.

## Recovered system

V1 combines an external Spring Boot/Vue application with a separate native iDempiere plugin. Both read ERP data but do not share code or persistence. The external application creates temporary local proposals; it does not create operational `IVS_PreShipment` documents.

## Direction of travel

V2 is an evolution rather than a rewrite. Existing behavior must be explicitly classified as preserved, modified, extended, replaced, removed, or new. Physical nesting, vehicle feasibility, route planning, cost, and transactional ERP acceptance remain separate capabilities with distinct evidence gates.

## Current boundary

- Active plugin source: pending retrieval.
- Recovered context: immutable under `raw/contexto-v2/`.
- Curated guidance: maintained in this wiki.
- SDD constitution: not created until the workflow is explicitly entered.

## Related

- [V1 Baseline and Invariants](../v1/V1%20Baseline%20and%20Invariants.md)
- [V2 Requirements and Gap](../v2/V2%20Requirements%20and%20Gap.md)
- [Physical Nesting Model](../logistics/Physical%20Nesting%20Model.md)
- [MapView Plugin Reference](../plugins/MapView%20Plugin%20Reference.md)
