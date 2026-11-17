# Incorporar continuidad del replay físico

## Intent

Conservar en la wiki activa el estado y las advertencias del replay físico para que una próxima sesión no declare cabida vehicular sin evidencia completa.

## Scope

- Sintetizar los vehículos y pilotos, la conciliación pendiente y los datos que faltan para validar colocación física.
- Mantener separada esta línea V2 del ciclo SDD actual de nesting seguro.
- No copiar un diario de otro entorno, rutas de origen, nombres de clientes, credenciales ni datos personales; no cambiar código ni ejecutar operaciones sobre Incus o bases de datos.

## Checklist

- [x] Contrastar los puntos del diario con el diseño y piloto ya presentes en este proyecto; prueba: coincidencia de casos y límite de cabida.
- [x] Crear una nota de continuidad autónoma y enlazarla en la wiki; prueba: contenido y navegación local.
- [x] Comprobar que no se arrastran referencias al origen ni se eleva una hipótesis a hecho; prueba: búsqueda y revisión textual.

## Evidence

- El diseño local ya identifica la secuencia `1006760` → `1006489` → `1006200` → `1006438` y declara que faltan ancho, alto, payload y otras medidas para probar cabida. El piloto local de `1006760` registra 300 ORI-550 agrupadas en 8 pilas y también prohíbe concluir factibilidad vehicular.
- La nueva nota de logística registra los cuatro casos, distingue la longitud de `1006200` de la identidad no conciliada de `1006438`, y mantiene la exclusión de dos clientes como decisión analítica de alcance pendiente. Está enlazada desde el modelo físico y el índice de la wiki; se comprobaron ambos destinos locales.
- La búsqueda en los documentos nuevos no encontró rutas, nombres ni enlaces al entorno anterior. `git diff --check` no reportó errores. No se ejecutó código, consulta de datos ni operación Incus.
- Se añadió al journal local una entrada breve de pendientes para la próxima sesión: prioridad entre SDD y replay, mediciones/conciliación, prueba funcional en `id-03` y recarga de `e2e-testing`. Se verificó su texto y que no incluye referencias al entorno anterior.

## Next

En la próxima sesión, decidir si continúa primero el ciclo SDD del plugin o la medición del replay físico; para este último, solicitar a Operaciones las medidas faltantes y la identidad física de `1006438`.
