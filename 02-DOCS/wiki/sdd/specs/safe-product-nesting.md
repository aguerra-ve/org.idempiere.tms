---
type: spec
title: Spec — Saneamiento seguro del nesting del plugin TMS
description: Comportamiento verificable del primer ciclo para nesting, capacidad e indeterminación física.
tags: [sdd, spec, nesting, vehicle-capacity]
timestamp: 2026-09-21T21:50:38Z
topic: sdd
slug: safe-product-nesting
status: approved
approval: clarified-text-approved-2026-09-21
score: 1.0
---

# Spec — Saneamiento seguro del nesting del plugin TMS

> Estado: especificación clarificada y aprobada por el propietario · Creada: 2026-09-21.
> Hereda la [constitución v1.0.0](../constitution.md) y el alcance A confirmado en la [propuesta](../proposals/secure-nesting-first-cycle.md).

## Problem & why

El encargado de despacho necesita distinguir entre una carga que cumple límites comprobables, una que no los cumple y una que no puede evaluarse con los datos disponibles. Hoy el plugin puede reducir volumen al mezclar pedidos que no compartirán carga, aplicar nesting sin regla confirmada, presentar como cero un volumen desconocido y asignar vehículos sin comprobar la capacidad volumétrica. También puede generar cantidades incoherentes al repartir una carga limitada por volumen. Esas salidas no son una base defendible para la operación.

## Cost of not building it

Seguiría siendo posible asignar una carga a un vehículo sin comprobar su volumen, subestimar espacio requerido o producir líneas con cantidades negativas. La operación tendría que detectar y corregir manualmente estos casos, y no podría confiar en el resultado del plugin como evidencia de capacidad. No existe una medición de frecuencia o coste económico de estos fallos; no se presume una cifra.

## The cheapest alternative

Suspender el nesting automático y revisar manualmente todas las cargas evita usar el ahorro de volumen no demostrado sin construir este cambio. Sin embargo, conserva el fallo de asignación por volumen y el reparto incoherente, aumenta la revisión manual y no proporciona una salida trazable que distinga datos desconocidos de cargas realmente incompatibles.

## Goals

- El plugin solo aplica una reducción por nesting cuando la compatibilidad y el alcance operativo de la mezcla están confirmados.
- El resultado conserva identidad de pedido y entrega, unidades comerciales, cantidades y peso; nunca produce cantidades negativas.
- Un dato físico requerido y desconocido queda explícito como indeterminado, no como cero ni como capacidad comprobada.
- La asignación automática comprueba los límites disponibles de peso y volumen y no presenta huella teórica como prueba de cabida geométrica.
- El comportamiento puede verificarse de forma repetible en iDempiere 12 antes de considerarlo listo para despliegue.

## Non-goals / out of scope

- Construir el motor V2 de colocación geométrica, posiciones, colisiones, altura o elección óptima de flota/ruta.
- Declarar factible una carga por la sola reducción de huellas; la cabida geométrica queda fuera de este ciclo.
- Inferir medidas faltantes, compatibilidad TCL, nesting ORI o interiores paralelos a partir de nombres o tamaños.
- Implementar una excepción o autorización manual dentro del plugin para aceptar cargas indeterminadas; la revisión manual queda fuera de este flujo.
- Aplicar nesting entre entregas distintas, aunque exista una secuencia de descarga conocida; eso corresponde a un ciclo posterior.
- Ejecutar migraciones, instalar componentes o desplegar en una instancia operativa durante especificación y planificación.
- Completar/aprobar automáticamente documentos de negocio ni cambiar su política actual en este ciclo; cualquier interacción de ese tipo requerirá una decisión separada.
- Resolver costos de flete, tarifas o selección propia/alquilada, salvo impedir que reciban un volumen físicamente presentado como medido cuando es desconocido.

## Users & context

El encargado de despacho revisa unidades de transporte y vehículos candidatos para pedidos elegibles. Necesita conocer las cantidades comerciales y el peso sin pérdida, qué reducción física está sustentada por una regla confirmada y cuándo una asignación requiere revisión porque falta evidencia. El equipo técnico de iDempiere necesita instalar y comprobar el cambio en un entorno controlado antes de cualquier uso operativo.

## Behaviour

- **Caso principal:** para productos TLL con compatibilidad confirmada y una misma entrega/carga operativamente compatible, el sistema reconoce el anidamiento estructural; conserva todas las unidades y su peso; solo comunica un volumen efectivo si las medidas necesarias están confirmadas. La elección automática de vehículo usa límites de peso y volumen disponibles, sin afirmar cabida geométrica.
- **Límites:** productos de entregas, rutas, fechas de carga o vehículos distintos no comparten reducción en este ciclo, aun si su secuencia de descarga fuese compatible. Productos TCL y otras familias sin regla confirmada no reciben reducción. El orden de entrada no cambia el resultado. Una carga que necesita dividirse conserva cantidades no negativas y la suma original.
- **Datos faltantes y error:** si falta volumen marginal, una dimensión o una regla necesaria, el resultado identifica el faltante y no usa cero supuesto; la asignación que dependa de ese dato no se confirma automáticamente. Una carga que supera un límite confirmado se identifica como no apta para ese vehículo, diferenciándola de una carga indeterminada por falta de datos.
- **Alcance de la salida:** se distingue el número teórico de huellas de la capacidad de un vehículo; el resultado no afirma que exista un acomodo físico completo.

## Acceptance criteria

- **AC-NEST-001:** Dada una mezcla TLL con compatibilidad confirmada dentro de una misma entrega y carga operativa, cuando se evalúa el nesting, entonces solo las combinaciones permitidas reducen la huella teórica y cada unidad comercial sigue identificada exactamente una vez.
- **AC-NEST-002:** Dados productos de rutas, fechas de carga o vehículos distintos, cuando se evalúan juntos en una ejecución, entonces no se aplica entre ellos ninguna reducción compartida.
- **AC-NEST-003:** Dadas entregas diferentes, cuando se evalúan juntas, entonces ninguna unidad de una entrega se anida con una unidad de la otra, incluso si su precedencia de descarga fuese conocida.
- **AC-NEST-004:** Dado un producto TCL u otra familia sin regla de nesting confirmada, cuando se evalúa la carga, entonces no se aplica la regla TLL ni se informa un ahorro físico confirmado para esa familia.
- **AC-NEST-005:** Dada una mezcla con volumen marginal requerido pero no medido, cuando se informa su volumen efectivo, entonces figura como desconocido con el dato faltante identificado y nunca como cero medido.
- **AC-NEST-006:** Dada una carga cuyo volumen efectivo o regla física necesaria es desconocido, cuando se intenta asignar un vehículo automáticamente basándose en ese dato, entonces la asignación no se confirma y la causa queda visible para revisión.
- **AC-NEST-007:** Dada una carga con peso y volumen conocidos, cuando se evalúa un vehículo, entonces superar cualquiera de los dos límites conocidos impide asignarla a ese vehículo; cumplir ambos límites no se presenta como prueba de cabida geométrica.
- **AC-NEST-008:** Dada una carga que debe repartirse porque uno de esos límites se alcanza, cuando se generan sus partes, entonces todas las cantidades y pesos son no negativos y sus sumas coinciden exactamente con los originales.
- **AC-NEST-009:** Dada la misma entrada y reglas confirmadas en distinto orden, cuando se vuelve a evaluar, entonces las identidades, cantidades, pesos y clasificación del resultado son idénticos.
- **AC-NEST-010:** Dada una familia sin compatibilidad confirmada, cuando se combina con familias sí soportadas, entonces las unidades de cada familia conservan identidad, cantidad y peso sin heredar reglas de otra.
- **AC-NEST-011:** Dado un entorno iDempiere 12 desechable y autorizado, cuando se instala el cambio mediante el procedimiento documentado, entonces queda disponible sin identificadores pendientes ni pasos manuales ocultos y puede revertirse mediante el procedimiento documentado.
- **AC-NEST-012:** Dada la ausencia del entorno de compilación, pruebas o instalación requerido, cuando se informa el estado de entrega, entonces figura bloqueado y no como verificado o listo para despliegue.
- **AC-NEST-013:** Dado un volumen efectivo desconocido, cuando se prepara cualquier salida posterior de transporte o costo basada en volumen, entonces no se usa cero ni otro valor supuesto como volumen medido y la causa permanece visible.
- **AC-NEST-014:** Dada una carga indeterminada, cuando se revisa el resultado de este ciclo, entonces no existe dentro del plugin una acción de excepción manual que la convierta en asignación automáticamente aceptada.

## Points to clarify

- **suposición tomada, validada** — Un resultado indeterminado que depende de volumen o compatibilidad desconocidos bloquea la asignación automática y deja revisión manual fuera del plugin. *Base:* principios 2, 3 y 5 de la constitución y la respuesta del propietario de 2026-09-21. *Riesgo:* puede aumentar el trabajo manual; el riesgo fue expuesto antes de la respuesta.
- **decisión diferida** — La autorización manual excepcional dentro del plugin, incluida su evidencia y permisos, se diseñará en otro ciclo si llega a requerirse.
- **decisión diferida** — El nesting entre entregas distintas, incluso con precedencia conocida, se diseñará en otro ciclo.
- **decisión diferida** — Las mediciones de volumen marginal y las reglas de interiores paralelos se cerrarán con negocio antes de usarse; este ciclo no inventa sus valores.
- **decisión diferida** — El motor geométrico V2, ORI y familias no confirmadas tendrán especificaciones propias después de este saneamiento.

## Revisions

- 2026-09-21: primer borrador; alcance A confirmado, texto y criterios aún pendientes de aprobación. El control estructural local encontró todas las secciones y puntos tipados; `npm run spec:gate` no puede ejecutarse porque este plugin no tiene `package.json`.
- 2026-09-21: el propietario respondió «go» a la solicitud explícita de aprobación de este borrador; no se interpretó como respuesta a los puntos abiertos, aprobación del plan ni permiso de implementación.
- 2026-09-21: el propietario confirmó excluir excepciones manuales dentro del plugin y nesting entre entregas distintas; los criterios AC-NEST-001/003 se ajustaron y se añadió AC-NEST-014. El texto revisado requiere aprobación final.
- 2026-09-21: el propietario aprobó expresamente la especificación clarificada junto con el plan técnico para pasar al desglose de tareas, sin autorizar todavía implementación.

## Clarifications

| Fecha | Pregunta o supuesto | Decisión | Motivo y efecto |
|---|---|---|---|
| 2026-09-21 | ¿Excepción manual dentro del plugin para una carga indeterminada? | No en este ciclo. | El propietario confirmó el límite; AC-NEST-006/014 mantienen el bloqueo automático y la revisión externa. |
| 2026-09-21 | ¿Nesting entre entregas distintas con precedencia conocida? | No en este ciclo. | El propietario confirmó el límite; AC-NEST-003 prohíbe la reducción compartida entre entregas. |
| 2026-09-21 | Supuesto: indeterminación bloquea la asignación automática. | Validado para este ciclo. | Se mantiene AC-NEST-006; no se sustituye un dato desconocido por un cero. |
| 2026-09-21 | Medidas marginales, interiores paralelos, motor V2/ORI. | Permanecen diferidos. | No se inventan reglas ni valores pendientes de negocio. |
