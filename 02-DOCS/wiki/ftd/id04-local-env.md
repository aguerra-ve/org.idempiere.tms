# Acceso local de QTanque en `id-04` mediante `.env`

## Intent

Dejar en la raíz un archivo `.env` privado con los datos de conexión comprobados y los campos de contraseña pendientes para validación del operador.

## Scope

Incluye únicamente el contenedor `id-04`, la conexión local a PostgreSQL, la cuenta de base de la aplicación y el usuario web `SuperUser`. No incorpora contraseñas supuestas ni cambia cuentas o autenticación de la VM. Las contraseñas permanecen solo en `.env`, excluido de Git.

## Checklist

- [x] Crear `.env` con datos comprobados y contraseñas vacías; prueba: comparación de las ocho variables esperadas (`True`) y campos de contraseña vacíos (`True`).
- [x] Restringir permisos y excluir `.env` de Git; prueba: `stat` devolvió modo `600` y `git check-ignore` señaló `/.env` en `.gitignore:2`.
- [x] Incorporar la contraseña de la cuenta de aplicación sin mostrarla; prueba: campo `ID04_APP_DB_PASSWORD` presente en `.env`, contraseña web conservada, modo `600`, exclusión de Git y conexión PostgreSQL que devolvió `current_user=adempiere`.
- [x] Validar `SuperUser` en la WebUI de `id-04`; prueba: con el valor local de `ID04_WEB_PASSWORD`, iDempiere pasó a selección de rol y abrió la pantalla principal con `SuperUser@GardenWorld.*/GardenWorld Admin`.

## Evidence

- La conexión usada fue al contenedor `id-04`, base `idempiere`, usuario PostgreSQL `postgres` como usuario del sistema `postgres` por socket Unix.
- `pg_hba_file_rules` muestra `local all postgres peer` en la línea 3; no se usó contraseña de PostgreSQL en ese acceso.
- Las claves de demostración publicadas para `SuperUser` y `GardenAdmin` fueron rechazadas por la WebUI; se desconoce una contraseña web válida.
- En `id-04`, la ruta real es `/opt/idempiere-server/utils/getVar.sh` (con `V` mayúscula). La variable `ADEMPIERE_DB_PASSWORD` proviene de `.idpass`; `idempiereEnv.properties` identifica la cuenta de aplicación como `adempiere` en `localhost:5432/idempiere`. Esta cuenta es distinta de `postgres`, cuyo acceso local usa `peer`.
- El operador incorporó una contraseña web a `.env`. El 2026-09-24, Playwright completó el ingreso en la WebUI de QTanque (`QSistema - Qtanque`) en `https://127.0.0.1:9004/webui/index.zul`: primero apareció `Select Role` con tenant interno `GardenWorld` y rol `GardenWorld Admin`; después, la pantalla principal mostró `SuperUser@GardenWorld.*/GardenWorld Admin`. Se cerró la sesión de prueba. No se registró ni mostró la contraseña.
- La contraseña de aplicación se recuperó sin imprimirla y una conexión nueva por TCP local autenticó como `adempiere`; la consulta `SELECT current_user` devolvió `adempiere`.
- La verificación global `git diff --check` detectó espacios finales en archivos de código ya modificados antes de este turno; no afectan `.env` ni `.gitignore` y se conservaron.

## Next

Usar la cuenta validada para inspecciones visuales futuras en `id-04`; la autenticación web y la conexión PostgreSQL de la aplicación ya están comprobadas.
