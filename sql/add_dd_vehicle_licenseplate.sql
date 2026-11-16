-- Explicit deployment migration. Does not seed fleet or change existing codes.
-- Run with psql -X -v ON_ERROR_STOP=1 -f this-file.sql on the selected instance.
BEGIN;
SET LOCAL search_path TO adempiere,public;
ALTER TABLE dd_vehicle ADD COLUMN IF NOT EXISTS licenseplate varchar(20);
DO $$
BEGIN
 IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid='dd_vehicle'::regclass
   AND conname='dd_vehicle_licenseplate_format') THEN
  ALTER TABLE dd_vehicle ADD CONSTRAINT dd_vehicle_licenseplate_format
   CHECK (licenseplate IS NULL OR licenseplate ~ '^[A-Z0-9]{1,20}$');
 END IF;
END $$;
CREATE UNIQUE INDEX IF NOT EXISTS dd_vehicle_licenseplate_client_uq
 ON dd_vehicle(ad_client_id,licenseplate) WHERE licenseplate IS NOT NULL;
COMMENT ON COLUMN dd_vehicle.licenseplate IS
 'Placa de la unidad concreta. Mayúsculas, sin espacios ni guiones; única por cliente. No identifica el remolque del tractor.';
DO $$
DECLARE
 table_id integer;
 column_id integer;
 new_field_id integer;
 tab_record record;
 column_sequence integer;
 field_sequence integer;
BEGIN
 SELECT ad_table_id INTO STRICT table_id FROM ad_table WHERE tablename='DD_Vehicle';
 SELECT ad_sequence_id INTO STRICT column_sequence FROM ad_sequence WHERE name='AD_Column' FOR UPDATE;
 SELECT ad_sequence_id INTO STRICT field_sequence FROM ad_sequence WHERE name='AD_Field' FOR UPDATE;
 SELECT ad_column_id INTO column_id FROM ad_column WHERE ad_table_id=table_id AND lower(columnname)='licenseplate';
 IF column_id IS NULL THEN
  column_id=nextid(column_sequence,'N');
  IF column_id IS NULL THEN RAISE EXCEPTION 'No se pudo asignar AD_Column_ID'; END IF;
  INSERT INTO ad_column(ad_column_id,ad_client_id,ad_org_id,createdby,updatedby,
   name,description,version,entitytype,columnname,ad_table_id,ad_reference_id,
   fieldlength,ismandatory,isupdateable,ad_column_uu)
  VALUES(column_id,0,0,100,100,'Placa','Placa de la unidad; no del remolque',1,'U',
   'LicensePlate',table_id,10,20,'N','Y','b7829976-c17c-4d90-836f-ea56c93c627a');
 ELSE
  IF NOT EXISTS(SELECT 1 FROM ad_column WHERE ad_column_id=column_id AND ad_reference_id=10
   AND fieldlength=20 AND ismandatory='N') THEN
   RAISE EXCEPTION 'LicensePlate ya existe con una definición incompatible';
  END IF;
 END IF;
 FOR tab_record IN SELECT ad_tab_id FROM ad_tab WHERE ad_table_id=table_id LOOP
  IF NOT EXISTS(SELECT 1 FROM ad_field WHERE ad_tab_id=tab_record.ad_tab_id AND ad_column_id=column_id) THEN
   new_field_id=nextid(field_sequence,'N');
   IF new_field_id IS NULL THEN RAISE EXCEPTION 'No se pudo asignar AD_Field_ID'; END IF;
   INSERT INTO ad_field(ad_field_id,ad_client_id,ad_org_id,createdby,updatedby,
    name,description,ad_tab_id,ad_column_id,entitytype,iscentrallymaintained,seqno,ad_field_uu)
   VALUES(new_field_id,0,0,100,100,'Placa','Placa única por cliente',tab_record.ad_tab_id,column_id,
    'U','N',(SELECT COALESCE(MAX(seqno),0)+10 FROM ad_field WHERE ad_tab_id=tab_record.ad_tab_id),
    md5('iqp-tms-LicensePlate-'||tab_record.ad_tab_id)::uuid::text);
  END IF;
 END LOOP;
END $$;
COMMIT;
