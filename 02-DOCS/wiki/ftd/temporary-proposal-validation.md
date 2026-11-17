# Validación de propuesta temporal

## Intent

Permitir que el despachador revalide, sin escribir en el ERP, los pedidos de la propuesta temporal antes de considerar cualquier aprobación o creación documental.

## Scope

- Añadir `Validar propuesta`, habilitado cuando exista una selección.
- Volver a consultar el universo autorizado para detectar pedidos que dejaron de ser elegibles.
- Clasificar cada pedido como `ACEPTADO`, `ADVERTIDO` o `BLOQUEADO` usando saldo, elegibilidad documental, almacén, dirección y entrega preparada.
- Mostrar un informe temporal con totales y razones por pedido, codificando los datos ERP para el diálogo HTML.
- No persistir propuestas, reclamar pedidos, consultar reservas/inventario, optimizar rutas ni crear `DD_Freight` o `M_InOut`.

## Checklist

- [ ] Definir y probar las reglas de clasificación; prueba: smoke test Java para aceptado, advertido, bloqueado y pedido desaparecido.
- [ ] Integrar la reconsulta y el diálogo en el formulario; prueba: compilación completa del plugin.
- [ ] Desplegar y verificar en el clon; prueba: bundle/fábrica activos, WebUI disponible y comprobación visible.

## Evidence

Pendiente.

## Next

Codificar primero el fallo esperado de las reglas de validación, antes de conectarlas a la interfaz.
