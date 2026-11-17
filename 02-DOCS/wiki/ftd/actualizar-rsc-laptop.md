---
author: Alejandro Guerra
branch: feature/dd-product-nesting
status: in-progress
---
# Actualizar RSC en la laptop

## Intent
Localizar y actualizar las instalaciones activas de RSC a la última versión publicada.

## Scope
Configuraciones RSC instaladas en proyectos locales. Preservar respaldos, cachés históricas, credenciales y cambios del producto. Sin commits, fetch ni push.

## Checklist
- [x] Consultar npm: latest es 3.0.15.
- [x] Buscar instalaciones en las carpetas accesibles de usuario, proyectos y aplicaciones.
- [x] Actualizar las cuatro instalaciones 3.0.10 mediante sync oficial.
- [x] Verificar versiones y diagnóstico final en los cinco proyectos.

## Evidence
2026-10-10: npm view @ericrisco/rsc version dist-tags --json devuelve 3.0.15.
IQP TMS ya tiene catalogVersion y .rsc/.version en 3.0.15.
Orion, idempiere-lve-modular, nota_debito y nota_credito tienen ambas versiones en 3.0.10.

Las cuatro ejecuciones de sync finalizaron con código 0, conservando los targets declarados.
Las cinco instalaciones tienen catalogVersion y .rsc/.version en 3.0.15.
Doctor pasó en las 13 configuraciones de asistentes: healthy=true, behind=false, missing=[], pending=[].
No se encontraron paquetes globales RSC en ubicaciones comunes de npm, NVM, pnpm, Bun o Yarn.
Se conservaron las cinco descargas históricas de npm y los respaldos locales.
La búsqueda de usuario y aplicaciones terminó con cinco proyectos. Algunas carpetas de contenedores y del sistema no permiten lectura. La búsqueda inicial de toda la raíz se interrumpió por duración; se complementó con búsquedas por ubicaciones.

## Next
Abrir sesiones nuevas de los asistentes para cargar las instrucciones actualizadas. Actualización y verificación local completadas; no se requiere merge para aplicar RSC en esta laptop.
