---
type: proposal
title: Primer ciclo SDD para corregir el nesting
description: Decisión de alcance entre saneamiento del plugin existente y motor físico V2.
tags: [sdd, proposal, nesting]
timestamp: 2026-09-21T21:48:01Z
topic: sdd
slug: secure-nesting-first-cycle
status: scope-selected
score: 1.0
---

# Propuesta — Primer ciclo de corrección del nesting

> Hereda la [constitución ratificada](../constitution.md). El propietario eligió la opción A para este primer ciclo; la propuesta no equivale a aprobar la especificación ni autoriza código.

## Problem

El plugin calcula volumen reducido para mezclas que quizá no comparten vehículo, habilita reglas TCL sin confirmación, transforma volumen desconocido en cero y asigna vehículos sin comprobar volumen. Un camino de reparto puede producir cantidades negativas; el alta de la nueva tabla no es reproducible todavía.

## FRAME

- **Actor:** el encargado de despacho que necesita decidir qué cargas y vehículos son defendibles.
- **Alternativa actual:** revisar manualmente la composición física y la capacidad; o usar el cálculo actual sin garantías suficientes.
- **Progreso deseado:** un resultado trazable que no declare cabida ni ahorro físico cuando faltan reglas o mediciones.
- **Restricciones:** conservar el plugin Java/OSGi iDempiere 12; respetar la evidencia TLL/ORI y las mediciones pendientes; no ejecutar SQL/2Pack sin entorno autorizado.
- **Conocido / supuesto:** los cinco defectos y el bloqueo de instalación constan en la auditoría estática; no consta aún una validación compilada ni una necesidad aprobada de resolver el packing V2 en este mismo ciclo.

## Intent

Cerrar primero el riesgo de resultados físicos engañosos y establecer evidencia automatizada de conservación y capacidad, sin convertir una huella teórica en una promesa de carga vehicular.

## Scope / Non-scope

- **Opción A — saneamiento del plugin (recomendada):** corregir los cinco bloqueos detectados, permitir solo reglas físicas confirmadas, hacer explícitos datos/alcances desconocidos, y preparar instalación y pruebas reproducibles. No prometer cabida geométrica ni optimizar rutas.
- **Opción B — motor V2 completo en este ciclo:** añadir unidades físicas trazables y packing con dimensiones, posición, colisiones, altura, descarga y estados de factibilidad. Depende de mediciones y decisiones empresariales aún abiertas.
- **Opción C — no construir ahora:** mantener el nesting sin despliegue ni uso operativo; continuar solo con revisión manual hasta cerrar datos y entorno. Evita cambio inmediato pero no corrige el código existente.

## Research input

- [Constitución ratificada](../constitution.md), principios 2–6.
- [Modelo de nesting](../../logistics/Physical%20Nesting%20Model.md): solo TLL tiene regla de nesting confirmada; huella, unidad física y posición vehicular no son equivalentes.
- [Plan físico V2](../../v2/V2%20Physical%20Implementation%20Plan.md): estado `DRAFT_READY_FOR_MEASUREMENT_GATE`; código de producción no autorizado para el motor V2.
- Auditoría estática del commit `8004620e10c6457c997162f7bb07a58adc7bb9b9`: cinco bloqueos funcionales, instalación pendiente y cero pruebas de producto.

## Alternatives considered and tradeoffs

| Dirección | Beneficio cercano | Dependencia o coste principal |
|---|---|---|
| A · Saneamiento | Reduce el riesgo operativo y deja una base comprobable. | No entrega un optimizador ni una prueba geométrica de cabida. |
| B · Motor completo | Se acerca al objetivo V2 total. | La medición y las reglas pendientes pueden volver indeterminado el diseño y bloquear la implementación. |
| C · No construir | Cero riesgo de introducir cambios ahora. | Mantiene el plugin actual inutilizable como evidencia confiable de capacidad. |

## Risks

El alcance A puede requerir declarar más escenarios indeterminados de los que hoy el plugin acepta. Eso es un cambio visible para despacho y necesita aceptación explícita; no debe ocultarse como un simple bug fix.

## Rollback

No habrá aplicación de SQL/2Pack ni despliegue en este paso. En implementación futura, toda migración deberá tener validación y reversión en instancia desechable antes de un despliegue autorizado.

## Success criteria

Una especificación aprobada delimita qué salidas son confiables, indeterminadas o no soportadas; cada bloqueo auditado tiene criterio de aceptación y prueba planificada; la instalación de iDempiere 12 es reproducible o se declara bloqueada.

## Recommendation

Opción A confirmada por el propietario para el primer ciclo. Confianza media: los defectos son visibles en código, pero aún falta acordar el comportamiento exacto ante datos físicos desconocidos en la especificación y su clarificación. Un motor geométrico V2 requerirá otro ciclo y sus mediciones/reglas pendientes.
