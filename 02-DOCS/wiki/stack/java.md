---
type: stack
title: Java e iDempiere 12
description: Convenciones Java, Maven y OSGi del plugin TMS activo.
tags: [stack, java, maven, idempiere, osgi]
timestamp: 2026-09-23T00:00:00-04:00
topic: stack
updated: 2026-09-23
---

# Java e iDempiere 12

## Runtime y build

- Objetivo del producto: iDempiere 12 como plugin OSGi/Eclipse.
- JDK local y nivel objetivo: Java 17 (`17.0.20.1`). No introducir sintaxis ni APIs posteriores.
- Build: Maven `3.9.10`; el POM usa `org.idempiere.parent:12.0.0-SNAPSHOT`.
- El build reproducible debe ejecutarse dentro de un reactor iDempiere 12 que resuelva ese padre;
  un `mvn package` aislado en este directorio no demuestra compatibilidad.
- Las clases `X_*` e interfaces `I_*` son modelos generados por Application Dictionary; no se
  modernizan manualmente como si fueran código de dominio ordinario.

## Convenciones para código nuevo

- Valores de dominio puros e inmutables por defecto; usar `record` solo cuando sea compatible con
  Java 17 y no interfiera con modelos persistentes de iDempiere.
- Colecciones defensivas y campos `final`; bucles explícitos cuando haya mutación o salida temprana.
- Ausencia física se modela con un estado explícito (`UNKNOWN`/resultado tipado), nunca con cero.
- `Optional` se limita a retornos de código nuevo; las fronteras iDempiere pueden devolver `null` y
  deben adaptarse antes de entrar al dominio puro.
- Consultas dinámicas deben usar las APIs parametrizadas de iDempiere; no concatenar entradas en
  SQL.
- Las excepciones conservan la causa. Las condiciones operativas recuperables se exponen mediante
  resultados/diagnósticos estables, no mediante excepciones tragadas.

## Concurrencia

El ciclo de nesting no introduce concurrencia propia. Usa el contexto transaccional de iDempiere y
mantiene el evaluador puro. Virtual threads, `ScopedValue` y APIs preview quedan fuera porque el
runtime objetivo es Java 17.

## Pruebas

- El comportamiento nuevo sigue rojo→verde antes de modificar producto.
- Los tests puros viven bajo `tests/nesting/`; las pruebas de persistencia, OSGi y AD necesitan un
  runtime iDempiere 12 desechable.
- Ningún cambio Java se declara listo sin test relevante y build del reactor iDempiere 12.

## Referencias

- [Configuración SDD](../sdd/config.yaml)
- [Constitución SDD](../sdd/constitution.md)
- [Plan de nesting seguro](../sdd/plans/safe-product-nesting.md)
