---
type: article
title: MapView Plugin Reference
description: Capabilities and limits of the recovered iDempiere 12 MapView and native predispatch plugin.
resource: ../../raw/contexto-v2/org.idempiere.tms.mapview/README.md
tags: [plugin, osgi, idempiere]
timestamp: 2026-09-21T18:54:40Z
aliases: [mapview-plugin-reference]
topic: plugins
status: draft
sources: ["[MapView README](../../raw/contexto-v2/org.idempiere.tms.mapview/README.md)", "[TMS Analysis](../../raw/contexto-v2/docs/04-analisis-org-idempiere-tms.md)"]
score: 9.0
---

# MapView Plugin Reference

> Sources: recovered plugin and TMS analysis, 2026-09-21
> Raw: [MapView README](../../raw/contexto-v2/org.idempiere.tms.mapview/README.md); [TMS Analysis](../../raw/contexto-v2/docs/04-analisis-org-idempiere-tms.md)

## Overview

`org.idempiere.tms.mapview` is an iDempiere 12/Java 17 reference plugin for freight routes and a native predispatch view. It is not the target `org.idempiere.tms` branch and must not be merged blindly.

## Implemented behavior

The plugin registers forms through OSGi, improves route-location handling, and provides a role-aware native view of completed sales orders with pending delivery. It supports territorial filters, aging indicators, `DeliveryRule` interpretation, multi-selection, and opening a limited set of destinations in Google Maps.

## Safety boundary

The recovered cut is read-only. It does not reserve inventory, create deliveries, persist proposals, implement nesting, or perform transactional acceptance. Those capabilities were intentionally deferred to separately governed native services.

## Reuse guidance

Reuse concepts and verified queries only after comparing them with the target branch. Preserve OSGi registration, role access, 2Pack patterns, and explicit fallbacks, but treat local build paths and historical builder workarounds as environment-specific evidence.

## Related

- [Project Overview](../project/Project%20Overview.md)
- [Technical Architecture](../architecture/Technical%20Architecture.md)
- [Physical Nesting Model](../logistics/Physical%20Nesting%20Model.md)
