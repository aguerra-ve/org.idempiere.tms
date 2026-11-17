# Evaluación física inicial del predespacho QTanque 1007137

## Intent

Reconstruir las 32 unidades del predespacho real `1007137`, cuya salida en un FVR fue confirmada por el usuario, sobre la plataforma abierta de **6,00 × 2,20 m útiles reportados**, usando dimensiones ya cargadas en `id-04`, tenant `AD_Client_ID=1000000`, y reglas de agrupación documentadas. Distinguir el resultado operacional observado de una disposición física todavía no reconstruida.

## Scope

Es una reconstrucción estructural de unidades seguida de sensibilidades de área, sin posiciones reales del camión. Los TLL usan las cadenas transitivas confirmadas en la [metodología física](../../raw/contexto-v2/docs/v2/replay/PHYSICAL_NESTING_METHODOLOGY.md), `370→500→550→700→900→1060`; los ORI forman pilas de una sola clase hasta `MaxStack`, según las [reglas ORI](../../raw/contexto-v2/docs/v2/replay/PHYSICAL_ORI_STACKING_RULES.md). El usuario confirmó el 2026-09-25 que esas reglas ORI también aplican a `1007137`. El 2026-09-29 confirmó reglas adicionales de transporte: un TBA-0208 vacío puede ir **encima** de un TBC-0208; un TCD-0520, con su tapa dentro, puede entrar en un TLL-0700 o TLL-1060; y tanto TLL-0370 como TLL-0500 pueden entrar dentro de TCD-0520. También precisó la orientación permitida: TCL-1700 **de pie**; TA-3300 y TA-5500 **de pie o de lado**. Se pueden mezclar entregas para maximizar la cantidad transportada: el orden de descarga no limita este cálculo de capacidad. La ficha oficial TBA/TBC describe uso instalado y dice «no apilable», por lo que la regla de transporte procede del operador y no de la ficha. La primera sensibilidad suma círculos de diámetro **superior/máximo** y el rectángulo THL-1000 `0,99 × 1,62 m`; la segunda sustituye únicamente las tres secciones superiores ORI por sus diámetros **inferiores publicados**; para tambores se comparan diámetro del cuerpo (570 mm) y tapa (590 mm). TA de lado se calcula adicionalmente como envolvente rectangular de diámetro × altura nominal, solo para comparar orientaciones. Ninguna suma es un packing tridimensional ni un límite físico demostrado: faltan perfiles por altura, holguras y amarres. No se interpreta `M_Product.Volume=0` como volumen físico cero.

## Checklist

- [x] Releer las 23 líneas de las 7 entregas en conexión nueva a `id-04`, con cantidad, peso y tres medidas por SKU; [consulta reproducible](evidence/qtanque-preshipment-1007137-physical-readonly-2026-09-25.sql).
- [x] Conciliar 32 unidades y 625 kg actuales del maestro con la [consulta de cobertura](evidence/qtanque-preshipment-1007137-coverage-2026-09-25.sql).
- [x] Construir agrupación estructural TLL/ORI y conservar los demás productos como unidades individuales; comprobar conteos y áreas por familia.
- [x] Confirmar alcance de las reglas ORI en `1007137` — declaración del usuario del 2026-09-25; revisar directamente ambos documentos de origen y recalcular la cadena TLL transitiva óptima.
- [x] Calcular sensibilidad de secciones máximas e inferiores ORI en 13,20 m² y corregir la conclusión excesiva de imposibilidad plana — prueba: [cálculo reproducible](evidence/qtanque-preshipment-1007137-area-check.py) y diámetros inferiores de la fuente.
- [x] Registrar la salida del FVR confirmada por el usuario y separar ese resultado de la posición física no documentada de cada grupo.
- [x] Incorporar la regla de transporte TBC+TBA y verificar 2 parejas + 1 TBC solo, cantidad/peso y sensibilidad de huella con cuerpo de 570 mm o tapa de 590 mm; prueba: fichas oficiales de [TBA](https://qtanque.com.ve/product/tambor-abierto/) y [TBC](https://qtanque.com.ve/product/tambor-cerrado/), declaración del operador y [cálculo reproducible](evidence/qtanque-preshipment-1007137-area-check.py).
- [x] Confirmar que un TCD-0520 puede entrar en TLL-0700/1060 con su propia tapa dentro y que el cálculo de capacidad puede mezclar entregas; prueba: declaración del operador del 2026-09-29.
- [x] Confirmar que TLL-0370 y TLL-0500 pueden entrar dentro de TCD-0520; prueba: respuesta afirmativa del operador a la pregunta explícita del 2026-09-29.
- [x] Asignar cada unidad TLL/TCD a una cadena sin duplicados y recalcular grupos y sensibilidades de huella; prueba: ledger de 13 unidades en seis cadenas con ID de línea/ordinal, 193 kg y [cálculo reproducible](evidence/qtanque-preshipment-1007137-area-check.py).
- [x] Registrar TCL de pie y TA de pie o de lado; comparar la envolvente nominal de cada TA en ambas orientaciones — prueba: declaración del operador y [cálculo reproducible](evidence/qtanque-preshipment-1007137-area-check.py).
- [x] Evaluar si el cálculo distingue la orientación histórica de TA-5500 con la variación dimensional oficial de hasta 3 %; prueba: umbral de 1,924 % reproducido por [script](evidence/qtanque-preshipment-1007137-area-check.py) y conclusión de orientación histórica no identificable.
- [ ] Verificar con Operaciones altura real de las pilas ORI y cadenas mixtas, holguras, amarres y acceso.
- [ ] Identificar el FVR concreto y obtener carga admisible después de la carrocería, distribución por ejes y restricciones de ruta antes de emitir un juicio de cabida operacional.

## Evidence

La lectura fresca del predespacho devolvió 23 líneas, 7 `M_InOut_ID`, **32 unidades**, **625 kg** del maestro y tres dimensiones positivas en todas las líneas. El SKU `THL-1000-64` mide 990 × 970 × 1620 mm; los otros cuerpos se evaluaron por diámetro en planta. Se conservan los pesos maestros, incluida la discrepancia TLL-1060 de 23 kg frente a 22 kg catalogados para Q'Gigante.

Los [límites ORI originales](../../raw/contexto-v2/docs/v2/replay/PHYSICAL_ORI_STACKING_RULES.md) son 40 para ORI-0550, 30 para ORI-0850 y 18 para ORI-1250. El usuario confirmó expresamente que aplican a `1007137`. Este predespacho contiene 4, 2 y 4 unidades respectivamente, de modo que cabe una pila homogénea por clase: **10 ORI → 3 pilas**, con diámetro exterior 1037, 1195 y 1328 mm. Compartir una pila entre entregas admite descarga parcial, según `DD-STACKING-001`. La altura de cada pila sigue sin dato físico.

Los 10 TLL son 6×370, 1×500, 1×700 y 2×1060; se suman 3×TCD-0520. Con las compatibilidades confirmadas y entregas mezcladas, esas **13 unidades forman seis cadenas**. Es el mínimo dentro de estas reglas de cadena: hay seis TLL-0370 y no se introducen dos del mismo tamaño uno dentro del otro. La secuencia que permite el nuevo dato es `TLL-0370→TLL-0500→TCD-0520→TLL-0700→TLL-1060`. La [regla TLL original](../../raw/contexto-v2/docs/v2/replay/PHYSICAL_NESTING_METHODOLOGY.md) respalda los saltos TLL; los saltos con TCD proceden de la confirmación del operador del 2026-09-29. El orden de entrega no restringe la búsqueda de capacidad, según su instrucción.

### Cadenas TLL/TCD trazables a cada unidad

El sufijo `#n` distingue una unidad dentro de una línea con cantidad mayor que uno. La flecha va del cuerpo interior al exterior. Esta es una asignación **constructiva posible**, no la posición observada en el FVR.

| Cadena | Unidades por `M_InOutLine_ID#ordinal` | Exterior | Peso maestro |
|---|---|---|---:|
| C1 | `1157080#1` 370 → `1157088#1` 500 → `1157089#1` TCD → `1157079#1` 700 → `1157060#1` 1060 | TLL-1060 | 80 kg |
| C2 | `1157066#1` 370 → `1157064#1` TCD → `1157060#2` 1060 | TLL-1060 | 50 kg |
| C3 | `1157066#2` 370 → `1157064#2` TCD | TCD-0520 | 27 kg |
| C4 | `1157066#3` 370 | TLL-0370 | 12 kg |
| C5 | `1157066#4` 370 | TLL-0370 | 12 kg |
| C6 | `1157081#1` 370 | TLL-0370 | 12 kg |

El [ledger reproducible](evidence/qtanque-preshipment-1007137-area-check.py) verifica que aparecen exactamente una vez las 13 unidades TLL/TCD de las ocho líneas y que suman **193 kg**. Los exteriores son 2×TLL-1060, 1×TCD-0520 y 3×TLL-0370. La cadena C3 tiene un 370 nominalmente más alto (2010 mm) que el TCD exterior (1900 mm); la altura real y sujeción de ese conjunto siguen sin medir.

Las otras 19 unidades forman 3 pilas ORI (4×550, 2×850, 4×1250; **142 kg**), 3 grupos de tambores (dos TBC+TBA y un TBC; **50 kg**) y cuatro cuerpos independientes (TA×2, TCL×1, THL×1; **240 kg**). Se conservan **32 unidades y 625 kg** en **16 grupos estructurales**: 6 + 3 + 3 + 4. Las ORI pueden compartir pila entre entregas por `DD-STACKING-001`; una pila admite descarga parcial. Ningún grupo equivale por sí solo a una posición comprobada del camión.

Las [fichas oficiales TBA](https://qtanque.com.ve/product/tambor-abierto/) y [TBC](https://qtanque.com.ve/product/tambor-cerrado/) publican 570 mm de diámetro del cuerpo, 590 mm de tapa, 880 mm de altura individual y pesos de 7/12 kg. Ambas dicen «no apilable» para instalación/uso. La instrucción específica del operador para transportar vacíos —**un TBA encima de un TBC**— permite en `1056428` dos parejas de 19 kg y un TBC de 12 kg; ninguna entrega se mezcla en estas parejas. No se infiere su altura ensamblada, amarre ni si la tapa sobresale cuando están juntos. Se usa 570 mm para la sensibilidad de cuerpos y 590 mm para la de tapa conservadora.

Las tapas se tratan según la práctica comunicada por el usuario (TLL y TCD dentro de cada tanque; TCL en espacio libre; TA en cabina), pero la posición exacta de las TCL y la viabilidad de las TA en cabina no se han medido. El modelo previo tenía 19 grupos porque TCD se contaba separado; era una etapa provisional antes de confirmar `370/500→TCD→700/1060` y queda sustituido por las seis cadenas mixtas.

| Exterior, escenario de 16 grupos | Cantidad | Diámetro / base (m) | Proyección con cuerpos (m²) |
|---|---:|---:|---:|
| TLL-1060 | 2 | Ø0,780 | 0,956 |
| TCD-0520 | 1 | Ø0,590 | 0,273 |
| TLL-0370 | 3 | Ø0,485 | 0,554 |
| Pilas ORI-0550 / 0850 / 1250 | 3 | Ø1,037 / 1,195 / 1,328 | 3,351 |
| TA-3300 / 5500 | 2 | Ø1,555 / 1,835 | 4,544 |
| Parejas TBC+TBA y TBC solo | 3 | Ø0,570 (cuerpo) | 0,765 |
| TCL-1700 | 1 | Ø1,370 | 1,474 |
| THL-1000 | 1 | 0,990 × 1,620 | 1,604 |
| **Total** | **16** | | **13,522** |

La [cuenta reproducible](evidence/qtanque-preshipment-1007137-area-check.py) usa valores sin redondear. En el escenario vigente de **16 grupos**, la suma de secciones de cuerpo con diámetros exteriores/máximos es **13,521748 m²**. Si los tres grupos de tambores requieren el Ø590 mm de sus tapas, es **13,576412 m²**, frente a **13,200000 m²** de plataforma reportada. El escenario provisional anterior, sin `370/500→TCD`, tenía 19 grupos y 14,253288 m² de secciones de cuerpo. Estas cifras son sumas de secciones separadas, no una prueba de cabida o imposibilidad.

El [catálogo recuperado, página 19](../../raw/contexto-v2/docs/v2/replay/PHYSICAL_FOOTPRINT_METHODOLOGY.md) también publica los diámetros inferiores ORI: 781, 914 y 1050 mm para las tres pilas. La suma de sus secciones circulares inferiores es **2,001082 m²**, frente a **3,351277 m²** de las superiores: diferencia de **1,350194 m²**. Si se sustituyen esas tres secciones en el escenario vigente con Ø590 mm de tapa en tambores, resulta **12,226217 m²**. Queda 0,973783 m² por debajo de los 13,20 m² aritméticos de plataforma, pero **no demuestra un acomodo**: las bocas ORI pueden chocar a otras alturas, los círculos necesitan empaquetarse sin invadir el borde y faltan secciones inferiores de TCL/TA. Es una sensibilidad útil para explicar la carga confirmada, no una representación de posiciones reales.

El operador confirmó que **TCL-1700 va de pie** y que **TA-3300/5500 pueden ir de pie o de lado**. Los [TA-3300](https://qtanque.com.ve/product/tanque-australiano-3300/) y [TA-5500](https://qtanque.com.ve/product/tanque-australiano-5500/) publican Ø1555 × 1755 mm y Ø1835 × 2050 mm. Para comparar orientaciones, cada TA de pie se representa por un círculo de diámetro nominal y cada TA de lado por un rectángulo envolvente de diámetro × altura nominal. No es el área real de contacto de un tanque acostado ni registra cómo se orientó en este viaje.

| TA de lado en la sensibilidad con tapas Ø590 y bases inferiores ORI | Suma de secciones (m²) |
|---|---:|
| Ninguno; ambos TA de pie | 12,226217 |
| Solo TA-3300 | 13,056130 |
| Solo TA-5500 | 13,343355 |
| Ambos TA | 14,173268 |

El rectángulo nominal de TA-3300 de lado agrega **0,829913 m²** sobre su círculo de pie; el de TA-5500 agrega **1,117138 m²**. Así, ambos de pie son la primera hipótesis para minimizar envolventes proyectadas, pero el área aritmética no selecciona por sí sola la orientación histórica ni certifica cabida.

**Auditoría de la orientación de TA-5500 solicitada por el operador:** de lado, la suma nominal excede la plataforma reportada por solo **0,143355 m²**. La [ficha oficial TA-5500](https://qtanque.com.ve/product/tanque-australiano-5500/) advierte que diámetro y altura son aproximados y pueden variar hasta **3 %**. Si únicamente esas dos medidas fueran 3 % menores, la misma cuenta bajaría a **13,121036 m²**; basta una reducción de **1,924 % en cada medida** para llegar a 13,20 m². Esto es una sensibilidad, no una medición de la unidad despachada. Además, la envolvente rectangular de lado y las secciones ORI inferiores pertenecen a alturas distintas y no prueban empaquetamiento. **Resultado:** `TA-5500_ORIENTACION_HISTORICA=NO_IDENTIFICABLE_CON_DATOS_ACTUALES`. De pie es la primera hipótesis de menor envolvente; **no se confirma que así haya viajado**.

**Resultado histórico:** el usuario confirma que el predespacho salió en ese FVR y comunica que no hay foto de la carga. **Modelo reproducible:** la distribución de los grupos en la plataforma sigue sin documentarse; puede involucrar aprovechamiento del estrechamiento de los cuerpos, solapamiento de envolventes a alturas distintas u otra maniobra. No se asigna ninguna como hecho observado. Faltan altura de pilas ORI, geometría 3D de otras familias, placa y carga admisible del FVR concreto. La conclusión correcta es `TRANSPORTE_CONFIRMADO_POR_OPERADOR; ACOMODO_FISICO_NO_RECONSTRUIDO`, sin pronunciamiento sobre seguridad o cumplimiento de la carga.

## Next

Contrastar modelos 3D condicionales de los 16 grupos en 6,00 × 2,20 m con TA-5500 de pie y de lado; TCL siempre de pie. Para identificar la orientación histórica haría falta una medición o testimonio específico de esta carga: las sumas de área y la tolerancia de fabricación no la determinan. Incorporar altura final de cadenas/pilas, geometría por altura de los cuerpos, holguras y amarres. Se conservan las identidades de las siete entregas, pero su orden no limita esta cuenta de capacidad, según el operador.
