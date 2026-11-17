---
type: article
title: Continuidad del replay físico
description: Casos piloto, evidencia pendiente y límites para no declarar cabida vehicular prematuramente.
tags: [physical-loading, replay, evidence-gate]
timestamp: 2026-09-21T23:40:00Z
aliases: [physical-replay-continuity]
topic: logistics
status: draft
sources: []
score: 1.0
---

# Continuidad del replay físico

Esta nota conserva el punto de continuación del análisis V2: retomar el replay físico y el armado de cargas, no una plantilla Excel ajena al proyecto. Los vínculos de vehículos y largos provienen de evidencia operacional previamente registrada; **no se midieron de nuevo en esta sesión**. Ningún caso tiene aún una declaración de cabida vehicular. El ciclo SDD aprobado de nesting seguro sigue siendo un trabajo distinto: esta nota no amplía su alcance ni autoriza código productivo V2.

## Casos y límites de evidencia

| Predespacho | Vehículo documentado | Flota | Largo útil documentado | Estado para cabida |
| --- | --- | --- | ---: | --- |
| `1006760` | Kodiak | Externa/alquilada | 6,70 m | Piloto estructural; cabida indeterminada. |
| `1006489` | JAC | Interna/propia | 6,60 m | Segundo piloto, después del caso homogéneo. |
| `1006200` | 1721 | Interna/propia | 8,00 m | Caso mixto; identidad y geometría completas pendientes. |
| `1006438` | Sin unidad física conciliada | Por confirmar | No confirmado | No usar 8 m ni 9 m como medida validada. |

La atribución de 8 m corresponde a `1006200`, no demuestra que el 1721 asociado anteriormente a `1006438` sea la misma unidad. Operaciones debe confirmar placa o identidad física y largo útil de `1006438` antes de usarlo para evaluar cabida.

## Piloto y próxima medición

Para `1006760`, 300 unidades ORI-550 se agrupan en 8 pilas homogéneas. Esto conserva cantidades y define unidades de manipulación; **no demuestra que las pilas quepan en el Kodiak**. Faltan ancho y alto útiles, carga admisible operativa, altura real de las pilas, holguras y amarres, acceso de carga y cantidad de vehículos o viajes. El largo de plataforma por sí solo no define la geometría del vehículo.

Secuencia de continuación: obtener esas mediciones y validar la colocación física de `1006760`; después evaluar `1006489`. Tratar `1006200` y `1006438` solo tras resolver su identidad y geometría. No declarar `FEASIBLE` ni `INFEASIBLE` por un simple cálculo de pilas o por ausencia de un arreglo encontrado.

En la selección analítica de casos significativos se excluyeron los clientes con `C_BPartner_ID=1005593` y `1000330`. Sigue sin decidirse si esa exclusión corresponde solo al benchmark o a una regla general V2; **no trasladarla al comportamiento productivo** sin decisión explícita.

En esta etapa no se optimizan rutas ni se modifica el sistema productivo. Véanse el [modelo de nesting físico](./Physical%20Nesting%20Model.md) y el [plan físico V2](../v2/V2%20Physical%20Implementation%20Plan.md) para los límites y criterios generales.
