---
type: article
title: V1 Baseline and Invariants
description: Canonical implemented behavior and fragile assumptions that V2 must preserve or change explicitly.
resource: ../../raw/contexto-v2/docs/v1/V1_CANONICAL_MODEL.md
tags: [baseline, regression, idempiere]
timestamp: 2026-09-21T18:54:40Z
aliases: [v1-baseline-and-invariants]
topic: v1
status: draft
sources: ["[Canonical Model](../../raw/contexto-v2/docs/v1/V1_CANONICAL_MODEL.md)", "[V1 Invariants](../../raw/contexto-v2/docs/v1/V1_INVARIANTS.md)", "[Business Rules](../../raw/contexto-v2/docs/v1/BUSINESS_RULES.md)"]
score: 10.0
---

# V1 Baseline and Invariants

> Sources: V1 audit corpus, 2026-09-21
> Raw: [Canonical Model](../../raw/contexto-v2/docs/v1/V1_CANONICAL_MODEL.md); [V1 Invariants](../../raw/contexto-v2/docs/v1/V1_INVARIANTS.md); [Business Rules](../../raw/contexto-v2/docs/v1/BUSINESS_RULES.md)

## Overview

V1 is a radar and manual coordinator for predispatch candidates, not a transport optimizer. Its audit is considered ready and should not be repeated without a specific contradiction.

## Implemented boundary

The backend filters and enriches completed sales orders, evaluates inventory and local policies, supports human selection, revalidates candidates, and persists temporary `DRAFT` proposals with TTL in H2. The browser offers map/table views and a partial vehicle suggestion. The native plugin performs role-aware, read-only queries.

V1 does not implement physical load units, nesting, automatic vehicle assignment, routing, cost optimization, inventory reservation, or creation of ERP predispatch documents.

## Fragile invariants

Particular care is required around the non-exhaustive candidate window, global warehouse/locator configuration, Kit K simplification, aggregation without UOM conversion, text-only vehicle identity, and JVM-local claims. These are implemented assumptions, not universal business truths.

## Regression boundary

V2 must retain existing filters, warnings, revalidation, human control, and temporary proposal behavior until a documented decision replaces them. Historical patterns are operational evidence, not automatic rules.

## Related

- [Project Overview](../project/Project%20Overview.md)
- [V2 Requirements and Gap](../v2/V2%20Requirements%20and%20Gap.md)
- [Technical Architecture](../architecture/Technical%20Architecture.md)
