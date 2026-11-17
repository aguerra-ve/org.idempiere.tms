---
type: article
title: Technical Architecture
description: Proposed system boundaries, authority split, and validation requirements for Predispatch V2.
resource: ../../raw/contexto-v2/docs/07-arquitectura-tecnica.md
tags: [architecture, security, idempiere]
timestamp: 2026-09-21T18:54:40Z
aliases: [technical-architecture]
topic: architecture
status: draft
sources: ["[Architecture Proposal](../../raw/contexto-v2/docs/07-arquitectura-tecnica.md)"]
score: 10.0
---

# Technical Architecture

> Sources: architecture proposal, 2026-09-21
> Raw: [Architecture Proposal](../../raw/contexto-v2/docs/07-arquitectura-tecnica.md)

## Overview

The proposed target is a modular Java 17 backend with asynchronous work, a Vue/MapLibre interface, auxiliary PostgreSQL for non-binding computation, and a minimal iDempiere plugin for atomic ERP operations. This remains a proposal, not the active repository layout.

## Authority split

iDempiere remains authoritative for orders, inventory, reservations, and binding documents. The backend may read, normalize, simulate, and compare alternatives; the browser receives no ERP credentials. Acceptance must revalidate and reserve atomically inside the ERP boundary.

## Plugin boundary

The proposed OSGi plugin owns only binding entities and idempotent accept/release processes. It must not contain routing, maps, AI, or duplicated masters, and it must not bypass validated iDempiere document services.

## Validation gates

The exact deployed build and customizations, final eligibility and nesting policies, native OR-Tools packaging, REST pagination, authentication, observability, and rollback behavior require verification before implementation.

## Related

- [Project Overview](../project/Project%20Overview.md)
- [V2 Physical Implementation Plan](../v2/V2%20Physical%20Implementation%20Plan.md)
- [MapView Plugin Reference](../plugins/MapView%20Plugin%20Reference.md)
