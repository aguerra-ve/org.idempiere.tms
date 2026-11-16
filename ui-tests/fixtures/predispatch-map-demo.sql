-- Synthetic UI fixtures for the isolated id-03-tms-map-test clone only.
-- They copy valid GardenWorld records, but bypass the iDempiere document workflow.
-- Do not use these rows to validate posting, reservations, shipment accounting or reversals.
\set ON_ERROR_STOP on
BEGIN;

CREATE TEMP TABLE iqp_map_fixture (
  n integer PRIMARY KEY,
  state_name text NOT NULL,
  municipality text NOT NULL,
  parish text NOT NULL,
  age_hours integer NOT NULL,
  qty numeric NOT NULL
) ON COMMIT DROP;

-- Territorial combinations come from the recovered V2 location corpus.
INSERT INTO iqp_map_fixture VALUES
  ( 1, 'Barinas',   'Barinas',                           'CORAZON DE JESUS',         12,  2),
  ( 2, 'Barinas',   'Barinas',                           'ALTO BARINAS',            26,  3),
  ( 3, 'Barinas',   'Bolívar',                           'BARINITAS',               50,  4),
  ( 4, 'Barinas',   'Ezequiel Zamora',                   'SANTA BARBARA',           74,  5),
  ( 5, 'Cojedes',   'San Carlos',                        'SAN CARLOS DE AUSTRIA',  120,  2),
  ( 6, 'Portuguesa','Turén',                            'VILLA BRUZUAL',           26,  3),
  ( 7, 'Portuguesa','Monseñor José Vicente de Unda',    'CHABASQUEN',              50,  4),
  ( 8, 'Carabobo',  'Libertador',                        'TOCUYITO',                74,  5),
  ( 9, 'Mérida',    'Padre Noguera',                     'SANTA MARIA DE CAPARO',  168,  6),
  (10, 'Barinas',   'Barinas',                           'EL CARMEN',               12,  2),
  (11, 'Barinas',   'Barinas',                           'ROMULO BETANCOURT',       26,  3),
  (12, 'Barinas',   'Pedraza',                           'PAEZ',                   120,  4);

DO $$
BEGIN
  IF (SELECT current_database()) <> 'idempiere'
     OR NOT EXISTS (SELECT 1 FROM adempiere.ad_client WHERE ad_client_id = 11 AND name = 'GardenWorld')
     OR NOT EXISTS (SELECT 1 FROM adempiere.ad_role WHERE ad_role_id = 200002 AND name = 'TMS Dispatch Clone Test')
  THEN
    RAISE EXCEPTION 'This fixture is restricted to the isolated GardenWorld TMS clone';
  END IF;
  IF EXISTS (SELECT 1 FROM adempiere.c_order
             WHERE c_order_id BETWEEN 9320001 AND 9320012
               AND documentno NOT LIKE 'IQP-MAP-%')
  THEN
    RAISE EXCEPTION 'Fixture order ID range is occupied by unrelated data';
  END IF;
END $$;

INSERT INTO adempiere.c_location
SELECT (jsonb_populate_record(NULL::adempiere.c_location,
         to_jsonb(template) || jsonb_build_object(
           'c_location_id', 9300000 + f.n,
           'c_location_uu', gen_random_uuid()::text,
           'c_country_id', 339,
           'c_region_id', NULL,
           'c_city_id', NULL,
           'regionname', f.state_name,
           'city', f.municipality,
           'address1', 'Dirección de prueba IQP ' || lpad(f.n::text, 2, '0'),
           'address2', 'Parroquia: ' || f.parish,
           'created', now(), 'updated', now()
         ))).*
FROM iqp_map_fixture f CROSS JOIN adempiere.c_location template
WHERE template.c_location_id = 115
ON CONFLICT (c_location_id) DO NOTHING;

INSERT INTO adempiere.c_bpartner_location
SELECT (jsonb_populate_record(NULL::adempiere.c_bpartner_location,
         to_jsonb(template) || jsonb_build_object(
           'c_bpartner_location_id', 9310000 + f.n,
           'c_bpartner_location_uu', gen_random_uuid()::text,
           'c_location_id', 9300000 + f.n,
           'name', 'IQP Mapa ' || lpad(f.n::text, 2, '0'),
           'created', now(), 'updated', now()
         ))).*
FROM iqp_map_fixture f CROSS JOIN adempiere.c_bpartner_location template
WHERE template.c_bpartner_location_id = 108
ON CONFLICT (c_bpartner_location_id) DO NOTHING;

INSERT INTO adempiere.c_order
SELECT (jsonb_populate_record(NULL::adempiere.c_order,
         to_jsonb(template) || jsonb_build_object(
           'c_order_id', 9320000 + f.n,
           'c_order_uu', gen_random_uuid()::text,
           'documentno', 'IQP-MAP-' || lpad(f.n::text, 2, '0'),
           'description', 'IQP synthetic map fixture; not an ERP workflow completion',
           'c_doctype_id', 132,
           'c_doctypetarget_id', 132,
           'c_bpartner_location_id', 9310000 + f.n,
           'totallines', f.qty * 47.5,
           'grandtotal', f.qty * 47.5,
           'dateordered', now() AT TIME ZONE 'America/Caracas' - f.age_hours * interval '1 hour',
           'datepromised', now() AT TIME ZONE 'America/Caracas' + interval '1 day',
           'created', now() AT TIME ZONE 'America/Caracas' - (f.age_hours + 2) * interval '1 hour',
           'updated', now() AT TIME ZONE 'America/Caracas'
         ))).*
FROM iqp_map_fixture f CROSS JOIN adempiere.c_order template
WHERE template.c_order_id = 100
ON CONFLICT (c_order_id) DO NOTHING;

INSERT INTO adempiere.c_orderline
SELECT (jsonb_populate_record(NULL::adempiere.c_orderline,
         to_jsonb(template) || jsonb_build_object(
           'c_orderline_id', 9330000 + f.n,
           'c_orderline_uu', gen_random_uuid()::text,
           'c_order_id', 9320000 + f.n,
           'qtyordered', f.qty,
           'qtyentered', f.qty,
           'qtydelivered', 0,
           'qtyinvoiced', 0,
           'linenetamt', f.qty * 47.5,
           'created', now() AT TIME ZONE 'America/Caracas' - (f.age_hours + 2) * interval '1 hour',
           'updated', now() AT TIME ZONE 'America/Caracas'
         ))).*
FROM iqp_map_fixture f CROSS JOIN adempiere.c_orderline template
WHERE template.c_orderline_id = 100
ON CONFLICT (c_orderline_id) DO NOTHING;

INSERT INTO adempiere.ad_changelog
  (ad_changelog_id, ad_session_id, ad_table_id, ad_column_id,
   ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby,
   record_id, oldvalue, newvalue, iscustomization, description,
   ad_changelog_uu, externaltraceid)
SELECT 9340000 + f.n,
       (SELECT max(ad_session_id) FROM adempiere.ad_session WHERE ad_client_id = 11),
       259, 2170, 11, 11, 'Y',
       now() AT TIME ZONE 'America/Caracas' - f.age_hours * interval '1 hour',
       100, now() AT TIME ZONE 'America/Caracas', 100,
       9320000 + f.n, 'DR', 'CO', 'N',
       'IQP synthetic first CO transition for map age testing',
       gen_random_uuid()::text, 'IQP-MAP-FIXTURE-' || lpad(f.n::text, 2, '0')
FROM iqp_map_fixture f
ON CONFLICT DO NOTHING;

-- Re-running the fixture refreshes its relative age bands for Playwright.
UPDATE adempiere.ad_changelog cl
SET created = now() AT TIME ZONE 'America/Caracas' - f.age_hours * interval '1 hour',
    updated = now() AT TIME ZONE 'America/Caracas'
FROM iqp_map_fixture f
WHERE cl.ad_changelog_id = 9340000 + f.n
  AND cl.externaltraceid = 'IQP-MAP-FIXTURE-' || lpad(f.n::text, 2, '0');

UPDATE adempiere.c_order o
SET created = now() AT TIME ZONE 'America/Caracas' - (f.age_hours + 2) * interval '1 hour',
    dateordered = now() AT TIME ZONE 'America/Caracas' - f.age_hours * interval '1 hour',
    updated = now() AT TIME ZONE 'America/Caracas'
FROM iqp_map_fixture f
WHERE o.c_order_id = 9320000 + f.n
  AND o.documentno = 'IQP-MAP-' || lpad(f.n::text, 2, '0');

DO $$
BEGIN
  IF (SELECT count(*) FROM adempiere.c_order
      WHERE c_order_id BETWEEN 9320001 AND 9320012
        AND documentno LIKE 'IQP-MAP-%' AND docstatus = 'CO') <> 12
     OR (SELECT count(*) FROM adempiere.c_orderline
         WHERE c_orderline_id BETWEEN 9330001 AND 9330012 AND qtyordered > qtydelivered) <> 12
     OR (SELECT count(*) FROM adempiere.ad_changelog
         WHERE ad_changelog_id BETWEEN 9340001 AND 9340012 AND newvalue = 'CO') <> 12
  THEN
    RAISE EXCEPTION 'Synthetic order fixture verification failed';
  END IF;
END $$;

COMMIT;
