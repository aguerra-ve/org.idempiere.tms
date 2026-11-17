---
author: Alejandro Guerra
branch: feature/dd-product-nesting
status: in-progress
---
# Evidencia fotográfica y documental de cargas — 13/10/2026

## Intent
Consolidar las fotografías aportadas, las confirmaciones del operador y los documentos ERP consultados en solo lectura para los casos FVR/guía 1520, JAC/predespacho 1007231 y Cargo/guía 1517.

## Scope
Evidencia local en wiki y attachments; copia íntegra de 26 fotos, inventario SHA-256, resultados y consultas reproducibles, conciliación de cantidades, observaciones y límites. Sin modificar ERP, código de producto, credenciales, raw, ramas ni commits. Las hipótesis no se convierten en reglas confirmadas.

## Checklist
- [x] Preservar e identificar las 26 imágenes con hashes y origen.
- [x] Reconsultar documentos ERP y conservar resultados y SQL de solo lectura.
- [x] Conciliar guía 1520 con movimiento 1010814, excluyendo los 14 pupitres del viaje por confirmación del operador.
- [x] Completar evidencia del predespacho 1007231 y buscar documento de guía 1517.
- [x] Consolidar medidas útiles por placa, reglas operativas, observaciones e incertidumbres.
- [x] Verificar integridad, enlaces, cálculos y cobertura antes de cerrar.
- [x] Aclarar la placa fotográfica 04R DBD: el operador confirmó que son ejemplos generales de carga, sin vínculo con el JAC A89EO19.

## Evidence

### Fuentes y procedencia

- [Inventario de 26 fotos](../../attachments/dispatch-20261013/README.md), copiadas sin modificación desde las tres carpetas WhatsApp indicadas por el usuario: `09.47.42` (4), `09.55.34` (4) y `10.23.06` (18).
- [Manifiesto SHA-256 y rutas originales](../../attachments/dispatch-20261013/manifest.json). La fecha del nombre WhatsApp no prueba la captura. Las fotos muestran varios vehículos/envíos; una carpeta no constituye una identidad de viaje.
- Confirmaciones del operador en este chat del 13/10/2026: dimensiones útiles fijas por placa; identidad de tres casos y conductores; apoyo directo de los cilindros negros; exclusión de pupitres del FVR; uso del orden TLL ya documentado.
- [Estado de la conexión IQP](evidence/dispatch-20261013-connection.json): `transaction_read_only=on`, base `idempiere`, consulta del 13/10/2026. Los SQL guardados son SELECT con alcance `AD_Client_ID=1000000`. No se usaron credenciales locales ni se escribieron datos en ERP.
- [Conciliación calculada](evidence/dispatch-20261013-reconciliation.json): cantidades y pesos con aritmética decimal. Los kg son pesos del maestro actual, no pesaje de báscula ni carga admisible del vehículo.

### Vehículos y medidas útiles confirmadas por el operador

**Orientaciones efectivas aclaradas posteriormente:** en la guía 1517 los cuatro TA-3300 viajaron de pie. En la guía 1520, el TA-11500-64 viajó acostado y atravesado, junto a la cabina, confirmado expresamente por el operador. Su eje queda transversal al camión. La distancia exacta a la cabina no fue medida. No trasladar la orientación de un viaje al otro. La posibilidad general de combinar TA de pie y acostados en otras cargas no describe la composición de 1517.

- **FVR AO1BA3A:** 6,00 m de largo × 2,40 m de ancho. José Contreras, Transporte LC, guía 1520.
- **JAC de planta A89EO19:** 6,54 × 2,36 m. Wilmer Moreno, predespacho 1007231.
- **Cargo 1721, chuto de planta, 55EJAG:** plataforma asociada reportada de 12,70 × 2,40 m. Oscar Gómez, guía 1517. El 14/10/2026 el operador confirmó placa propia de plataforma **04R-DBD**; esas medidas pertenecen a la plataforma, no al tractor.

La plataforma es fija **para la unidad identificada por placa**. No extrapolar medidas por modelo ni trasladarlas a otros FVR, JAC o Cargo. Esta consolidación inicial no creó registros `DD_Vehicle`. El 14/10/2026 el usuario autorizó el [registro de las tres unidades en el clon](register-reported-fleet-clone.md), con procedencia y datos pendientes explícitos. La confirmación posterior separa **55EJAG (chuto)** de **04R-DBD (plataforma)**; [corrección en el clon](chuto-platform-identity.md). No presupone combinación permanente ni identifica un viaje por una foto aislada.

**Asociación aclarada por el operador:** [G2-01](../../attachments/dispatch-20261013/G2-01.jpeg) y [G3-05](../../attachments/dispatch-20261013/G3-05.jpeg), con placa `04R DBD`, son ejemplos de carga usados como referencia general. No corresponden a evidencia específica del JAC `A89EO19` ni se les asignan sus dimensiones o el predespacho 1007231. [Confirmación textual del operador](evidence/dispatch-20261013-operator-clarification.json). La carpeta WhatsApp mezcla ejemplos; no identifica por sí sola un viaje.

### Caso FVR / guía 1520: reposición a Caracas

El operador vinculó la guía 1520 al movimiento **1010814**, `DocumentNo=620173`. La consulta adicional identifica la salida de planta **1010812**, `DocumentNo=620171`. Ambas etapas están `CO`, con fecha 07/10/2026:

- 1010812: `QTANQUE SANTA CRUZ → TRANSITO CARACAS`.
- 1010814: `TRANSITO CARACAS → QTANQUE CARACAS`.

[Salida: resultado](evidence/dispatch-20261013-movement-1010812.json) · [SQL](evidence/dispatch-20261013-movement-1010812.sql).
[Recepción: resultado](evidence/dispatch-20261013-movement-1010814.json) · [SQL](evidence/dispatch-20261013-movement-1010814.sql).

Las dos listas coinciden en productos/cantidades. **No sumar sus unidades ni pesos**: son etapas contables, no prueba de dos cargas físicas distintas.

Contenido de la [guía fotografiada G1-04](../../attachments/dispatch-20261013/G1-04.jpeg), coincidente con las cinco primeras líneas ERP:

- 8 × `TLL-0500-00`, negro, 13 kg/unidad: 104 kg.
- 8 × `TLL-0550-00`, negro, 14 kg/unidad: 112 kg.
- 9 × `TLL-1060-64`, azul, 23 kg/unidad: 207 kg.
- 1 × `TA-11500-64`, azul, 200 kg.
- 2 × `TP-0000-00-TAPA IMPORTADA-00`, 1 kg/unidad: 2 kg.

**Carga identificada: 26 tanques + 2 tapas = 28 unidades, 625 kg.** Las tapas se conservan como línea comercial separada porque así aparecen en guía/movimiento; no sumar nuevamente tapas ordinarias cuya inclusión en otros pesos no se haya verificado.

**Ubicación de las tapas:** el operador indicó «o con el chofer o sobre todos los tanques». Se registran dos alternativas: con el chofer o sobre el conjunto de tanques. La respuesta no determina cuál se aplicó a la guía 1520 ni confirma dimensiones, apoyo o ubicación exacta. No atribuir a estas dos tapas la ubicación dentro de cuerpos observada en otras fotos.

Los movimientos incluyen además 14 × `PID-COV5-64` (168 kg). El operador declaró que **no viajaron en este FVR**. Se excluyen únicamente del caso físico; no se elimina ni altera la línea ERP. Totales documentales por etapa: 42 unidades / 793 kg; diferencia documentada respecto al viaje: 14 unidades / 168 kg.

La guía impresa está fechada **06/10/2026**, los movimientos **07/10/2026**. La diferencia se conserva; no inferir de ella hora o día exacto del viaje.

[G1-02](../../attachments/dispatch-20261013/G1-02.jpeg) muestra un tanque grande acostado transversalmente y grupos cilíndricos de pie. La identificación del grande como TA-11500 se apoya en guía y operador, no en una etiqueta legible de la foto. Las dimensiones del vehículo son reportadas; la foto no las mide.

**Disposición relativa confirmada:** ante la pregunta de si los TLL iban detrás del TA grande como en la simulación, el operador respondió «sí, en las fotos se ven». Se registra el TA transversal junto a la cabina y los TLL detrás. Esta confirmación no determina coordenadas exactas ni composición de cada grupo anidado.

**Hipótesis de composición, no reconstrucción confirmada:** ocho cadenas `500 → 550 → 1060` más un 1060 independiente; 25 TLL → 9 grupos TLL. Se conserva cada cantidad (8+8+9), más TA y tapas. El usuario respondió «es lo más seguro», no confirmó cada grupo. Las fotos posteriores de otros envíos ilustran el mecanismo, pero no convierten esta hipótesis en observación del envío 1520. No hay plano con posiciones ni demostración geométrica de cabida.

Este caso es diferente del predespacho **1007137** (32 unidades, 625 kg y plataforma reportada de 6,00 × 2,20 m). La coincidencia de 625 kg no los identifica. No sustituir el ancho de aquel caso por 2,40 m.

### Caso JAC / predespacho 1007231

[Resultado completo](evidence/dispatch-20261013-preshipment-1007231.json) · [SQL](evidence/dispatch-20261013-preshipment-1007231.sql).

`DocumentNo=1007231`, fecha 06/10/2026, estado `CO`. Ocho líneas, tres entregas origen distintas (1057131, 1057132, 1057137):

- 1 × TA-11500 blanco: 200 kg.
- 1 × TBU-3200 azul: 55 kg.
- 1 × TBU-2200 azul: 42 kg.
- 1 × TLL-550 blanco: 14 kg.
- 5 × TLL-1060 azul: 115 kg.
- 1 × TA-3300 azul: 55 kg.
- 1 × TA-5500 azul: 92 kg.
- 2 × TBA-208 azul: 14 kg.

**13 unidades / 587 kg**, coincidentes con la cabecera. Se usa `QtyDispatched`, no `MovementQty`: la suma de esta última es 19; TLL-1060 contiene 10 de origen y 5 despachadas, TA-5500 contiene 2 y 1 respectivamente. No atribuir las seis unidades de diferencia a esta carga.

`ShelfWidth`, `ShelfHeight` y `ShelfDepth` son cero en las ocho líneas del maestro consultado. Esto no son medidas físicas válidas. Los datos dimensionados anteriormente en `id-04` no se presuponen presentes en esta conexión IQP ni se copian al ERP.

La vinculación placa/documento procede del operador. Las fotos de placa `04R DBD` son referencias generales, según aclaración posterior del operador, y no se atribuyen a este predespacho. No se prueba la orientación ni anidado de cada una de estas 13 unidades por simple coincidencia de carpeta.

**Orientación aclarada por el operador:** al consultar por TA11500, TA5500 y TA3300 del JAC, respondió «de pie y el ta-11500 va de último». Los tres se registran de pie y el TA11500 hacia la parte trasera. Al preguntarle si sobresalía por los lados, respondió «no, iba en el medio»: centrado al ancho y sin sobresalir lateralmente; se conserva su posición longitudinal trasera. El diámetro nominal TA11500 de2560mm supera el ancho reportado del JAC de2360mm en200mm. La respuesta no mide dimensiones efectivas ni deformación o coordenadas exactas; conciliar esa diferencia antes de validar cabida.

**Color físico confirmado:** tres tomas del JAC localizadas posteriormente en la carpeta de Descargas `WhatsApp Unknown 2026-10-13 at 09.55.27`, archivos de las10.37.52 (sin sufijo, `(1)` y `(2)`), muestran el tanque grande trasero azul y de pie. El operador lo identifica como TA11500 y confirma «es azul». El maestro/línea preservada del predespacho registra `TA-11500-10`, blanco: se conserva esa discrepancia documental, sin alterar el resultado fuente ni el ERP. Estas tres fotos son adicionales a los26 originales del manifiesto anterior; se inspeccionaron en su ruta original, sin incorporarlas silenciosamente a ese inventario.

### Caso Cargo / guía 1517: reposición a Barquisimeto

La búsqueda inicial en `IVS_PreShipment.DocumentNo` no identificó esta guía. `1001517`/`1001520` son documentos de abril de 2023 y se descartan para estos casos. [Búsqueda reproducible](evidence/dispatch-20261013-shipment-search.sql) · [resultado](evidence/dispatch-20261013-shipment-search.json).

La búsqueda en referencias de movimientos sí encuentra menciones explícitas **«GUIA 1517»** y **«GUIA 001517»**:
[Referencias sanitizadas](evidence/dispatch-20261013-guide-references.json) · [SQL](evidence/dispatch-20261013-guide-references.sql).

- **1010807 / 620166**, 06/10/2026, `CO`: `QTANQUE SANTA CRUZ → TRANSITO BARQUISIMETO`.
- **1010813 / 620172**, 07/10/2026, `CO`: `TRANSITO BARQUISIMETO → QTANQUE BARQUISIMETO`.

[Inventarios de ambas etapas](evidence/dispatch-20261013-movement-guide1517.json) · [SQL](evidence/dispatch-20261013-movement-guide1517.sql). Coinciden exactamente en estas diez líneas:

- 5 × TLL-1060 blanco: 115 kg.
- 14 × TLL-500 azul: 182 kg.
- 14 × TLL-550 azul: 196 kg.
- 14 × TLL-700 azul: 238 kg.
- 14 × TLL-1060 azul: 322 kg.
- 6 × TLL-1060 negro: 138 kg.
- 4 × TA-3300 azul: 220 kg.
- 11 × ORI-550 negro: 99 kg.
- 5 × ORI-1050 negro: 85 kg.
- 16 × ORI-850 azul: 208 kg.

**103 unidades / 1.803 kg por etapa, sin duplicarlas.** El vínculo guía→movimientos está respaldado por texto explícito ERP; vehículo/conductor/medidas siguen teniendo como fuente al operador. No se ha fotografiado aquí la guía 1517.

El inventario confirma **seis TLL-1060 negros en el documento**. No demuestra que cada cuerpo negro visible sea uno de esos seis: también hay ORI negros. El usuario confirmó apoyo directo de los cilindros negros sobre plataforma; su identificación visual como 1060 era probable («deben ser 1060»). Se mantienen separados ambos niveles de evidencia.

Una composición compatible con la regla TLL permitiría 14 cadenas `500→550→700→1060`, cinco TLL-1060 blancos y seis negros independientes: 67 TLL → 25 grupos TLL. Es una **hipótesis estructural**, no asignación real de unidades a grupos. No asignar pilas ORI, posiciones ni ahorros TA sin evidencia del caso; tampoco mezclar cadenas de entregas distintas automáticamente en el motor activo.

### Compilación: observaciones y reglas

[Foto de detalle G3-10](../../attachments/dispatch-20261013/G3-10.jpeg) · [vista de grupos G3-13](../../attachments/dispatch-20261013/G3-13.jpeg) · [vista general G3-15](../../attachments/dispatch-20261013/G3-15.jpeg).

1. **Observado:** cuerpos dentro de otros cuerpos abiertos; bocas hacia arriba. Algunos contornos interiores se ven ovalados, inclinados o descentrados. Esto es compatible con flexión durante el acomodo; no mide deformación admisible ni demuestra compatibilidad adicional entre SKU.
2. **Observado:** tapas transportadas dentro de cuerpos, en posiciones inclinadas/casi verticales. Una curva visible no equivale necesariamente a otro tanque. No hay conteo exacto de interiores a partir de estas fotos.
3. **Confirmación del operador:** se usa el orden ya documentado, `370→500→550→700→900→1060`, de interior pequeño a exterior mayor, con los tamaños disponibles. [Regla preexistente](../logistics/Physical%20Nesting%20Model.md). La compilación no muestra etiquetas suficientes para identificar todos los tamaños ni obliga a que cada grupo contenga los seis.
4. **No demostrado:** interiores paralelos independientes, límites nuevos de apilado, altura exacta de cadenas, holguras, volumen efectivo, carga admisible, distribución por ejes, plano final o compatibilidad de cualquier par solo por flexibilidad. Las cuerdas visibles no certifican su capacidad de sujeción.
5. **Identidad:** cada unidad comercial debe conservar su origen documental; cada grupo físico necesita miembros, orientación y dimensiones verificadas. Una transferencia de inventario puede incluir mercancía de otros viajes, como los pupitres, y salida/recepción pueden representar la misma mercancía.
6. **Plataformas:** las últimas dos fotos muestran un remolque vacío. No se le asignan las medidas de otro vehículo por parecido. Las fotos con placa `04R DBD` son referencias generales confirmadas por el operador, sin asignación al JAC.

### Cobertura y límites abiertos

Los tres casos tienen identificación declarada y medidas útiles; los tres documentos/cadenas ERP ya están localizados. Las 26 imágenes quedan preservadas e inventariadas. Las incertidumbres **no se resuelven con inferencias**:

- Correspondencia `04R DBD`: resuelta como referencia general, fuera de la evidencia específica del JAC/predespacho 1007231.
- Asignación concreta de miembros y posiciones a cada grupo del FVR/Cargo: no registrada. Confirmar práctica general no equivale a reconstruir esos viajes.
- Identidad y documentos de los demás vehículos de la compilación: no aportados; sirven como evidencia de manejo general.
- Alto útil/restricciones, carga admisible, holguras y amarres por unidad: no medidos. No se declara validación de cabida ni se implementa selección automática.

### Búsqueda adicional de identidad fotográfica

El 13/10/2026 se buscó `04R DBD` y `A89EO19` (incluida variante `A89E019`), normalizando espacios y signos en las descripciones de predespachos del tenant 1000000 entre 01/09 y 13/10/2026. Solo se devolvió el documento 1007231 solicitado, sin mención de ninguna placa. [SQL](evidence/dispatch-20261013-plate-search.sql) · [resultado](evidence/dispatch-20261013-plate-search.json).

La inspección de esquema no encontró tablas con `vehicle`/`fleet` en el nombre ni columnas explícitas `plate`/`licenseplate`/`licenceplate`/`placa`. [Consulta de metadatos](evidence/dispatch-20261013-vehicle-schema.sql) · [resultado](evidence/dispatch-20261013-vehicle-schema.json). Es una búsqueda acotada; no demuestra que la identidad no exista en otro módulo o campo.

Estas consultas no vincularon la placa al JAC. Posteriormente el operador aclaró que esas fotos eran ejemplos generales; por ello no se requiere un vínculo ERP para usarlas como referencia de manejo. La búsqueda queda como antecedente, no como bloqueo. No hay procesos ni consultas pendientes en segundo plano.

### Verificación observada

[Resultado de auditoría](evidence/dispatch-20261013-verification.json): 26 copias idénticas a originales, SHA-256 y decodificación JPEG correctos; enlaces locales resueltos; diez pares SQL/JSON revisados; conexión en solo lectura. Los cálculos verifican 42/793 frente a 28/625 para FVR, 13/587 para JAC y 103/1803 por etapa Cargo. La correspondencia de placa se cerró mediante aclaración del operador como referencia general. La comprobación técnica no demuestra cabida física.

**Hallazgo ajeno a esta actualización:** la línea base de raw valida 343/344 archivos. `02-DOCS/raw/contexto-v2/docs/v2/GAP_MATRIX.md` difiere del hash histórico; su fecha de modificación es anterior al trabajo del 13/10/2026. Los hashes esperado/actual y fecha constan en la auditoría. No se modifica raw, no se renueva la línea base y no se declara integridad completa de raw.

## Next

Consolidación de evidencia completada: fotos preservadas, documentos conciliados, aclaración del operador incorporada y verificaciones registradas. Las composiciones probables y limitaciones físicas permanecen explícitas; cualquier validación de cabida o cambio de producto/ERP requiere una tarea posterior.
