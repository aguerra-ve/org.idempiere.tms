---
branch: feature/dd-product-nesting
status: in-progress
---
# Chuto 55EJAG y plataforma 04R-DBD

## Intent

El usuario confirmó el 14/10/2026 que la plataforma asociada al chuto 55EJAG tiene placa propia 04R-DBD. Separar la identidad y corregir la atribución provisional de dimensiones en el clon.

## Scope

- Solo `id-03-tms-map-test`, cliente de demostración 11.
- Chuto en `DD_Vehicle`, plataforma/remolque en `DD_TransportUnit`, relación de flota en `DD_TransportAssignment`.
- Placa de plataforma normalizada `04RDBD`, conservando `04R-DBD` en nombre y procedencia.
- Añadir campo propio `LicensePlate` al diccionario de plataformas; no reutilizar código ni número de transportista como placa.
- Plataforma: largo/ancho útil reportado 1270 × 240 cm. Chuto: largo/ancho de carga NULL; no inferir medidas del tractor.
- No crear viaje, entrega ni asignación de chofer. Mantener disponibilidad y cabida pendientes.
- No atribuir automáticamente las fotos de referencia a la guía 1517: la nueva confirmación identifica la plataforma, no fecha ni composición de cada foto.

## Checklist

- [x] Inspeccionar las tablas existentes y comprobar separación vehículo/plataforma.
- [x] Ensayar la migración y corrección con reversión.
- [x] Registrar plataforma, asociación y corrección sin duplicados.
- [x] Verificar dimensiones, relación, diccionario y documentos comerciales.

## Evidence

El esquema existente contiene `DD_TransportUnit` con dimensiones por unidad, `DD_TransportUnitType` con tipo `TRL` y `DD_TransportAssignment` con referencias a vehículo y plataforma. No hay campo de placa en plataformas. La placa y el vínculo proceden de la confirmación explícita del usuario.

## Resultados observados

- `sql/add_dd_transportunit_licenseplate.sql`: columna propia y campo «Placa», formato canónico y unicidad por cliente. Registro normalizado `04RDBD`; nombre y notas conservan `04R-DBD`. Escribir la placa en formato canónico al usar la ventana de plataformas; esta operación no modifica el modelo Java para normalizar entradas de esa ventana.
- `01-TOOLS/clone-fleet/separate-platform.sql`: corrección limitada a los registros que creó esta sesión. Campo, plataforma y relación se aplicaron juntos dentro de una transacción; primero se ensayaron con ROLLBACK.
- El ensayo negativo detectó una guarda inicial que dejaba pasar una dimensión parcialmente NULL. Se corrigió usando comparaciones `IS NOT DISTINCT FROM`. La copia de reemplazo al mismo archivo del clon falló con `Forbidden`; se conservó y se copió una versión nueva `iqp-separate-platform-v2.sql`, con SHA-256 idéntico al archivo local: `35d68646289d2b92280dbc87bd73c1f33bd89ad810a64a7f00b9ffb8d68b6361`.
- Con la versión corregida, el ensayo de dimensión parcialmente ausente abortó y no conservó cambios. El ensayo correcto pasó; luego la aplicación y la repetición pasaron.
- Chuto: ID 1000002, placa 55EJAG, largo/ancho de carga NULL. Plataforma: `DD_TransportUnit_ID=1000000`, placa 04RDBD, 1270 × 240 cm. Relación de flota: `DD_TransportAssignment_ID=1000000`. No representa un viaje ni garantiza una asociación permanente.
- [Lectura anterior](evidence/chuto-platform-before-20261014.log) y [lectura posterior](evidence/chuto-platform-after-20261014.log). FVR y JAC conservaron 600×240 y 654×236 cm. Conteos finales: 3 vehículos, 1 plataforma, 1 asociación, 20 pedidos, 11 entregas y 1 movimiento.
- Se corrigió el registro general de flota para que 55EJAG mantenga NULL en sus medidas de carga. Su repetición conservó los tres IDs existentes. No se cambió el selector del mapa: sigue seleccionando el vehículo; aún no muestra ni valida el remolque asociado.

## Next

El usuario confirmó después que 04R-DBD trabaja siempre con 55EJAG: combinación fija reportada por el operador. Se implementó la [visualización de ambas placas y medidas](proposal-platform-details.md). Disponibilidad, altura útil y carga admisible siguen pendientes; la relación de flota no crea un viaje.
