---
branch: feature/dd-product-nesting
status: in-progress
---
# Simulación de acomodo de la guía 1520

## Intent

Explorar la carga del FVR AO1BA3A de 6,00 × 2,40 m con TA-11500-64 acostado transversalmente, según confirmación expresa del operador.

## Scope

Visualización en conversación, sin cambios al plugin ni al ERP. Evidencia preservada de movimientos 1010812/1010814: usar una sola etapa y excluir los 14 pupitres según confirmación del operador. Conservar 28 unidades: ocho TLL-0500-00, ocho TLL-0550-00, nueve TLL-1060-64, un TA-11500-64 y dos tapas importadas. Las tapas se contabilizan aparte; posición y dimensiones no verificadas, sin asignarlas a tanques ni inventar su huella.

Hipótesis seleccionable: ocho cadenas 500→550→1060 y un 1060 independiente. Diámetros nominales TLL 570/600/780 mm. TA transversal: envolvente 2560 mm a lo largo y 2230 mm al ancho. Geometría 2D nominal; no prueba posición histórica, altura, apoyo, sujeción o cabida completa. Peso excluido por preferencia del usuario.

## Checklist

- [x] Conservar las 28 identidades, con 26 tanques representados y dos tapas pendientes de geometría.
- [x] Verificar límites y colisiones, agrupación y separación.
- [x] Verificar interacción, restauración del estado y legibilidad móvil.

## Evidence

[Dossier](dispatch-photo-evidence-20261013.md): guía 1520 y orientación confirmada por el operador «atravesado». Dimensiones nominales de catálogo; no consulta nueva a ERP.

Fuente: `output/visualizations/acomodo-fvr-guia-1520.html`. Sintaxis JavaScript comprobada con Node (exit 0). Navegador Chromium: 28 identidades únicas en todas las configuraciones, con TA transversal 2560 × 2230 mm y dos tapas pendientes. Comprobación independiente de límites y colisiones con tolerancia 0,01 mm: cero errores.

Resultados observados: agrupados, 10/10 grupos colocados tanto con separación de 0 mm como de 100 mm; sin anidar, 15/26 tanques colocados y 11 sin posición encontrada por la heurística. No interpretar el fallo de la heurística como imposibilidad real. Recuperación tras recarga conserva agrupación y separación de 100 mm. Selector T1 muestra las tres unidades correctas. Escritorio 736 px y móvil 360 px sin desbordamiento horizontal; capturas inspeccionadas en `output/playwright/iqp-fvr1520-20261014/`. La última precisión de etiqueta «orientación confirmada» no cambia geometría ni lógica.

## Next

El operador confirmó que el TA iba junto a la cabina. La vista identifica ese extremo como largo 0 m: izquierda en escritorio y arriba en móvil. El TA ya se colocaba en ese extremo; no cambia el algoritmo ni el resultado 10/10. Distancia exacta a la cabina y coordenadas de TLL siguen siendo ilustrativas. Verificación de sintaxis JavaScript exit 0 tras añadir la etiqueta.

El operador indicó dos opciones para las tapas importadas: con el chofer o sobre el conjunto de tanques. No confirmó cuál se usó en la guía 1520. Se conservan ambas como alternativas operativas; no se asignan automáticamente al interior de un tanque ni se crea una posición adicional en el piso. Llevarlas encima tampoco verifica su geometría, altura o apoyo. La simulación 2D actual mantiene dos unidades contabilizadas sin huella ni ubicación concreta.

El operador confirmó que los TLL iban detrás del TA grande, como muestra la simulación, y señaló que se ven en las fotos. Queda confirmada esa disposición relativa para la guía 1520: TA transversal junto a la cabina y TLL detrás. La respuesta no confirma cada cadena, sus miembros ni las coordenadas exactas. La vista actual ya representa esa disposición y no requiere un cambio geométrico.

Precisar composición de cada cadena y ubicación de las tapas solo si se requiere reconstruir ese viaje o modelar altura/apoyo.
