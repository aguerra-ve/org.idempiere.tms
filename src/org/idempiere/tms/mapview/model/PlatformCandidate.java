package org.idempiere.tms.mapview.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Authorized platform identity and current dimensions, without a capacity verdict. */
public record PlatformCandidate(long tenantId,int platformId,String plate,
  BigDecimal length,BigDecimal width,String unit) {
 public PlatformCandidate {
  if(tenantId<=0 || platformId<=0) throw new IllegalArgumentException("Invalid platform identity");
  plate=LicensePlate.normalize(plate);
  if(plate==null) throw new IllegalArgumentException("Platform plate required");
  unit=unit==null ? null : unit.trim();
 }
 public boolean sameIdentity(PlatformCandidate other) {
  return other!=null && tenantId==other.tenantId && platformId==other.platformId && plate.equals(other.plate);
 }
 public String displayText() {
  String measures="pendientes";
  if(length!=null && width!=null && length.signum()>0 && width.signum()>0 && unit!=null && !unit.isBlank()) {
   if(unit.equals("cm")) measures=meters(length.movePointLeft(2))+" × "+meters(width.movePointLeft(2))+" m";
   else if(unit.equals("m")) measures=meters(length)+" × "+meters(width)+" m";
   else measures=length.stripTrailingZeros().toPlainString()+" × "+width.stripTrailingZeros().toPlainString()+" "+unit;
  }
  return "Plataforma asociada: "+plate+"\nMedidas de plataforma: "+measures;
 }
 private static String meters(BigDecimal value) {
  return value.setScale(Math.max(2,value.stripTrailingZeros().scale()),RoundingMode.UNNECESSARY)
    .toPlainString().replace('.',',');
 }
}
