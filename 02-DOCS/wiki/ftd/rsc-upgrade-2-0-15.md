# Actualización de RSC a 2.0.15

## Intent

Actualizar el harness RSC declarado en este repositorio de 2.0.5 a 2.0.15, autorizado por el usuario el 2026-09-29, para corregir los hooks de memoria y conservar el soporte de Codex y Cursor.

## Scope

Solo archivos administrados por RSC dentro de `/home/aguerrave/proyectos/iqp-tms-idempiere`. No se modifica código del plugin, datos de `id-04`, archivos `.env`, credenciales ni trabajo ajeno. No se crea commit. Se usa la versión exacta 2.0.15, no `latest`.

## Checklist

- [x] Registrar estado previo — `git status --short`; `.rsc.json` y `.rsc/.version` declaran 2.0.5, 33 skills, 4 agentes, destinos Codex y Cursor.
- [x] Comparar paquetes 2.0.5/2.0.15 — 273 skills del catálogo y agentes sin cambios; diferencias en CLI y hooks, especialmente memoria, detección de enlaces y opciones de exclusión.
- [x] Ejecutar vista previa de sincronización de ambos destinos y revisar rutas afectadas — `npx --yes @ericrisco/rsc@2.0.15 sync --dry-run --target codex,cursor` terminó con código 0; enumeró 33 skills para cada destino y solo rutas administradas del harness, incluido `AGENTS.md`.
- [x] Sincronizar con la versión fijada 2.0.15 y verificar que RSC crea respaldo — `npx --yes @ericrisco/rsc@2.0.15 sync --target codex,cursor` terminó con código 0; respaldos `20260929-123613-sync-codex` (79 entradas) y `20260929-123613-sync-cursor` (96 entradas).
- [x] Verificar `.rsc.json`, `.rsc/.version`, estados de Codex/Cursor y diagnóstico RSC; comprobar que el trabajo no relacionado sigue intacto — CLI y proyecto reportan 2.0.15; `doctor` por destino sin faltantes y con memoria `ready`; ocho hashes de archivos locales ajenos al cambio quedaron iguales.

## Evidence

La versión pública 2.0.15 se confirmó en npm. Los paquetes oficiales `@ericrisco/rsc@2.0.5` y `@ericrisco/rsc@2.0.15` se descargaron solo a `/tmp` para comparar; difieren en 12 archivos y agregan 2, sin cambios de contenido bajo `skills/`.

La vista previa no enumeró archivos de código del plugin, datos de `id-04`, `CATALOGO_QTANQUE.md` ni credenciales. Incluye los hooks de memoria compartidos, archivos de estado de Codex/Cursor, 33 enlaces de skills por destino, el marcador local `.rsc/.no-context7` y `AGENTS.md`. La lista de rutas expresa alcance potencial de sincronización, no prueba que todas cambien de contenido.

La sincronización final informó 33 skills para Codex y 33 para Cursor, manteniendo cuatro agentes. `.rsc.json`, `.rsc/.version` y los dos archivos de estado declaran `2.0.15`; `npx --yes @ericrisco/rsc@2.0.15 --version` devolvió CLI e instalación `2.0.15`. `doctor --target codex` y `doctor --target cursor` devolvieron `missing=[]`, `missingAgents=[]`, `missingCommands=[]`, `commandOrphans=[]`, `optOutsNotApplied=[]`, `hookWired=true`, `memory.status=ready` y `versions.behind=false`. Ambos hooks de memoria modificados pasaron `node --check`.

La versión 2.0.15 trata `context7` como opción de esta máquina: la lista compartida `optOuts` quedó vacía en `.rsc.json`, mientras `.rsc/.no-context7` permanece presente y `policy.context7=false` en ambos estados. `AGENTS.md`, `CLAUDE.md`, catálogo y cinco archivos modificados del plugin conservaron sus hashes anteriores. El estado de Git mostró las mismas modificaciones y rutas sin seguimiento preexistentes, además de este documento dentro de `02-DOCS/`; no hubo commit ni cambio en `id-04`.

## Next

Usar RSC 2.0.15 en las siguientes sesiones. Si se presenta un fallo nuevo del harness, consultar los respaldos locales de esta sincronización antes de restaurar; la restauración no es necesaria ahora.
