# Propuesta temporal de ruta

## Intent

Añadir al mapa de predespacho una acción que convierta la selección actual de pedidos en un resumen temporal para revisión operativa, sin crear ni modificar documentos de iDempiere.

## Scope

- Incluir un botón `Revisar propuesta temporal`, habilitado cuando exista al menos un pedido seleccionable elegido.
- Mostrar los pedidos seleccionados con cliente, territorio, almacén y cantidad pendiente, además de totales básicos.
- Indicar de forma visible que la propuesta no está guardada.
- No crear `DD_Freight`, entregas, reservas, rutas optimizadas ni cambios de inventario.

## Checklist

- [x] Extraer un resumen determinista de la selección; prueba: smoke test Java con selección vacía y múltiple.
- [x] Integrar el botón y el diálogo temporal en el formulario; prueba: compilación del plugin, despliegue aislado y verificación del bundle.
- [x] Verificar el flujo visible en el clon aislado; prueba: confirmación manual del propietario con sesión autenticada.

## Evidence

- `TemporaryRouteProposalSmoke` pasó con selección vacía y múltiple, exclusión de históricos, orden estable, suma `5.5`, conteos, aviso de no persistencia y codificación HTML de datos ERP.
- El reactor temporal compiló el plugin completo con Maven/Tycho y produjo el JAR de hash SHA-256 `30a33d1c4d77aefe4e34809c6de7ce0461a69f9537d8d068057209b5b24e5854`.
- El JAR final se instaló solo en `id-03-tms-map-test` como revisión `r9`; el bundle 406 quedó `Active` en versión `12.0.0.202609222242` y `TMSFormFactory` quedó `ACTIVE`.
- La WebUI respondió HTTP 200 y Playwright comprobó la pantalla de acceso (`1 passed`). Las dos pruebas autenticadas quedaron omitidas porque esta sesión no tiene las variables de la cuenta de ensayo; el diálogo no tiene verificación automatizada autenticada, pero sí la comprobación manual indicada a continuación.
- El propietario cerró y reabrió el formulario en el clon, confirmó que apareció `Revisar propuesta temporal` y que, después de seleccionar pedidos, el botón abrió la propuesta correspondiente. Esta comprobación manual completa la validación visible sin incorporar credenciales al repositorio.

## Next

Definir en una tarea posterior, y solo con decisión explícita, si la propuesta temporal evolucionará hacia un documento persistente de flete/ruta.
