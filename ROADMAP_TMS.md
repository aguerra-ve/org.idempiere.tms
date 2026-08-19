# Roadmap del TMS — brecha frente a SAP TM

Qué falta frente al feature-set real de SAP Transportation Management

| Base | Comparado con | Estado |
|---|---|---|
| org.idempiere.tms | SAP TM (público) | Gap analysis |

> Comparativa generada por Claude Code a partir de documentación pública de SAP TM y lectura directa del código de `org.idempiere.tms`. No es documentación oficial de SAP ni un benchmark exhaustivo. No se actualiza automáticamente.

## Contenido

- [01 · Metodología](#01--metodología)
- [02 · Comparativa completa](#02--comparativa-completa)
- [03 · Roadmap priorizado](#03--roadmap-priorizado)

---

## 01 · Metodología

Las áreas de SAP TM salen de documentación pública (SAP Help Portal, SAP Learning, blogs oficiales) — no de acceso a un sistema SAP real. El estado del TMS de iDempiere sale de lectura directa del código fuente (`org.idempiere.tms`), no de su documentación: para cada fila se verificó si la lógica existe de verdad o si es solo una tabla/clase sin uso (varias piezas del plugin están registradas pero con métodos vacíos, como ya se documentó en `MANUAL_TECNICO_TMS.md`).

---

## 02 · Comparativa completa

| Área (SAP TM) | Qué hace SAP | Estado en el TMS | |
|---|---|---|---|
| Freight Order | Documento de ejecución: transportista, ruta, carga, cronograma. | `DD_Freight` vía `createFreightOrder` — cabecera + líneas + paradas, con ruta/vehículo/conductor. | 🟢 Implementado |
| Freight Unit / FUBR | Agrupa ítems en unidades de flete según reglas configurables (peso, volumen, mercancía peligrosa, requisitos de manejo, destino). | `createFreightUnit` hace un split fijo por peso/volumen en Java — no hay motor de reglas, no hay mercancía peligrosa, no hay anidado. | 🟡 Parcial |
| Capacity Planning 3D | Optimiza espacio de contenedor/camión en 3 dimensiones (largo × ancho × alto), no solo peso/volumen. | `DD_Vehicle`/`DD_TransportUnit` tienen `TransportLength/Width/Height` — pero verificado en código: **ningún proceso las lee**. Solo se usa peso y volumen planos. | ⚪ Ausente |
| Nested/Unified Package Building | Empaques anidados (lata→caja→pallet), jerarquía recursiva de contenedores. | No existe. Es el diseño de `DD_ProductNesting` (ver `ANIDADO_TANQUES_SAP.md`). | ⚪ Ausente |
| Rate & Pricing | Tarifas automáticas por distancia, recargo de combustible, tamaño de carga, con múltiples métodos de cálculo. | `DD_Calculation_Method` + `DD_Freight_Agreement` + `FreightCostCalculation` — varios métodos soportados, pero sin recargo de combustible ni tarifas por distancia detectados. | 🟡 Parcial |
| Real-time tracking | Seguimiento en vivo de la posición del embarque. | `DD_GPS_Device`/`DD_GPS_Position` existen, pero de los 3 formularios de mapa declarados en `plugin.xml`, 2 (`OSMRouteViewForm`, `OSMTrackViewForm`) **no tienen clase fuente** — el plugin no compila con eso activo. | 🟡 Roto |
| Carrier selection / bidding / subcontracting | Selección automática de transportista por servicio/capacidad/tarifa; subasta o licitación de fletes. | `createRFQ` genera una solicitud de cotización básica — no hay selección automática ni flujo de licitación. | 🟡 Parcial |
| Driver/vehicle compliance & handling requirements | Verifica licencias, certificaciones y requisitos de manejo especial antes de asignar un recurso. | `DD_Requirement`/`DD_License` existen como datos maestros; el proceso de selección (`org.eevolution.fms.process.Processing*Selection`) que debería validarlos contra Manufactura es **puro `// TODO Auto-generated method stub`** — nunca se implementó. | ⚪ Ausente |
| Route / Network Management | Rutas, tramos, zonas, ventanas de tiempo. | `DD_Route`/`DD_RouteLine` (+ variantes BP/Coordinates/Location/SR) — el área más desarrollada del plugin. | 🟢 Implementado |
| Planning & VSR Optimization | Optimizador automático (Vehicle Scheduling & Routing) para proponer la mejor combinación de cargas/rutas. | No existe ningún motor de optimización — toda la asignación (`VehicleAssignment`) es determinística, sin comparar alternativas. | ⚪ Ausente |

---

## 03 · Roadmap priorizado

Orden sugerido por relación esfuerzo/impacto para el caso real de Q'Tanque — no es el orden en que SAP los construyó.

| Prioridad | Ítem | Por qué ahora |
|---|---|---|
| **P1** | Anidado de productos (`DD_ProductNesting`) | Ya diseñado y validado con datos reales (§08 de `ANIDADO_TANQUES_SAP.md`) — reduce 44→17 bultos en un pedido real. Impacto probado, falta solo `NestedVolume` medido. |
| **P1** | Arreglar el tracking roto | Es un bug de compilación, no una función nueva — `OSMRouteViewForm`/`OSMTrackViewForm` faltan o hay que sacarlos del `plugin.xml`. Bloquea cualquier build limpio del plugin. |
| **P2** | Dimensiones reales (largo/ancho/alto) en el cálculo de capacidad | Los campos ya existen en `DD_Vehicle`/`DD_TransportUnit`, solo falta que `createFreightUnit.java` los lea — no requiere nueva tabla, es cambio de lógica pura. |
| **P2** | Requisitos de manejo (`DD_Requirement`) conectados a asignación de vehículo/conductor | El dato maestro ya existe; falta la validación en `VehicleAssignment.java` (ej. no asignar un conductor sin la licencia requerida). |
| **P3** | Tarifas por distancia / recargo de combustible | Extiende `DD_Calculation_Method` — útil pero no bloquea nada del flujo actual. |
| **P3** | Motor de optimización (VSR) | Es la pieza más cara de construir de toda la lista y la que menos se nota con el volumen de operación actual — dejar para cuando el resto esté sólido. |

> 🟢 **Lectura del roadmap —** de las 9 áreas comparadas, el TMS tiene 2 sólidas (Freight Order, Rutas), 4 parciales o rotas, y 3 completamente ausentes. El anidado de productos que se viene diseñando no es un capricho aislado — es, con datos reales, el ítem de mayor impacto probado de todo el roadmap.
