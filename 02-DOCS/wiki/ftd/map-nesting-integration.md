---
author: Alejandro Guerra
branch: feature/dd-product-nesting
status: in-progress
---
# Integración del anidado en la propuesta temporal del mapa

## Intent
Conectar las líneas reales de los pedidos seleccionados con el resumen físico de la propuesta temporal, usando únicamente reglas confirmadas y distinguiendo simulación estructural de capacidad vehicular validada. Solicitud del propietario: «sí» a retomar esta integración, tras cerrar el dossier fotográfico.

## Scope
Lectura de líneas de pedido bajo los permisos del usuario, cantidades pendientes y productos; resumen temporal y pruebas. Sin escrituras ERP, creación de documentos, asignación automática, commits, remotos ni despliegue. La consulta se revalida al generar el resumen; la selección histórica no participa.

No cambiar el contrato operativo de SourceAdapter: un pedido no identifica una entrega física. No construir DeliveryKey(MMS, orderId), candidateVehicleId ficticio ni fecha/ruta inventadas para hacer pasar NestingPolicy. Cualquier simulación previa a entrega/vehículo debe tener un contrato propio explícito y no afirmar cabida.

## Checklist
- [x] Inspeccionar flujo mapa → propuesta → resumen y contratos del motor.
- [x] Elección de rama recibida: el propietario confirmó feature/dd-product-nesting.
- [x] Definir la entrada de simulación provisional y su separación del contrato operativo.
- [x] Escribir y observar pruebas fallando para lectura/alcance, cálculo y estados indeterminados.
- [x] Implementar la lectura autorizada y conectar el resumen temporal sin persistencia.
- [x] Verificar pruebas, compilación y diferencias; documentar límites.

## Evidence
Inspección del 13/10/2026:
- PredispatchMapForm genera TemporaryRouteProposal.from(candidates, selectedOrderIds).
- OrderCandidateRepository agrega cantidades por pedido; la propuesta no recibe líneas ni productos.
- ProposalLoadSummary.pending mantiene las unidades físicas y vehículo sin evaluar.
- SourceAdapter solo resuelve entregas MMS/MMR, no pedidos SOO.
- DeliveryKey solo permite MMS/MMR y LoadScope exige candidateVehicleId positivo.
- La especificación aprobada exige no inventar identidad de entrega ni mezclar candidatos.
- Hay cambios locales ajenos ya presentes; se conservarán. Rama observada: feature/dd-product-nesting.
- Elección de rama confirmada por el propietario en este chat. FTD exige preguntar antes de cada cambio de código; documentación puede prepararse mientras tanto.

## Next
Integración local compilada y probada. La fase posterior de despliegue/prueba del clon fue autorizada y completada el 13–14/10/2026; [evidencia instalada](map-nesting-clone-verification.md). Selección real de vehículo y validación de cabida permanecen fuera de esta entrega.

## Contrato de la simulación

Leer cantidades pendientes de líneas y Classification bajo permisos del rol y almacenes permitidos. Reconsultar elegibilidad de los pedidos antes del resumen. El total de líneas debe coincidir por pedido con el total reconsultado; si cambió o falta información, mostrar pendiente y motivo. Duplicados, otro tenant/pedido, cantidades fraccionarias o desbordamiento no producen un número parcial.

Simulación conservadora por pedido y almacén: no mezclar pedidos ni almacenes para obtener ahorro. Reutilizar el conteo de cadenas del motor operativo, sin fabricar DeliveryKey, ruta, fecha o candidato. Solo secuencia TLL confirmada; clasificación ausente/no soportada permanece independiente. Unidades físicas se etiquetan como simulación provisional; vehículo no seleccionado y cabida no validada. No se asocian las fotos generales a pedidos actuales.

## Implementación y resultados

- PredispatchMapForm usa TemporaryProposalReviewer: reconsulta candidatos autorizados y rechaza selecciones desaparecidas/históricas antes de leer líneas. Los errores de base de datos no muestran un resultado físico ni SQL en el diálogo.
- OrderCandidateRepository lee líneas pendientes con parámetros enlazados para IDs, tenant explícito en cabecera/línea/producto, MRole.addAccessSQL en modo RO y la política de almacenes del rol en cabecera y línea. Revisión de fuente iDempiere 12: addAccessSQL aplica acceso de tablas/registros a las tablas de la consulta; no se sustituye por la conexión MCP. La ejecución real del repositorio bajo un usuario WebUI queda para el clon.
- ProposalNestingPreview concilia totales por pedido, rechaza duplicados, tenant/identidad inválidos, cantidades fraccionarias y desbordamiento. Si falta una línea o cambia una cantidad, todo el resultado permanece pendiente.
- ChainGrouping comparte el conteo de cadenas entre el motor operativo y la simulación sin crear identidades ficticias. Usa BigInteger para no expandir miles de millones de unidades; las identidades operativas existentes permanecen en NestingPolicy.
- La regla TLL confirmada se acota a GRUPO QTANQUE (AD_Client_ID=1000000). Otros tenants no heredan ahorro aunque compartan Classification. ORI/TCL, clasificación ausente o tamaños sin regla quedan independientes. No se hacen nuevas inferencias físicas desde fotografías.
- El resumen conserva la cantidad comercial, etiqueta el número como «simulación por pedido y almacén», mantiene «vehículo no seleccionado» y «No valida cabida». Las razones se escapan para el diálogo HTML.

### Evidencia de prueba

- RED conductual inicial: [11 pruebas, 7 fallos](evidence/map-nesting-red-20261013.log), porque el resumen solo devolvía pendiente. GREEN tras implementar conteo y estados.
- RED de reconsulta: [4 pruebas, 3 fallos](evidence/map-nesting-review-red-20261013.log), porque el flujo anterior no detenía selección desaparecida ni propagaba errores de lectura.
- Triangulación RED multitenant: [1 fallo esperado](evidence/map-nesting-tenant-red-20261013.log), cliente distinto recibía 1 grupo en vez de 2. GREEN al limitar la evidencia al cliente confirmado.
- [Validación consolidada](evidence/map-nesting-validation-20261013.json): **43 tests, 0 fallos, 0 errores** (27 existentes + 16 nuevos). Incluye aislamiento por pedido/almacén, fracciones, totales cambiantes, IDs duplicados, tamaños no confirmados y cantidades grandes.
- Smoke tests anteriores TemporaryRouteProposalSmoke y HistoryWindowSmoke pasaron: selección/escape/estado temporal y límites del histórico conservados. Detector de secretos y su self-test pasaron.
- [Consulta de esquema sin filas](evidence/map-nesting-schema-20261013.sql) ejecutada en conexión IQP de solo lectura: columnas y joins válidos; no prueba los permisos WebUI ni cuenta como prueba visual. [Clasificaciones reales](evidence/map-nesting-classification-20261013.json) coinciden con las claves TLL esperadas.
- Compilación Java completa de 180 fuentes contra bibliotecas locales iDempiere 12 pasó. Empaquetado **Tycho/OSGi completo pasó con código 0**, bundle 12.0.0.202610132027. Las 180 fuentes del staging coinciden byte a byte con el workspace.
- JAR: `output/map-nesting-20261013/org.idempiere.tms-12.0.0-SNAPSHOT.jar`, 2.652.274 bytes, SHA-256 `da71ad59cc45b9b5c1fd8a6b44f29e673d42bacdfb412c52506590381c3202b3`.

### Entorno y límites de la verificación

El staging previo en /tmp ya no existía. Se usó `/tmp/iqp-map-nesting-build`, padre 12.0.0-SNAPSHOT de la caché local, target Directory con 403 bundles copiados de la imagen oficial local 12-release, JavaSE-17 y los requisitos extra org.eclipse.core.runtime/org.eclipse.equinox.common del padre. El POM activo no se cambió. Docker solo copió bibliotecas; no se modificaron contenedores ni datos. Maven operó offline; la escritura del bloqueo Tycho de la caché necesitó la autorización del entorno.

`./tests/nesting/verify.sh` ejecutó las pruebas con éxito y luego falló por la diferencia **preexistente** de `raw/contexto-v2/docs/v2/GAP_MATRIX.md` ya documentada en el dossier del 13/10/2026. [Salida](evidence/map-nesting-verify-20261013.log). No se altera raw ni su línea base para convertir ese fallo en pase. Los detectores de secretos se ejecutaron por separado.

La entrega local inicial no desplegó la pantalla; posteriormente el propietario autorizó la prueba y se completó en el clon, según [registro instalado](map-nesting-clone-verification.md). No hay registros de flota creados, aceptación ERP, persistencia, asignación de vehículo, nuevas reglas ORI/TCD ni demostración de cabida. Los pedidos con entregas en preparación quedan pendientes en esta simulación.

Revisión de whitespace del cambio propio: los 12 archivos de código/pruebas/configuración de esta entrega no contienen espacios al final. `git diff --check` global informa CRLF en MANIFEST.MF, build.properties y MapViewForm.java previamente modificados; no se normalizan esos archivos ajenos al cambio.
