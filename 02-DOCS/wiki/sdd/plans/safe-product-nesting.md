---
type: plan
title: Plan — Saneamiento seguro del nesting del plugin TMS
description: Arquitectura, contratos, flujo de datos, pruebas y riesgos para el primer ciclo de corrección.
tags: [sdd, plan, nesting, vehicle-capacity]
timestamp: 2026-09-21T22:08:43Z
topic: sdd
slug: safe-product-nesting
status: approved-for-tasks
score: 1.0
---

# Plan — Saneamiento seguro del nesting del plugin TMS

> Especificación: [safe-product-nesting](../specs/safe-product-nesting.md) · [Constitución v1.0.0](../constitution.md) · Estado: aprobado por el propietario para desglosar tareas el 2026-09-21.
> Última actualización: 2026-09-21. La aprobación no autoriza implementación, ejecución de SQL/2Pack ni despliegue.
> Revisión de 2026-09-21 aprobada por el propietario tras el primer `analyze`: ruta conservadora sin ahorro para familias sin regla, transacción limitada a evaluación/asignación, portadores de alcance y verificaciones de integridad.

## 0. Global Constraints

Las siguientes frases son límites literales de la especificación/constitución; toda tarea futura los hereda:

- **Familias:** «Aplicar nesting o stacking únicamente a familias, combinaciones y límites respaldados por decisión empresarial; una familia sin reglas confirmadas no hereda la secuencia TLL.» (constitución, principio 2).
- **Datos faltantes:** «Un volumen, dimensión o compatibilidad sin medición confirmada produce estado desconocido/indeterminado; no se convierte en cero ni se declara capacidad verificada.» (principio 3).
- **Conservación:** «Toda agrupación mantiene identidad de pedido/entrega y cada unidad comercial exactamente una vez; conserva cantidad y peso agregados, con resultado determinista.» (principio 4).
- **Separación física:** «Una reducción de huellas teóricas no demuestra por sí sola que una carga quepa en un vehículo» (principio 5).
- **Alcance:** «productos de entregas, rutas, fechas de carga o vehículos distintos no comparten reducción en este ciclo, aun si su secuencia de descarga fuese compatible.» (spec, §Behaviour).
- **Excepciones:** «Implementar una excepción o autorización manual dentro del plugin para aceptar cargas indeterminadas» está fuera de alcance (spec, §Non-goals).
- **Instalación:** «No aplicar SQL/2Pack en datos operativos sin autorización y reversión verificable.» (principio 6).
- **Entorno:** iDempiere 12, Java 17 local y Maven 3.9.10; el padre `org.idempiere.parent` `12.0.0-SNAPSHOT` aún no está disponible en la ruta relativa del POM; no existe test runner del producto configurado ([config](../config.yaml)).
- **Autoría y datos:** no modificar `02-DOCS/raw/contexto-v2/`, no incorporar secretos, y no hacer commit/push/merge sin autorización explícita (principios 1, 7 y 8).
- **Sin regla TLL confirmada:** AC-NEST-004/010 prohíben el ahorro para esa familia, no su transporte; usar sus volúmenes exteriores solo si están confirmados y mantener `UNKNOWN` si faltan. Una mezcla TLL cuya reducción requiere marginal desconocido sigue `UNKNOWN` (AC-NEST-005/006).

## 1. Context & constraints

- El proceso actual `createFreightUnit` agrupa cantidades de toda la ejecución por `Classification`, antes de saber qué vehículo llevará cada línea. `MDDProductNesting` asigna cero marginal a interiores con `NestedVolume` sin medir. Esto explica AC-NEST-001/002/005/006 y prohíbe conservar ese mapa global como autoridad de capacidad.
- `VehicleAssignment.assignVH()` acumula volumen, pero solo compara peso. El último commit cambia `calculateWeight1()`, método sin llamadas en el producto. El plan debe conectar AC-NEST-007/008 al camino activo, no al método muerto.
- La OTR conserva líneas de origen de varios tipos; no expone un ID único de entrega. AC-NEST-001/003 exige resolver su identidad de entrega de forma comprobable o devolver indeterminado, nunca agrupar por cliente o clasificación como sustituto.
- La tabla nueva solo está descrita por SQL con IDs placeholder; el 2Pack existente no la incluye. El POM requiere un padre iDempiere 12 ausente localmente. AC-NEST-011/012 son puertas reales, no documentación cosmética.
- El objetivo es saneamiento de TLL y capacidad básica. Geometría, rutas óptimas, aprobación documental, ORI y nesting entre entregas no entran en este plan (spec §Non-goals).

## 2. Architecture

```text
[iDempiere: OTR, producto, reglas y vehículo] (EXTERNO)
                 |
                 v
[Adaptador de origen e identidad] (INTERNO)
                 |
                 v
[Evaluador de carga candidata por entrega + ruta + fecha + vehículo] (INTERNO)
       |                 |                         |
       v                 v                         v
[Política TLL]   [Medidas y evidencia]    [Particionador conservativo]
       \                 |                         /
        \________________v_______________________/
                         |
                         v
            [Resultado tipado y diagnóstico]
                 |                  |
                 v                  v
     [Persistencia de OTRLine]  [Asignación de vehículo]
                 \                  /
                  v                v
             [Órdenes/costos protegidos]
                         |
                         v
                  [iDempiere] (EXTERNO)
```

- **Adaptador de origen e identidad** — convierte cada OTR en entradas con procedencia de pedido/línea, entrega, producto, fecha, ruta y tenant; si no puede resolver entrega o unidades, falla cerrado.
- **Política TLL** — admite solo la cadena TLL confirmada; igual rango, TCL, ORI y otras familias sin regla confirmada siguen como unidades independientes sin ahorro. Las mezclas entre entregas nunca comparten grupo.
- **Medidas y evidencia** — distingue peso, volumen exterior y volumen marginal conocidos/confirmados de faltantes; conserva unidades de medida y procedencia, no usa el cero como señal de desconocido.
- **Evaluador de carga candidata** — recibe explícitamente `LoadScope` con vehículo candidato; produce grupos TLL o independientes y volumen `KNOWN` o `UNKNOWN`, sin contaminar otro candidato; es determinista e independiente de la base de datos.
- **Particionador conservativo** — divide cantidades solo cuando ambas magnitudes aplicables son conocidas; conserva identidad, cantidad y peso sin partes negativas ni redondeo silencioso. Recalcula el volumen efectivo de cada parte, porque separar una cadena puede eliminar un ahorro por nesting.
- **Resultado tipado y diagnóstico** — diferencia `EVALUABLE`, `INDETERMINATE_MISSING_DATA`, `CAPACITY_EXCEEDED` y errores de procedencia; `NO_NESTING_RULE` describe un grupo independiente, no bloquea por sí mismo una carga con volumen exterior confirmado.
- **Adaptadores de persistencia/asignación/salida** — escriben únicamente resultados `EVALUABLE`; comparan ambos límites en el camino real y rechazan `UNKNOWN` antes de la entrada a órdenes/costos existentes, sin rediseñar sus transacciones.
- **iDempiere** — proporciona modelos, transacción, Application Dictionary, paquete instalable y entorno de pruebas; no se simula para la prueba de instalación.

**Decisión principal:** evaluar una carga por entrega y vehículo candidato antes de persistir su volumen o asignación. Mantener un mapa global por clasificación sería menor cambio de código, pero no puede probar identidad ni impedir nesting entre vehículos; ampliar a packing V2 resolvería más física, pero depende de datos no aprobados y excede la spec. El evaluador acotado es el mínimo diseño que satisface AC-NEST-001/002/003/005/007 sin inventar medidas.

## 3. Interfaces & contracts

```text
SourceAdapter.resolve(OTRRef) -> CommercialLine | MissingDeliveryIdentity | InvalidQuantity
  CommercialLine: tenant, origin document/line, deliveryKey, routeKey, loadDate,
                  product/classification, quantity, unitWeight, measurement refs.
  Postcondition: every input line has one stable origin identity; no cross-tenant lookup.

LoadScope: tenant, deliveryKey, routeKey, loadDate, candidateVehicleId.

NestingPolicy.evaluate(scope: LoadScope, group: CommercialLine[], ruleSnapshot)
  -> StructuralGroup | UnnestedGroup | InvalidComposition
  Precondition: every line matches scope tenant/deliveryKey/routeKey/loadDate;
                candidateVehicleId is explicit and non-null.
  Invariant: flatten(groups) equals input commercial units; no duplicate unit identity;
             one rank per TLL chain, no unconfirmed parallel interiors;
             UnnestedGroup has no volume reduction and retains scope.

MeasurementResolver.resolve(LoadGroup, evidenceSnapshot)
  -> KnownVolume(value, UOM, source) | UnknownVolume(missingFields[])
  LoadGroup = StructuralGroup | UnnestedGroup.
  Invariant: UnnestedGroup uses sum of confirmed exterior volumes; a StructuralGroup
             requiring an unmeasured marginal is UNKNOWN; unknown never coerces to zero.

CapacityEvaluator.check(scope: LoadScope, group: LoadGroup, vehicleSnapshot)
  -> WithinKnownLimits | CapacityExceeded(reason) | Indeterminate(missingFields[])
  Checks: scope.candidateVehicleId equals vehicleSnapshot.id and group.scope;
          total weight and effective volume use compatible units; passing both
          is not a geometric-fit assertion.

Splitter.partition(scope: LoadScope, group: LoadGroup, transportUnitSnapshot)
  -> Parts[] | Indeterminate | InvalidCapacity
  Invariant: all parts nonnegative; original identity/quantity and total weight
             conserved; effective volume re-evaluated for every part, never
             assumed equal to the unsplit result; no empty extra part;
             zero/invalid limit is an error.

AssignmentWriter.apply(evaluation, transaction)
  -> Written | Rejected(reason)
  Precondition: EVALUABLE and WithinKnownLimits for the exact candidate vehicle.
  Postcondition: no partial OTRLine/vehicle assignment writes introduced by this
                 evaluation on failure; existing order/cost transactions unchanged.

TransportVolumeGuard.requireKnown(evaluation) -> KnownVolume | Rejected(missingFields[])
  Postcondition: existing order/cost entry points receive no fictitious measured
                 volume; their calculation and transaction semantics are unchanged.
```

`calculateWeight1()` has no caller; it must not be the only place corrected. During implementation, either remove it as dead logic or make it delegate to the same tested capacity contract, with tests proving the active process uses that contract. That choice is local refactoring, not a change in observable scope.

## 4. Data model & flow

**Entities at model level**

- **CommercialLine/Unit** — stable origin ID, delivery ID, product, quantity or unit ordinal, tenant/org, route/date, weight. Tank quantities must be valid integral units for unit-level grouping; other quantities retain their ERP semantics or are rejected when unit identity cannot be proven.
- **LoadScope** — same tenant, delivery, route, load date, and `candidateVehicleId`; equality is explicit, not inferred from a shared classification. The policy and capacity evaluator receive this scope as an argument, not as implicit caller state.
- **RuleSnapshot** — TLL family/rank/compatibility plus version/source of business evidence; inactive or non-confirmed rows are not eligible.
- **Measurement** — numeric value, unit, evidence state and provenance; unknown is a distinct state. Current `M_Product.Volume` and `NestedVolume` are inputs only when their evidence is confirmed.
- **Evaluation** — structural or independent grouping, commercial totals, theoretical footprint, effective volume state, diagnostics, candidate vehicle, status. `NO_NESTING_RULE` is a diagnostic on an independent group; it remains evaluable only with confirmed exterior volume. No `UNKNOWN` value is flattened into numeric `DD_OTRLine.Volume`.

**Primary flow: one TLL delivery**

1. Select OTRs and resolve source-document lineage to one delivery; unresolved identity yields a diagnostic and no nesting for those lines.
2. Partition by tenant, delivery, route and date; evaluate each vehicle candidate separately, so no precomputed reduction leaks across vehicles.
3. Load only confirmed TLL rules and validated measures. Materialize unit identities and deterministic TLL chains; put families without an applicable confirmed rule in independent groups, without inherited savings. Reject duplicates or unconfirmed parallel interiors.
4. Compute theoretical footprint independently from effective volume. Independent groups sum confirmed exterior volumes; if one exterior is unknown, their total is `UNKNOWN`. A TLL structural group that requires an unmeasured marginal remains `UNKNOWN` and stops automatic assignment; it does not silently switch to a numeric saving.
5. If both measures are known, partition conservatively as needed, recompute nesting and effective volume for each resulting part, then compare each part against the candidate's weight and volume limits.
6. Persist only the new OTR lines and vehicle assignment of an accepted evaluation as one atomic unit. At existing order/cost entry points, reject `UNKNOWN` as a measured volume without changing their calculation or transaction policy; otherwise surface a stable diagnostic.

**Consistency and migration boundary:** new OTRLine/assignment writes from one evaluation share an iDempiere transaction; a failed or indeterminate evaluation leaves none of those new writes. Existing order/cost transactions and completed records are not rewritten by this cycle; only a boundary guard prevents them from receiving fictitious measured volume. The new nesting definition must be installed through an iDempiere-12-compatible, versioned AD/2Pack migration with allocated IDs and rollback; raw placeholder SQL is design input only. T019 obtains IDs from the documented iDempiere AD/PackOut export in an authorized staging instance and records the package and ID evidence; T020 imports into a separate clean disposable instance to check uniqueness, installation and rollback. Seeds include confirmed TLL rules, not TCL/TCD nesting rules; measured volumes stay nullable until real evidence exists. Neither instance may be operational.

## 5. Testing strategy

First establish a runnable Java test seam and actual iDempiere 12 parent/build runtime. No product tests exist now, so `testing.strict_tdd: false` reflects the *current* tool state, not permission to skip tests: each changed behavior receives a failing test before the fix, then a passing test and edge-case triangulation. If build/install dependencies remain unavailable, implementation/verification cannot be marked complete.

| Criterio | Nivel | Aserción principal | Sustitutos en prueba rápida |
|---|---|---|---|
| AC-NEST-001 | unidad + contrato | TLL confirmado forma cadenas válidas; cada ID aparece una vez | Reglas y líneas en memoria |
| AC-NEST-002 | unidad | ruta/fecha/candidato diferentes nunca comparten reducción | Líneas en memoria |
| AC-NEST-003 | unidad | entregas distintas nunca comparten nesting | Líneas en memoria |
| AC-NEST-004 | unidad + integración | TCL/ORI/sin regla no heredan TLL; exterior confirmado permite evaluar sin ahorro y exterior faltante sigue UNKNOWN | Reglas falsas en unidad; AD real en integración |
| AC-NEST-005 | unidad | marginal ausente → `UNKNOWN`, no cero | Medidas en memoria |
| AC-NEST-006 | contrato + integración | `UNKNOWN` no llega al escritor de asignación | Escritor falso en contrato; transacción real en integración |
| AC-NEST-007 | unidad + integración | exceder peso **o** volumen rechaza; pasar ambos no afirma geometría | Vehículo en memoria; proceso real en integración |
| AC-NEST-008 | unidad | partición por peso/volumen conserva identidad, cantidad y peso; recalcula volumen por parte; no negativos, cero/límites exactos | Sin base de datos |
| AC-NEST-009 | unidad de propiedades | permutar entrada conserva identidad y resultado normalizado | Generador determinista local |
| AC-NEST-010 | unidad | familias mixtas no se contaminan; totales conservados | Reglas y líneas en memoria |
| AC-NEST-011 | integración de instalación | AD/2Pack instala, muestra tabla/modelo y revierte sin IDs manuales | Ninguno: instancia iDempiere 12 desechable real |
| AC-NEST-012 | comprobación de entrega | faltar padre, runner o instancia impide estado «verificado» | Ninguno: preflight sobre dependencias reales |
| AC-NEST-013 | contrato + integración | entradas existentes de órdenes/costos rechazan volumen supuesto sin cambiar sus cálculos ni transacciones | Salida falsa en contrato; flujo real en integración |
| AC-NEST-014 | integración | no hay opción de excepción/aceptación manual nueva en el plugin | Ninguno: inspección del proceso y UI real |

**Prueba extremo a extremo:** una entrega TLL con medidas confirmadas recorre selección → unidades → evaluación → OTRLine → asignación → entrada protegida de orden/costo sin perder identidad o peso; otra con marginal ausente termina indeterminada sin OTRLine/asignación nuevos. Una familia sin regla TLL y con exteriores medidos avanza sin ahorro; con exterior faltante queda indeterminada. El runner puede sustituir ERP en las pruebas puras, pero no en instalación, transacciones ni mapeo AD. Añadir pruebas de mutación manuales para: cambiar `UNKNOWN` por cero, quitar la comparación de volumen y mezclar dos `deliveryKey`; cada una debe poner rojo el test correspondiente.

## 6. Sequencing & dependencies

1. **Cerrar identidad de entrega y datos físicos disponibles** — inventariar procedencia de cada OTR soportada, unidades de medida, evidencia de volúmenes y estados; depende de la spec clarificada; serial. Si no hay identidad demostrable, acordar el fallo cerrado antes de cualquier cálculo.
2. **Establecer entorno de pruebas** — conseguir padre/build iDempiere 12 y runner aislado; depende de #1 para fixtures representativos; serial para pruebas de integración. No se ejecuta ni instala nada externo sin autorización adicional.
3. **Definir resultados y evaluación pura** — estados tipados, alcance, identidad, reglas TLL y mediciones; depende de #1; pruebas red→verde.
4. **Sustituir el cálculo global y repartir cargas** — adaptar `createFreightUnit` al evaluador y particionador; depende de #3; comprobar conservación y ausencia de escrituras parciales.
5. **Conectar la asignación real y salidas posteriores** — comparar peso/volumen en `assignVH`, aislar/refactorizar `calculateWeight1`, bloquear volumen desconocido en entradas de orden/costo sin cambiar sus transacciones; depende de #3–4; pruebas de contrato e integración.
6. **Preparar AD/2Pack seguro** — reemplazar artefactos placeholder por migración versionada con reglas TLL aprobadas, sin TCL/TCD; exportar desde staging autorizado con IDs evidenciados y preparar script de prueba de importación; depende de #1; puede avanzarse en paralelo con #3–5, pero su ejecución espera entorno autorizado.
7. **Verificar instalación, reversión y regresión** — build/tests iDempiere 12, instalación/rollback desechable, casos de extremo a extremo y checklist de 14 AC; depende de #2–6; serial y bloqueante para despliegue.

Las tareas detalladas y sus done-checks se derivan en `tasks` tras la aprobación de este plan. No se ejecuta producto ni se crea una rama adicional hasta la decisión de aislamiento anterior a implementación.

## 7. Risks & open decisions

| Riesgo (prioridad) | Disparador | Impacto | Mitigación / prueba que lo retira |
|---|---|---|---|
| 1 · Identidad de entrega no disponible | OTR solo expone línea de origen, no delivery ID | Agrupar por pedido/cliente inventaría compatibilidad | Spike de linaje por `OrderType`; si no se demuestra, `INDETERMINATE` y sin nesting |
| 2 · Medidas sin procedencia | `NestedVolume` es NULL y `M_Product.Volume` no distingue medición confirmada | Casi todas las cargas TLL quedan indeterminadas; presión para volver a cero | Inventario de evidencia; mostrar tasa de indeterminación; no relajar principio 3 sin enmienda |
| 3 · Build iDempiere 12 ausente | Padre Maven no disponible | No se puede probar/entregar código | Obtener runtime aprobado; mantener AC-NEST-012 bloqueado hasta verde real |
| 4 · Migración AD incompleta | SQL tiene placeholders y 2Pack antiguo | Plugin no instala o colisiona con IDs | Export AD/PackOut desde staging autorizado, informe de IDs, importación y rollback en instancia desechable limpia |
| 5 · Estado `UNKNOWN` en columnas numéricas legadas | `DD_OTRLine.Volume` y costos esperan número | Cero falso o persistencia parcial | Resultado tipado antes de escritura; transacción de OTRLine/asignación y guardas de entrada de órdenes/costos |
| 6 · Regresiones en ruta/fecha y órdenes existentes | `assignVH` procesa todas las OTRLine del cliente | Asignaciones ajenas o cambios documentales no previstos | Filtrar mismo alcance, fixtures de otros clientes/fechas, prueba de regresión de órdenes |

**Decisiones abiertas:** ninguna de producto dentro del alcance aprobado. Las dependencias técnicas #1–2 pueden bloquear implementación; no autorizan inventar una identidad o medida. La decisión de aislamiento en rama antes de cambiar código y cualquier ejecución de instalación/rollback se solicitarán separadamente. `db-migrations` fue sugerida, pero no instalada ni asumida en este plan.

## Tasks
<!-- generated by tasks on 2026-09-21; IDs are stable, do not renumber -->

Las tareas se ejecutarán en orden y con pruebas rojo→verde para cada comportamiento. T004 crea los comandos de prueba y la línea base de integridad del contexto raw antes de tareas de código; T019 crea el comprobador de paquete y T020 el script de integración. Ninguno existe todavía. T003/T019/T020 dependen de entornos autorizados y no pueden darse por terminadas con simulaciones. La aprobación actual autoriza elaborar este desglose, no ejecutarlo.

| ID | [P] | Task | Done-check | Depends-on | Trace |
| --- | --- | --- | --- | --- | --- |
| T001 |  | Documentar el linaje de entrega por tipo de OTR | En `tests/nesting/lineage.md`, cada `OrderType` soportado enlaza campos y código de origen hasta una entrega única; revisión de código confirma que los tipos no demostrables figuran `INDETERMINATE`, sin inferir por cliente/clasificación | — | AC-NEST-001/003/006 |
| T002 |  | Inventariar reglas y medidas físicas confirmadas | En `tests/nesting/evidence.md`, cada campo de peso, volumen, unidad de medida y regla TLL registra fuente/estado; revisión contra datos aprobados confirma que `NULL`, cero no medido, TCL/TCD y paralelos no se marcan confirmados | T001 | AC-NEST-004/005/010 |
| T003 |  | Preparar el build iDempiere 12 autorizado | `mvn -q -DskipTests package` sale 0 con `org.idempiere.parent:12.0.0-SNAPSHOT` resuelto; si no, AC-NEST-012 permanece bloqueado y se registra la causa | T001 | AC-NEST-012 |
| T004 |  | Crear el módulo y comandos de pruebas de nesting | `mvn -q -f tests/nesting/pom.xml test` y `./tests/nesting/verify.sh` salen 0 con test de humo real; antes de editar producto se crea `tests/nesting/raw-baseline.sha256` y `sha256sum -c tests/nesting/raw-baseline.sha256` sale 0; se crea `./tests/nesting/check-secrets.sh` que inspecciona archivos cambiados y nuevos sin imprimir valores | T003 | AC-NEST-012; constitución 1/7 |
| T005 |  | Escribir pruebas rojas de identidad y alcance | `mvn -q -f tests/nesting/pom.xml -Dtest=SourceScopeTest test` falla por identidad/aislamiento aún no implementados; cubre dos entregas, rutas, fechas, candidatos y tenant | T001, T004 | AC-NEST-001/002/003 |
| T006 |  | Implementar el adaptador de origen y alcance | El comando de T005 pasa; origen ambiguo devuelve `MissingDeliveryIdentity` y ninguna unidad cambia de entrega/tenant | T005 | AC-NEST-001/002/003/006 |
| T007 |  | Escribir pruebas rojas de política TLL y grupos independientes | `mvn -q -f tests/nesting/pom.xml -Dtest=NestingPolicyTest test` falla por la política ausente; prueba cadena válida, igual rango, TCL/ORI sin ahorro, familia mixta, duplicados, candidatos distintos y permutaciones | T004, T002 | AC-NEST-001/002/004/009/010 |
| T008 |  | Implementar la política estructural TLL y ruta sin ahorro | El comando de T007 pasa; cada unidad aparece una vez, solo reglas confirmadas reducen huellas y familias sin regla producen `UnnestedGroup` del mismo `LoadScope`, nunca `UnsupportedRule` bloqueante | T006, T007 | AC-NEST-001/002/004/009/010 |
| T009 |  | Escribir pruebas rojas de medición e indeterminación | `mvn -q -f tests/nesting/pom.xml -Dtest=MeasurementStateTest test` falla por `UNKNOWN` aún ausente; distingue marginal TLL faltante, exterior sin regla medido/faltante, cero realmente medido y unidades incompatibles | T002, T004 | AC-NEST-004/005/006/013 |
| T010 |  | Implementar la resolución de medidas tipadas | El comando de T009 pasa; `UnnestedGroup` suma exteriores confirmados sin ahorro, un exterior faltante es `UNKNOWN`, y ningún marginal TLL desconocido se transforma en cero | T008, T009 | AC-NEST-004/005/006/013 |
| T011 |  | Escribir pruebas rojas de partición conservativa | `mvn -q -f tests/nesting/pom.xml -Dtest=ConservativeSplitterTest test` falla por partición ausente; incluye límite exacto, cero, indivisible y pérdida de ahorro al separar una cadena | T004, T010 | AC-NEST-008/009 |
| T012 |  | Implementar la partición y reevaluación por parte | El comando de T011 pasa; suma de cantidad/peso e identidades coincide con entrada, ninguna parte negativa/vacía y volumen efectivo se recalcula tras dividir | T011 | AC-NEST-008/009 |
| T013 |  | Escribir pruebas rojas de capacidad candidata | `mvn -q -f tests/nesting/pom.xml -Dtest=CandidateCapacityTest test` falla por comparación ausente; separa `UNKNOWN`, exceso de peso/volumen, `candidateVehicleId` distinto y paso de ambos límites sin afirmar cabida geométrica | T004, T010, T012 | AC-NEST-002/006/007 |
| T014 |  | Implementar la evaluación de capacidad por candidato | El comando de T013 pasa; `scope.candidateVehicleId` debe igualar vehículo y grupo evaluados, ningún candidato contamina otro y solo `WithinKnownLimits` habilita escritura | T013 | AC-NEST-002/006/007 |
| T015 |  | Escribir pruebas rojas del flujo activo de asignación | `mvn -q -f tests/nesting/pom.xml -Dtest=FreightFlowTest test` falla por `createFreightUnit`/`assignVH` actuales; reproduce mapa global, aceptación por peso solo y cantidad incoherente | T004, T014 | AC-NEST-001/002/007/008 |
| T016 |  | Conectar el evaluador al flujo activo de carga y vehículo | El comando de T015 pasa; `createFreightUnit` no usa reducción global y `assignVH` compara peso y volumen del candidato exacto; `calculateWeight1()` no queda como único camino corregido | T015 | AC-NEST-001/002/007/008 |
| T017 |  | Escribir pruebas rojas de barrera de volumen y atomicidad local | `mvn -q -f tests/nesting/pom.xml -Dtest=TransportOutputTest test` falla por entrada sin protección; exige diagnóstico visible, cero OTRLine/asignación nuevos ante `UNKNOWN` y ausencia de excepción manual nueva | T004, T016 | AC-NEST-006/013/014 |
| T018 |  | Proteger escritura de evaluación y entradas posteriores | El comando de T017 pasa; `UNKNOWN` no entra como volumen medido a órdenes/costos, fallo revierte solo OTRLine/asignación nuevos y no cambia cálculos/transacciones existentes ni añade excepción manual | T017 | AC-NEST-006/013/014 |
| T019 |  | Preparar la migración AD/2Pack versionada | Export AD/PackOut en staging desechable autorizado registra IDs asignados y manifiesto de procedencia; `./tests/nesting/check-pack.sh` (creado aquí) sale 0 al comprobar IDs únicos, tabla/columnas, semillas TLL confirmadas y ausencia de placeholders; rollback documentado | T002, T003 | AC-NEST-004/011 |
| T020 |  | Validar instalación y reversión | `./tests/nesting/integration.sh install-rollback` (creado aquí) sale 0 en otra instancia limpia y desechable; preflight rechaza destino operativo y evidencia importación de tabla/modelo/seed, unicidad de IDs y reversión | T003, T019 | AC-NEST-011/012 |
| T021 |  | Ejecutar regresión extremo a extremo y cobertura de criterios | `./tests/nesting/integration.sh acceptance` sale 0 con 14 AC trazados; caso TLL medido llega a salida, familia sin regla medible avanza sin ahorro y caso `UNKNOWN` no deja OTRLine/asignación nuevos ni excepción manual | T016, T018, T020 | AC-NEST-001..014 |
| T022 |  | Verificar todos los checks y registrar el estado de entrega | `./tests/nesting/verify.sh`, `sha256sum -c tests/nesting/raw-baseline.sha256`, `./tests/nesting/check-secrets.sh` y `git diff --check` salen 0; revisión de `git status --short` incluye archivos nuevos, la matriz de 14 AC tiene evidencia y faltas de padre/runner/instancia dejan estado `blocked`, nunca `verified` | T021 | AC-NEST-001..014; constitución 1/6/7 |

**Interfaces por tarea para implementación aislada**

**T006 — Interfaces**
- Consumes: `OTRRef`, campos y linaje verificados en T001.
- Produces: `SourceAdapter.resolve(OTRRef) -> CommercialLine | MissingDeliveryIdentity | InvalidQuantity`; `CommercialLine {tenant, originDocumentId, originLineId, deliveryKey, routeKey, loadDate, productId, classification, quantity, unitWeight, measurementRefs}`. `deliveryKey` nunca se infiere de cliente/clasificación.

**T008 — Interfaces**
- Consumes: `CommercialLine` de T006 y `RuleSnapshot {family, rank, compatibility, evidenceState, sourceVersion}` de T002.
- Produces: `LoadScope {tenant, deliveryKey, routeKey, loadDate, candidateVehicleId}` y `NestingPolicy.evaluate(scope: LoadScope, group: CommercialLine[], ruleSnapshot: RuleSnapshot) -> StructuralGroup | UnnestedGroup | InvalidComposition`; ambos grupos llevan `scope` y unidades originales, `UnnestedGroup` tiene ahorro cero.

**T010 — Interfaces**
- Consumes: `LoadGroup = StructuralGroup | UnnestedGroup` de T008 y `EvidenceSnapshot` de T002.
- Produces: `MeasurementResolver.resolve(group: LoadGroup, evidenceSnapshot: EvidenceSnapshot) -> KnownVolume(value, UOM, source) | UnknownVolume(missingFields[])`; `UnnestedGroup` suma exteriores confirmados, TLL estructural con marginal requerido no medido queda `UnknownVolume`.

**T012 — Interfaces**
- Consumes: `LoadScope` y `LoadGroup` de T008; resultado `KnownVolume | UnknownVolume` de T010.
- Produces: `Splitter.partition(scope: LoadScope, group: LoadGroup, transportUnitSnapshot: TransportUnitSnapshot) -> Parts[] | Indeterminate | InvalidCapacity`; cada parte reevalúa volumen tras separar unidades.

**T014 — Interfaces**
- Consumes: `LoadScope` de T008, `LoadGroup` de T008/T012 y `KnownVolume | UnknownVolume` de T010.
- Produces: `CapacityEvaluator.check(scope: LoadScope, group: LoadGroup, vehicleSnapshot: VehicleSnapshot) -> WithinKnownLimits | CapacityExceeded(reason) | Indeterminate(missingFields[])`; `scope.candidateVehicleId == vehicleSnapshot.id == group.scope.candidateVehicleId` es precondición comprobada, no supuesto del caller.

**T016 — Interfaces**
- Consumes: `SourceAdapter.resolve` de T006, `NestingPolicy.evaluate` de T008, `MeasurementResolver.resolve` de T010, `Splitter.partition` de T012 y `CapacityEvaluator.check` de T014.
- Produces: `Evaluation {scope, groups, totalQuantity, totalWeight, effectiveVolumeState, status, diagnostics}` para el candidato exacto; `createFreightUnit` y `assignVH` usan ese resultado, nunca el mapa global por clasificación.

**T018 — Interfaces**
- Consumes: `Evaluation` de T016; `CapacityEvaluator.check` de T014.
- Produces: `AssignmentWriter.apply(evaluation: Evaluation, transaction: Trx) -> Written | Rejected(reason)` con atomicidad solo de OTRLine/asignación nuevos; `TransportVolumeGuard.requireKnown(evaluation: Evaluation) -> KnownVolume | Rejected(missingFields[])` antes de entradas existentes de orden/costo, sin cambiar sus cálculos o transacciones.

**T019 — Interfaces**
- Consumes: `RuleSnapshot` y estados de evidencia de T002; instancia staging autorizada de iDempiere 12.
- Produces: paquete AD/2Pack versionado, manifiesto `tests/nesting/pack-evidence.md` con ID→objeto/fuente, rollback y `./tests/nesting/check-pack.sh` que falla con ID duplicado, placeholder o seed no confirmado.

**T020 — Interfaces**
- Consumes: paquete, manifiesto y `check-pack.sh` de T019; instancia de destino distinta, limpia, desechable y autorizada.
- Produces: `./tests/nesting/integration.sh install-rollback|acceptance`, con preflight que rechaza destino operativo y evidencia de importación/reversión para T021.

## Review Workload Forecast

| Dimension | Forecast | Why |
| --- | --- | --- |
| Líneas cambiadas | 900–1.600, estimación sujeta al spike T001 | Evaluador puro, adaptación de procesos, pruebas y migración |
| Archivos / áreas | 15–25 en Java/OSGi, pruebas, AD/2Pack y documentación | Cruza origen, nesting, capacidad, transacciones y empaquetado |
| Riesgo de revisión | alto | Identidad de entrega, datos físicos incompletos y cambios de persistencia |
| Entrega sugerida | `ask-on-risk`, en 3 lotes de revisión: contratos/pruebas; flujo activo; migración/integración | Supera los presupuestos de 400 líneas y 12 archivos de `config.yaml`; cada lote conserva sus pruebas y no implica PR/merge automático |

No se marca ninguna tarea `[P]`: varias comparten contratos, fixtures o estado de iDempiere, y la ganancia potencial no justifica colisiones antes de resolver T001–T004. T003, T020 y cualquier instalación requieren autorización y entorno específicos cuando llegue la fase de ejecución.
