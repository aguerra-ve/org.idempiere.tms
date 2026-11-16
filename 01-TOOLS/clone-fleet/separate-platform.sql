-- Correct only the reported fleet previously registered in the TMS test clone.
BEGIN;
SET LOCAL search_path TO adempiere,public;
DO $$
DECLARE
 tractor dd_vehicle%ROWTYPE;
 platform dd_transportunit%ROWTYPE;
 type_id integer;
 unit_id integer;
 assignment_id integer;
 type_sequence integer;
 unit_sequence integer;
 assignment_sequence integer;
BEGIN
 IF NOT EXISTS(SELECT 1 FROM ad_client WHERE ad_client_id=11 AND name='GardenWorld')
 OR NOT EXISTS(SELECT 1 FROM ad_role WHERE ad_client_id=11 AND name='TMS Dispatch Clone Test')
 THEN RAISE EXCEPTION 'Destino no reconocido como clon'; END IF;
 SELECT * INTO STRICT tractor FROM dd_vehicle WHERE ad_client_id=11 AND licenseplate='55EJAG' FOR UPDATE;
 IF tractor.value IS DISTINCT FROM 'IQP-VEH-55EJAG'
 OR tractor.dd_vehicle_uu IS DISTINCT FROM md5('iqp-tms-clone-reported-fleet-55EJAG')::uuid::text
 OR NOT ((tractor.transportlength IS NOT DISTINCT FROM 1270 AND tractor.transportwidth IS NOT DISTINCT FROM 240)
  OR (tractor.transportlength IS NULL AND tractor.transportwidth IS NULL))
 OR NOT EXISTS(SELECT 1 FROM c_uom WHERE c_uom_id=tractor.dimension_uom_id AND uomsymbol='cm')
 THEN RAISE EXCEPTION 'Registro de chuto distinto del esperado; no se modifica'; END IF;
 SELECT ad_sequence_id INTO STRICT type_sequence FROM ad_sequence WHERE name='DD_TransportUnitType' FOR UPDATE;
 SELECT ad_sequence_id INTO STRICT unit_sequence FROM ad_sequence WHERE name='DD_TransportUnit' FOR UPDATE;
 SELECT ad_sequence_id INTO STRICT assignment_sequence FROM ad_sequence WHERE name='DD_TransportAssignment' FOR UPDATE;
 SELECT dd_transportunittype_id INTO type_id FROM dd_transportunittype
 WHERE ad_client_id=11 AND dd_transportunittype_uu='baac3d3e-9a2b-40df-b2ef-1dfb781a4b13';
 IF type_id IS NULL THEN
  type_id=nextid(type_sequence,'N');
  IF type_id IS NULL THEN RAISE EXCEPTION 'No se pudo asignar tipo'; END IF;
  INSERT INTO dd_transportunittype(dd_transportunittype_id,ad_client_id,ad_org_id,createdby,updatedby,
   name,value,transportmode,transportphysicaltype,dimension_uom_id,weight_uom_id,volume_uom_id,dd_transportunittype_uu)
  VALUES(type_id,11,0,100,100,'Plataforma / remolque','IQP-PLATFORM','ROD','TRL',
   tractor.dimension_uom_id,tractor.weight_uom_id,tractor.volume_uom_id,'baac3d3e-9a2b-40df-b2ef-1dfb781a4b13');
 ELSIF NOT EXISTS(SELECT 1 FROM dd_transportunittype WHERE dd_transportunittype_id=type_id
  AND isactive='Y' AND transportmode='ROD' AND transportphysicaltype='TRL')
 THEN RAISE EXCEPTION 'Tipo de plataforma incompatible'; END IF;
 SELECT * INTO platform FROM dd_transportunit WHERE ad_client_id=11 AND licenseplate='04RDBD' FOR UPDATE;
 IF FOUND THEN
  IF platform.value IS DISTINCT FROM 'IQP-PLAT-04RDBD' OR platform.transportlength IS DISTINCT FROM 1270
   OR platform.transportwidth IS DISTINCT FROM 240 OR platform.dimension_uom_id IS DISTINCT FROM tractor.dimension_uom_id
   OR platform.dd_transportunittype_id IS DISTINCT FROM type_id OR platform.isactive<>'Y'
  THEN RAISE EXCEPTION 'Plataforma existente discrepante; no se sobrescribe'; END IF;
  unit_id=platform.dd_transportunit_id;
 ELSE
  IF EXISTS(SELECT 1 FROM dd_transportunit WHERE ad_client_id=11 AND value='IQP-PLAT-04RDBD')
   THEN RAISE EXCEPTION 'Código de plataforma ocupado'; END IF;
  unit_id=nextid(unit_sequence,'N');
  IF unit_id IS NULL THEN RAISE EXCEPTION 'No se pudo asignar plataforma'; END IF;
  INSERT INTO dd_transportunit(dd_transportunit_id,ad_client_id,ad_org_id,createdby,updatedby,
   dd_transportunittype_id,name,value,licenseplate,dimension_uom_id,weight_uom_id,volume_uom_id,
   transportstatus,transportlength,transportwidth,description,documentnote,dd_transportunit_uu)
  VALUES(unit_id,11,0,100,100,type_id,'Plataforma 04R-DBD · Planta','IQP-PLAT-04RDBD','04RDBD',
   tractor.dimension_uom_id,tractor.weight_uom_id,tractor.volume_uom_id,'NAV',1270,240,
   'Plataforma reportada por operador; disponibilidad y cabida pendientes.',
   'Operador confirmó el 14/10/2026 placa propia 04R-DBD asociada al chuto 55EJAG. Largo/ancho útil reportado: 12,70 × 2,40 m. No se atribuye fecha o carga a las fotos de referencia.',
   '75b9c39b-8d28-428d-9dd4-bac25dd939b2');
 END IF;
 IF EXISTS(SELECT 1 FROM dd_transportassignment WHERE isactive='Y' AND ad_client_id=11
  AND ((dd_vehicle_id=tractor.dd_vehicle_id AND dd_transportunit_id IS DISTINCT FROM unit_id)
   OR (dd_transportunit_id=unit_id AND dd_vehicle_id IS DISTINCT FROM tractor.dd_vehicle_id)))
 THEN RAISE EXCEPTION 'Asociación de flota diferente; no se reemplaza'; END IF;
 SELECT dd_transportassignment_id INTO assignment_id FROM dd_transportassignment
 WHERE ad_client_id=11 AND dd_vehicle_id=tractor.dd_vehicle_id AND dd_transportunit_id=unit_id AND isactive='Y';
 IF assignment_id IS NULL THEN
  assignment_id=nextid(assignment_sequence,'N');
  IF assignment_id IS NULL THEN RAISE EXCEPTION 'No se pudo asignar relación'; END IF;
  INSERT INTO dd_transportassignment(dd_transportassignment_id,ad_client_id,ad_org_id,createdby,updatedby,
   dd_vehicle_id,dd_transportunit_id,seqno,dd_transportassignment_uu)
  VALUES(assignment_id,11,0,100,100,tractor.dd_vehicle_id,unit_id,10,'d251b9d4-c775-4c10-a754-6ca0f6698561');
 END IF;
 IF tractor.transportlength IS NOT NULL OR tractor.transportwidth IS NOT NULL
  OR tractor.documentnote IS DISTINCT FROM 'Chuto placa 55EJAG. Plataforma asociada 04R-DBD: dimensiones útiles en DD_TransportUnit, no en el tractor. Confirmación del operador 14/10/2026. Disponibilidad y cabida pendientes.'
 THEN
  UPDATE dd_vehicle SET transportlength=NULL,transportwidth=NULL,updated=now(),updatedby=100,
   documentnote='Chuto placa 55EJAG. Plataforma asociada 04R-DBD: dimensiones útiles en DD_TransportUnit, no en el tractor. Confirmación del operador 14/10/2026. Disponibilidad y cabida pendientes.'
  WHERE dd_vehicle_id=tractor.dd_vehicle_id;
 END IF;
 RAISE NOTICE 'CHUTO % PLATAFORMA % RELACION %',tractor.dd_vehicle_id,unit_id,assignment_id;
END $$;
COMMIT;
