---
branch: feature/dd-product-nesting
status: in-progress
---
# Registro de flota reportada en el clon

## Intent

Registrar los tres vehículos confirmados por el usuario para seleccionarlos por placa en la propuesta temporal. Autorización: «sí» a registrar camiones reales y medidas por placa, después de la implementación del selector en el clon.

## Scope

- Destino exclusivo: `id-03-tms-map-test`, cliente de demostración GardenWorld (11). Son datos operativos reportados de QTanque usados como referencia de prueba; no implican registro en el ERP real del cliente 1000000.
- Placas exactas: AO1BA3A, A89EO19 y 55EJAG. No sustituir letras O por ceros.
- Largo/ancho útil reportado: 6,00 × 2,40; 6,54 × 2,36; 12,70 × 2,40 metros, respectivamente. Convertir a centímetros usando la UOM existente del clon.
- Modelo y procedencia en nombre/notas. No guardar choferes como asignaciones permanentes.
- Altura útil, carga admisible, volumen y disponibilidad real desconocidos. Estado operativo no disponible hasta confirmar disponibilidad; el selector permite una revisión de candidato, sin reservarlo.
- 55EJAG identifica el chuto reportado; los 12,70 × 2,40 m son de la plataforma asociada reportada. Falta identificación independiente del remolque. No crear un remolque ficticio ni afirmar dimensiones del tractor.
- No modificar documentos comerciales ni producción. No actualizar placas existentes si difieren de los datos previstos.

## Checklist

- [x] Leer flota, unidades, secuencias y cliente de destino.
- [x] Ensayar registro transaccional con reversión automática. Después del ensayo quedaron cero vehículos y cero tipos.
- [x] Registrar tres vehículos mediante secuencias reales y comprobar repetición sin duplicados.
- [x] Verificar medidas, valores desconocidos, selector y propuesta en el clon.

## Evidence

**Corrección posterior del 14/10/2026:** el usuario confirmó la placa propia de plataforma 04R-DBD. El [registro separado y su verificación](chuto-platform-identity.md) dejan 55EJAG sin dimensiones de carga y 04R-DBD con 1270 × 240 cm. Los resultados siguientes documentan el registro inicial, anterior a esa corrección.

El clon no tiene vehículos ni tipos activos registrados. Secuencias `DD_Vehicle` (1000026) y `DD_VehicleType` (1000037). UOM existentes: Centimeter 200001, kilogram 50001 y litre 50003. No existe UOM de metro detectada. Conversión exacta en centímetros: AO1BA3A 600 × 240; A89EO19 654 × 236; 55EJAG 1270 × 240.

Fuente de dimensiones y placas: confirmaciones del operador registradas en [dossier de carga](dispatch-photo-evidence-20261013.md). No se infieren desde modelo o fotografías. Los pesos de los productos de los viajes no son carga admisible del vehículo.

### Operación y validación

- Operación preparada en `01-TOOLS/clone-fleet/register.sql`; no utiliza credenciales ni toca archivos `.env`. Comprueba cliente GardenWorld, rol de pruebas y pedido IQP-MAP-02. Solo se ejecutó dentro de `id-03-tms-map-test`.
- Se guardaron tres vehículos activos con código interno independiente de la placa: AO1BA3A ID 1000000, A89EO19 ID 1000001 y 55EJAG ID 1000002. Tipo genérico «Camión / chuto — flota reportada» ID 1000000, sin medidas heredadas.
- La repetición devolvió `EXISTING` para los mismos tres IDs. Un ensayo con ancho FVR de 241 cm en vez de 240 fue rechazado: «ya existe con datos diferentes; no se sobrescribe». La transacción abortó y la lectura posterior conservó 240 cm.
- [Lectura de base de datos](evidence/reported-fleet-db-20261014.log): las tres medidas exactas en cm, altura/carga máxima/volumen máximo NULL y estado NAV. NAV representa no disponibilidad para operación mientras falta confirmación, sin impedir la revisión de candidato del selector.
- La UI del rol `TMS Dispatch Clone Test` mostró las tres placas. El pedido ficticio IQP-MAP-02 se revisó con AO1BA3A: 3 unidades comerciales, 3 grupos simulados y placa correcta. Permaneció «No valida cabida». No es un replay de la guía 1520 ni evidencia de un viaje real.
- [Captura inspeccionada](../../attachments/reported-fleet-clone-20261014/proposal-fvr.png) y [validación estructurada](evidence/reported-fleet-validation-20261014.json). Navegador cerrado al finalizar.
- Conteos finales: 3 vehículos, 1 tipo, 20 pedidos, 11 entregas y 1 movimiento. Los conteos de documentos comerciales coinciden con el control previo. No hubo despliegue o registro en producción ni commit Git.

### Mantenimiento

Los datos cargados son referencia operativa en el cliente de demostración del clon. No borrarlos como si fueran los vehículos ficticios de la prueba anterior. Repetir la operación valida medidas e identidad; no la usar para actualizar cambios reales de plataforma. Esos cambios requieren una operación explícita y su procedencia.

## Next

Placa de plataforma confirmada y separada: 04R-DBD. Confirmar si se intercambia entre chutos y la disponibilidad operativa de cada unidad. Obtener carga admisible y demás datos pendientes antes de habilitar evaluación física o asignaciones de viaje.
