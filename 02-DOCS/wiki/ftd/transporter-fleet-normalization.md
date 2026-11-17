# Normalización de la flota de transporte

## Intent

Convertir el PDF `Listado de Transporter.pdf` en un inventario coherente para el TMS. El valor
`Medidas` se interpreta como **largo útil de la plataforma o caja de carga, expresado en metros**;
no es el largo total del vehículo ni su capacidad de peso.

## Scope

Incluye las unidades externas e internas, identificación provisional, transportista, modelo
reportado, placa, largo útil y chofer actualmente indicado. Se separa el remolque del inventario de
camiones.

No incluye teléfonos personales, capacidad de carga inferida, ancho, alto, tara, peso bruto
vehicular, año, número de ejes ni tipo exacto de carrocería. Estos datos requieren ficha técnica o
inspección de cada unidad.

## Checklist

- [x] Transcribir las unidades del PDF. Prueba: 11 registros externos y 4 registros internos.
- [x] Normalizar decimales, nombres, placas y propiedad. Prueba: tablas de camiones y remolques.
- [x] Separar vehículo motorizado y remolque sin inventar relaciones. Prueba: el remolque figura en
  una tabla independiente y su camión tractor queda por confirmar.
- [x] Identificar datos faltantes para planificar carga. Prueba: lista de campos pendientes.

## Evidence

Fuente primaria: `/home/aguerrave/Descargas/Listado de Transporter.pdf`, una página. Extracción
verificada con `pdftotext -layout`: 11 filas bajo transporte externo y 4 bajo transporte interno.

### Camiones

Los identificadores son provisionales para discusión; el identificador definitivo debe crearlo el
TMS.

| ID provisional | Propiedad | Transportista | Marca/modelo reportado | Placa | Largo útil (m) | Chofer reportado | Estado del dato |
|---|---|---|---|---|---:|---|---|
| EXT-001 | Externo | Inversiones y Transporte JD | Dongfeng | A53C04S | 6,4 | Nestor Irumba | Modelo exacto pendiente |
| EXT-002 | Externo | Inversiones y Transporte JD | Isuzu NPR | A38AG3N | 4,5 | Marcos Diaz | Año/versión pendiente |
| EXT-003 | Externo | Gruas Nani | Chevrolet Kodiak | 95TDAAA | 6,7 | Samuel | Apellido y año/versión pendientes |
| EXT-004 | Externo | Gruas Nani | Chevrolet Kodiak | A44AL4W | 6,7 | Jesus Linares | Año/versión pendiente |
| EXT-005 | Externo | Transporte LC | Mitsubishi | 66NJAH | 6,0 | Edgar Moreno | Modelo exacto pendiente |
| EXT-006 | Externo | Transporte LC | Isuzu FVR | A86AJ4A | 6,0 | Everest Mendoza | Año/versión pendiente |
| EXT-007 | Externo | Transporte LC | Isuzu FVR | A38AN8G | 6,0 | Rafael Gonzalez | Año/versión pendiente |
| EXT-008 | Externo | Transporte LC | Isuzu FVR | A01BA3A | 6,0 | Jose Contreras | Año/versión pendiente |
| EXT-009 | Externo | Transporte Bacco | Ford (modelo no indicado) | A73EY1P | 6,3 | Carlos Juan Bacco | Modelo y año pendientes |
| EXT-010 | Externo | Transporte Bacco | Ford 750 | A36AW4D | 6,2 | Carlos Enrique Bacco | Familia/año exactos pendientes |
| EXT-011 | Externo | Transporte Bacco | International | A68CPTD | 6,2 | Carlos Enrique Bacco | Modelo y año pendientes |
| INT-001 | Interno | Planta | JAC | A89E019 | 6,6 | Wilmer Moreno | Modelo exacto pendiente |
| INT-002 | Interno | Planta | 1721 / Chuto | 55EJAG | 12,7 | Oscar Gomez | Marca, configuración y medida por confirmar |
| INT-003 | Interno | Planta | Ford Cargo 1721 | 72WLAR | 8,0 | Luis Perdomo | Año/versión pendiente |

**Total normalizado:** 14 camiones: 11 externos y 3 internos identificados individualmente.

### Remolques

| ID provisional | Propiedad | Transportista | Tipo | Placa | Largo útil (m) | Chofer | Relación/observación |
|---|---|---|---|---|---:|---|---|
| REM-INT-001 | Interno | Planta | Remolque | A77DD8S | 8,0 | No aplica | El PDF también dice `Camión 6,50`; no identifica la placa ni el camión tractor asociado |

**Total normalizado:** 1 remolque interno.

### Interpretación operativa

- El largo útil sirve como primer filtro físico: la carga debe caber longitudinalmente en la
  plataforma o caja.
- No basta para asignar pedidos. El TMS también necesita peso admisible, ancho, alto, volumen útil,
  tipo de carrocería y restricciones de ruta.
- `INT-002` no debe asumirse como una plataforma rígida de 12,7 m hasta confirmar si `Chuto` describe
  un tractocamión, un conjunto articulado o una denominación local.
- `REM-INT-001` debe mantenerse como activo separado. No se deben sumar automáticamente 6,5 m y
  8,0 m ni asociarlo con otro camión sin confirmación de Planta.
- Las búsquedas web por NPR, FVR, Kodiak, Ford 750, Cargo 1721, JAC y Mitsubishi solo permiten
  reconocer familias con múltiples configuraciones. No justifican asignar capacidad de peso a estas
  unidades concretas sin año, versión y placa de fabricante.
- Revisión de fichas oficiales FVR del 2026-09-24: [FVR34UL-QDPES](https://www.isuzu-latam-caribbean.com/wp-content/uploads/2024/07/FVR34UL-QDPES-ES.pdf) publica 2.400 mm de **ancho total exterior** y 11.530 kg de carga útil del chasis sin carrocería; [FVR34UL-QDN](https://www.isuzupanama.com.pa/wp-content/uploads/2025/08/Ficha-Tecnica-FVR-1.pdf) publica 2.460 mm y 11.790 kg para esos conceptos. [Otra ficha FVR34](https://isuzunicaragua.com/wp-content/uploads/2023/02/FICHA-TECNICA-FVR-x1-act_2023.pdf) publica 12.000 kg y aclara que excluye la carrocería. Son versiones y mercados distintos: ninguna cifra da el ancho útil ni el payload disponible de `EXT-006`/`EXT-007`/`EXT-008` o del FVR usado en el predespacho `1007137`; se conservan como referencia web, no como dato de flota.

### Campos recomendados para el TMS

| Campo | Uso | Situación actual |
|---|---|---|
| Tipo de activo | Camión, tractocamión o remolque | Parcial; `INT-002` requiere confirmación |
| Transportista / propietario | Disponibilidad y contratación | Disponible |
| Placa | Identificación única operativa | Disponible |
| Marca, modelo y año | Selección de ficha técnica | Marca/modelo parcial; año ausente |
| Largo útil de carga (m) | Compatibilidad longitudinal | Disponible, con dos confirmaciones pendientes |
| Ancho y alto útiles (m) | Compatibilidad física y volumen | Ausentes |
| Capacidad máxima de carga (kg) | Control de peso | Ausente; no inferir de internet |
| Tara y peso bruto vehicular (kg) | Cálculo legal de carga | Ausentes |
| Tipo de carrocería | Plataforma, cava, furgón, baranda, etc. | Ausente |
| Número de ejes | Restricciones viales y capacidad | Ausente |
| Estado operativo | Disponible, mantenimiento o bloqueado | Ausente |
| Chofer asignado | Ejecución del viaje | Disponible como asignación actual; no tratar como fija |
| Compatibilidad camión-remolque | Formar conjuntos válidos | Ausente |

## Next

Confirmar con Planta los dos datos ambiguos (`INT-002` y `REM-INT-001`) y levantar ancho, alto,
capacidad de carga y tipo de carrocería antes de usar la flota para validar propuestas de despacho.
