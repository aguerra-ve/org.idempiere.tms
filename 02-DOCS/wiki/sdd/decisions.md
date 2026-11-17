# Decisiones SDD

## 2026-09-21 — Constitución v1.0.0 ratificada

- **Decisión:** rigen los nueve principios de la [constitución](constitution.md).
- **Opciones consideradas:** continuar sin principios explícitos, dejar el borrador pendiente o ratificarlo antes de especificar.
- **Motivo:** el propietario ratificó expresamente el borrador; las contradicciones entre el plugin y el contexto físico requieren un contrato verificable antes de planificar cambios.
- **Límite:** esta ratificación no aprueba todavía una especificación, un plan, instalación de skills, ejecución de SQL ni cambios del producto.

## 2026-09-21 — Alcance del primer ciclo: saneamiento del plugin

- **Decisión:** elaborar la especificación del primer ciclo para corregir el flujo actual de nesting y capacidad, dejando el motor físico V2 para otro ciclo.
- **Opciones consideradas:** A, saneamiento del plugin; B, motor V2 completo; C, no construir ahora. Detalle en la [propuesta](proposals/secure-nesting-first-cycle.md).
- **Motivo:** el propietario confirmó la recomendación A; B depende de mediciones y reglas empresariales aún pendientes, y C mantiene defectos conocidos.
- **Límite:** la elección de alcance no aprueba todavía el texto de la especificación, su plan técnico ni la implementación.

## 2026-09-21 — Especificación inicial aprobada para clarificación

- **Decisión:** el propietario aprobó el texto inicial de [safe-product-nesting](specs/safe-product-nesting.md) respondiendo «go» a la pregunta explícita de aprobación.
- **Opciones consideradas:** aprobar el borrador para aclarar sus dos puntos abiertos, pedir revisión del texto o detener el ciclo.
- **Motivo:** la aprobación permite pasar a `clarify` manteniendo visibles los supuestos y preguntas del documento.
- **Límite:** no resuelve las preguntas abiertas, no aprueba el plan técnico, no activa autopiloto ni autoriza implementación.

## 2026-09-21 — Límite operativo del primer ciclo

- **Decisión:** sin excepción manual dentro del plugin y sin nesting entre entregas distintas, incluso cuando la precedencia de descarga sea conocida.
- **Opciones consideradas:** incorporar ambos flujos ahora o diferirlos mientras se sanea el plugin actual.
- **Motivo:** el propietario confirmó expresamente diferirlos; evita mezclar la corrección de capacidad con autorización excepcional y planificación de descarga.
- **Efecto:** la especificación se clarificó con AC-NEST-003/014 y aún requiere aprobación final tras esta revisión de texto.

## 2026-09-21 — Evaluación acotada por carga candidata

- **Decisión técnica propuesta:** sustituir el mapa global de volumen por clasificación por una evaluación pura y trazable por entrega/vehículo candidato antes de persistir o asignar.
- **Alternativas:** parchear el mapa global con más claves (menos cambio inicial, identidad/capacidad aún frágiles) o construir el motor geométrico V2 (fuera del alcance aprobado).
- **Motivo:** la reducción global actual puede mezclar vehículos; el contrato exige identidad, indeterminación explícita y comparación real de peso/volumen.
- **Estado:** incluida en el [plan](plans/safe-product-nesting.md), aprobado para desglose de tareas; ninguna implementación autorizada.

## 2026-09-21 — Especificación clarificada y plan aprobados para tareas

- **Decisión:** el propietario aprobó la [especificación clarificada](specs/safe-product-nesting.md) y el [plan técnico](plans/safe-product-nesting.md) respondiendo «sí» a la pregunta explícita de pasar al desglose de tareas.
- **Opciones consideradas:** aprobar ambos documentos, solicitar cambios o detener el ciclo.
- **Motivo:** los dos límites operativos y la arquitectura de evaluación acotada quedaron visibles para revisión antes de escribir tareas.
- **Límite:** la respuesta autoriza documentar y analizar tareas; no activa autopiloto ni autoriza cambios de código, ejecución de SQL/2Pack, instalación, commit, push o despliegue.

## 2026-09-21 — Resolución conservadora de hallazgos del análisis

- **Decisión:** una familia sin regla TLL confirmada no recibe ahorro, pero puede evaluarse con la suma de sus volúmenes exteriores confirmados; volumen exterior faltante sigue indeterminado. Una cadena TLL cuyo marginal requerido no está medido sigue indeterminada.
- **Decisión de alcance:** limitar la atomicidad nueva a OTRLine/asignación de esta evaluación; las entradas de órdenes/costos solo rechazan volumen desconocido como medido, sin cambiar sus cálculos o transacciones.
- **Alternativas consideradas:** bloquear toda familia sin regla aunque tenga exterior medido, o ampliar el refactor a órdenes/costos completos.
- **Motivo:** el propietario aceptó expresamente la opción conservadora y el recorte tras leer el [análisis](analysis/safe-product-nesting.md); satisfacen AC-NEST-004/013 sin inventar datos ni ampliar el ciclo.
- **Límite:** se autorizó corregir [plan y tareas](plans/safe-product-nesting.md) y repetir el análisis, no instalar skills externos ni implementar código, SQL/2Pack o despliegue.

## 2026-09-23 — Runner puro antes de la integración OSGi  (feature: safe-product-nesting, task: T004)

Context — Las reglas de identidad, agrupación, medidas y capacidad deben probarse rojo→verde, pero
el runtime OSGi completo es costoso y no debe ser requisito para cada prueba unitaria.

Options — Ejecutar todas las pruebas dentro de iDempiere; duplicar las clases bajo prueba dentro del
módulo de tests; o compilar desde el módulo aislado únicamente fuentes puras reales del producto.

Decision — `tests/nesting/pom.xml` usa Java 17 y JUnit 5, compila fuentes productivas puras
seleccionadas —incluido el futuro paquete `org.idempiere.tms.nesting`— y reserva persistencia, AD y
OSGi para pruebas de integración.

Why — Prueba el código que se entregará sin duplicarlo, mantiene rápido el ciclo TDD y conserva una
frontera explícita: un test puro no puede sustituir la validación posterior dentro de iDempiere 12.

## 2026-09-23 — Identidad del candidato en el portador de alcance  (feature: safe-product-nesting, task: T005)

Context — El done-check de T005 exige probar aislamiento por vehículo candidato, mientras
`CommercialLine` deliberadamente conserva solo procedencia física y el plan introduce `LoadScope`
como consumidor formal en T008.

Options — Diferir la prueba del candidato hasta T008; agregar el candidato a `CommercialLine`; o
fijar desde T005 el contrato inmutable de `LoadScope` sin implementar todavía política de nesting.

Decision — `SourceScopeTest` exige `LoadScope {tenant, deliveryKey, routeKey, loadDate,
candidateVehicleId}`. T006 podrá crear ese portador mínimo junto al adaptador; T008 añadirá el uso
en la política, no un segundo tipo de alcance.

Why — Cumple el aislamiento exigido sin contaminar la identidad de origen ni duplicar portadores;
cambiar cualquiera de las cinco dimensiones produce un alcance distinto antes de agrupar unidades.

## 2026-09-23 — Resultado plural para composiciones mixtas  (feature: safe-product-nesting, task: T007)

Context — El plan expresa el resultado de la política como `StructuralGroup | UnnestedGroup |
InvalidComposition`, pero AC-NEST-010 exige evaluar en una misma entrada una familia TLL soportada
y otra sin regla, conservando ambas sin heredar compatibilidad.

Options — Rechazar toda mezcla como composición inválida; mezclar unidades incompatibles en un
solo grupo; o devolver una lista determinista cuyos elementos pertenezcan a la unión tipada del
plan.

Decision — `NestingPolicy.evaluate` devolverá `List<NestingResult>`. `NestingResult` será la unión
cerrada; los resultados válidos podrán incluir simultáneamente `StructuralGroup` y
`UnnestedGroup`, mientras una identidad duplicada producirá un único `InvalidComposition`.

Why — La lista representa sin pérdida los grupos físicos distintos de una carga mixta, conserva el
`LoadScope` en cada grupo y permite comprobar que cada unidad aparece exactamente una vez. No crea
una regla entre familias ni convierte la ausencia de regla en un bloqueo.

## 2026-10-13 — Simulación de nesting previa a entrega y vehículo (feature: map-nesting-integration, FTD)
Contexto — el mapa selecciona pedidos, pero SourceAdapter/DeliveryKey/LoadScope exigen identidad física y candidato reales. El propietario autorizó integrar el mapa y continuar en feature/dd-product-nesting.
Opciones — fabricar una entrega/vehículo a partir del pedido; dejar todo pendiente; separar una simulación estructural explícita que reusa el conteo puro.
Decisión — entrada OrderProductLine y ProposalNestingPreview independientes del contrato operativo; conteo compartido ChainGrouping; simulación aislada por pedido y almacén. No se pasa por SourceAdapter con identidad ficticia.
Motivo — permite mostrar progreso útil sin afirmar entrega, vehículo, volumen ni cabida. La evidencia TLL se limita al cliente QTanque confirmado; coincidencia de clasificación en otros tenants no autoriza reducción.
