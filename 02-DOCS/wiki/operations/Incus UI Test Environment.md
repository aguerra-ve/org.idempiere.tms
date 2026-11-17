# Entorno Incus para pruebas del plugin

Estado revisado: 2026-09-21. Este mapa distingue hechos comprobados en vivo de funciones descritas en las notas operativas; antes de una operación que escriba, se debe confirmar nuevamente la instancia y el conjunto de datos.

| Instancia | Función de trabajo | Límite de uso |
| --- | --- | --- |
| `id-02` | iDempiere con datos de nómina, según las notas operativas | No usar para pruebas automatizadas del plugin. |
| `id-03` | iDempiere limpio para pruebas de interfaz, según las notas operativas | Destino predeterminado de las pruebas de navegador; empezar con comprobaciones sin autenticación ni cambios de datos. |
| `id-04` | iDempiere para pruebas y compilaciones; las notas indican una copia de datos de cliente | No considerar su base desechable. Ejecutar solo escenarios delimitados y autorizados, con datos sintéticos cuando correspondan. |
| `jk-01` | Jenkins | Confirmar el agente y las herramientas de compilación antes de integrarlo al flujo del plugin. |

Verificación en vivo: las cuatro instancias figuraban encendidas en Incus; las URLs `https://localhost:9003/webui/index.zul` y `https://localhost:9004/webui/index.zul` respondieron HTTP 200. Esta comprobación no demuestra que un plugin esté desplegado ni que una prueba funcional pase.

## Prueba de interfaz

El módulo `ui-tests/` de este proyecto usa Playwright contra `id-03` por defecto. Su primera prueba se limita a cargar la pantalla de acceso y comprobar que el campo de contraseña aparece. No necesita cuentas ni conexión directa a PostgreSQL.

iDempiere WebUI usa actualizaciones asíncronas de ZK. Las pruebas deben esperar elementos visibles y acciones concretas; no deben depender de que toda la red quede inactiva (`networkidle`). Una futura prueba autenticada requerirá cuenta de ensayo, variables de entorno para los secretos y confirmación del estado de datos antes de ejecutarse.

El generador de capturas para manuales y las pruebas funcionales son herramientas distintas. No se incorpora el generador de capturas como dependencia del módulo de pruebas.
