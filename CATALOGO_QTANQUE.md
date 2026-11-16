# Catálogo Q'Tanque — datos técnicos para poblar M_Product

Extraído de [qtanque.com.ve](https://qtanque.com.ve) (Industrias Q'Productos, S.A., Santa Cruz de Aragua, Venezuela) el 2026-07-27. Todos los tanques son de **PEMD** (polietileno virgen de media densidad), con protección UV, spud/conexión de 3/4" salvo donde se indique. El fabricante aclara que las dimensiones pueden variar hasta 3% por el proceso de fabricación.

> **Uso actual en QTanque:** esta tabla respalda dimensiones del cuerpo por modelo y permite contrastar el peso existente. La carga acotada de 2026-09-24 convirtió centímetros a milímetros para `ShelfWidth`, `ShelfHeight` y `ShelfDepth`, sin incluir la tapa, según la convención confirmada por el operador. Los pesos maestros se conservaron cuando difieren del catálogo. **Cap. Total (L)** y **Cap. Útil (L)** describen líquido contenido; ninguna de las dos se carga en `M_Product.Volume` como volumen de transporte. `Volume` quedó sin cambios en la [carga por familia](02-DOCS/wiki/ftd/qtanque-family-physical-data.md).

## Cilíndricos

| Código | Modelo | Cap. Total (L) | Cap. Útil (L) | Diámetro (cm) | Altura (cm) | Tapa (cm) | Peso vacío (kg) |
|---|---|---:|---:|---:|---:|---:|---:|
| TLL-0370 | Cilíndrico 370 | 371.0 | ~371 − 3.69/2cm bajo tope | 48.5 | 201 | 51 | 12 |
| — | Cilíndrico 500 | 484.8 | 454.2 | 57 | 190 | 57 | 13 |
| TLL-0550 | Cilíndrico 550 | 545.7 | 511.7 | 60 | 193 | 60.5 | 14 |
| TLL-0700 | Cilíndrico 700 | 712.0 | ~712 − 7.05/2cm bajo tope | 67 | 202 | 70 | 16 |
| TCD-0520 | Cilíndrico 2kapas 520 | 519.46 | 492.12 | 59 | 190 | 60 | 15 |
| — | Cilíndrico 2kapas 1060 | 1069.60 | 1011.52 | 78.5 | 221 | 80 | 23 |
| — | Q'Gigante 900 | 882.0 | ~882 − 7.92/2cm bajo tope | 71 | 223 | 73.5 | 18 |
| — | Q'Gigante 1060 | 1065.0 | ~1065 − 9.56/2cm bajo tope | 78 | 223 | 79 | 22 |

## Cónicos

| Código | Modelo | Cap. Total (L) | Cap. Útil (L) | Diámetro (cm) | Altura (cm) | Tapa (cm) | Peso vacío (kg) |
|---|---|---:|---:|---:|---:|---:|---:|
| TCL-0600 | Cónico 600 | 596.4 | 460.1 | 107 | 105 | 109 | 12 |
| TCL-0900 | Cónico 900 | 881.7 | 742.5 | 123 | 104.5 | 126.5 | 16 |
| TCL-1000 | Cónico 1000 | 951.3 | 800.0 | 110 | 154 | 114 | 20 |
| TCL-1200 | Cónico 1200 | 1193.4 | 1026.4 | 113 | 168 | 120 | 24 |
| TCL-1700 | Cónico 1700 | 1700.0 | 1465.9 | 137 | 167 | 143 | 33 |
| TCL-2500 | Cónico 2500 | 2510.3 | 2126.3 | 159 | 170 | 166 | 42 |
| TCL-5200 | Cónico 5200 | 5160.7 | 4869.6 | 192 | 195 | 214.5 | 90 |

## Australianos

| Código | Modelo | Cap. Total (L) | Cap. Útil (L) | Diámetro (cm) | Altura (cm) | Peso vacío (kg) |
|---|---|---:|---:|---:|---:|---:|
| TA-3300 | Australiano 3300 | 3333.0 | 3076.6 | 155.5 | 175.5 | 55 |
| TA-5500 | Australiano 5500 | 5421.5 | 5104.1 | 183.5 | 205 | 92 |
| — | Australiano 7700 | 7621.9 | 7108.9 | 216 | 208 | 130 |
| TA-11500 | Australiano 11500 | 11478.3 | 10397.3 | 256 | 223 | 200 |

*(Boca de visita 39 cm, tapa 54 cm en todos los Australianos; los de 7700 y 11500 incluyen 2 tapones de 2".)*

## Horizontales / Cisternas

| Código | Modelo | Cap. Total (L) | Cap. Útil (L) | Ancho×Alto×Largo (cm) | Peso vacío (kg) |
|---|---|---:|---:|---|---:|
| — | Horizontal 1000 | 1247.0 | 1019.4 | 99×97×162 | 60 |
| THL-4500 | Horizontal 4500 | 4432.6 | 4336.4 | 210×125×205 | 160 |

*Nota: el fabricante los describe explícitamente para "transporte de líquidos" — son los únicos pensados para ir montados/transportados, no solo almacenar fijos.*

## Contenedores (residuos)

| Código | Modelo (según página) | Nominal en URL | Cap. (L) | Ancho×Largo×Alto (cm) | Peso (kg) |
|---|---|---|---:|---|---:|
| CON-0120 | Contenedor 120 | "100" | no especificada en la ficha | 50×47×85 | 8 |
| CON-0240 | Contenedor 240 | "200" | 240 | 59×64×95 | 16 |
| CON-0300 | Contenedor 360 | "300" | 360 | 65.5×68×102 | 18 |
| CON-1000 | Contenedor 1000 | "1000" | 1073 | 114×96×98 | 52 |

> ⚠️ El nombre en la URL (100/200/300) no coincide con la capacidad real del producto (120/240/360) — cuidado al mapear `Value`/SKU si se automatiza la carga.

## Tambores

| Código | Modelo | Cap. Total (L) | Cap. Útil (L) | Diámetro×Altura (cm) | Peso (kg) |
|---|---|---:|---:|---|---:|
| TBA-0208 | Tambor Abierto | 224.6 | 220.7 | 57×88 | 7 |
| TBC-0208 | Tambor Cerrado | 224.6 | 220.7 | 57×88 | 12 |

*(Material PELBD/PEMD — polietileno de baja y media densidad, distinto del resto de la línea.)*

## Botella (vertical, doble capa opcional)

| Código | Modelo | Cap. Total (L) | Cap. Útil (L) | Diámetro (mm) | Altura (mm) | Tapa (cm) |
|---|---|---:|---:|---:|---:|---:|
| TB-2200 | Q'Botella 2200 | 2276.0 | 2190.0 | 1200 | 2280 | 54 |
| TB-3200 | Q'Botella 3200 | 3216.0 | 3100.0 | 1300 | 2600 | 54 |
| TBD-2200 | Q'Botella 2200 2K Doble Capa | 2276.0 | 2190.0 | 1200 | 2180 | 54 |
| TBD-3200 | Q'Botella 3200 2K Doble Capa | 3150.0 | 3100.0 | 1300 | 2600 | 54 |

*(Peso vacío no publicado en la ficha web para esta línea — sí existe en la BD: TBU-2200=42kg, TBU-3200=55kg, TBD-2200=42kg, TBD-3200=55kg, según `m_product`.)*

## No son tanques (excluidos de este catálogo)

- **Qdesk 4** / **Qdesk 5** — pupitres/escritorios escolares. No tienen capacidad en litros ni aplican a logística de tanques.

## SKUs en la base de datos sin ficha técnica publicada (probable descontinuados)

Al consultar `adempiere.m_product` directamente aparecen estos códigos que **no tienen página activa en qtanque.com.ve ni en el PDF oficial** — puede que sean referencias legacy, productos descontinuados, o de próximo lanzamiento sin publicar aún. No incluí datos porque no encontré una fuente confiable:

- `ORI-1250` — Tanque Orinoco 1250 (la web menciona la serie Orinoco 1050/1250/1800/2500 en la portada, pero ninguna ficha individual existe hoy)
- `Q3-3300` — Tanque Vertical Q3-3300
- `TCL-10000` — Tanque Cónico Liso (10.000 L aprox., a juzgar por el peso 160kg vs 90kg del 5200)
- `TCL-5000` — probablemente el nombre anterior del actual `TCL-5200` (mismo peso: 90kg)
- `TCL-0600-1/2` — "Medio Tanque Cónico Liso 600" (peso 6kg = exactamente la mitad de los 12kg del TCL-0600 — probablemente medio tanque = ~298L)

Antes de usarlos en el modelo de anidado, habría que confirmar internamente si siguen vigentes.

---

**Fuente:** fichas técnicas individuales en `qtanque.com.ve/product/*` (vía `sitemap.xml`) + catálogo oficial PDF `qtanque.com.ve/qr/catalogo/guia_resumida_sep22.pdf`, cruzado contra `adempiere.m_product` en el servidor `dev-id12`. Generado por Claude Code — no se actualiza automáticamente si el catálogo cambia.
