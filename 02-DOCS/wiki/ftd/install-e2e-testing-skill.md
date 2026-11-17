# Instalar la skill e2e-testing

## Intent

Equipar este proyecto con la skill de pruebas de navegador aprobada por el usuario para futuros flujos Playwright de iDempiere.

## Scope

- Instalar `e2e-testing` mediante rsc en los asistentes declarados por el proyecto y verificar que quede disponible.
- No modificar pruebas existentes, credenciales, instancias Incus ni bases de datos.
- No instalar otras skills ni cambiar la configuración de asistentes.

## Checklist

- [x] Confirmar que `e2e-testing` no está instalada; prueba: listado de rsc y manifiesto.
- [x] Instalar solo `e2e-testing`; prueba: salida del instalador y manifiesto actualizado.
- [x] Verificar disponibilidad y ausencia de cambios ajenos; prueba: listado de rsc y revisión de estado/diff.

## Evidence

- El manifiesto `.rsc.json` anterior al cambio no contiene `e2e-testing` y declara los destinos `codex` y `cursor`.
- `npx @ericrisco/rsc list` antes del cambio no mostró `e2e-testing`.
- `npx @ericrisco/rsc add e2e-testing` terminó con `Installed for codex, cursor: e2e-testing` e indicó recargar o reiniciar el asistente para activarla.
- El listado posterior incluye `skill e2e-testing`; `.rsc.json` contiene la entrada; existen `.rsc/skills/e2e-testing/SKILL.md`, el enlace de Codex y la regla de Cursor. `git diff --check` no reportó errores y no se modificaron pruebas ni instancias.

## Next

Recargar el asistente antes de usar la skill para diseñar un flujo autenticado del plugin; acordar primero cuenta de ensayo y escenario de datos.
