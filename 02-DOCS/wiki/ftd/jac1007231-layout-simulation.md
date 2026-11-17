---
branch: feature/dd-product-nesting
status: in-progress
---
# Acomodo JAC / predespacho 1007231

## Intent

Explorar en conversación la carga despachada en el JAC A89EO19, con plataforma fija reportada de 6540 × 2360 mm.

## Scope

**Escenario provisional autorizado 2026-10-15:** el operador indicó que cree que el ancho es2,54m, y autorizó recalcular con ese ancho provisional. Usar6540×2540mm únicamente en la exploración, conservando2360mm como reporte anterior. No actualizar flota, SQL ni ERP con una medida estimada. TA11500 mantiene diámetro nominal2560mm: diferencia residual20mm; no reducirlo automáticamente por tolerancia de fabricación. Orientación de pie, ubicación trasera y color físico azul permanecen confirmados por el operador.

Usar las ocho líneas de `dispatch-20261013-preshipment-1007231.json`, sumando QtyDispatched=13, sin usar MovementQty=19. Conservar origen documental por línea y entrega. No cambiar plugin, ERP o documentos comerciales.

Medidas nominales locales: TLL550 Ø600, TLL1060 Ø780; TA11500 Ø2560 × altura2230, TA5500 Ø1835 × altura2050, TA3300 Ø1555 × altura1755; TBA0208 Ø570 × altura880 mm. TBU2200/3200 quedan pendientes de geometría: el catálogo nombra TB/TBD, sin correspondencia explícita con TBU. No atribuir dimensiones cero del ERP a cuerpos físicos válidos.

Orientaciones TA confirmadas posteriormente por el operador: los tres viajaron de pie; TA11500 va de último, hacia la parte trasera. Controles por unidad permiten escenarios alternativos, sin atribuirlos al viaje. No transferir posición del TA11500 de guía1520 a este envío. Hipótesis opcional de una cadena TLL550→1060 (misma entrega1057137); demás1060 y tambores independientes. Sin anidado TA/TBU ni tambores supuesto. Vista2D nominal sin altura, deformación, amarres o peso. Fotos04RDBD son referencias generales, no evidencia específica del JAC.

## Checklist

- [x] Conservar 13 identidades y origen de las ocho líneas en todas las vistas.
- [x] Comprobar posición nominal, límites y colisiones con orientaciones distintas.
- [x] Verificar controles, estado y legibilidad en móvil y escritorio.

## Evidence

[Dossier](dispatch-photo-evidence-20261013.md), [líneas preservadas](evidence/dispatch-20261013-preshipment-1007231.json), catálogo local y correspondencias documentadas. No consulta reciente a ERP.

Fuente: `output/visualizations/acomodo-jac-predespacho-1007231.html`. JavaScript: `node --check`, exit0. [Pruebas observadas](evidence/jac1007231-layout-validation-20261014.json): todas las configuraciones conservaron13 identidades y dosTBU pendientes, con origen por línea/entrega. Cero colisiones y cero cuerpos colocados fuera de límites (tolerancia0,01mm).

Escenario inicial TA de pie:9/10 grupos dimensionados colocados; TA11500 sin posición porque Ø2560 supera ancho2360. TA11500 a lo largo tampoco entra dentro del ancho nominal. TA11500 atravesado con los otrosTA de pie:9/10, queda TLL1060#5 sin posición encontrada por la heurística; no prueba imposibilidad. Mezcla de prueba TA11500/3300 transversales y TA5500 longitudinal:5/10; sin agrupar6/11; separación100mm3/10. Son escenarios de prueba, no orientaciones históricas confirmadas.

Restauración tras recarga conserva orientaciones y separación100mm. Control de orientación oculto para TLL; cadena muestra TLL550+1060 y entrega1057137. Escritorio736px y móvil360/320px sin desbordamiento horizontal. Capturas escritorio/móvil inspeccionadas en `output/playwright/iqp-jac1007231-20261014/`. La primera ejecución de la prueba falló por DNS restringido de npm; la repetición autorizada fuera del aislamiento completó todas las comprobaciones.

## Next

El operador confirmó «de pie y el ta-11500 va de último» al preguntarle por los tres TA. Se registra posición trasera del TA11500 y orientación de pie de los tres. La revisión de estado nueva evita restaurar mezclas de prueba previas como vista inicial. Se marca el sentido cabina→trasera, sin asignar coordenadas exactas al TA11500 que no encuentra posición dentro de los límites nominales.

Al preguntarle si sobresalía por los lados, el operador respondió «no, iba en el medio». Se registra el TA11500 centrado al ancho, en la posición trasera previamente confirmada, sin sobresalir lateralmente. «En el medio» responde a la posición lateral; no reemplaza la aclaración anterior «va de último» por una posición longitudinal intermedia.

Conciliar diámetro nominal TA11500=2560mm con ancho reportado JAC=2360mm (diferencia total200mm): la confirmación de centrado sin sobresalir hace necesaria una medición o aclaración dimensional, no autoriza a modificar medidas ni suponer deformación. Correspondencia dimensional TBU también pendiente. Las pruebas anteriores siguen siendo evidencia geométrica de escenarios, no reconstrucción del viaje.

### Fotografías del JAC y color efectivo

Se localizaron tres tomas adicionales en `/home/aguerrave/Descargas/WhatsApp Unknown 2026-10-13 at 09.55.27/`, con nombres `WhatsApp Image 2026-10-07 at 10.37.52.jpeg`, `(1).jpeg` y `(2).jpeg`. Se inspeccionaron directamente, sin transformar ni copiar. El tanque grande trasero es azul y está de pie. Justo delante se ven cuerpos altos con forma de botella; hacia la cabina otros cilindros y un cuerpo blanco. Los códigos no son legibles; tambores e interiores anidados no quedan identificados completamente por estas tomas.

El operador identifica el tanque trasero como TA11500 y confirma su color real: «es azul». Se conserva la diferencia con la línea documental1039851, código `TA-11500-10`, nombre blanco. Color físico confirmado azul; identidad documental permanece tal como se consultó, sin cambiar el JSON fuente ni escribir en ERP. El código de variante azul no se sustituye automáticamente por inferencia del sufijo.

### Recálculo provisional 2540mm — 2026-10-15

[Resultados observados](evidence/jac-width2540-validation-20261015.json): ejecución JavaScript en Node con DOM mínimo, 13 identidades únicas y dos TBU pendientes. Agrupado9/10 posiciones; sin agrupar10/11; agrupado con separación100mm9/10. En los tres casos el único cuerpo sin posición es TA11500 nominalØ2560. Los cuerpos colocados pasan límites con tolerancia0,01mm; no se repitió la inspección de navegador de la versión anterior. `node --check` exit0. La revisión de estado descarta escenarios guardados de2360mm; medidas del tanque permanecen nominales. Diferencia residual2540frente2560=20mm. No se modificó la flota en ERP ni en el clon.
