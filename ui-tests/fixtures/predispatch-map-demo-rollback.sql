-- Removes only the synthetic IQP-MAP fixture rows from id-03-tms-map-test.
\set ON_ERROR_STOP on
BEGIN;

-- The fixture now has linked shipments 500000/500001 in the test clone.
-- This SQL never removes or reverses ERP documents; abort until a separate cleanup is planned.
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM adempiere.m_inoutline il
    JOIN adempiere.c_orderline ol ON ol.c_orderline_id = il.c_orderline_id
    JOIN adempiere.c_order o ON o.c_order_id = ol.c_order_id
    WHERE o.c_order_id BETWEEN 9320001 AND 9320012
      AND o.documentno LIKE 'IQP-MAP-%'
  ) THEN
    RAISE EXCEPTION 'Fixture has linked shipments; document and clean their references before rollback';
  END IF;
END $$;

DELETE FROM adempiere.ad_changelog
WHERE ad_changelog_id BETWEEN 9340001 AND 9340012
  AND externaltraceid LIKE 'IQP-MAP-FIXTURE-%';

DELETE FROM adempiere.c_orderline ol
USING adempiere.c_order o
WHERE ol.c_order_id = o.c_order_id
  AND ol.c_orderline_id BETWEEN 9330001 AND 9330012
  AND o.c_order_id BETWEEN 9320001 AND 9320012
  AND o.documentno LIKE 'IQP-MAP-%';

DELETE FROM adempiere.c_order
WHERE c_order_id BETWEEN 9320001 AND 9320012
  AND documentno LIKE 'IQP-MAP-%';

DELETE FROM adempiere.c_bpartner_location
WHERE c_bpartner_location_id BETWEEN 9310001 AND 9310012
  AND name LIKE 'IQP Mapa %';

DELETE FROM adempiere.c_location
WHERE c_location_id BETWEEN 9300001 AND 9300012
  AND address1 LIKE 'Dirección de prueba IQP %';

COMMIT;
