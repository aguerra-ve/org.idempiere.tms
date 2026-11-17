# Cruce de catálogo y brechas de producto

## Intent

Cruzar los modelos publicados en el catálogo Q'Tanque con los snapshots disponibles de
`M_Product` y dejar una tabla operativa de datos físicos faltantes para nesting y transporte.

## Scope

Incluye códigos/modelos presentes en el catálogo resumido, el PDF convertido, los extractos
físicos del replay y el SQL de correspondencias preparado. Distingue capacidad nominal, peso,
dimensiones exteriores, reglas físicas y medidas necesarias para transporte.

No consulta ni modifica la base operativa, no ejecuta `populate_volume_qtanque.sql`, no propaga
datos a variantes de color y no convierte litros de contenido en volumen exterior de carga.

## Checklist

- [x] Consolidar modelos y correspondencias. Prueba: matriz de 23 filas agrupadas por familia/modelo.
- [x] Identificar faltantes y contradicciones. Prueba: cada fila contiene estado, brecha y acción.
- [x] Verificar cobertura y límites. Prueba: integridad raw, secretos y whitespace verificados.
- [x] Indexar el resultado. Prueba: enlace agregado en la sección `logistics` del índice.

## Evidence

- `02-DOCS/wiki/logistics/Product Specification Gap Matrix.md` cruza catálogo resumido, PDF
  convertido, snapshots físicos, catálogo TLL y maestro objetivo del replay.
- La matriz contiene 23 filas de modelos/grupos y explicita contradicciones de peso/código, ausencia
  de fichas, medidas de transporte pendientes y combos no físicos.
- `sha256sum --quiet -c tests/nesting/raw-baseline.sha256` terminó con código 0: el contexto raw
  permaneció intacto.
- `./tests/nesting/check-secrets.sh` terminó con código 0.
- `rg -n '[[:blank:]]+$'` sobre los tres Markdown modificados no encontró whitespace final.
- `stat` confirmó la existencia del artículo y de las cuatro fuentes principales enlazadas.

## Next

Ejecutar una consulta de solo lectura sobre el `M_Product` vigente cuando esté disponible la
conexión, para detectar SKU nuevos/inactivos y expandir la matriz de modelo a variante sin asumir
que el snapshot histórico sigue completo.
