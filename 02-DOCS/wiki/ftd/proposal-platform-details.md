---
branch: feature/dd-product-nesting
status: in-progress
---
# Plataforma y medidas en la propuesta temporal

## Intent

Mostrar el vehículo seleccionado y su plataforma asociada con placas independientes y medidas por plataforma. El usuario confirmó combinación fija 55EJAG–04R-DBD y autorizó mostrar ambos en la propuesta.

## Scope

- Continuar en la rama actual de la función de propuesta temporal.
- Consultas de solo lectura con permisos de tabla, organización, cliente y registro.
- Reconsultar asociación, actividad e identidad antes de revisar la propuesta.
- Mostrar medidas actuales con unidad explícita, sin interpretar ausencia como cero ni validar cabida.
- No guardar viajes, reservas ni cambios en disponibilidad. No extender medidas del remolque al tractor.
- La combinación fija proviene del operador; la UI mostrará la asociación del maestro vigente y no una asociación deducida por modelo.

## Checklist

- [x] Probar visualización, unidades, datos ausentes, HTML y asociación cambiada.
- [x] Implementar consulta autorizada y resumen de plataforma.
- [x] Compilar y verificar en el clon 55EJAG–04R-DBD con 12,70 × 2,40 m.

## Evidence

Clon vigente: chuto DD_Vehicle 1000002, plataforma DD_TransportUnit 1000000, relación DD_TransportAssignment 1000000. Medidas de la plataforma 1270 × 240 cm; chuto sin medidas de carga. El selector anterior solo consulta y muestra el vehículo.

### Resultados

- `PlatformCandidate` mantiene identidad, cliente y medidas de la plataforma. Conversión exacta de cm a m; conserva precisión adicional y muestra al menos dos decimales. Datos ausentes, cero o unidad desconocida no producen medidas completas. Unidades distintas de cm/m se muestran sin conversión con su símbolo original.
- `VehicleCandidate` conserva el constructor previo e incorpora plataforma opcional del mismo cliente. El resumen escapa HTML después de construir el texto completo y no altera cantidades de nesting.
- La consulta de flota primero comprueba permiso de lectura de las tablas involucradas. La consulta de asociaciones usa parámetros, filtro explícito de cliente, estado activo y permisos de organización/registro mediante MRole. Solo consulta IDs de vehículos previamente autorizados. No muestra plataformas inaccesibles. Múltiples asociaciones autorizadas producen un error, sin elegir una arbitrariamente.
- La revisión reconsulta el maestro y compara ID y placa de la plataforma. Cambio o desaparición de asociación detiene la revisión; dimensiones modificadas de la misma plataforma se muestran desde la lectura actual.
- Suite JUnit: **52 pruebas, cero errores/fallos**. Nuevas pruebas cubren unidades exactas, medidas ausentes/cero, unidad ausente, texto HTML, tenant distinto, asociación perdida/cambiada y medidas actualizadas. [Reportes y validación](evidence/proposal-platform-validation-20261014.json). Las pruebas nuevas fallaron primero por ausencia de la clase, luego pasaron.
- Compilación completa offline exit 0. JAR `output/proposal-platform-20261014/org.idempiere.tms-12.0.0-SNAPSHOT.jar`, SHA-256 `90ed79fbf5715c661705d6a460084f6014f5876dd24f1baba69a7a4eaba4423f`. Instalado solo en el clon, bundle 406 ACTIVE `12.0.0.202610141827`, sin reporte de resolución; FormFactory y ModelFactory activos.
- UI del rol `TMS Dispatch Clone Test`: pedido ficticio IQP-MAP-02, 3 unidades comerciales y 3 grupos simulados, vehículo 55EJAG, plataforma 04RDBD y «12,70 × 2,40 m». Conservó «No valida cabida». [Captura inspeccionada](../../attachments/proposal-platform-clone-20261014/proposal-platform.png).
- Prueba de revalidación: se desactivó temporalmente solo la asociación de este clon; la propuesta fue rechazada. Se restauró mediante bloque de limpieza y se verificó `IsActive=Y`. Al actualizar, la propuesta volvió a mostrar la plataforma. La primera búsqueda de texto exacto inmediata devolvió cero; la captura posterior y una espera explícita confirmaron el aviso de rechazo.
- Conteos comerciales finales iguales al control: 20 pedidos, 11 entregas, 1 movimiento. Navegador y consola cerrados; ningún viaje o reserva creados. No hubo despliegue a producción ni commit Git.

### Operación

Requiere las migraciones explícitas de Placa en vehículo y plataforma y los registros del maestro. La relación fija se documenta como confirmación del operador, sin hardcodear placas en el código. Para revertir el plugin del clon puede volver al archivo anterior `org.idempiere.tms_12.0.0-map-vehicle-20261014.jar` mediante update del bundle 406; conservar datos y columnas aditivas. Reversión no ejecutada porque las comprobaciones pasaron.

## Next

Obtener datos pendientes de disponibilidad y capacidad admisible antes de automatizar asignaciones o evaluar cabida. El resumen actual muestra identidad y dimensiones; no autoriza una carga.
