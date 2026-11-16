package org.idempiere.tms.mapview.model;

import java.sql.SQLException;
import java.util.List;

/** An authorized concrete vehicle, never inferred from model or dimensions. */
public record VehicleCandidate(long tenantId, int vehicleId, String plate, String name, PlatformCandidate platform) {
 public VehicleCandidate(long tenantId,int vehicleId,String plate,String name) {
  this(tenantId,vehicleId,plate,name,null);
 }
 public VehicleCandidate {
  if(platform!=null && platform.tenantId()!=tenantId) throw new IllegalArgumentException("Cross-tenant platform");
  if (tenantId<=0 || vehicleId<=0) throw new IllegalArgumentException("Invalid vehicle identity");
  plate=LicensePlate.normalize(plate);
  if (plate==null) throw new IllegalArgumentException("Vehicle plate is required");
  name=name==null ? "" : name.trim();
 }
 public String label() { return plate + (name.isEmpty() ? "" : " · " + name); }
 public String reviewText() {
  return label()+"\n"+(platform==null ? "Plataforma asociada: pendiente / no disponible" : platform.displayText());
 }
 public boolean samePlatformIdentity(VehicleCandidate other) {
  return platform==null ? other.platform==null : platform.sameIdentity(other.platform);
 }
 public static VehicleCandidate requireSelected(long tenantId,int vehicleId,List<VehicleCandidate> authorized)
   throws SQLException {
  if (vehicleId==0) return null;
  var matches=authorized.stream().filter(v->v.tenantId==tenantId && v.vehicleId==vehicleId).toList();
  if (matches.size()!=1) throw new SQLException("Vehículo no disponible; actualice la selección.");
  return matches.get(0);
 }
}
