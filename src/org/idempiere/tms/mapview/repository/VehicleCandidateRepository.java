package org.idempiere.tms.mapview.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import org.idempiere.tms.mapview.model.PlatformCandidate;
import org.compiere.model.MRole;
import org.compiere.model.MTable;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.idempiere.tms.mapview.model.VehicleCandidate;

/** Read-only fleet lookup with tenant, organization, table and record access. */
public final class VehicleCandidateRepository {
 private final Properties context;
 public VehicleCandidateRepository(Properties context) { this.context=context; }
 public List<VehicleCandidate> findCandidates() throws SQLException {
  MRole role=MRole.getDefault(context,false);
  int tableId=MTable.getTable_ID("DD_Vehicle");
  if (tableId<=0 || !role.isTableAccess(tableId,true)) return List.of();
  String sql="SELECT v.AD_Client_ID,v.DD_Vehicle_ID,v.LicensePlate,v.Name"
    + " FROM DD_Vehicle v WHERE v.AD_Client_ID=? AND v.IsActive='Y'"
    + " AND v.LicensePlate IS NOT NULL AND TRIM(v.LicensePlate)<>''";
  sql=role.addAccessSQL(sql,"v",MRole.SQL_FULLYQUALIFIED,MRole.SQL_RO)
    + " ORDER BY v.LicensePlate,v.DD_Vehicle_ID";
  List<VehicleCandidate> result=new ArrayList<>();
  try (PreparedStatement statement=DB.prepareStatement(sql,null)) {
   statement.setInt(1,Env.getAD_Client_ID(context));
   try (ResultSet rows=statement.executeQuery()) {
    while(rows.next()) {
     try { result.add(new VehicleCandidate(rows.getLong(1),rows.getInt(2),rows.getString(3),rows.getString(4))); }
     catch(IllegalArgumentException invalid) { throw new SQLException("Invalid fleet identity",invalid); }
    }
   }
  }
  Map<Integer,PlatformCandidate> platforms=findPlatforms(role,result);
  return result.stream().map(v->new VehicleCandidate(v.tenantId(),v.vehicleId(),v.plate(),v.name(),
    platforms.get(v.vehicleId()))).toList();
 }

 private Map<Integer,PlatformCandidate> findPlatforms(MRole role,List<VehicleCandidate> vehicles) throws SQLException {
  Map<Integer,PlatformCandidate> result=new HashMap<>();
  if(vehicles.isEmpty()) return result;
  for(String table:List.of("DD_TransportAssignment","DD_TransportUnit","C_UOM")) {
   int id=MTable.getTable_ID(table);
   if(id<=0 || !role.isTableAccess(id,true)) return result;
  }
  String placeholders=String.join(",",Collections.nCopies(vehicles.size(),"?"));
  String sql="SELECT a.DD_Vehicle_ID,u.AD_Client_ID,u.DD_TransportUnit_ID,u.LicensePlate,"
    + " u.TransportLength,u.TransportWidth,m.UOMSymbol"
    + " FROM DD_TransportAssignment a"
    + " JOIN DD_TransportUnit u ON u.DD_TransportUnit_ID=a.DD_TransportUnit_ID AND u.AD_Client_ID=a.AD_Client_ID"
    + " LEFT JOIN C_UOM m ON m.C_UOM_ID=u.Dimension_UOM_ID AND m.IsActive='Y'"
    + " WHERE a.AD_Client_ID=? AND a.IsActive='Y' AND u.IsActive='Y'"
    + " AND u.LicensePlate IS NOT NULL AND TRIM(u.LicensePlate)<>''"
    + " AND a.DD_Vehicle_ID IN ("+placeholders+")";
  sql=role.addAccessSQL(sql,"a",MRole.SQL_FULLYQUALIFIED,MRole.SQL_RO);
  try(PreparedStatement statement=DB.prepareStatement(sql,null)) {
   statement.setInt(1,Env.getAD_Client_ID(context));
   for(int i=0;i<vehicles.size();i++) statement.setInt(i+2,vehicles.get(i).vehicleId());
   try(ResultSet rows=statement.executeQuery()) {
    while(rows.next()) {
     PlatformCandidate platform;
     try { platform=new PlatformCandidate(rows.getLong(2),rows.getInt(3),rows.getString(4),
       rows.getBigDecimal(5),rows.getBigDecimal(6),rows.getString(7)); }
     catch(IllegalArgumentException invalid) { throw new SQLException("Invalid platform identity",invalid); }
     if(result.putIfAbsent(rows.getInt(1),platform)!=null)
      throw new SQLException("Multiple authorized platform associations; explicit review required");
    }
   }
  }
  return result;
 }
}
