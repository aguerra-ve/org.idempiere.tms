-- Reported fleet for id-03-tms-map-test / GardenWorld test client only.
BEGIN;
SET LOCAL search_path TO adempiere,public;
DO $$
DECLARE
 target_client integer=11;
 vehicle_sequence integer;
 type_sequence integer;
 type_id integer;
 vehicle_id integer;
 cm_id integer;
 kg_id integer;
 litre_id integer;
 existing dd_vehicle%ROWTYPE;
 item record;
BEGIN
 IF NOT EXISTS(SELECT 1 FROM ad_client WHERE ad_client_id=target_client AND name='GardenWorld')
 OR NOT EXISTS(SELECT 1 FROM ad_role WHERE ad_client_id=target_client AND name='TMS Dispatch Clone Test')
 OR NOT EXISTS(SELECT 1 FROM c_order WHERE ad_client_id=target_client AND documentno='IQP-MAP-02')
 THEN RAISE EXCEPTION 'Destino no reconocido como clon de pruebas'; END IF;
 SELECT c_uom_id INTO STRICT cm_id FROM c_uom WHERE isactive='Y' AND name='Centimeter' AND uomsymbol='cm';
 SELECT c_uom_id INTO STRICT kg_id FROM c_uom WHERE isactive='Y' AND name='kilogram' AND uomsymbol='Kg';
 SELECT c_uom_id INTO STRICT litre_id FROM c_uom WHERE isactive='Y' AND name='litre' AND uomsymbol='L';
 SELECT ad_sequence_id INTO STRICT vehicle_sequence FROM ad_sequence WHERE name='DD_Vehicle' FOR UPDATE;
 SELECT ad_sequence_id INTO STRICT type_sequence FROM ad_sequence WHERE name='DD_VehicleType' FOR UPDATE;
 SELECT dd_vehicletype_id INTO type_id FROM dd_vehicletype
 WHERE dd_vehicletype_uu='817681a8-7e6d-44bd-9139-fb4b835bb329' AND ad_client_id=target_client;
 IF type_id IS NULL THEN
  type_id=nextid(type_sequence,'N');
  IF type_id IS NULL THEN RAISE EXCEPTION 'No se pudo asignar tipo'; END IF;
  INSERT INTO dd_vehicletype(dd_vehicletype_id,ad_client_id,ad_org_id,createdby,updatedby,
   name,vehicletype,description,dd_vehicletype_uu)
  VALUES(type_id,target_client,0,100,100,'Camión / chuto — flota reportada','TRC',
   'Clasificación de transporte. Medidas específicas en cada vehículo; sin medidas por modelo.',
   '817681a8-7e6d-44bd-9139-fb4b835bb329');
 ELSE
  IF NOT EXISTS(SELECT 1 FROM dd_vehicletype WHERE dd_vehicletype_id=type_id
   AND isactive='Y' AND vehicletype='TRC') THEN RAISE EXCEPTION 'Tipo existente incompatible'; END IF;
 END IF;
 FOR item IN SELECT * FROM (VALUES
  ('AO1BA3A','IQP-VEH-AO1BA3A','FVR · Transporte LC',600::numeric,240::numeric,
   'Largo/ancho útil de plataforma reportado por operador el 13/10/2026: 6,00 × 2,40 m. Referencia: guía 1520. Disponibilidad, altura y carga admisible pendientes.'),
  ('A89EO19','IQP-VEH-A89EO19','JAC · Planta',654::numeric,236::numeric,
   'Largo/ancho útil de plataforma reportado por operador el 13/10/2026: 6,54 × 2,36 m. Referencia: predespacho 1007231. Disponibilidad, altura y carga admisible pendientes.'),
  ('55EJAG','IQP-VEH-55EJAG','Cargo 1721 / chuto · Planta',NULL::numeric,NULL::numeric,
   'Placa de chuto reportada: 55EJAG. Plataforma asociada reportada: 04R-DBD, 12,70 × 2,40 m. Medidas de carga del chuto ausentes; medidas útiles en DD_TransportUnit. Guía 1517. Disponibilidad, altura y carga admisible pendientes.')
 ) AS rows(plate,code,name,length_cm,width_cm,note) LOOP
  SELECT * INTO existing FROM dd_vehicle WHERE ad_client_id=target_client AND licenseplate=item.plate;
  IF FOUND THEN
   IF existing.value IS DISTINCT FROM item.code OR existing.name IS DISTINCT FROM item.name
    OR existing.transportlength IS DISTINCT FROM item.length_cm
    OR existing.transportwidth IS DISTINCT FROM item.width_cm OR existing.dimension_uom_id IS DISTINCT FROM cm_id
    OR existing.dd_vehicletype_id IS DISTINCT FROM type_id OR existing.isactive<>'Y'
   THEN RAISE EXCEPTION 'Placa % ya existe con datos diferentes; no se sobrescribe',item.plate; END IF;
   RAISE NOTICE 'EXISTING % ID %',item.plate,existing.dd_vehicle_id;
   CONTINUE;
  END IF;
  IF EXISTS(SELECT 1 FROM dd_vehicle WHERE ad_client_id=target_client AND value=item.code)
   THEN RAISE EXCEPTION 'Código % ya existe; no se sobrescribe',item.code; END IF;
  vehicle_id=nextid(vehicle_sequence,'N');
  IF vehicle_id IS NULL THEN RAISE EXCEPTION 'No se pudo asignar vehículo'; END IF;
  INSERT INTO dd_vehicle(dd_vehicle_id,ad_client_id,ad_org_id,createdby,updatedby,
   dd_vehicletype_id,dimension_uom_id,volume_uom_id,weight_uom_id,name,value,licenseplate,
   vehiclestatus,transportlength,transportwidth,transportheight,maximumweight,minimumweight,
   maximumvolume,minimumvolume,unladenweight,weight,volume,description,documentnote,dd_vehicle_uu)
  VALUES(vehicle_id,target_client,0,100,100,type_id,cm_id,litre_id,kg_id,item.name,item.code,item.plate,
   'NAV',item.length_cm,item.width_cm,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,
   'Referencia operativa para pruebas; no valida cabida ni disponibilidad.',item.note,
   md5('iqp-tms-clone-reported-fleet-'||item.plate)::uuid::text);
  RAISE NOTICE 'CREATED % ID %',item.plate,vehicle_id;
 END LOOP;
END $$;
COMMIT;
