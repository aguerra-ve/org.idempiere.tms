---
type: article
title: V2 Requirements and Gap
description: Capability changes, unresolved policy tensions, and data readiness for the V2 evolution.
resource: ../../raw/contexto-v2/docs/v2/GAP_ANALYSIS.md
tags: [requirements, gap-analysis, policy]
timestamp: 2026-09-21T18:54:40Z
aliases: [v2-requirements-and-gap]
topic: v2
status: draft
sources: ["[Raw Requirements](../../raw/contexto-v2/docs/v2/REQUIREMENTS_RAW.md)", "[Gap Analysis](../../raw/contexto-v2/docs/v2/GAP_ANALYSIS.md)", "[Capability Map](../../raw/contexto-v2/docs/v2/V2_CAPABILITY_MAP.md)", "[Data Gaps](../../raw/contexto-v2/docs/v2/V2_DATA_GAPS.md)"]
score: 11.0
---

# V2 Requirements and Gap

> Sources: V2 analysis corpus, 2026-09-21
> Raw: [Raw Requirements](../../raw/contexto-v2/docs/v2/REQUIREMENTS_RAW.md); [Gap Analysis](../../raw/contexto-v2/docs/v2/GAP_ANALYSIS.md); [Capability Map](../../raw/contexto-v2/docs/v2/V2_CAPABILITY_MAP.md); [Data Gaps](../../raw/contexto-v2/docs/v2/V2_DATA_GAPS.md)

## Overview

The formal analysis identifies 37 gaps and concludes `READY_WITH_DECISIONS`. V2 is not a total replacement: candidate filtering, bounded inventory reasoning, geography, human selection, revalidation, claims, and temporary proposals remain useful baselines.

## Main changes

V2 must correct candidate exhaustiveness and aging semantics, extend policy explanation, territorial service, auditing, and decision support, and add real fleet concepts, carriers, cost, physical handling, planning, and controlled AI assistance. The text-only V1 vehicle representation is the only explicitly required functional replacement; no wholesale removal is authorized.

## Replay and evidence

Current data supports structural and geographic replay. Some FIFO, nesting, and physical scenarios become possible after enrichment. Actual vehicle, route, ownership, tariff, realized cost, incident, and human-alternative data require prospective capture.

## Open policy tensions

Service versus cost, regional priority versus FIFO, inventory protection versus immediate dispatch, own versus rented fleet, and physical feasibility versus business priority still require explicit governance.

## Related

- [V1 Baseline and Invariants](../v1/V1%20Baseline%20and%20Invariants.md)
- [V2 Physical Implementation Plan](./V2%20Physical%20Implementation%20Plan.md)
- [Physical Nesting Model](../logistics/Physical%20Nesting%20Model.md)
