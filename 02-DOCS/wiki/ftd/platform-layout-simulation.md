---
branch: feature/dd-product-nesting
status: in-progress
---
# Simulación visual de acomodo en 04R-DBD

## Intent

Explorar el espacio ocupado por los grupos anidados sobre la plataforma 04R-DBD de 12,70 × 2,40 m, priorizando geometría por solicitud del usuario. Primera exploración: guía 1517, elegida como caso de referencia mientras la pregunta opcional sobre otra carga no recibe respuesta.

## Scope

**Orientación confirmada posteriormente:** el usuario indicó que los TA-3300 pueden viajar tanto de pie como acostados. Añadir comparación seleccionable: de pie (círculo Ø1555 mm), acostados a lo largo (envolvente 1755 × 1555 mm), acostados al ancho (1555 × 1755 mm). La envolvente rectangular acostada usa altura y diámetro nominales del catálogo; no simula deformación, apoyo o amarres.

- Visualización interactiva dentro de la conversación; no cambiar el plugin ni escribir en ERP.
- Cantidades de una sola etapa de la guía: movimiento 1010807. No sumar su recepción 1010813.
- 103 unidades: 67 TLL, 4 TA-3300 y 32 ORI.
- Hipótesis de agrupación de la guía: 14 cadenas TLL azules 500→550→700→1060, más 11 TLL-1060 independientes (5 blancos, 6 negros); ORI homogéneo en tres pilas de 11, 16 y 5 unidades. Las reglas están documentadas, pero el acomodo histórico de esos grupos no se observó completo ni se conocen todas las identidades de entrega.
- Diámetros nominales de cuerpo por familia: TLL 500=570, 550=600, 700=670, 1060=780 mm; TA3300=1555; ORI550=1037, ORI850=1195, ORI1050=1263. Procedencia: catálogo/matrices y correspondencias documentadas; no lectura reciente del maestro.
- Vista superior con cuerpos verticales y envolventes circulares; orientaciones fuera de TLL y geometría del conjunto son supuestos de escenario. No modelar deformación, alturas de pilas, tapas exteriores, amarres o descarga.
- Resultado de un algoritmo heurístico, no prueba de acomodo óptimo ni reconstrucción del viaje. Peso fuera de esta exploración por preferencia del usuario; no deducir carga admisible.

## Checklist

- [x] Conservar cantidades e identidades de grupos en ambas vistas.
- [x] Dibujar plataforma y cuerpos a escala; comprobar límites y colisiones.
- [x] Probar interacción, estado y legibilidad a anchos de escritorio y móvil.

## Evidence

La consulta de solo lectura IQP falló con ECONNREFUSED 127.0.0.1:5432. Se usa evidencia local preservada: `dispatch-20261013-movement-guide1517.json`, con movimientos de salida y recepción separados. Diámetros en `qtanque-preshipment-physical-coverage.md`, `qtanque-family-physical-data.md` y SQL de correspondencias. Reglas ORI: 550 hasta 40; 850/1050 hasta 30 por pila homogénea, en el contexto recuperado inmutable.

No hay carpeta Canvas de este proyecto en el inventario de proyectos Cursor del entorno. Se prepara la exploración en conversación con la skill Visualize, conservando la fuente dentro del workspace autorizado.

### Resultado observado

- Exploración local: `output/visualizations/acomodo-plataforma-04r-dbd.html`. No utiliza red ni datos dinámicos de ERP; conserva el estado de interacción local en el visor.
- Colocación geométrica por tangencias de círculos y bordes, sin solapamiento: primero diámetros descendentes, después hasta 40 órdenes deterministas de grupos grandes. No es un optimizador completo ni una prueba de imposibilidad cuando falla.
- Primera ordenación encontró 30/32 grupos; la búsqueda de órdenes encontró **31/32**, con 102/103 unidades colocadas. Queda TLL-1060-10#5 sin posición encontrada. No se oculta esa unidad ni se afirma que el viaje real fuera imposible.
- En todas las vistas se conservaron 103 identidades únicas. Agrupada: 32 grupos, 31 posiciones encontradas. Sin agrupar: 103 grupos, 22 posiciones encontradas. Separación de 100 mm entre grupos: 32 grupos, 22 posiciones encontradas. Todas las posiciones encontradas pasaron límites y colisiones con tolerancia numérica de 0,01 mm.
- El selector mostró correctamente 16 ORI-0850 en su pila homogénea. Las medidas nominales permanecen iguales al cambiar agrupación o separación.
- Vista de escritorio inspeccionada y vista de 360 px inspeccionada; sin desbordamiento horizontal. A 736 px, contenido de 704 px sin desbordamiento. La plataforma gira en pantallas estrechas conservando escala y etiquetas largo/ancho.
- [Validación estructurada](evidence/platform-layout-validation-20261014.json), [captura escritorio](../../attachments/platform-layout-20261014/desktop.png), [captura estrecha](../../attachments/platform-layout-20261014/mobile.png). Las capturas preceden al ajuste final de redacción de coordenadas, sin cambiar geometría.
- La apertura directa file: fue bloqueada por el navegador de pruebas; se usó un servidor temporal exclusivo para la vista, ligado a 127.0.0.1:9041. Navegador cerrado y proceso temporal detenido al finalizar. No se modificaron plugin, instancias ERP, credenciales ni documentos comerciales.

## Next

El usuario precisó que en la guía 1517 los cuatro TA-3300 viajaron de pie. Esta es la orientación inicial confirmada; acostados y mixtos quedan como escenarios alternativos de otras cargas. Contrastar la geometría/altura real de pilas ORI con la operación observada.

### Actualización de orientación TA

El selector cambia la orientación de los cuatro TA de cada escenario: de pie, acostados a lo largo o acostados al ancho. No modifica sus cantidades ni el nesting de TLL/ORI. Las formas acostadas usan una envolvente rectangular nominal, sin representar deformación o sujeción.

Se añadieron colocación y detección de colisiones círculo–rectángulo y rectángulo–rectángulo. La vista de pie conserva el resultado previo 31/32. La búsqueda heurística de diez órdenes para casos mixtos encontró 29/32 acostados a lo largo y 23/32 al ancho; esos números describen posiciones encontradas por el método, no máximos físicos ni preferencia operativa recomendada.

Las tres orientaciones conservaron 103 unidades e identidades únicas, con cero solapamientos y cero cuerpos fuera de los límites. La prueba con separación de 100 mm, sin agrupación y pantalla de 360 px también conservó 103 unidades; sin desbordamiento horizontal. [Resultados](evidence/platform-layout-ta-orientation-20261014.json) y [captura inspeccionada](../../attachments/platform-layout-20261014/ta-crosswise.png). Navegador y servidor de prueba cerrados. Sin cambios en el ERP o plugin.

### Configuración mixta por tanque

El selector de grupo permite elegir uno de los cuatro TA y cambiar solo su posición. El control individual se oculta para grupos distintos de TA. La configuración se identifica como mixta cuando las cuatro posiciones difieren y se conserva por identidad de unidad comercial al cambiar agrupación, separación o recargar. Los presets uniformes siguen aplicándose a los cuatro TA.

Ensayo: TA #1/#3 de pie, #2 al ancho y #4 a lo largo. Se conservaron 103 identidades únicas y 32 grupos; la heurística encontró 28 posiciones, sin solapamientos ni cuerpos fuera de límites. Se verificaron conservación tras desagrupar, recuperación tras recarga y retorno al preset de pie (31 posiciones). Pantalla 360 px sin desbordamiento; controles para no-TA ocultos. Es una combinación de prueba, no reconstrucción histórica de la guía. [Validación](evidence/platform-layout-ta-mixed-20261014.json) y [captura inspeccionada](../../attachments/platform-layout-20261014/ta-mixed.png). Servidor temporal y navegador cerrados.

### Orientación efectiva de la guía confirmada

El operador aclaró posteriormente: «no, fueron todos parados». Los cuatro TA-3300 de la guía 1517 se registran como de pie en la evidencia del caso. La posibilidad de mezclarlos en otras cargas permanece válida, pero no se atribuye a esta guía. La visualización indica ese dato y usa una revisión de estado nueva para no recuperar automáticamente ensayos mixtos anteriores como vista inicial. Cambios posteriores hechos por el usuario se conservan como exploración de alternativas. Verificación de sintaxis JavaScript exit 0; la geometría de pie ya fue comprobada (31/32 posiciones encontradas, sin solapamientos), y no cambió con esta aclaración.

El operador confirmó que en la guía 1520 el TA-11500-64 fue acostado y atravesado: su eje queda transversal al camión. Es otro caso, sobre el FVR AO1BA3A de 6,00 × 2,40 m, no sobre 04R-DBD. Con las dimensiones nominales del catálogo (diámetro 2,56 m y altura axial 2,23 m), la envolvente acostada corresponde a 2,56 m a lo largo × 2,23 m al ancho. Esto orienta el modelo; no verifica por sí solo la cabida de la carga completa. Se conserva la aclaración en el dossier; la visualización presente continúa limitada a 1517 y no incorpora datos de 1520 en esa carga.

- [x] Registrar la orientación transversal de TA-11500-64 en 1520; evidencia: respuesta expresa del operador «atravesado» y comprobación de concordancia entre este documento y el dossier fotográfico.
