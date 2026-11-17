---
type: analysis
title: Análisis de consistencia — Saneamiento seguro del nesting
description: Segunda puerta SDD previa a implementación para constitución, especificación, plan y tareas.
tags: [sdd, analysis, nesting]
timestamp: 2026-09-21T22:57:04Z
topic: sdd
slug: safe-product-nesting
status: passed
score: 1.0
---

# Análisis de consistencia — Saneamiento seguro del nesting

**GATE: PASS** — 0 CRITICAL, 0 HIGH, 0 MEDIUM, 0 LOW. Segunda lectura cruzada de la [constitución](../constitution.md), la [especificación aprobada](../specs/safe-product-nesting.md) y el [plan con tareas revisadas](../plans/safe-product-nesting.md). Este pase verifica coherencia documental, **no** compilación, pruebas Java, instalación, autorización de implementación ni despliegue.

## Cobertura de requisitos

| Requisito | Comportamiento abreviado | Plan | Tareas | Estado |
| --- | --- | --- | --- | --- |
| AC-NEST-001 | TLL confirmado, identidad única | §2–5 | T001, T005–T008, T015–T016, T021 | covered |
| AC-NEST-002 | rutas, fechas y vehículos aislados | §2–5, LoadScope explícito | T005–T008, T013–T016, T021 | covered |
| AC-NEST-003 | entregas distintas sin nesting compartido | §1–5 | T001, T005–T006, T021 | covered |
| AC-NEST-004 | familia sin regla TLL sin ahorro | §2–5, UnnestedGroup | T002, T007–T010, T019, T021 | covered |
| AC-NEST-005 | marginal requerido faltante es desconocido | §2–5 | T002, T009–T010, T021 | covered |
| AC-NEST-006 | desconocido bloquea asignación automática | §2–5 | T001, T006, T009–T010, T013–T014, T017–T018, T021 | covered |
| AC-NEST-007 | peso y volumen limitan candidato | §2–5 | T013–T016, T021 | covered |
| AC-NEST-008 | partición conserva cantidad y peso | §2–5 | T011–T012, T015–T016, T021 | covered |
| AC-NEST-009 | resultado independiente del orden | §2, §4–5 | T007–T008, T011–T012, T021 | covered |
| AC-NEST-010 | familias mixtas conservan identidad | §2, §4–5 | T002, T007–T010, T021 | covered |
| AC-NEST-011 | AD/2Pack instala y revierte | §1, §4–6 | T019–T021 | covered |
| AC-NEST-012 | falta de entorno deja estado bloqueado | §0–1, §5–7 | T003–T004, T020, T022 | covered |
| AC-NEST-013 | ninguna salida recibe volumen supuesto | §2–5, guarda de entrada | T009–T010, T017–T018, T021 | covered |
| AC-NEST-014 | sin excepción manual dentro del plugin | §0, §5 | T017–T018, T021 | covered |

## Hallazgos de esta ejecución

Ninguno. Los cinco hallazgos de la primera lectura se resolvieron en plan/tareas con aprobación del propietario, registrada en [decisiones](../decisions.md): F1, ruta independiente sin ahorro y solo con exteriores confirmados; F2, atomicidad limitada a OTRLine/asignación nueva y guarda de volumen en órdenes/costos; F3, línea base raw y revisión de secretos/archivos nuevos en T004/T022; F4, `LoadScope.candidateVehicleId` en contratos y portadores por tarea; F5, export AD/PackOut en staging, manifiesto de IDs y script de preflight creado antes de importación. La especificación original no se amplió.

## Seis controles

1. **Constitución:** no hay excepción a reglas físicas, autoría, raw o secretos; T004/T022 incluyen comprobaciones observables. El entorno de ejecución sigue condicionado por el principio 6.
2. **Cobertura hacia delante:** 14/14 criterios tienen enfoque en §5 y tareas con done-checks.
3. **Deriva hacia atrás:** cada tarea se vincula con criterios o barreras constitucionales; órdenes/costos solo reciben la protección exigida por AC-NEST-013.
4. **Contradicciones:** `UnnestedGroup` no declara ahorro TLL y un marginal TLL requerido sin medir sigue `UNKNOWN`; ambos respetan AC-NEST-004/005/006.
5. **Duplicación:** las pruebas puras y las de flujo repiten límites a distinta escala deliberadamente; no hay tareas duplicadas con contratos divergentes.
6. **Ambigüedad/portadores:** §0 conserva restricciones globales y los contratos `Consumes/Produces` por tarea incluyen candidato, estados de volumen, escrituras y artefactos AD/2Pack.

## Condiciones para la fase siguiente

El gate documental pasa, pero T001 debe demostrar linaje de entrega y T002 evidencia física; T003/T004 requieren padre iDempiere 12 y runner real; T019/T020 requieren staging e instancia desechable autorizados. Si faltan, AC-NEST-012 exige estado `blocked`, no `verified`. Antes de implementar se necesita decisión de aislamiento en rama y autorización explícita de código; el pase no la sustituye. Ningún skill externo fue instalado durante este análisis.
