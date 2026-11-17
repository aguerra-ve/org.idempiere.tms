---
type: constitution
title: IQP TMS iDempiere — Constitución SDD
description: Principios comprobables ratificados por el propietario para el ciclo SDD.
tags: [sdd, constitution]
timestamp: 2026-09-21T21:48:01Z
topic: sdd
version: v1.0.0
status: ratified
score: 1.0
---

# IQP TMS iDempiere — Constitución SDD

> Versión: v1.0.0 · Ratificada: 2026-09-21 · Última enmienda: 2026-09-21.

## Principios

1. **Conservar el producto existente.** Evolucionar el plugin Java/OSGi de iDempiere 12 en `feature/dd-product-nesting`; no sustituirlo por el prototipo recuperado ni modificar `02-DOCS/raw/contexto-v2/`. Comprobar en cada diff los archivos de producto y el área raw. Referencia: [panorama del proyecto](../project/Project%20Overview.md).
2. **Distinguir regla confirmada de hipótesis.** Aplicar nesting o stacking únicamente a familias, combinaciones y límites respaldados por decisión empresarial; una familia sin reglas confirmadas no hereda la secuencia TLL. Verificar la configuración contra [el modelo físico](../logistics/Physical%20Nesting%20Model.md) y sus fuentes.
3. **No inventar datos físicos.** Un volumen, dimensión o compatibilidad sin medición confirmada produce estado desconocido/indeterminado; no se convierte en cero ni se declara capacidad verificada. Probar casos con datos faltantes contra [criterios AC-LOAD-010 y AC-LOAD-013](../../raw/contexto-v2/docs/v2/ACCEPTANCE_CRITERIA.md).
4. **Conservar identidad, cantidad y peso.** Toda agrupación mantiene identidad de pedido/entrega y cada unidad comercial exactamente una vez; conserva cantidad y peso agregados, con resultado determinista. Verificar con casos de borde y [criterios AC-LOAD-001..004 y AC-LOAD-012](../../raw/contexto-v2/docs/v2/ACCEPTANCE_CRITERIA.md).
5. **Separar estructura de cabida vehicular.** Una reducción de huellas teóricas no demuestra por sí sola que una carga quepa en un vehículo; validar límites confirmados de peso, volumen/envolvente y descarga antes de llamarla factible. Verificar contra [DEC-V2-LOAD-001..004](../../raw/contexto-v2/docs/v2/DECISIONS.md).
6. **Exigir verificación reproducible para cambios del producto.** Antes de declarar listo un cambio Java, SQL o 2Pack, aportar pruebas automatizadas y compilación iDempiere 12; si falta el runtime o no existe prueba, registrarlo como bloqueo, nunca como pase. No aplicar SQL/2Pack en datos operativos sin autorización y reversión verificable. Detalle del entorno: [config SDD](config.yaml).
7. **Mantener secretos fuera de Git.** No incorporar credenciales, tokens ni `.env` reales al repositorio; verificar el diff antes de entregar. Referencia: [control del workspace](../meta/Workspace%20Control%20Plane.md).
8. **Mantener la autoría humana.** Todo commit o PR autorizado llevará autoría del propietario del repositorio, sin `Co-Authored-By` de IA ni pie de generación; el agente no hará commit/push/merge sin autorización explícita. Comprobar durante entrega.
9. **Registrar decisiones sustantivas.** Toda decisión de alcance, reglas físicas o despliegue registra fecha, opciones y motivo en `02-DOCS/wiki/sdd/decisions.md`; la constitución se ratifica y modifica mediante historial explícito.

## Definición de terminado

- [ ] Especificación y plan aprobados; tareas y matriz de aceptación verificadas.
- [ ] Pruebas Java relevantes ejecutadas y compilación iDempiere 12 validada, o entrega declarada bloqueada.
- [ ] Configuración física fundada en evidencia; faltantes explícitos, conservación y límites vehiculares demostrados.
- [ ] Migración AD/2Pack validada en entorno autorizado y desechable antes de cualquier despliegue real.
- [ ] Sin secretos ni modificaciones del contexto raw; decisiones registradas.
- [ ] Autoría, commit, push o merge solo con autorización humana específica.

## Registro de enmiendas

| Fecha | Versión | Estado | Motivo |
|---|---|---|---|
| 2026-09-21 | v1.0.0-draft | Pendiente de ratificación | Propuesta inicial basada en la auditoría y las decisiones V2 recuperadas. |
| 2026-09-21 | v1.0.0 | Ratificada por el propietario | Aprobación explícita del borrador de nueve principios. |
