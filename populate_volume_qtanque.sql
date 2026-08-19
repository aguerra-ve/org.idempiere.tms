-- Popula M_Product.Volume para el catálogo real de tanques Q'Tanque.
-- Generado por Claude Code a partir de CATALOGO_QTANQUE.md (fichas oficiales + PDF + cruce con la BD).
--
-- Actualiza 119 filas: 31 modelos x sus variantes de color.
-- Excluye a propósito:
--   - CON-0120-60  (código de prueba "PRUEBA CODIGO CONTENEDOR")
--   - TCL-0600-1/2-64 (medio tanque, no debe heredar el volumen del tanque completo)
-- No toca (sin ficha técnica confiable encontrada, ver CATALOGO_QTANQUE.md):
--   ORI-1250, Q3-3300, TCL-10000, TCL-5000, y el resto de "medio tanque"
--
-- Uso:
--   sudo -u postgres psql -d idempiere -f populate_volume_qtanque.sql
--
-- Es transaccional (BEGIN/COMMIT) e incluye verificación antes de confirmar.
-- Si algo se ve mal en el SELECT de verificación, cambiá el COMMIT final por ROLLBACK.

BEGIN;

CREATE TEMP TABLE qtanque_volume(code_prefix text, volume numeric);
INSERT INTO qtanque_volume(code_prefix, volume) VALUES
('TLL-0370', 371.0),
('TLL-0500', 484.8),
('TLL-0550', 545.7),
('TLL-0700', 712.0),
('TLL-0900', 882.0),
('TLL-1060', 1065.0),
('TCD-0520', 519.46),
('TCD-1060', 1069.60),
('TCL-0600', 596.4),
('TCL-0900', 881.7),
('TCL-1000', 951.3),
('TCL-1200', 1193.4),
('TCL-1700', 1700.0),
('TCL-2500', 2510.3),
('TCL-5200', 5160.7),
('TA-3300',  3333.0),
('TA-5500',  5421.5),
('TA-7700',  7621.9),
('TA-11500', 11478.3),
('THL-1000', 1247.0),
('THL-4500', 4432.6),
('TBA-0208', 224.6),
('TBC-0208', 224.6),
('TBU-2200', 2276.0),
('TBU-3200', 3216.0),
('TBD-2200', 2276.0),
('TBD-3200', 3150.0),
('CON-0120', 120.0),
('CON-0240', 240.0),
('CON-0360', 360.0),
('CON-1000', 1073.0);

-- match exige que el prefijo termine en guion, para no confundir p.ej. TCL-1000 con TCL-10000
UPDATE adempiere.m_product p
SET volume = v.volume,
    updated = now()
FROM qtanque_volume v
WHERE p.value LIKE v.code_prefix || '-%'
  AND p.value NOT IN ('CON-0120-60', 'TCL-0600-1/2-64');

-- verificación dentro de la misma transacción, antes de confirmar
SELECT count(*) AS filas_con_volumen_mayor_a_cero FROM adempiere.m_product WHERE volume > 0;

SELECT value, name, volume
FROM adempiere.m_product
WHERE value IN ('TCL-10000-64','TCL-0600-1/2-64','CON-0120-60','TLL-0370-64','TA-11500-00','TCD-1060-65')
ORDER BY value;
-- esperado: TCL-10000-64, TCL-0600-1/2-64 y CON-0120-60 deben mostrar volume = 0 (sin tocar)

COMMIT;
