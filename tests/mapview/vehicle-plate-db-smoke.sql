-- Disposable database checks: all fixtures are rolled back, deferred foreign keys never committed.
BEGIN;
SET LOCAL search_path TO adempiere,public;
INSERT INTO dd_vehicle(dd_vehicle_id,ad_client_id,ad_org_id,createdby,updatedby,
 dd_vehicletype_id,dimension_uom_id,volume_uom_id,weight_uom_id,name,vehiclestatus,licenseplate)
VALUES(-910001,11,0,100,100,0,0,0,0,'TEST ONLY','A','TEST001');
DO $$
BEGIN
 BEGIN
  INSERT INTO dd_vehicle(dd_vehicle_id,ad_client_id,ad_org_id,createdby,updatedby,
   dd_vehicletype_id,dimension_uom_id,volume_uom_id,weight_uom_id,name,vehiclestatus,licenseplate)
  VALUES(-910002,11,0,100,100,0,0,0,0,'TEST ONLY','A','TEST001');
  RAISE EXCEPTION 'Duplicate plate unexpectedly accepted';
 EXCEPTION WHEN unique_violation THEN RAISE NOTICE 'PASS same-client duplicate rejected'; END;
 BEGIN
  UPDATE dd_vehicle SET licenseplate='test-001' WHERE dd_vehicle_id=-910001;
  RAISE EXCEPTION 'Noncanonical plate unexpectedly accepted';
 EXCEPTION WHEN check_violation THEN RAISE NOTICE 'PASS invalid format rejected'; END;
END $$;
INSERT INTO dd_vehicle(dd_vehicle_id,ad_client_id,ad_org_id,createdby,updatedby,
 dd_vehicletype_id,dimension_uom_id,volume_uom_id,weight_uom_id,name,vehiclestatus,licenseplate)
VALUES(-910003,0,0,100,100,0,0,0,0,'TEST ONLY','A','TEST001'),
 (-910004,11,0,100,100,0,0,0,0,'TEST ONLY','A',NULL),
 (-910005,11,0,100,100,0,0,0,0,'TEST ONLY','A',NULL);
DO $$ BEGIN
 IF (SELECT COUNT(*) FROM dd_vehicle WHERE dd_vehicle_id BETWEEN -910005 AND -910001)<>4
 THEN RAISE EXCEPTION 'Expected four fixtures'; END IF;
 RAISE NOTICE 'PASS cross-client plate and missing legacy plates accepted';
END $$;
ROLLBACK;
