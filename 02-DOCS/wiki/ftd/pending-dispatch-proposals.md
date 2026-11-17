---
branch: feature/dd-product-nesting
status: in-progress
---
# Propuestas temporales de despachos pendientes

## Intent

Preparar propuestas revisables de pedidos pendientes reales, basadas en política empresarial, coherencia de ruta y economía del viaje. Solicitud expresa del operador el2026-10-15. No crear despachos comerciales ni reservar inventario durante la exploración.

## Scope

Consultar en solo lectura pedidos y saldo despachable, origen/almacén, inventario disponible, direcciones y coordenadas, compromisos y prioridades reconocidas, flota y disponibilidad, costos/tarifas vigentes cuando existan. Agrupar por corredor de ruta y origen, conservando pedidos/líneas y cantidades. Comparar servicio y economía con supuestos explícitos. No llamar «óptima» a una propuesta sin distancias/tiempos/costos verificados; no inventar tarifas o disponibilidad. No usar fixtures del clon como pedidos reales.

Política preservada en LOGISTICA_DESPACHO.md: servicio sobre costo; FIFO por C_Order.Created; prioridad máxima de mal servicio reconocido; compromiso de Gran Caracas; suficiencia CAIC; prioridad Zulia/Occidente; flota propia productiva y alquiler a comparar para recorridos largos. Umbrales Falcón/Andes y precedencias en conflicto requieren parámetros/decisión, no se inventan. Distinguir entregas comerciales de reposiciones entre almacenes.

## Checklist

- [x] Localizar política de Dirección y matriz de conflictos.
- [ ] Recuperar fuente actual de pedidos reales, con fecha de corte y procedencia.
- [ ] Conciliar pendientes con inventario y documentos preparados para evitar doble despacho.
- [ ] Preparar propuestas por corredor y origen, con pedidos, prioridad, secuencia sugerida y razones económicas.
- [ ] Mostrar exclusiones, límites de cabida y comparación propia/alquilada, sin escribir documentos comerciales.

## Evidence

Política original leída y política V2/matriz consultadas. Intento de lectura IQP `SELECT current_database(), CURRENT_TIMESTAMP, current_setting('transaction_read_only')` falló con ECONNREFUSED127.0.0.1:5432. No se obtuvo aún un corte actual de pedidos; no hay propuestas reales calculadas.

## Next

Recuperar acceso de solo lectura a IQP y localizar parámetros comerciales/costos, manteniendo la propuesta temporal separada del ERP.
