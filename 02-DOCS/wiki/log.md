# Wiki Log

## [2026-09-29] ftd | orientación histórica TA-5500 no identificable por área
- A petición del operador, se contrastó si el predespacho `1007137` obliga a TA-5500 a viajar de pie. En la sensibilidad de 16 grupos con tapa de tambor Ø590 mm y bases inferiores ORI, TA-5500 de lado suma 13,343355 m², 0,143355 m² sobre los 13,20 m² reportados. La ficha oficial admite hasta 3 % de variación dimensional; con diámetro y altura de TA-5500 3 % menores, la misma suma es 13,121036 m² y el umbral aritmético se cruza con solo 1,924 % menos en ambas medidas. La envolvente de lado tampoco es huella de contacto medida ni prueba de packing. Se conserva `TA-5500_ORIENTACION_HISTORICA=NO_IDENTIFICABLE_CON_DATOS_ACTUALES`; de pie es solo la primera hipótesis de menor envolvente. [Auditoría reproducible](ftd/qtanque-preshipment-full-load-evaluation.md).

## [2026-09-29] ftd | orientación TCL y TA en el FVR de `1007137`
- El operador confirmó TCL-1700 de pie y TA-3300/5500 de pie o de lado. Para los 16 grupos estructurales se compararon secciones nominales con tapas de tambor Ø590 mm y bases inferiores ORI: ambos TA de pie 12,226217 m²; solo TA-3300 de lado 13,056130; solo TA-5500 de lado 13,343355; ambos de lado 14,173268 m². De lado se usa únicamente la envolvente rectangular diámetro × altura publicada, no un apoyo medido ni la orientación histórica. El modelo de menor sección propone empezar con los TA de pie; aún requiere un acomodo 3D y amarres. [Evaluación y cálculo](ftd/qtanque-preshipment-full-load-evaluation.md).

## [2026-09-29] ftd | `1007137` recalculado en 16 grupos con TLL/TCD mixtos
- El operador confirmó explícitamente que tanto TLL-0370 como TLL-0500 entran dentro de TCD-0520; ya estaba confirmado TCD-0520 dentro de TLL-0700/1060 y la prioridad de capacidad sin restringir orden de entrega. El ledger de unidades por `M_InOutLine_ID#ordinal` asigna sin duplicados los 10 TLL y 3 TCD a seis cadenas: dos exteriores TLL-1060, uno TCD-0520 y tres TLL-0370; peso maestro 193 kg. Las otras 19 unidades son tres pilas ORI (142 kg), tres grupos de tambores (50 kg) y cuatro cuerpos independientes (240 kg). Total **32 unidades, 625 kg y 16 grupos estructurales**. La suma de secciones superiores/cuerpos es 13,521748 m²; con tapas de tambor Ø590 mm y secciones inferiores ORI es 12,226217 m² frente a 13,20 m² de plataforma reportada. El cálculo no es un plano de carga ni prueba de posiciones reales. [Ledger y sensibilidades](ftd/qtanque-preshipment-full-load-evaluation.md).

## [2026-09-29] ftd | TCD dentro de TLL; prioridad a capacidad del FVR
- El operador confirmó que `TCD-0520` puede entrar en `TLL-0700` o `TLL-1060` con su tapa dentro, y que el cálculo busca transportar la mayor cantidad sin imponer el orden de las entregas. Se retiró la precedencia de descarga como condición de la sensibilidad de capacidad TLL. El conteo de 19 grupos con TCD aún separados se mantiene como cuenta base; el nuevo anidado TCD→TLL no se descuenta automáticamente porque la cadena interior solo admite un elemento por nivel y podría desplazar un TLL-0370. Un ejemplo con dos TCD dentro de los dos TLL-1060 conserva 19 grupos y reduce la suma de secciones de cuerpo de 14,253288 a 14,243804 m². Falta confirmar si `TLL-0370` o `TLL-0500` puede entrar en `TCD-0520` antes de cuantificar otro ahorro de grupos. [Evaluación y cálculo](ftd/qtanque-preshipment-full-load-evaluation.md).

## [2026-09-29] ftd | parejas TBC+TBA para `1007137`
- El operador confirmó que, durante el transporte, un TBA-0208 vacío puede ir encima de un TBC-0208. Las cinco unidades de la entrega `1056428` se representan como dos parejas TBC+TBA de 19 kg cada una y un TBC de 12 kg: tres grupos en vez de cinco, sin mezclar entregas. Las fichas oficiales TBA/TBC publican cuerpo Ø570 mm, tapa Ø590 mm y altura individual 880 mm, y desaconsejan apilar el producto instalado; la regla de transporte vacío procede del operador. El total del predespacho sigue en 32 unidades/625 kg y baja a 20 grupos sin mezclar TLL o 19 bajo una ruta hipotética compatible con TLL mezclados. La suma de secciones con Ø590 mm de tapa y secciones inferiores ORI es 12,957757 m² en el caso de 19 grupos, pero no demuestra un acomodo 3D ni la ruta real. El alcance de la regla TCD comunicada después sigue pendiente de aclaración. [Evaluación y cálculo](ftd/qtanque-preshipment-full-load-evaluation.md).

## [2026-09-29] ftd | reconstrucción de `1007137` y corrección de la inferencia de área
- El usuario confirmó que las 32 unidades del predespacho salieron en el FVR de plataforma abierta de 6,00 × 2,20 m útiles reportados. Se conciliaron las 23 líneas y 625 kg del maestro en 3 pilas ORI homogéneas, 7 grupos TLL conservando entregas y 12 cuerpos individuales: 22 grupos estructurales, sin posiciones reales registradas. El escenario TLL que mezcla entregas baja a 21 grupos, sujeto a precedencia de descarga. La suma de secciones máximas es 15,186635 m² por entrega o 14,763639 m² globales; los diámetros inferiores publicados de ORI reducen esas sumas en 1,350194 m². Ambas cuentas son sensibilidades, no pruebas de cabida o imposibilidad. Se retira la inferencia anterior de que la suma superior descartaba el acomodo; la salida está confirmada por el operador y su geometría permanece sin reconstruir. [Inventario y cálculo reproducible](ftd/qtanque-preshipment-full-load-evaluation.md).

## [2026-09-29] ftd | RSC actualizado a 2.0.15
- Con autorización del usuario, se sincronizó el harness de este repositorio desde 2.0.5 hasta 2.0.15 para Codex y Cursor. Vista previa y ejecución terminaron con código 0; se crearon respaldos por destino. La CLI, el manifiesto y ambos estados declaran 2.0.15; los dos diagnósticos reportan memoria lista y sin skills, agentes ni comandos faltantes. Los archivos locales ajenos al harness conservan sus hashes y no se creó commit. [Evidencia](ftd/rsc-upgrade-2-0-15.md).

## [2026-09-25] ftd | alcance ORI confirmado y cota TLL corregida
- El usuario confirmó que los `MaxStack` ORI de `PHYSICAL_NESTING_METHODOLOGY.md` y `PHYSICAL_ORI_STACKING_RULES.md` aplican también a `1007137`. Al cotejar directamente ambos documentos, se corrigió la cadena TLL transitiva más compacta: `370→500→700→1060`. El escenario optimista conserva 21 grupos y baja la proyección de 14,931459 a **14,763639 m²**, todavía mayor que los 13,20 m² reportados. Sin mezclar entregas TLL son 22 grupos y 15,186635 m². [Evaluación corregida](ftd/qtanque-preshipment-full-load-evaluation.md).

## [2026-09-25] ftd | prueba de área del predespacho FVR completo
- Consulta fresca de `1007137`: 23 líneas, 7 entregas, 32 unidades, 625 kg y dimensiones positivas en todas. Las reglas confirmadas permiten 10 TLL→6 cadenas globales y 10 ORI→3 pilas; 12 cuerpos quedan individuales. La suma de secciones máximas se corrigió después al cotejar la cadena transitiva y los diámetros inferiores ORI. Aquella interpretación de la suma como descarte de un acomodo plano fue retirada el 2026-09-29: no representa la geometría real de cuerpos ahusados. [Cálculo y evidencia](ftd/qtanque-preshipment-full-load-evaluation.md).

## [2026-09-25] ftd | 32/32 unidades dimensionadas del predespacho 1007137
- El usuario confirmó las correspondencias de TLL-0500, TLL-1060 (Q'Gigante) y THL-1000. Se cargaron los tres productos que representan las cuatro unidades finales; la simulación 3/3 terminó con `ROLLBACK`, la aplicación 3/3 con `COMMIT` y una conexión nueva devolvió 23/23 líneas, 32/32 unidades, 7 entregas y 625 kg del maestro. El peso ERP de TLL-1060 (23 kg) difiere del catálogo Q'Gigante (22 kg) y se conservó. La cabida total del FVR aún requiere reglas físicas de manejo y carga admisible. [SQL y detalle](ftd/qtanque-preshipment-physical-coverage.md).

## [2026-09-25] ftd | cobertura física de 28/32 unidades del FVR
- En `id-04`, tenant QTanque, se cargaron con guardas 21 variantes ORI y 10 variantes TBA/TBC/TCD de peso positivo. Cada lote pasó simulación con `ROLLBACK`, aplicación con `COMMIT` y verificación en conexión nueva. El predespacho `1007137` pasó de 10/32 a 28/32 unidades dimensionadas; peso maestro 625 kg sin cambios. [Evidencia](ftd/qtanque-preshipment-physical-coverage.md).

## [2026-09-25] ftd | carga total del FVR frente al replay de una entrega
- La consulta de solo lectura del predespacho `1007137` encontró 23 líneas de 7 entregas, 32 unidades y 625 kg del maestro actual; la cabecera coincide en 32 unidades y 625 kg. Solo 10 unidades tienen las tres dimensiones positivas. El envío `1056427` aporta 3 unidades y 41 kg: su disposición parcial no prueba cabida de las otras 29 unidades y 584 kg. Consulta reproducible y desglose en `02-DOCS/wiki/ftd/qtanque-family-physical-data.md`.

## [2026-09-25] ftd | altura operativa de la cadena TLL `370→700`
- El usuario indicó que la altura del conjunto anidado es la del tanque mayor, `TLL-0700`. En el maestro cargado son 2.020 mm nominales; el `TLL-0370` independiente mide 2.010 mm. Se registró como regla operativa del replay, sin presentarlo como medición de campo de la cadena con tapas. La plataforma abierta no impone techo interior, pero aún faltan holgura, sujeción, restricciones del trayecto y carga admisible del FVR.

## [2026-09-25] ftd | pesos TLL de `1056427` incluyen tapas
- El usuario confirmó que los pesos maestros de 12 kg (`TLL-0370`) y 17 kg (`TLL-0700`) usados en el replay incluyen sus tapas. Las tres unidades conservan 41 kg y no se añade peso adicional por las tapas. La confirmación se registró en la evidencia; no se modificó `M_Product` ni se extrapoló al peso de otras familias.

## [2026-09-25] ftd | orientación vertical de TLL anidados
- El usuario confirmó que los TLL anidados de la entrega `1056427` viajan de pie sobre la plataforma abierta. Se actualizó la evidencia: los diámetros del cuerpo ahora tienen orientación operativa confirmada para la comparación en planta de 6,00 × 2,20 m. Los altos individuales de 2.020 y 2.010 mm no prueban la altura de la cadena con ambas tapas; peso admisible, sujeción y cabida completa siguen pendientes.

## [2026-09-25] ftd | tapas dentro de cada TLL anidado
- El usuario confirmó que, en la cadena `TLL-0370→TLL-0700`, la tapa de cada tanque permanece dentro de su propio tanque. Se actualizó la evidencia física y del replay; quedó resuelta la ubicación operativa de las tapas, pero no la altura/holgura medida de la cadena. La inclusión de las tapas en los pesos maestros fue confirmada después. Las dos huellas siguen siendo estructurales y la cabida completa continúa pendiente.

## [2026-09-25] ftd | acomodo operativo de tapas por familia
- El usuario explicó que las tapas de tanques cónicos se colocan en espacios libres de la plataforma, las de TA en la cabina del chófer y las de TLL dentro del tanque. Se registró esta práctica en la evidencia física y del replay, sin agregar huellas ficticias ni alterar `M_Product`. La posición de ambas tapas y la inclusión en los pesos maestros de la entrega `1056427` fueron precisadas posteriormente. Las dos huellas calculadas siguen siendo estructurales.

## [2026-09-24] ftd | plataforma abierta del FVR de `1007137`
- El usuario confirmó que el FVR del predespacho `1007137` lleva plataforma abierta. Se eliminó del pendiente un techo o alto interior de caja, pero continúan sin verificar la altura final de los TLL anidados, estabilidad, amarres, acceso, barandas, restricciones de altura del trayecto y carga admisible. La comprobación en planta de 6,00 × 2,20 m sigue siendo condicional; no se afirmó cabida completa.

## [2026-09-24] ftd | ancho útil reportado y comprobación en planta del FVR
- El usuario informó 2,20 m de ancho útil para el FVR del predespacho `1007137`; con los 6,00 m de largo útil previamente confirmados, la plataforma reportada mide 6,00 × 2,20 m. Las dos huellas estructurales de la entrega `1056427` (cuerpos 670 y 485 mm sin tapa) caben en planta en una disposición ilustrativa de 1.155 mm de ancho por 670 mm de largo, sin holguras. Faltan altura del grupo, tapa/accesorios, amarres, acceso y carga admisible; no se declara cabida completa ni se crea perfil vehicular en `id-04`. Detalle en `02-DOCS/wiki/ftd/qtanque-family-physical-data.md`.

## [2026-09-24] ftd | fichas oficiales FVR como referencia, sin asignación de cabida
- Se consultaron fichas oficiales de Isuzu para variantes FVR. Reportan anchos **exteriores** de 2.400 y 2.460 mm y cargas útiles de chasis-cabina de 11.530, 11.790 y 12.000 kg según versión/mercado; la carrocería no está incluida. No identifican el FVR del predespacho `1007137` ni su ancho útil o carga admisible real. Fuentes y clasificación de evidencia en `02-DOCS/wiki/ftd/qtanque-family-physical-data.md` y `02-DOCS/wiki/ftd/transporter-fleet-normalization.md`; cabida sigue indeterminada.

## [2026-09-24] ftd | semántica del largo reportado del FVR
- El usuario confirmó que “FVR medidas 6,00 m” en la descripción reciente del predespacho `1007137` significa **largo útil de plataforma**. Se actualizó la evidencia de `1056427` con esa procedencia; no se infirió placa ni cabida. Una consulta de solo lectura halló dos notas de septiembre con “medidas … m”, solo una con FVR. Falta ancho/alto útil, carga admisible, acceso y geometría del grupo anidado.

## [2026-09-24] ftd | trazabilidad de vehículo para los cuatro replays
- La revisión de solo lectura de `id-04` encontró `QTransporte` en las cuatro entregas, sin fechas `ShipDate`/`PickDate` ni ruta o unidad de flota. `1056427` enlaza al predespacho `1007137`, cuya nota menciona FVR y “medidas 6,00 m”; `1056083` enlaza a `1007104`, sin nota. Las otras dos no enlazan a predespacho por línea ni por `ShipmentsGenerated`. El texto libre del primer predespacho contiene identificadores personales y no se copió a la wiki.
- Faltan placa/ID, ruta, fecha real de carga, ancho/alto útil, carga admisible, acceso y medidas del grupo anidado. La semántica del largo fue confirmada posteriormente por el usuario. No se evaluó cabida. Consulta sin texto personal y pasos concretos en `02-DOCS/wiki/ftd/qtanque-family-physical-data.md`.

## [2026-09-24] ftd | aislamiento entre cuatro entregas reales de QTanque
- Se releyeron en `id-04` tres entregas adicionales completadas (`1056240`, `1056083`, `1056002`) con TLL dimensionados. El replay local ahora cubre cuatro entregas, 16 unidades y 239 kg del maestro actual: 2+1+2+4 = 9 huellas estructurales independientes. También rechaza mezclar una línea de otra entrega aunque la ruta y fecha de prueba coincidan. Consulta y detalles: `02-DOCS/wiki/ftd/qtanque-family-physical-data.md`.
- La ruta y el vehículo del test siguen siendo sintéticos; volumen efectivo y cabida permanecen indeterminados.

## [2026-09-24] ftd | primer replay real de nesting de QTanque
- Se releyó en `id-04`, sin escrituras, la entrega de salida completada `M_InOut_ID=1056427` del tenant `1000000`: tres líneas TLL con dimensiones cargadas, una unidad cada una y 41 kg de peso maestro. El test local `RealShipmentReplayTest` pasó y preservó los tres IDs de línea; produjo una cadena 700→370 más un 370 independiente, dos huellas estructurales.
- `id-04` carece de tablas TMS de ruta, vehículo y nesting. El alcance de test usa ruta/vehículo sintéticos y la fecha de movimiento; no demuestra volumen efectivo ni cabida. Consulta, test y límites quedan en `02-DOCS/wiki/ftd/qtanque-family-physical-data.md`.

## [2026-09-24] ftd | dimensiones por familia en GRUPO QTANQUE (`id-04`)
- Con autorización del usuario para cargar datos físicos por `M_Product.Classification` y usar diámetro del cuerpo sin tapa, se poblaron `ShelfWidth`, `ShelfHeight` y `ShelfDepth` en 73 variantes de 18 familias del tenant `AD_Client_ID=1000000`. Una simulación con `ROLLBACK` y la transacción final con `COMMIT` verificaron exactamente 73 filas; una conexión nueva confirmó 74 productos terminados dimensionados, incluido el piloto previo. `Weight`, `Classification` y `Volume` se conservaron.
- Se excluyeron el piloto ya cargado y `CON-0120-60` (`PRUEBA CODIGO CONTENEDOR`, peso cero). El SQL reproducible, la tabla de familias y el plan de replay están en `02-DOCS/wiki/ftd/qtanque-family-physical-data.md`. Candidato de prueba real: entrega completada `1056427`, con dos TLL-0370 y un TLL-0700; se espera dos huellas estructurales y 41 kg, mientras volumen efectivo y cabida vehicular permanecen indeterminados.

## [2026-09-24] ftd | piloto físico de `TLL-0370-00` en `id-04`
- Se cargaron únicamente los cuatro valores físicos faltantes del producto `M_Product_ID=1000221` mediante una transacción SQL con guardas del estado inicial: ancho 485 mm, alto 2010 mm, profundidad 485 mm y volumen de caja exterior 0,47280225 m³. El peso de 12 kg se conservó. Una conexión nueva confirmó la persistencia exacta.
- Los cinco campos figuran activos y visibles en la ventana Product. Falta captura en esa interfaz: las cuentas de demostración publicadas por `id-04` fueron rechazadas. El SQL ejecutado y la evidencia están en `02-DOCS/wiki/ftd/product-physical-data-pilot.md`.

## [2026-09-23] continuidad | prueba autenticada del mapa
- En `id-03-tms-map-test`, Playwright CLI abrió el mapa con `SuperUser` y `TMS Dispatch Clone Test` sobre el bundle `12.0.0.202609221613`. Mostró 12 pedidos, uno histórico. La tabla seleccionó `IQP-MAP-02`; `Ctrl` + clic alternó `IQP-MAP-06` y el rectángulo lo añadió. El histórico gris no se añadió al pulsarlo ni al rodear su punto; la selección persistió al filtrar.
- PostgreSQL confirmó después dos entregas completadas, 12 pedidos de prueba, 11 pendientes y uno sin saldo. Capturas y detalle en `02-DOCS/wiki/ftd/native-dispatch-map-requirement.md` y `output/playwright/`. Siguen pendientes `DR`/`IP` visible, reverso, expiración real del día 8, `Cmd` en macOS, volumen mayor de 500 y paridad de señales.

## [2026-09-22] continuidad | mapa de pedidos y despachos
- Se recuperó acceso administrativo al clon usando la membresía existente de `aguerrave` en `incus-admin`. Una consulta PostgreSQL de solo lectura concilió `M_InOut` 500000/500001: cada entrega mueve 1 unidad, `C_OrderLine.QtyDelivered` avanzó `0→1→2` y `AD_ChangeLog` conserva las dos secuencias `DR→IP→CO`.
- El JAR recompilado con `HistoryWindow` se instaló como revisión `r7`; Felix confirmó el bundle 406 `ACTIVE` en versión `12.0.0.202609221613` y una instancia habilitada de `TMSFormFactory`. La WebUI continuó respondiendo HTTP 200. Falta repetir Playwright autenticado del mapa porque las variables de la cuenta de ensayo no están cargadas en esta sesión.
- Tras reiniciar iDempiere solo en el clon, el bundle 406 y `TMSFormFactory` volvieron a estar activos. Playwright de acceso al clon pasó `1/1`; PostgreSQL encontró 12 pedidos de prueba, 11 pendientes y `IQP-MAP-01` con saldo cero. `HistoryWindowSmoke` pasó con el JAR actualizado. La prueba autenticada del mapa sigue pendiente.
- En el clon aislado `id-03-tms-map-test`, el mapa mostró 12 pedidos sintéticos de distintas zonas y antigüedades. Dos entregas reales de `IQP-MAP-01` (una parcial y otra final) dejaron el pedido sin saldo; apareció gris y no seleccionable. La suite Playwright pasó (3/3) y el plugin compiló sin conexión.
- Pendiente prioritario: repetir Playwright autenticado sobre el bundle actualizado y continuar con los ensayos `DR`/`IP`, reverso y día 8.
- Verificar en vivo el límite de siete días calendario de Caracas, los estados `DR`/`IP`, la reversa y reaparición con saldo, la selección con Ctrl/Cmd y rectángulo, permisos y volumen mayor de 500 pedidos. La ventana de siete días ya pasó una prueba Java de límites, pero el JAR nuevo aún no está desplegado.
- No ejecutar directamente `ui-tests/fixtures/predispatch-map-demo-rollback.sql`: ahora detiene el borrado si hay entregas vinculadas. Preparar una limpieza segura de documentos ERP. Detalle y capturas: `02-DOCS/wiki/ftd/native-dispatch-map-requirement.md` y `02-DOCS/wiki/ftd/evidence/`.

## [2026-09-21] continuidad | pendientes para la próxima sesión
- Decidir la prioridad: implementar el primer ciclo SDD de nesting seguro, cuyo análisis documental pasó pero cuyo código sigue pendiente, o continuar el replay físico V2. Son trabajos separados.
- Replay físico: para `1006760`, las 300 ORI-550 forman 8 pilas, pero la cabida en el Kodiak sigue indeterminada. Solicitar ancho y alto útiles, carga admisible, altura de pilas, holguras/amarres, acceso y número de vehículos o viajes; luego evaluar `1006489`.
- Conciliar con Operaciones la identidad y el largo útil del vehículo de `1006438`: los 8 m documentados corresponden a `1006200`. Confirmar también si la exclusión de los clientes `1005593` y `1000330` es solo del benchmark o una regla V2.
- Pruebas del plugin: `id-03` respondió y pasó una prueba Playwright de acceso sin login (1/1). Falta desplegar una función del plugin y definir una cuenta de ensayo para probarla; no usar `id-04` como base desechable porque contiene una copia de datos de cliente.
- La skill `e2e-testing` quedó instalada para Codex y Cursor; recargar el asistente antes de usarla. No se ejecutaron cambios de código productivo, migraciones ni pruebas que escriban datos en esta continuidad.

## [2026-09-21] ftd | Incorporated `feature/dd-product-nesting`
- Checked out and tracked the approved commit without executing or modifying product code.
- Preserved the harness and 344-file isolated context; hook, Git integrity, and `rsc doctor` passed.

## [2026-09-21] maintenance | autofixes: 0, new See Also: 0, gaps detected: 3

## [2026-09-21] ingest | Harness bootstrap knowledge map
- Created: Project Overview
- Created: V1 Baseline and Invariants
- Created: V2 Requirements and Gap
- Created: V2 Physical Implementation Plan
- Created: Physical Nesting Model
- Created: Technical Architecture
- Created: MapView Plugin Reference
- Created: Operational Tooling
- Created: Workspace Control Plane

## [2026-10-13] ftd | evidencia de cargas y conciliación de transferencias
- Se preservaron 26 fotos con SHA-256 y origen. La guía 1520 se vinculó a movimientos 1010812/1010814: 42 unidades documentales por etapa, pero 28 unidades/625 kg del viaje tras excluir 14 pupitres por confirmación del operador. La guía 1517 figura en 1010807/1010813: 103 unidades/1.803 kg por etapa, incluyendo seis TLL-1060 negros. El predespacho 1007231 concilia 13 unidades/587 kg usando QtyDispatched. Solo lectura ERP verificada. Medidas útiles fijas por placa y cadena TLL reafirmadas; hipótesis de grupos, dimensiones faltantes y discrepancia fotográfica 04R DBD/JAC A89EO19 quedan explícitas. [Dossier](ftd/dispatch-photo-evidence-20261013.md).

## [2026-10-13] evidencia | aclaración de referencia fotográfica
- El operador confirmó: «04R DBD eso fue ejemplo de carga de camiones, como referencia». G2-01 y G3-05 se clasifican como ejemplos generales; se retira la asociación pendiente al JAC A89EO19. El dossier conserva hipótesis y límites físicos sin convertirlos en hechos observados.

## [2026-10-13] ftd | simulación de nesting conectada al mapa
- En feature/dd-product-nesting, con confirmación del propietario, la revisión temporal reconsulta pedidos y líneas autorizadas y muestra grupos simulados por pedido/almacén. Reusa el conteo de cadenas sin inventar entrega o vehículo. 43 tests, smokes previos y compilación/paquete OSGi iDempiere 12 pasaron; el verificador global mantiene el fallo previo de integridad raw. Sin despliegue, ERP writes ni commits. [Evidencia](ftd/map-nesting-integration.md).

## [2026-10-14] ftd | nesting instalado y probado en el clon
- Con autorización del propietario, bundle 406 actualizado a 12.0.0.202610132027 en id-03-tms-map-test. UI autenticada del 13/10 mostró 3 y 7 grupos simulados de GardenWorld sin ahorro indebido; prueba binaria sintética pasó QTanque 25→9 y GardenWorld 25→25. Reinicio del clon del 14/10 conservó bundle/fábrica ACTIVE y HTTP 200. Documentos y QtyDelivered del fixture idénticos antes/después; id-03 original sin cambios. Capturas, control de métricas y restauración r10 en [registro](ftd/map-nesting-clone-verification.md).
# 2026-10-14 — Placa y selección temporal de vehículo

Campo `LicensePlate` independiente del código interno, normalizado y único por cliente; selector autorizado con reconsulta de identidad. Migración y plugin aplicados exclusivamente a `id-03-tms-map-test`. 48 pruebas pasaron; UI validada con vehículos ficticios luego eliminados, exclusión por cliente/actividad/placa y rechazo de selección obsoleta. Reinicio, bundle activo y HTTP 200 verificados. [Evidencia y operación](ftd/map-vehicle-selection.md).

# 2026-10-14 — Flota reportada registrada en el clon

AO1BA3A, A89EO19 y 55EJAG registrados exclusivamente en `id-03-tms-map-test`, GardenWorld 11. Medidas por placa en centímetros: 600×240, 654×236 y 1270×240. Altura/carga/volumen desconocidos conservados NULL; disponibilidad pendiente. Ensayo reversible, repetición sin duplicados y rechazo de discrepancia pasaron. Selector y propuesta con FVR comprobados; no valida cabida ni asigna viaje. [Detalle y evidencia](ftd/register-reported-fleet-clone.md).

# 2026-10-14 — Plataforma 04R-DBD identificada

El operador confirmó placa propia del remolque asociado a 55EJAG. En el clon se separó `DD_TransportUnit` 04RDBD con 1270×240 cm; el chuto 55EJAG quedó sin dimensiones de carga. Relación de flota reportada, sin crear viaje ni asumir combinación permanente. Ensayo reversible, rechazo de datos discrepantes, repetición sin duplicados y conteos comerciales comprobados. [Evidencia](ftd/chuto-platform-identity.md).

# 2026-10-14 — Ambas placas y medidas en propuesta temporal

El operador confirmó relación fija 55EJAG–04R-DBD. La propuesta ahora muestra vehículo, plataforma y 12,70×2,40 m desde registros autorizados. Reconsulta y rechaza cambios de asociación; no valida cabida. 52 pruebas pasaron, plugin compilado e instalado solo en el clon, UI y restauración tras prueba de asociación obsoleta verificadas. [Evidencia](ftd/proposal-platform-details.md).

