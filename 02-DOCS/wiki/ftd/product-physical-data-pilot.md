# Piloto de datos físicos de producto

## Intent

Completar y validar en `id-04` los datos físicos de `TLL-0370-00` para comprobar que los campos
estándar de `M_Product` sirven como base del cálculo de carga.

## Scope

Incluye únicamente `Weight`, `ShelfWidth`, `ShelfHeight`, `ShelfDepth` y `Volume` del producto
`TLL-0370-00`, más los metadatos `Updated` y `UpdatedBy` de la actualización. No incluye carga masiva,
reglas de nesting, vehículos, pedidos ni cambios en otros productos.

Convención piloto: peso en kg, dimensiones exteriores en mm y volumen de la envolvente exterior en
m³. La capacidad interna del tanque en litros no se guarda en `M_Product.Volume`.
Correspondencia de columnas: `ShelfWidth=485`, `ShelfHeight=2010`, `ShelfDepth=485`. El volumen
`0.47280225 m³` es la caja rectangular calculada con esas medidas, no el volumen interior de 371 L
ni una medición del volumen marginal al anidar.

## Checklist

- [x] Confirmar la existencia de los cinco campos — prueba: consulta de metadatos en `id-04`.
- [x] Capturar el estado inicial de `TLL-0370-00` — prueba: consulta de solo lectura.
- [x] Cargar 12 kg, 485 × 485 × 2010 mm y 0.47280225 m³ — prueba: transacción SQL acotada a `M_Product_ID=1000221`, con guardas del estado inicial, terminó en `COMMIT` y afectó una fila. `Weight=12` se conservó.
- [x] Verificar persistencia en la base — prueba: una conexión PostgreSQL nueva devolvió `12|485|2010|485|0.47280225` para peso, ancho, alto, profundidad y volumen.
- [ ] Verificar visualización en la ventana Product — prueba pendiente: las credenciales de demostración publicadas por `id-04` fueron rechazadas. El diccionario marca los cinco campos como activos y visibles en esa ventana, pero eso no sustituye abrir el registro.

## Evidence

- `id-04` contiene `weight`, `volume`, `shelfwidth`, `shelfheight` y `shelfdepth` en
  `adempiere.m_product`.
- Cobertura inicial entre 723 productos activos: Weight 292; los otros cuatro campos, 0.
- Estado inicial de `M_Product_ID=1000221`: Weight 12; ShelfWidth, ShelfHeight, ShelfDepth y
  Volume en 0; `Updated=2025-07-23 10:07:46.955`.
- El catálogo documenta `TLL-0370` con Ø48,5 × 201 cm y 12 kg; por eso la altura ocupa
  `ShelfHeight=2010` y el diámetro ocupa ancho y profundidad de 485 mm. La caja exterior calculada
  es `0.485 × 0.485 × 2.010 = 0.47280225 m³`.
- El 2026-09-24 las cuentas de demostración `GardenAdmin` y `SuperUser` mostradas por la WebUI de
  `id-04` respondieron `Invalid User ID or Password`. Se usó entonces una actualización SQL directa,
  de una sola fila y con precondiciones estrictas sobre ID, código, cliente, estado activo, peso,
  cuatro valores cero y fecha previa. [SQL ejecutado](evidence/id04-product-pilot-apply-2026-09-24.sql).
  La transacción finalizó en `COMMIT`; `Updated` avanzó a `2026-09-24 12:27:40.22503` y
  `UpdatedBy=0` identifica la intervención administrativa del sistema.
- Una conexión nueva confirmó `M_Product_ID=1000221`, `Weight=12`, `ShelfWidth=485`,
  `ShelfHeight=2010`, `ShelfDepth=485` y `Volume=0.47280225`. En `AD_Field`, los cinco campos de la
  pestaña Product de la ventana Product tienen `IsActive=Y` e `IsDisplayed=Y`. No se afirma
  validación visual ni generación de `AD_ChangeLog` por la aplicación para esta escritura SQL.

## Next

Abrir `TLL-0370-00` en la ventana Product de `id-04` con una cuenta válida, refrescar el registro y
comprobar visualmente los cinco valores. Después registrar la captura o el resultado; la carga y la
persistencia en PostgreSQL ya están verificadas.
