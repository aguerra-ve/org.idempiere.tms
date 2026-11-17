---
author: Alejandro Guerra
branch: feature/dd-product-nesting
status: in-progress
---
# Despliegue y comprobación de nesting en el clon

## Intent
Desplegar el JAR validado de la integración y verificar la propuesta temporal en una sesión autenticada del clon. Autorización del propietario: «sí» a la prueba en el clon.

## Scope
Solo id-03-tms-map-test, WebUI 127.0.0.1:9005. Mantener el bundle anterior r10 para restauración. No tocar id-03 original, id-02/nómina, id-04 ni IQP operativo. No modificar cuentas/contraseñas existentes, .env ni secretos. No crear documentos ERP para probar una vista de solo lectura.

## Checklist
- [x] Confirmar identidad, servicios y dataset sintético del clon.
- [x] Respaldar r10 y verificar SHA-256 del artefacto nuevo antes de actualizar.
- [x] Actualizar bundle 406 y comprobar estado ACTIVE y fábrica de formularios.
- [x] Abrir mapa autenticado, seleccionar pedidos y verificar el resumen temporal con captura.
- [x] Comprobar la reducción TLL contra el JAR real sin escribir datos operativos.
- [x] Verificar ausencia de documentos nuevos y registrar restauración y limitaciones.

## Evidence
13/10/2026: Incus muestra id-03 y id-03-tms-map-test RUNNING. Dentro del clon hostname=id-03-tms-map-test; postgresql.service e idempiere.service activos. Base: System (0) y GardenWorld (11), rol TMS Dispatch Clone Test (200002, cliente 11), 12 pedidos IQP-MAP. r10 SHA-256 dadb3209bd2022780a7eb3cade391aa6b30822ac5b17ce224e7b51a4c93f3062. El cliente GardenWorld no debe heredar automáticamente reglas empresariales de QTanque (1000000).

## Next
Prueba del clon completada. Mantener este bundle para revisión; una evaluación real de vehículo/plataforma y cabida constituye una tarea posterior.

## Resultado del despliegue y prueba

- El 13/10/2026 se actualizó el bundle **406** mediante la consola local (puerto 12612, solo loopback del clon). Versión **12.0.0.202610132027**, estado **ACTIVE**; TMSFormFactory habilitada con una instancia activa. `diag 406` no reportó dependencias sin resolver.
- SHA-256 del JAR en destino: `da71ad59cc45b9b5c1fd8a6b44f29e673d42bacdfb412c52506590381c3202b3`, idéntico al validado. Archivo: `/opt/idempiere-server/plugins/org.idempiere.tms_12.0.0-map-nesting-20261013.jar`.
- Acceso autenticado con la cuenta de demostración publicada por la propia pantalla del clon y rol **TMS Dispatch Clone Test**, tenant GardenWorld (11). No se abrió ni modificó .env; no se cambiaron contraseñas ni cuentas. No se exportó estado de autenticación.
- El mapa mostró **11 pedidos pendientes**, sin históricos visibles: los 12 pedidos de fixture siguen en la base, y el historial de la entrega completada de septiembre ya no pertenece a la ventana de siete días. No se rejuvenecieron ni alteraron fixtures para forzar conteos del ensayo anterior.
- Selección de **IQP-MAP-02**: 3 unidades comerciales → **3 grupos simulados**; [captura](../../attachments/map-nesting-clone-20261013/proposal-single.png).
- Selección de **IQP-MAP-02 + IQP-MAP-03**: 3+4=7 unidades → **7 grupos simulados**; [captura](../../attachments/map-nesting-clone-20261013/proposal-multiple.png). SQL de solo lectura confirmó cantidades 3 y 4 y Classification NULL. El aislamiento de GardenWorld y la falta de regla impiden descontar nesting.
- La pantalla muestra «simulación por pedido y almacén», «vehículo candidato: no seleccionado» y «No valida cabida». El diálogo se cierra y conserva la selección; inicialmente el botón estaba deshabilitado sin selección.
- [Mapa con selección](../../attachments/map-nesting-clone-20261013/map-selection.png) verificado visualmente: puntos, selección azul, tabla y atribución OpenFreeMap visibles. La consola del navegador registró cero errores y avisos WebGL/estilo; no impidieron la prueba.
- **Prueba binaria en el clon:** `java -cp /opt/idempiere-server/plugins/org.idempiere.tms_12.0.0-map-nesting-20261013.jar /tmp/iqp-nesting-tools/ProposalNestingRuntimeSmoke.java` pasó con código 0: **QTanque 25→9** y **GardenWorld 25→25**, sin vehículo ni cabida afirmada. La entrada es sintética y el programa no consulta ni escribe en ERP; no es evidencia de una carga física real ni de reducción TLL en datos GardenWorld.
- El **14/10/2026** se reinició únicamente `idempiere.service` del clon: servicio active/running, bundle 406 ACTIVE en la misma versión y fábrica de formularios ACTIVE. WebUI HTTP **200**; original id-03 y clon RUNNING. [Salida de consola después del reinicio](evidence/map-nesting-clone-console-20261014.log).
- [Validación consolidada](evidence/map-nesting-clone-validation-20261014.json): antes de la UI, después de la UI y después del reinicio, métricas iguales: **20 pedidos, 11 entregas, 1 movimiento**, 12 pedidos IQP-MAP y suma QtyDelivered del fixture **2**. Se comprueban documentos operativos y cantidades; no se afirma ausencia de sesiones/preferencias o metadata del plugin, que pueden actualizarse por login/activación.

## Restauración preparada

La revisión r10 permanece en `/opt/idempiere-server/plugins/org.idempiere.tms_12.0.0.SNAPSHOT-r10.jar`. También se descargó una copia de la revisión activa previa desde `configuration/org.eclipse.osgi/406/9/bundleFile` a `output/map-nesting-20261013/rollback/bundle-406-before.jar`; su SHA-256 es `dadb3209bd2022780a7eb3cade391aa6b30822ac5b17ce224e7b51a4c93f3062`.

Para restaurar únicamente el clon, abrir su consola local y ejecutar `update 406 file:/opt/idempiere-server/plugins/org.idempiere.tms_12.0.0.SNAPSHOT-r10.jar`; después comprobar `ss org.idempiere.tms`, `diag 406` y `scr:list 406`. Se espera la versión anterior 12.0.0.202609230134. **Restauración no ejecutada**, porque el despliegue y la prueba pasaron. No se borraron otras revisiones.

## Límites conservados

No se asignan vehículos ni se mide cabida, peso admisible o geometría. La reducción 25→9 se verificó con entrada sintética contra el binario; la prueba de UI real del clon usa GardenWorld y conteos independientes. No se despliega este JAR en id-03 original, id-04, nómina ni producción. No se hicieron commits ni se publicaron cambios. El fallo previo de hash raw documentado en la fase local sigue sin alterarse; esta prueba instalada no convierte el verificador global en aprobado.
