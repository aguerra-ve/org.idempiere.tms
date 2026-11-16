# Evidencia física autorizada para safe-product-nesting

Fecha de corte: 2026-09-23

Tarea: T002

Alcance: inventario de datos y reglas que el primer ciclo puede tratar como confirmados. Este
documento no modifica el contexto recuperado ni convierte hipótesis históricas en configuración
operativa.

## Estados usados

- `CONFIRMED`: existe una fuente aprobada e identificable para la semántica y el valor indicados.
- `CONFIRMED_CURRENT_MASTER`: el valor existe en el maestro actual; no prueba que fuera el peso
  histórico de cada despacho.
- `DERIVED`: cálculo reproducible a partir de datos confirmados; no equivale a medición directa.
- `UNKNOWN`: falta valor, unidad de medida, semántica o respaldo suficiente para usarlo en una
  decisión automática.
- `OUT_OF_SCOPE`: puede existir evidencia, pero este ciclo no implementa esa regla.

`NULL` y un cero provisional se clasifican `UNKNOWN`. Un cero solo podrá ser `CONFIRMED` cuando su
fuente declare expresamente que es una medición física de cero; no se encontró un caso así en las
fuentes aprobadas de este inventario.

## Matriz de campos físicos

| Campo / concepto | Fuente observada | Unidad | Estado para este ciclo | Uso permitido |
|---|---|---|---|---|
| Cantidad comercial por línea | Documento/línea de origen resuelto según `tests/nesting/lineage.md` | UOM de la línea; para TLL agrupable debe poder expresarse como unidades enteras identificables | `CONFIRMED` solo con linaje y cantidad válidos | Expandir identidades y comprobar que cada unidad aparezca exactamente una vez. No es cantidad de huellas ni posiciones del vehículo. |
| Peso unitario del producto | `M_Product` reproducido como `CurrentMasterWeightKg`/`WeightKg` en los CSV físicos | kg en el replay | `CONFIRMED_CURRENT_MASTER` para los SKU inventariados; semántica histórica no verificada | Conservar y sumar sin reducción por nesting. La comparación vehicular exige UOM compatible. No determina por sí solo las unidades después del nesting. |
| Peso total | Cantidad válida × peso unitario compatible | misma UOM normalizada | `DERIVED` si todos los pesos y UOM son conocidos; de otro modo `UNKNOWN` | Invariante de conservación y límite de seguridad posterior, no sustituto de capacidad física. |
| Diámetro y altura exterior TLL del benchmark | Catálogo original verificado visualmente, materializado en `PHYSICAL_PRODUCT_DIMENSIONS.csv` | mm | `CONFIRMED` únicamente para TLL-0500-64, TLL-0700-64 y TLL-1060-64 del benchmark | Describir la envolvente individual aproximada. No demuestra orientación, altura de cadena, packing ni volumen efectivo anidado. |
| Volumen/capacidad de `M_Product` | Código y análisis recuperado; el análisis distingue capacidad total del producto de espacio de transporte | sin UOM explícita en `DD_ProductNesting` | `UNKNOWN` como volumen exterior de transporte | No usar capacidad nominal de líquido como volumen de carga o prueba de cabida. |
| Volumen exterior de transporte | Requeriría envolvente confirmada, orientación y UOM trazable | volumen compatible con el vehículo | `UNKNOWN` en la configuración actual | Una familia sin ahorro solo será evaluable cuando cada exterior tenga este dato confirmado. |
| `DD_ProductNesting.NestedVolume` | Modelo generado y documentación histórica lo describen como pendiente/placeholder; los valores geométricos legacy son estimaciones | la tabla actual no contiene `Volume_UOM_ID` | `UNKNOWN`, tanto para `NULL` como para cero no medido | No sumar cero y no declarar volumen efectivo conocido. Identificar `NestedVolume` como faltante. |
| Volumen efectivo de un grupo TLL | exterior confirmado + marginales confirmados | una sola UOM normalizada | `UNKNOWN` mientras falte cualquier marginal requerido | No asignar automáticamente por volumen ni presentar porcentaje de ocupación verificado. |
| `DD_Vehicle.MaximumVolume` | `I_DD_Vehicle` | `Volume_UOM_ID` del vehículo | Campo disponible; el valor concreto debe validarse por vehículo candidato | Comparar solo contra volumen efectivo conocido y convertido a la misma UOM. No prueba packing geométrico. |
| `DD_Vehicle.MaximumWeight` | `I_DD_Vehicle` | `Weight_UOM_ID` del vehículo | Campo disponible; el valor concreto debe validarse por vehículo candidato | Límite de seguridad adicional cuando carga y vehículo tienen UOM compatible. |
| Largo/ancho/alto de plataforma o caja | `I_DD_Vehicle.TransportLength`, `TransportWidth`, `TransportHeight` | `Dimension_UOM_ID` del vehículo | Campos disponibles; medidas concretas quedan pendientes por vehículo | Insumo futuro de cabida geométrica. No se infiere desde `MaximumVolume`. |

### Pesos TLL observados en el benchmark

| Producto | Peso maestro actual | Comparación de catálogo | Estado |
|---|---:|---:|---|
| TLL-0500-64 | 13 kg | 13 kg | `CONFIRMED_CURRENT_MASTER`; coincidencia exacta |
| TLL-0700-64 | 17 kg | 16 kg | `CONFIRMED_CURRENT_MASTER`; discrepancia explícita |
| TLL-1060-64 | 23 kg | 22 kg | `CONFIRMED_CURRENT_MASTER`; discrepancia explícita |

La discrepancia no autoriza escoger silenciosamente el catálogo. Para conservar el comportamiento
ERP, el primer ciclo usa el snapshot maestro actual con su procedencia y mantiene visible que no es
una medición histórica de báscula.

## Reglas de agrupación

| Regla | Evidencia | Estado | Consecuencia ejecutable |
|---|---|---|---|
| TLL transitivo y multinivel | Decisión `DD-NESTING-001` cerrada en la metodología física | `CONFIRMED` | Cadena ascendente `370 -> 500 -> 550 -> 700 -> 900 -> 1060`; un tamaño menor puede ir dentro de cualquier tamaño posterior, conservando todas las identidades. |
| Estructura TLL | La misma decisión declara `NestingStructure = CHAIN` | `CONFIRMED` | Un grupo es una cadena exterior→interior. La cantidad de grupos/huellas es estructural, no volumen ni posición vehicular. |
| Varios interiores paralelos | `DD-NESTING-002` | `UNKNOWN` / pendiente de negocio | Prohibido asumirlos; una ancla no recibe varios interiores independientes. |
| TLL-600 | Evaluación de ciclo de vida del replay | `UNKNOWN` como producto vigente; no requerido para transitividad | No se inserta en la cadena confirmada ni bloquea los saltos transitivos. |
| TCL | Familia confirmada por SKU, nesting no confirmado | `UNKNOWN` para nesting | Mantener unidades independientes; no heredar TLL ni aplicar el seed/fixture legacy. |
| TCD | Solo aparece en propuestas/estimaciones legacy junto con niveles cilíndricos | `UNKNOWN` para nesting | Mantener unidades independientes; igualdad de rango o diámetro nominal no prueba compatibilidad. |
| ORI homogéneo | Stacking por capacidad y `MaxStack` tiene confirmación separada | `OUT_OF_SCOPE` en `safe-product-nesting` | No implementar stacking ORI en este ciclo y nunca heredar nesting TLL. |
| TA/TBA/TBC y demás familias | Familia derivada de prefijo o sin regla física confirmada | `UNKNOWN` para nesting | Mantener unidades independientes y sin ahorro. |
| Igual `SeqNo` | El rango no demuestra encaje; existen clasificaciones equivalentes en material legacy | `UNKNOWN` como compatibilidad | No anidar unidades del mismo nivel. |
| Entre entregas, rutas, fechas o candidatos distintos | Spec aprobada AC-NEST-002/003 | Prohibido en este ciclo | Particionar el alcance antes del cálculo; no compartir reducción. |

La práctica operativa comunicada el 2026-09-25 ubica las tapas de TCL en huecos de la plataforma, las de TA en cabina y las de TLL dentro de tanques TLL. En una cadena `TLL-0370→TLL-0700`, el usuario confirmó que cada tapa queda dentro de su propio tanque y que los pesos maestros de 12 y 17 kg usados en la entrega `1056427` ya incluyen las tapas. Es evidencia de **manejo de accesorios y semántica de ese peso**, no una compatibilidad adicional ni una medición de volumen. No se suma peso de tapa otra vez. El usuario indicó que la altura de esa cadena es la del tanque exterior mayor, 2.020 mm nominales según el maestro TLL-0700; falta verificar el valor y la holgura reales con ambas tapas. Por ello una huella de `StructuralGroup` sigue siendo un resultado estructural; no demuestra cabida ni estabilidad física del grupo.

Para el replay de la entrega `1056427`, el usuario confirmó el mismo día que los TLL anidados viajan de pie sobre la plataforma. Esta orientación permite interpretar los diámetros del cuerpo como huellas en planta de ese caso; la altura nominal del grupo se toma del mayor por la regla operativa recién confirmada, no de una medición del conjunto. No se extiende automáticamente a otras familias o modos de transporte.

## Frontera entre nesting y capacidad

```text
unidades comerciales
  -> grupos/huellas estructurales confirmadas
  -> volumen efectivo (solo con exterior + marginales medidos y UOM compatibles)
  -> límites del vehículo candidato
  -> packing/cabida geométrica (fuera de este ciclo)
```

Por tanto, una propuesta puede informar `21 unidades comerciales` y eventualmente un número menor
de `unidades después del nesting` cuando sus líneas TLL y su alcance estén confirmados. Ese número
no permite seleccionar un vehículo mientras el volumen efectivo o las medidas necesarias sigan
`UNKNOWN`, y nunca equivale automáticamente a posiciones sobre la plataforma.

## Fuentes revisadas

- `02-DOCS/raw/contexto-v2/docs/v2/replay/PHYSICAL_NESTING_METHODOLOGY.md` — SHA-256
  `9e6475fb1aedbcc021074442b68a680318517141df0472c47b44596c1d43d441`.
- `02-DOCS/raw/contexto-v2/docs/v2/replay/generated/PHYSICAL_PRODUCT_DIMENSIONS.csv` — SHA-256
  `e828a774fe85ffe997de1b66e1a836f5e5e4f11de02c89d9daefb70bd5092d0b`.
- `02-DOCS/raw/contexto-v2/docs/v2/replay/generated/PHYSICAL_PRODUCT_LOGISTICS_PROFILES.csv` — SHA-256
  `f04788381c2f33fbbf1e8eb019ef4ee06cd01ff7539e8cceae5f9fe38d2f941d`.
- `02-DOCS/raw/contexto-v2/docs/v2/replay/generated/PHYSICAL_NESTING_METRICS.json` — SHA-256
  `9880174258fff2361942b74c6a5afd63b08a2d4d5a63ef573b9df956c9121340`.
- `02-DOCS/raw/contexto-v2/docs/v2/GAP_ANALYSIS.md`, sección 8: confirma TLL y mantiene
  nesting TCL/ORI, interiores paralelos, alturas y límites físicos como pendientes.
- `02-DOCS/raw/contexto-v2/docs/00-matriz-evidencia.md`, `NST-004`: confirma que
  `NestedVolume = 0` es placeholder, no una medición.
- `src/org/idempiere/tms/model/I_DD_ProductNesting.java`: la tabla actual expone familia, rango y
  `NestedVolume`, pero no una UOM de volumen; el propio comentario marca el marginal pendiente.
- `src/org/idempiere/tms/model/I_DD_Vehicle.java`: el vehículo separa UOM de dimensión, volumen y
  peso, además de largo/ancho/alto y máximos.

## Resultado de la revisión T002

- `NULL`: no confirmado; produce `UNKNOWN`.
- Cero sin medición: no confirmado; produce `UNKNOWN`.
- TCL/TCD: ninguna regla de nesting confirmada para este ciclo.
- Interiores paralelos: pendiente; no permitido.
- TLL: solo la cadena transitiva y multinivel indicada está confirmada.
- Peso: se conserva, pero no sustituye el cálculo de grupos ni la capacidad física.
- Volumen efectivo y candidato vehicular: permanecen pendientes hasta contar con medidas y UOM
  compatibles; una huella estructural por sí sola no selecciona vehículo.
