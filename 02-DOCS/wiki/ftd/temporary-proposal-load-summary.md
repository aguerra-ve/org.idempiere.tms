# Resumen de capacidad física en la propuesta temporal

## Intent

Mostrar en la propuesta temporal la diferencia entre cantidad comercial, unidades físicas resultantes
del nesting y vehículo candidato, evitando presentar la cantidad de pedidos o el peso como una
capacidad vehicular ya comprobada.

## Scope

- Añadir al resumen temporal las tres etapas: unidades comerciales, unidades después del nesting y
  vehículo candidato.
- Usar la cantidad pendiente seleccionada como total comercial.
- Mantener nesting y vehículo en estado pendiente mientras el mapa no consulte líneas de producto,
  reglas físicas y flota disponible.
- No calcular nesting, no seleccionar vehículos, no validar cabida y no guardar documentos.

## Checklist

- [x] Modelar el estado pendiente sin confundirlo con cero; prueba: smoke test del resumen.
- [x] Mostrar las tres etapas en el diálogo existente; prueba: smoke test del texto escapado.
- [x] Compilar y verificar el cambio; prueba: ejecución del smoke test y compilación completa.

## Evidence

- La prueba se ejecutó primero contra el comportamiento anterior y falló en
  `commercial quantity is shown as the first capacity stage`.
- `TemporaryRouteProposalSmoke` pasó después con cantidad comercial `5.5`, nesting ausente sin
  convertirlo a cero, vehículo pendiente y las tres líneas visibles en el texto del diálogo.
- El reactor temporal de iDempiere 12 compiló 156 fuentes y terminó `BUILD SUCCESS`.
- El JAR SHA-256 `dadb3209bd2022780a7eb3cade391aa6b30822ac5b17ce224e7b51a4c93f3062`
  se instaló solo en `id-03-tms-map-test` como revisión `r10`. El bundle 406 quedó `Active` en
  versión `12.0.0.202609230134` y `TMSFormFactory` quedó `ACTIVE`.
- La WebUI del clon respondió HTTP 200 y la prueba Playwright no autenticada de acceso pasó `1/1`.
  La visualización autenticada del nuevo texto queda para comprobación manual del propietario.

## Next

Conectar en una tarea posterior las líneas reales de los pedidos al resumen para calcular unidades
físicas únicamente con reglas de nesting confirmadas; después incorporar la selección explícita de
un vehículo de `DD_Vehicle`.
