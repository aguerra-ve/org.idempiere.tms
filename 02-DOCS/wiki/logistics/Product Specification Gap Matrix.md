---
type: article
title: Matriz de brechas de especificaciones de producto
description: Cruce por modelo entre el catálogo Q'Tanque, snapshots de M_Product y datos requeridos para nesting y transporte.
tags: [products, dimensions, nesting, transport, data-gaps]
timestamp: 2026-09-23T15:05:00-04:00
topic: logistics
status: draft
sources: ["CATALOGO_QTANQUE.md", "Catálogo digital 26-05-26", "PHYSICAL_PRODUCT_DIMENSIONS.csv", "PHYSICAL_TLL_CATALOG_CONTEXT_RAW.csv", "PHYSICAL_TARGET_PRODUCT_MASTER_RAW.csv"]
score: 1.0
---

# Matriz de brechas de especificaciones de producto

## Alcance y criterio

Este cruce es **por modelo**, no por color. Usa los snapshots disponibles del replay; no afirma que
sean un inventario completo y vigente de toda la base operativa. La extracción TLL contiene 18 SKU
físicos —seis tamaños y tres colores— más dos combos; el benchmark de dimensiones contiene 23 SKU
seleccionados, no todo `M_Product`.

`Completo catálogo` significa que se localizaron capacidad nominal, peso vacío y dimensiones
exteriores del producto individual. No significa que exista volumen exterior de transporte,
orientación de carga, altura de un grupo anidado o cabida vehicular confirmados.

## Tabla de faltantes

| Modelo o grupo en iDempiere | Evidencia encontrada | Estado de especificación individual | Faltante o contradicción | Acción requerida antes de usarlo físicamente |
|---|---|---|---|---|
| `TLL-0370` | Catálogo: 371 L, Ø48,5 × 201 cm, 12 kg; tres colores en snapshot | Completo catálogo | Volumen exterior/UOM, orientación y volumen marginal anidado no medidos | Medir envolvente de transporte y marginal por nesting; no usar 371 L como volumen de carga |
| `TLL-0500` | Catálogo: 484,8 L, Ø57 × 190 cm, 13 kg; ERP 13 kg | Completo con correspondencia por modelo | La fila resumida no publica código; faltan volumen exterior/UOM y marginal | Confirmar formalmente código↔modelo y medir volumen exterior/marginal |
| `TLL-0550` | Catálogo: 545,7 L, Ø60 × 193 cm, 14 kg; tres colores en snapshot | Completo catálogo | Volumen exterior/UOM, orientación y marginal no medidos | Medir volumen exterior/marginal |
| `TLL-0700` | Catálogo: 712 L, Ø67 × 202 cm, 16 kg; ERP 17 kg | Completo con contradicción | Diferencia de peso `16 vs 17 kg`; faltan volumen exterior/UOM y marginal | Conservar 17 kg como maestro actual hasta validar báscula; medir volumen exterior/marginal |
| `TLL-0900` | Catálogo: Q'Gigante 900, 882 L, Ø71 × 223 cm, 18 kg; tres colores en snapshot | Parcialmente enlazado | El catálogo resumido no imprime `TLL-0900`; faltan volumen exterior/UOM y marginal | Confirmar código↔Q'Gigante 900 y medir volumen exterior/marginal |
| `TLL-1060` | Catálogo: Q'Gigante 1060, 1065 L, Ø78 × 223 cm, 22 kg; ERP 23 kg | Completo con contradicción | Diferencia `22 vs 23 kg`; existe además un modelo 2kapas 1060 y la fila no imprime código | Confirmar que `TLL-1060` es Q'Gigante, no `TCD-1060`; validar peso y medir marginal |
| `TCD-0520` | Catálogo: 519,46 L, Ø59 × 190 cm, 15 kg | Completo catálogo | No existe regla de nesting confirmada ni volumen exterior de transporte | Mantener independiente; medir volumen exterior/UOM si se evaluará capacidad |
| `TCD-1060` | Catálogo: 2kapas 1060, 1069,6 L, Ø78,5 × 221 cm, 23 kg | Parcialmente enlazado | La fila no publica el código; no existe regla de nesting confirmada | Confirmar código↔modelo y mantener independiente |
| `TCL-0600`, `0900`, `1000`, `1200`, `1700`, `2500`, `5200` | Catálogo con capacidad, diámetro, altura y peso por modelo | Completo catálogo | No hay regla TCL confirmada, orientación ni volumen exterior de transporte | Mantener unidades independientes; medir exterior/UOM antes de capacidad volumétrica |
| `TCL-0600-1/2` | ERP: peso 6 kg; sin ficha propia | Incompleto | Capacidad útil/total, diámetro, altura y código de catálogo propios ausentes | Medir el medio tanque; no dividir automáticamente por dos las medidas del `TCL-0600` |
| `TCL-5000` | Solo referencia legacy; peso coincide con `TCL-5200` | Sin ficha confiable | Identidad y equivalencia con `TCL-5200` no demostradas | Confirmar si es alias, versión anterior o producto distinto |
| `TCL-10000` | SKU mencionado en ERP; sin ficha publicada | Sin ficha confiable | Capacidad, dimensiones, peso de catálogo y vigencia | Solicitar ficha técnica y estado comercial |
| `ORI-0550`, `0700`, `0850`, `1050`, `1250`, `1800`, `2500` | Tabla del PDF con capacidades, diámetros superior/inferior y altura; pesos actuales en ERP | Completo combinado catálogo+ERP | Peso no aparece en la tabla del PDF; orientación y altura efectiva del stack siguen faltantes | Mantener procedencia separada; medir altura del grupo y orientación antes de usar stacking |
| `TA-3300`, `5500`, `7700`, `11500` | Catálogo con capacidad, Ø, altura y peso | Completo catálogo | `TA-7700` no imprime código en el resumen; sin regla de stacking ni volumen exterior de transporte | Confirmar código y mantener independiente |
| `THL-1000`, `THL-4500` | Catálogo con ancho×alto×largo y peso | Completo catálogo | `THL-1000` no imprime código; falta orientación operativa como producto despachado | Confirmar código y posición de carga, aunque el producto esté diseñado para transportar líquidos |
| `CON-0120` | Dimensiones 50×47×85 cm y 8 kg | Parcial | La ficha no especifica capacidad; el SQL propone 120 L a partir del nombre | Confirmar capacidad con fabricante; no cargar 120 L como medición confirmada |
| `CON-0240` | 240 L, 59×64×95 cm, 16 kg | Completo catálogo | URL nominal dice 200; falta confirmar código maestro | Validar correspondencia `CON-0240` frente al nombre de la URL |
| `CON-0300` / `CON-0360` | Catálogo llama `CON-0300` al contenedor de 360 L; SQL usa `CON-0360` | Contradictorio | Código maestro inequívoco ausente | Resolver el código correcto antes de cualquier carga automática |
| `CON-1000` | 1073 L, 114×96×98 cm, 52 kg | Completo catálogo | URL/código nominal 1000 difiere de capacidad real | Mantener 1073 L como capacidad de ficha y documentar la diferencia nominal |
| `TBA-0208`, `TBC-0208` | 224,6 L, Ø57 × 88 cm; 7 y 12 kg | Completo catálogo | Sin regla de agrupación ni orientación de transporte confirmadas | Mantener independientes y medir/confirmar envolvente operacional si se requiere volumen |
| `TBU-2200`, `TBU-3200`, `TBD-2200`, `TBD-3200` | Catálogo usa `TB-2200/TB-3200` y `TBD-*`; dimensiones/capacidad disponibles; pesos TBU/TBD solo en ERP | Completo combinado con contradicción de código | `TB` frente a `TBU`; peso no publicado en ficha web | Confirmar correspondencia de códigos y conservar procedencia ERP del peso |
| `Q3-3300` | Solo SKU/nombre en ERP | Sin ficha confiable | Capacidad, dimensiones, peso publicado, vigencia y regla física | Solicitar ficha técnica; resultado físico debe permanecer `UNKNOWN` |
| `TLL-CT-10`, `TLL-CT-64` | Snapshot TLL: combos vendibles, no almacenados, peso/volumen cero | No es una unidad física simple | Componentes/BOM y cantidades físicas no probados en este cruce | Resolver el combo a sus líneas físicas; nunca tratarlo como un tanque de volumen cero |

## Resumen operativo

- La cadena TLL tiene dimensiones individuales y pesos suficientes para identificar sus unidades,
  pero **todos los tamaños** carecen todavía de volumen marginal confirmado y UOM para calcular
  volumen efectivo después del nesting.
- Las familias sin regla confirmada pueden transportarse como unidades independientes únicamente
  cuando exista una envolvente exterior de transporte con UOM y orientación aprobadas; capacidad
  nominal en litros no satisface ese requisito.
- La brecha documental más clara del benchmark es `TCL-0600-1/2`: solo tiene peso actual.
- Las contradicciones prioritarias son `TLL-0700`, `TLL-1060`, `CON-0300/0360` y `TB/TBU`.
- El SQL `populate_volume_qtanque.sql` no fue ejecutado. Su columna `Volume` representa capacidad
  nominal del contenido y no debe alimentar directamente la capacidad física del vehículo.

## Fuentes

- [`CATALOGO_QTANQUE.md`](../../../CATALOGO_QTANQUE.md), resumen web/PDF y correspondencias.
- [Catálogo digital convertido](../../raw/contexto-v2/docs/CAT%C3%81LOGO%20DIGITAL%2026-05-26.md),
  referencia raw inmutable.
- [Dimensiones del replay](../../raw/contexto-v2/docs/v2/replay/generated/PHYSICAL_PRODUCT_DIMENSIONS.csv).
- [Contexto TLL de iDempiere](../../raw/contexto-v2/docs/v2/replay/generated/PHYSICAL_TLL_CATALOG_CONTEXT_RAW.csv).
- [Maestro objetivo del replay](../../raw/contexto-v2/docs/v2/replay/generated/PHYSICAL_TARGET_PRODUCT_MASTER_RAW.csv).
- [`tests/nesting/evidence.md`](../../../tests/nesting/evidence.md), clasificación de evidencia usada
  por el ciclo SDD activo.

## Límite de vigencia

La matriz no sustituye un `SELECT` actual de `M_Product`. La conexión local a Incus no estuvo
disponible sin credenciales administrativas, por lo que códigos nuevos, productos inactivos o
cambios posteriores al snapshot deben comprobarse con una consulta de solo lectura antes de cargar
datos al ERP.
