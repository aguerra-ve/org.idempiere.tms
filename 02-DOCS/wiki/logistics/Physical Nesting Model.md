---
type: article
title: Physical Nesting Model
description: Confirmed TLL and ORI grouping rules, proposed data model, invariants, and remaining validation gates.
resource: ../../raw/contexto-v2/docs/05-modelo-nesting.md
tags: [nesting, stacking, physical-loading]
timestamp: 2026-09-21T18:54:40Z
aliases: [physical-nesting-model]
topic: logistics
status: draft
sources: ["[Nesting Model](../../raw/contexto-v2/docs/05-modelo-nesting.md)", "[Nesting Evidence](../../raw/contexto-v2/docs/v2/replay/PHYSICAL_NESTING_METHODOLOGY.md)", "[Loading Algorithm](../../raw/contexto-v2/docs/v2/replay/PHYSICAL_LOADING_ALGORITHM_DESIGN.md)"]
score: 12.0
---

# Physical Nesting Model

> Sources: nesting design and replay evidence, 2026-09-21
> Raw: [Nesting Model](../../raw/contexto-v2/docs/05-modelo-nesting.md); [Nesting Evidence](../../raw/contexto-v2/docs/v2/replay/PHYSICAL_NESTING_METHODOLOGY.md); [Loading Algorithm](../../raw/contexto-v2/docs/v2/replay/PHYSICAL_LOADING_ALGORITHM_DESIGN.md)

## Overview

The model separates structural nesting, ORI stacking, theoretical footprint, physical handling units, vehicle packing, and unloading precedence. These concepts must not be collapsed into a single volume calculation.

## Confirmed rules

TLL compatibility is transitive along the confirmed sequence `370→500→550→700→900→1060`, supports multilevel chains, and unloads inner before outer. Independent parallel interiors remain undecided. ORI uses homogeneous maximum-stack rules and permits partial unloading without a rigid precedence edge merely because units share a stack.

## Model boundary

`DD_NestingFamily`, `DD_ProductNesting`, nullable nested volume, and persistence of load units are proposals rather than implemented schema. Missing family, dimensions, orientation, handling, or safety evidence must remain explicit.

## Acceptance gates

The engine requires approved family/rank data, traceable load units, passing invariants, separation of theoretical and operational footprint, versioned configuration, supported-family declarations, and cycle detection in the unload graph. The historical `44 → 17` composition remains pending validation.

## Related

- [Continuidad del replay físico](./Physical%20Replay%20Continuity.md)
- [V2 Physical Implementation Plan](../v2/V2%20Physical%20Implementation%20Plan.md)
- [Technical Architecture](../architecture/Technical%20Architecture.md)
- [MapView Plugin Reference](../plugins/MapView%20Plugin%20Reference.md)

## Evidencia fotográfica incorporada — 13/10/2026

[Compilación y conciliación ERP](../ftd/dispatch-photo-evidence-20261013.md): el operador reafirma el orden TLL existente. Las fotos muestran cuerpos anidados y tapas interiores; no aportan dimensiones ni autorizan pares nuevos por deformación. Medidas útiles fijas por unidad/placa, nunca por modelo. La guía 1520 excluye 14 pupitres presentes en movimientos ERP; las etapas salida/recepción de las guías 1517 y 1520 no son cargas adicionales. El operador aclaró que las fotos con placa 04R DBD son referencias generales de carga, no evidencia específica del JAC declarado.
