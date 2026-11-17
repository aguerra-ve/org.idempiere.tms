# Contexto operativo y prueba de interfaz independiente

## Intent

Conservar en este proyecto el conocimiento operativo necesario para probar el plugin iDempiere 12 y disponer de una prueba de navegador mínima, reproducible y sin dependencia de otros repositorios.

## Scope

- Incluye un mapa prudente de las instancias Incus, un módulo Playwright propio y una prueba de humo de solo lectura contra la pantalla de acceso de `id-03`.
- No incluye datos de cliente, credenciales, capturas previas, artefactos de compilación ni código de proyectos ajenos.
- No instala el plugin, no importa PackOut, no modifica bases de datos ni ejecuta flujos autenticados.
- La función de cada instancia documentada previamente se trata como contexto operativo; los servicios y URLs se verifican de nuevo antes de afirmar disponibilidad actual.

## Checklist

- [x] Registrar el mapa operativo con límites de uso; prueba: documento sin rutas ni dependencias externas y servicios observados.
- [x] Crear módulo Playwright autónomo y protegido contra secretos/artefactos; prueba: instalación propia y configuración revisada.
- [x] Ejecutar la prueba de humo de solo lectura sobre `id-03`; prueba: salida de Playwright con resultado y URL objetivo.
- [x] Revisar que no haya referencias al origen ni credenciales en los archivos añadidos; prueba: búsqueda y revisión del diff.

## Evidence

- Incus mostró `id-02`, `id-03`, `id-04` y `jk-01` en ejecución. `https://localhost:9003/webui/index.zul` y `https://localhost:9004/webui/index.zul` respondieron HTTP 200 el 2026-09-21. Esto no verifica el rol de sus datos ni autoriza pruebas que escriban.
- `ui-tests/` se instaló desde su propio `package.json` y `package-lock.json`; `npm ls --depth=0` mostró `@playwright/test@1.63.0` sin dependencias locales de otro repositorio. Los resultados y `node_modules` están ignorados por Git.
- `npm test` pasó: `1 passed (4.4s)` contra la URL predeterminada de `id-03`. El primer intento dentro del sandbox falló al lanzar Chromium por `Operation not permitted`; la misma prueba pasó fuera del sandbox con el permiso solicitado. No se inició sesión.
- La búsqueda de rutas, nombres del origen y credenciales conocidas en los archivos añadidos no devolvió coincidencias; `git diff --check` no reportó errores.

## Next

Antes de automatizar una función del plugin: desplegarla en una instancia autorizada, acordar una cuenta de ensayo y definir aserciones de UI y datos que no afecten registros de cliente.
