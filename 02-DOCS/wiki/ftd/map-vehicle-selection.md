---
branch: feature/dd-product-nesting
status: in-progress
---
# Selección de vehículo por placa en la propuesta temporal

## Intent

Permitir elegir una unidad concreta para revisar la propuesta temporal del mapa. La placa identifica la unidad; el modelo no determina sus medidas útiles.

## Scope

- Selección explícita y opcional de un vehículo autorizado del cliente actual.
- Revalidar identidad, actividad y acceso al revisar la propuesta.
- Mantener el resumen de nesting y la advertencia de cabida pendiente.
- No crear viajes, asignaciones ni documentos comerciales.
- Definir primero dónde se guarda la placa. No interpretar automáticamente el código o nombre como placa.
- No cargar vehículos reales desde las fotos ni trasladar medidas entre unidades.

## Checklist

- [x] Consultar esquema y vehículos del clon sin modificar datos.
- [x] Confirmar rama para el cambio y representación de placa. El usuario autorizó ejecutar la recomendación: campo propio `LicensePlate`, rama actual.
- [x] Implementar consulta autorizada y selección temporal; comprobar aislamiento entre clientes y selección obsoleta.
- [x] Compilar el plugin y verificar la interfaz en el clon.

## Evidence

Consulta de solo lectura del 14/10/2026 en `id-03-tms-map-test`: existe `adempiere.dd_vehicle`, pero no devolvió vehículos. Sus columnas no incluyen placa explícita. `Value` es la clave de búsqueda; `Name` es un identificador alfanumérico, según el diccionario. Las columnas físicas incluyen `TransportLength`, `TransportWidth`, `TransportHeight` y `Dimension_UOM_ID`; su existencia no confirma valores ni medidas útiles.

El código generado declara las mismas dimensiones. La consulta no modificó datos. La búsqueda inicial sin esquema no encontró la relación por el contexto de conexión; se corrigió usando `adempiere.dd_vehicle`.

### Implementación y comprobaciones del 14/10/2026

- `sql/add_dd_vehicle_licenseplate.sql`: migración transaccional y repetible. Añade `LicensePlate` opcional, formato canónico de 1–20 letras/números en mayúscula y unicidad por `AD_Client_ID`. Registra columna y campo «Placa» en las pestañas existentes de vehículos. Usa las secuencias reales del diccionario, sin IDs fijos. Se ensayó con `ROLLBACK`, se aplicó solo al clon y se repitió sin duplicar metadatos.
- `MDDVehicle` expone la placa y normaliza espacios, guiones y minúsculas antes de guardar. `Value` y los modelos generados permanecen independientes. No se copian medidas desde tipos de vehículo ni desde fotos.
- El selector lee vehículos activos con placa del cliente actual. Comprueba acceso a tabla y aplica filtros de organización y registro del rol. La propuesta reconsulta los vehículos; si cambia la placa, se desactiva el registro o desaparece su acceso, no produce la propuesta con esa selección.
- 48 pruebas JUnit, cero fallos/errores. Incluyen normalización, valores inválidos, tenant incorrecto, selección ausente/duplicada, placa modificada, HTML escapado y conservación del resultado de nesting. La compilación previa de estas pruebas falló al faltar las clases nuevas; luego pasó la suite completa.
- `tests/mapview/vehicle-plate-db-smoke.sql` pasó: duplicados del mismo cliente rechazados; formato inválido rechazado; igual placa entre clientes y múltiples placas ausentes aceptadas. Todos sus registros se revirtieron. La comprobación de llaves foráneas no es objetivo de ese ensayo.
- En la UI se crearon temporalmente cuatro vehículos rotulados `IQP TEST ONLY`, con IDs 90009001–90009004. Solo apareció `TEST001`: se excluyeron otro cliente, registro inactivo y placa ausente. El pedido de prueba `IQP-MAP-02` mostró 3 unidades comerciales, 3 grupos simulados y `TEST001`, manteniendo «No valida cabida». Al desactivar ese vehículo, la revisión mostró un aviso y no la propuesta.
- [Captura inspeccionada](../../attachments/map-vehicle-clone-20261014/proposal-vehicle.png). SHA-256: `7cba8c79dca0fac246f67949e84137e4553e8747317826f7e937f17d22759193`.
- Los cuatro vehículos y su tipo ficticio fueron eliminados al terminar. La comprobación posterior confirmó cero vehículos y cero tipos de prueba. La primera lectura de limpieza usó un nombre sin esquema tras el `COMMIT` y falló; la eliminación ya se había confirmado. Se comprobó de nuevo con nombres calificados. Pedidos/entregas/movimientos del cliente 11: 20/11/1, iguales al control anterior.
- JAR compilado y desplegado: `output/map-vehicle-20261014/org.idempiere.tms-12.0.0-SNAPSHOT.jar`, SHA-256 `796d3da571e39de7ff858b51c2a3a4310848f1758e2d2e4bbe08da722adf3c42`. Bundle 406 `ACTIVE`, versión `12.0.0.202610141238`; `TMSFormFactory` y `TMSModelFactory` activos. Reinicio del clon: servicio activo, HTTP 200 y bundle persistente.
- No despliegue a producción, commits ni cambios en documentos comerciales. No se ingresaron placas reales.

### Operación y reversión

Aplicar la migración explícitamente en la instancia elegida antes de instalar el JAR; reiniciar iDempiere para renovar el diccionario. Sin migración, el mapa conserva los pedidos y muestra fallo de consulta de flota. El selector no convierte un fallo de consulta en un vehículo válido.

La migración no se ejecuta automáticamente al activar el plugin. El respaldo del esquema previo del clon está en `output/map-vehicle-20261014/vehicle-schema-before.sql`. Para revertir únicamente el plugin del clon, volver al JAR anterior `org.idempiere.tms_12.0.0-map-nesting-20261013.jar` mediante `update 406 file:/opt/idempiere-server/plugins/org.idempiere.tms_12.0.0-map-nesting-20261013.jar`. Mantener la columna aditiva para conservar cualquier dato cargado posteriormente; no borrar placas como parte de esa reversión. Reversión no ejecutada porque las comprobaciones pasaron.

## Next

Registrar la flota real por placa en la instancia autorizada, confirmando unidad y plataforma para chuto/remolque. Las medidas útiles permanecen por vehículo. La selección temporal no guarda asignaciones ni verifica cabida física.
