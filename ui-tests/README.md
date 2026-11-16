# Prueba de interfaz de iDempiere

Este módulo es independiente del código Java/OSGi y comprueba, sin iniciar sesión, que la WebUI de pruebas muestra el acceso. Su destino predeterminado es `id-03` mediante `https://localhost:9003`.

Requisitos: Node.js 22, npm y Chromium de Playwright. Dentro de `ui-tests/`, `npm ci` instala las dependencias fijadas en `package-lock.json`; `npx playwright install chromium` instala el navegador; `npm test` ejecuta la prueba. La variable `IDEMPIERE_WEBUI_URL` permite indicar otra URL base si se ha autorizado otro destino, por ejemplo `IDEMPIERE_WEBUI_URL=https://localhost:9004 npm test`. No se requieren credenciales.

La prueba no instala el plugin ni confirma sus funciones. Una futura prueba autenticada deberá usar una cuenta de ensayo y secretos fuera del repositorio, y no debe ejecutarse sobre una base con datos de cliente sin delimitar y aprobar el escenario.
