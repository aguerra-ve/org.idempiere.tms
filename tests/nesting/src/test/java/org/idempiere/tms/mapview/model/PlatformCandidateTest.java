package org.idempiere.tms.mapview.model;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PlatformCandidateTest {
 private PlatformCandidate platform(String length,String width,String unit) {
  return new PlatformCandidate(11,8,"04R-DBD",length==null?null:new BigDecimal(length),
   width==null?null:new BigDecimal(width),unit);
 }
 @Test void displaysSeparatePlateAndExactCentimeterConversion() {
  var vehicle=new VehicleCandidate(11,3,"55EJAG","Cargo",platform("1270","240","cm"));
  var text=ProposalLoadSummary.simulation(BigDecimal.TEN,4).withVehicle(vehicle).displayText();
  assertTrue(text.contains("55EJAG"));assertTrue(text.contains("04RDBD"));
  assertTrue(text.contains("12,70 × 2,40 m"));assertTrue(text.contains("No valida cabida"));
 }
 @Test void unknownOrZeroDimensionsNeverBecomeCapacity() {
  for(var p: java.util.List.of(platform(null,"240","cm"),platform("0","240","cm"),
   platform("1270","240",null))) assertTrue(p.displayText().contains("pendientes"));
  assertTrue(platform("12.7","2.4","m").displayText().contains("12,70 × 2,40 m"));
  assertTrue(platform("500","100","mm").displayText().contains("500 × 100 mm"));
 }
 @Test void rejectsCrossTenantAttachmentAndEscapesUnitText() {
  assertThrows(IllegalArgumentException.class,()->new VehicleCandidate(12,3,"55EJAG","Cargo",platform("1270","240","cm")));
  var p=platform("10","2","<unit>");
  var text=ProposalLoadSummary.pending(BigDecimal.ONE).withVehicle(new VehicleCandidate(11,3,"55EJAG","Cargo",p)).displayText();
  assertTrue(text.contains("&lt;unit&gt;"));assertFalse(text.contains("<unit>"));
 }
}
