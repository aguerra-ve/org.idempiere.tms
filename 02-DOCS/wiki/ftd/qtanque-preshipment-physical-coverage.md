# Cobertura física del predespacho QTanque 1007137

## Intent

Completar las dimensiones individuales de productos del predespacho real `1007137` en `id-04`, tenant `GRUPO QTANQUE` (`AD_Client_ID=1000000`), con correspondencias de catálogo comprobables y sin dar por validada la cabida del FVR.

## Scope

Se actualizaron solo `M_Product.ShelfWidth`, `ShelfHeight` y `ShelfDepth` de productos terminados activos, almacenables, con peso positivo y las tres medidas previas en cero. `Weight`, `Classification` y `Volume` se conservaron. Los diámetros representan el cuerpo individual sin tapa; no se extrapolan reglas de anidado, apilado u orientación. Los SQL de evidencia usan IDs exactos y guardas que abortan toda la transacción si cambia el conjunto esperado.

## Checklist

- [x] Medir cobertura inicial del predespacho: 23 líneas, 7 entregas, 32 unidades, 625 kg del maestro; 10 unidades con tres dimensiones.
- [x] Conciliar siete familias ORI y 21 variantes con la tabla del fabricante en el PDF recuperado; simular con `ROLLBACK`, aplicar y verificar en conexión nueva. Cobertura: 20/32 unidades.
- [x] Conciliar TBA-0208, TBC-0208 y TCD-0520 con códigos y medidas explícitos del [catálogo local](../../../CATALOGO_QTANQUE.md); simular con `ROLLBACK`, aplicar y verificar en conexión nueva. Cobertura: 28/32 unidades.
- [x] Confirmar con el usuario las correspondencias `TLL-0500-00` (1 unidad), `TLL-1060-64` (2) y `THL-1000-64` (1), simular, aplicar y verificar en conexión nueva. Cobertura: 32/32 unidades.
- [ ] Identificar vehículo físico, carga admisible y medidas operativas necesarias; evaluar toda la carga y sus unidades de manejo, además de cada entrega por separado.

## Evidence

### ORI

El PDF del fabricante cuya procedencia quedó registrada en la [metodología recuperada](../../raw/contexto-v2/docs/v2/replay/PHYSICAL_FOOTPRINT_METHODOLOGY.md), página 19, SHA-256 `93853a50ab67995b26f1bf611c457b01da01066e60de3e2b3d288685d30629a2`, publica diámetro superior, inferior y altura de cada modelo. Para una envolvente individual conservadora, ancho y profundidad toman el diámetro superior máximo. El diámetro inferior se conserva en el SQL de evidencia; no hay columna para él en `M_Product`. La altura de una pila no se infiere de la altura de una unidad.

| Familia | Diámetro superior / inferior × alto (mm) | Variantes actualizadas |
|---|---|---:|
| ORI-0550 | 1037 / 781 × 838 | 3 |
| ORI-0700 | 1103 / 841 × 896 | 3 |
| ORI-0850 | 1195 / 914 × 973 | 3 |
| ORI-1050 | 1263 / 969 × 1068 | 3 |
| ORI-1250 | 1328 / 1050 × 1117 | 3 |
| ORI-1800 | 1455 / 1117 × 1359 | 3 |
| ORI-2500 | 1617 / 1261 × 1490 | 3 |

El [SQL ORI](evidence/qtanque-ori-body-dimensions-apply-2026-09-25.sql) mostró 21/21 filas en la simulación, terminó con `ROLLBACK` y una conexión nueva confirmó 0/21 dimensionadas. La ejecución posterior con `COMMIT` verificó 21/21; otra conexión confirmó 3/3 variantes en cada familia y 20/32 unidades cubiertas en el predespacho. Se preservó `ORI-0850-64` con 11 kg, diferente de las otras variantes de 13 kg; no se corrigió peso sin validación de origen.

### Tambores y TCD

El catálogo local imprime exactamente `TBA-0208` y `TBC-0208` con Ø57 × 88 cm, y `TCD-0520` con Ø59 × 190 cm. Los 13 productos de estas familias son activos y almacenables, pero tres variantes TBA tienen `Weight=0` y quedaron fuera de la carga. La [transacción exacta](evidence/qtanque-drum-tcd-body-dimensions-apply-2026-09-25.sql) actualizó 4 TBA, 3 TBC y 3 TCD: diámetro 570, alto 880 mm para tambores; diámetro 590, alto 1900 mm para TCD. Comprobó ID, código, nombre, peso, categoría, tenant, clasificación vacía y valores previos. Simulación: 10/10 actualizadas y verificadas, seguida de `ROLLBACK`; conexión nueva: 0/10. Aplicación: 10/10 con `COMMIT`. `Weight`, `Classification` y `Volume` quedaron iguales; `UpdatedBy=0` identifica esta escritura SQL directa, sin registro funcional de cambio de iDempiere.

La [consulta reproducible de cobertura](evidence/qtanque-preshipment-1007137-coverage-2026-09-25.sql), ejecutada después en conexión nueva, devolvió **20/23 líneas y 28/32 unidades dimensionadas**, conservando 7 entregas y 625 kg del maestro. De las 9 unidades sin clasificación, 8 quedaron dimensionadas (TBA/TBC/TCD) y solo queda el THL. Ninguno de estos resultados valida una disposición espacial de la carga completa.

### Correspondencias finales confirmadas

| Producto del predespacho | Unidades | Correspondencia confirmada y dimensiones (mm) |
|---|---:|---|
| `TLL-0500-00` | 1 | Cilíndrico 500: ancho 570, alto 1900, profundidad 570. La fila del catálogo no imprime código SKU; la correspondencia fue confirmada por el usuario. |
| `TLL-1060-64` | 2 | Q'Gigante 1060: ancho 780, alto 2230, profundidad 780. El catálogo publica 22 kg, pero el maestro registra 23 kg; se conserva este último. La alternativa 2kapas mide distinto y fue descartada por confirmación del usuario. |
| `THL-1000-64` | 1 | Horizontal 1000: ancho 990, alto 970, profundidad 1620. La fila del catálogo no imprime código SKU; la correspondencia fue confirmada por el usuario. |

El [SQL de cierre](evidence/qtanque-preshipment-final-dimensions-apply-2026-09-25.sql) contiene tres IDs exactos, nombres, clasificación y pesos anteriores. La simulación actualizó/verificó 3/3 y terminó con `ROLLBACK`; una conexión nueva mostró 0/3 dimensionados. La aplicación posterior actualizó/verificó 3/3 y terminó con `COMMIT`. La consulta de cobertura en otra conexión devolvió **23/23 líneas y 32/32 unidades** con ancho, alto y profundidad positivos; 7 entregas y 625 kg del maestro se conservaron. `Classification`, `Weight` y `Volume` de los tres productos permanecieron iguales. La capacidad en litros no se transformó en `Volume` de transporte. La correspondencia de SKU es una declaración operativa del usuario, no un código impreso en esas filas del catálogo.

## Next

La [evaluación inicial de la carga completa](qtanque-preshipment-full-load-evaluation.md) ya comprobó que un acomodo plano de un solo nivel, incluso con agrupación TLL/ORI optimista, excede el área reportada de la plataforma. Obtener identificación y carga admisible del FVR concreto, y reglas observadas de orientación, apilado, holguras y sujeción para decidir cabida operacional. Las dimensiones individuales están completas, pero no bastan por sí solas para calcular la altura de las pilas ORI ni la disposición real de todas las entregas. El replay estructural de `1056427` sigue siendo solo una de las siete entregas.
