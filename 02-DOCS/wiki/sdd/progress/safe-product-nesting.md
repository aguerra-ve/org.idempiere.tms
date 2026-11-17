# Progreso — safe-product-nesting

## T001 — 2026-09-21
- status: complete (linaje estático documentado; validación de ERP pendiente de T005/T006)
- evidence: `tests/nesting/lineage.md` enlaza las seis ramas `OrderType` con sus referencias y distingue cabeceras físicas de pedidos o movimientos.
- check: `MMS`/`MMR` permiten clave de documento `M_InOut` sujeta a validación; `SOO`, `POO`, `DOO` y `MMM` quedan `INDETERMINATE_MISSING_DELIVERY_IDENTITY` para el contrato de entrega actual.
- red/green: no aplica; esta tarea inventaría linaje existente y no cambia comportamiento Java.
- files: `tests/nesting/lineage.md`, `02-DOCS/wiki/sdd/progress/safe-product-nesting.md`.
- decision: ninguna regla física nueva; no inferir entrega por pedido, cliente o clasificación.
- blocker: prueba de las relaciones contra iDempiere 12 queda para el entorno y tareas posteriores.

## T002 — 2026-09-23
- status: complete.
- evidence: `tests/nesting/evidence.md` registra procedencia, UOM y estado de cantidad, peso, dimensiones, volumen exterior/marginal/efectivo, límites de `DD_Vehicle` y cada regla física considerada.
- check: la revisión contra la metodología aprobada, sus datasets con SHA-256, el análisis de brechas y los modelos Java confirma únicamente la cadena TLL `370 -> 500 -> 550 -> 700 -> 900 -> 1060`; `NULL`, cero no medido, nesting TCL/TCD e interiores paralelos no figuran como confirmados.
- red/green: no aplica; esta tarea inventaría evidencia existente y no cambia comportamiento Java. Su done-check es la matriz documental completa y revisada.
- files: `tests/nesting/evidence.md`, `02-DOCS/wiki/sdd/progress/safe-product-nesting.md`.
- decision: el peso maestro se conserva como snapshot actual y límite secundario; no determina las unidades estructurales. `M_Product.Volume` no se usa como envolvente de transporte, y una huella TLL no autoriza seleccionar vehículo sin volumen efectivo/UOM confirmados.
- blocker: no hay volumen marginal TLL medido ni UOM en `DD_ProductNesting`; la capacidad volumétrica automática permanece `UNKNOWN` hasta aportar evidencia compatible. ORI tiene evidencia separada, pero está fuera del alcance aprobado de este ciclo.

## T003 — 2026-09-23
- status: complete.
- evidence: `tests/nesting/build.md` registra Java 17, Maven 3.9.10, padre iDempiere 12, plataforma OSGi completa, comandos, fallos diagnósticos, hashes y artefacto final.
- check: `mvn -Dmaven.repo.local=/tmp/iqp-mapview-m2 -Drevision=12.0.0-SNAPSHOT -q -DskipTests package` terminó con código 0 en el reactor aislado; el JAR resultante mide 2613117 bytes y tiene SHA-256 `1f4ad7a60fda53b062ef3b267cb93fe2f8bcaa29277ff1ea1874c39776e18ea1`.
- red/green: no aplica a comportamiento; T003 es una puerta de entorno. Dos ejecuciones fallaron primero por escritura bloqueada en `~/.m2` y por target incompleto; el build pasó al aislar el repositorio e incluir los bundles iDempiere 12, sin cambiar código de producto.
- files: `tests/nesting/build.md`, `02-DOCS/wiki/sdd/config.yaml`, `02-DOCS/wiki/sdd/progress/safe-product-nesting.md`.
- decision: conservar intacto el `pom.xml` activo y construir mediante reactor temporal; usar repositorio Maven en `/tmp` y la imagen local iDempiere 12 como target completo.
- blocker: ninguno para compilación. El runner de pruebas de nesting aún no existe y corresponde a T004; el staging temporal debe reconstruirse si `/tmp` se limpia.

## T004 — 2026-09-23
- status: complete.
- evidence: `tests/nesting/pom.xml` compila código productivo puro con Java 17; `NestingSmokeTest` ejecuta dos pruebas sobre `ProposalLoadSummary`; `raw-baseline.sha256` cubre 344 archivos; `verify.sh` reúne tests, integridad raw y detección de secretos.
- check: `mvn -q -f tests/nesting/pom.xml test` y `./tests/nesting/verify.sh` terminaron con código 0; Surefire registró 2 tests, 0 fallos/errores/omitidos; `sha256sum -c tests/nesting/raw-baseline.sha256` validó las 344 entradas.
- red/green: RED inicial correcto porque faltaba `tests/nesting/pom.xml`; GREEN tras crear el runner. Dos intentos intermedios identificaron escritura dinámica de Surefire/JUnit contra `~/.m2`; se fijaron versiones y repositorio de solo lectura sin cambiar el comando de aceptación. El self-test de secretos demostró tanto detección como pase.
- files: `tests/nesting/pom.xml`, `tests/nesting/src/test/java/org/idempiere/tms/nesting/NestingSmokeTest.java`, `tests/nesting/raw-baseline.sha256`, `tests/nesting/check-secrets.sh`, `tests/nesting/verify.sh`, `02-DOCS/wiki/sdd/config.yaml`, `02-DOCS/wiki/sdd/decisions.md`, `02-DOCS/wiki/index.md`, `02-DOCS/wiki/sdd/progress/safe-product-nesting.md`.
- decision: runner Java 17 aislado que compila únicamente fuentes productivas puras explícitas; las futuras clases `org.idempiere.tms.nesting` entran en el mismo ciclo TDD sin arrancar OSGi. Persistencia/AD siguen reservados para integración iDempiere.
- blocker: ninguno para las pruebas unitarias puras. Las pruebas que necesiten runtime, persistencia o AD conservan la dependencia del staging iDempiere 12.

## T005 — 2026-09-23
- status: complete (expected-red test task).
- evidence: `SourceScopeTest` fija el contrato para `OTRRef`, `SourceAdapter`, resultados tipados, `CommercialLine`, `DeliveryKey` y `LoadScope`; cubre MMS/MMR, dirección errónea, SOO/POO/DOO/MMM indeterminados, cantidad inválida y aislamiento por tenant, entrega, ruta, fecha y candidato.
- check: `mvn -q -f tests/nesting/pom.xml -Dtest=SourceScopeTest test` terminó con código 1 porque los tipos productivos planificados aún no existen. El diagnóstico contiene únicamente símbolos ausentes del contrato de T006; no hay error de sintaxis, runner ni dependencia.
- red/green: RED correcto y conservado para T006. GREEN no corresponde a esta tarea de prueba; T006 debe obtenerlo con la implementación mínima sin debilitar las aserciones.
- files: `tests/nesting/src/test/java/org/idempiere/tms/nesting/SourceScopeTest.java`, `02-DOCS/wiki/sdd/decisions.md`, `02-DOCS/wiki/sdd/progress/safe-product-nesting.md`.
- decision: probar `LoadScope` como portador inmutable desde este contrato, aunque la política que lo consume llegue en T008; el done-check de T005 exige demostrar desde ahora que el candidato forma parte del aislamiento.
- blocker: ninguno; el siguiente paso es T006. Mientras T006 no termine, la suite completa permanece roja deliberadamente.

## T006 — 2026-09-23
- status: complete.
- evidence: el paquete puro `org.idempiere.tms.nesting` implementa `OTRRef`, `SourceAdapter`, los tres resultados cerrados, `CommercialLine`, `DeliveryKey`, `LoadScope` y `OrderType`; conserva tenant/origen/entrega/ruta/fecha/producto/cantidad/medidas y no usa cliente ni clasificación para inventar una entrega.
- check: `mvn -q -f tests/nesting/pom.xml -Dtest=SourceScopeTest test` y `./tests/nesting/verify.sh` terminaron con código 0. Surefire registró 12 pruebas de alcance más 2 de humo, sin fallos, errores ni omisiones; la línea base raw de 344 archivos y el detector de secretos también pasaron.
- integration: el código exacto se sincronizó al reactor temporal y `mvn -Dmaven.repo.local=/tmp/iqp-mapview-m2 -Drevision=12.0.0-SNAPSHOT -q -DskipTests package` terminó con código 0. El JAR iDempiere 12 contiene las nueve clases de nesting y tiene SHA-256 `4bd7cc4ebdeef70e017c34b42c5b419cab1c11c51393795a39e6ff65d38c847b`.
- red/green: además del RED inicial por tipos ausentes, la triangulación produjo dos RED conductuales antes de sus correcciones mínimas: `1.5` era aceptado como cantidad TLL y dos referencias activas devolvían una línea comercial. El GREEN final rechaza cantidades TLL no enteras y exige exactamente una referencia activa.
- files: `src/org/idempiere/tms/nesting/*.java`, `tests/nesting/src/test/java/org/idempiere/tms/nesting/SourceScopeTest.java`, `02-DOCS/wiki/sdd/progress/safe-product-nesting.md`.
- decision: mantener el límite como snapshots Java 17 inmutables y resultados tipados; `sourceReferenceCount` hace explícita la ambigüedad en vez de elegir una referencia, y `LoadScope` incluye el candidato sin ejecutar aún selección de vehículo.
- blocker: ninguno para el contrato puro. La lectura real de relaciones `DD_OTR` y la conexión con el flujo operativo siguen reservadas para las tareas de integración posteriores; por eso la pantalla aún no calcula `Unidades después del nesting`.

## T007 — 2026-09-23
- status: complete (expected-red test task).
- evidence: `NestingPolicyTest` fija la cadena confirmada `370 -> 500 -> 550 -> 700 -> 900 -> 1060`, una sola huella para una cadena válida, unidades del mismo rango independientes, TCL/ORI sin ahorro, composición mixta separada, rechazo de identidades duplicadas, aislamiento por candidato, resultado independiente del orden y reglas no confirmadas sin ahorro.
- check: `mvn -q -f tests/nesting/pom.xml -Dtest=NestingPolicyTest test` terminó con código 1. El compilador señaló únicamente los tipos productivos planificados que T008 aún debe crear (`NestingPolicy`, resultados/grupos, unidades, snapshot y estados de regla); no hubo fallo de sintaxis, dependencia o runner.
- red/green: RED correcto y conservado. GREEN no corresponde a esta tarea de pruebas; T008 debe obtenerlo sin debilitar las aserciones.
- files: `tests/nesting/src/test/java/org/idempiere/tms/nesting/NestingPolicyTest.java`, `02-DOCS/wiki/sdd/decisions.md`, `02-DOCS/wiki/sdd/progress/safe-product-nesting.md`.
- decision: una evaluación mixta devuelve una lista determinista de la unión tipada `StructuralGroup | UnnestedGroup | InvalidComposition`; esto permite representar a la vez el grupo TLL y las unidades independientes sin mezclarlos ni bloquear una familia sin regla.
- skill-resolution: usadas `sdd` e `implement`; `java` no se instaló por decisión del propietario. Fallback: Java 17, JUnit 5, Maven y los contratos ya calibrados en `config.yaml`; sin subagente ni commit por las restricciones vigentes del workspace.
- blocker: ninguno; el siguiente paso es T008. Mientras T008 no implemente la política mínima, la suite completa permanece roja deliberadamente.

## T008 — 2026-09-23
- status: complete.
- evidence: `NestingPolicy` expande cantidades enteras a `CommercialUnit` con `UnitIdentity`, valida el `LoadScope`, detecta duplicados y aplica únicamente snapshots `CONFIRMED` con estructura `CHAIN`. Los resultados cerrados separan `StructuralGroup`, `UnnestedGroup` e `InvalidComposition` y conservan el alcance exacto.
- check: `mvn -q -f tests/nesting/pom.xml -Dtest=NestingPolicyTest test` y `./tests/nesting/verify.sh` terminaron con código 0. Surefire registró 11 pruebas de política, 12 de identidad/alcance y 2 de humo: 25 en total, sin fallos, errores ni omisiones; integridad raw y detector de secretos también pasaron.
- integration: el código exacto se sincronizó al reactor temporal; el diff entre ambos paquetes quedó vacío y `mvn -Dmaven.repo.local=/tmp/iqp-mapview-m2 -Drevision=12.0.0-SNAPSHOT -q -DskipTests package` terminó con código 0. El JAR contiene las 20 clases del paquete de nesting y tiene SHA-256 `9cf698f8aece7627049e1521d16b87601215d64d57c5e5c4b30d880d9fae16b1`.
- red/green: RED heredado de T007 por tipos ausentes; GREEN tras la implementación mínima. La triangulación añadió dos cantidades pequeñas frente a una grande y confirmó 3 unidades comerciales → 2 huellas, sin interiores paralelos; la revisión añadió una línea de otra ruta y confirmó `InvalidComposition` en vez de reetiquetarla con el alcance recibido.
- files: `src/org/idempiere/tms/nesting/{EvidenceState,NestingStructure,RuleSnapshot,UnitIdentity,CommercialUnit,NestingResult,LoadGroup,StructuralGroup,UnnestedGroup,InvalidComposition,NestingPolicy}.java`, `tests/nesting/src/test/java/org/idempiere/tms/nesting/NestingPolicyTest.java`, `02-DOCS/wiki/sdd/progress/safe-product-nesting.md`.
- decision: construir el número de huellas mediante cadenas deterministas exterior→interior; cada cadena admite como máximo una unidad por rango. Los sobrantes, rangos iguales y familias sin regla confirmada quedan independientes con ahorro cero; ninguna reducción implica todavía volumen conocido ni cabida vehicular.
- skill-resolution: usadas `sdd`, `implement` y `orient`; `java` permaneció sin instalar. Fallback: contratos Java 17/JUnit 5 ya calibrados; revisión local en lugar de subagente y sin commit por las restricciones explícitas del workspace.
- blocker: ninguno para la política estructural pura. La pantalla aún no consume estos grupos y el volumen efectivo TLL sigue `UNKNOWN`; esas fronteras corresponden a T009–T010 y a la integración posterior del flujo activo.
