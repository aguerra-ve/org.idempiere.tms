# Flota reportada en el clon TMS

Registra tres vehículos reportados por el operador únicamente en `id-03-tms-map-test`, cliente GardenWorld 11. Usa el acceso local ya configurado de Incus/PostgreSQL; no necesita archivos de credenciales.

`register.sql` comprueba marcadores del clon, usa sus secuencias reales y rechaza sobrescribir registros discrepantes. Repetirlo no duplica las mismas placas. Conserva valores físicos desconocidos como NULL y disponibilidad pendiente (`NAV`). Las medidas se guardan en centímetros con la unidad existente.

Antes de aplicar, ensayar sustituyendo el COMMIT final por ROLLBACK dentro del clon. Ejecutar con `psql -X -v ON_ERROR_STOP=1`; un error revierte toda la transacción. Nunca ejecutar este registro en producción. La copia al cliente de demostración permite revisar el selector; no registra flota en el ERP de QTanque.

Ver [alcance y evidencia](../../02-DOCS/wiki/ftd/register-reported-fleet-clone.md).

## Plataforma del chuto

La confirmación posterior identifica la plataforma como 04R-DBD. `register.sql` conserva el chuto 55EJAG sin dimensiones de carga. La corrección `separate-platform.sql` requiere primero `sql/add_dd_transportunit_licenseplate.sql`; aplicar ambos en una sola transacción del clon. Registra la plataforma con 1270 × 240 cm y una relación de flota reportada, no un viaje ni una combinación permanente. Rechaza medidas anteriores discrepantes y asociaciones activas diferentes. Ver [corrección e identidad](../../02-DOCS/wiki/ftd/chuto-platform-identity.md).
