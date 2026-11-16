package org.idempiere.tms.mapview.model;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;

class VehicleCandidateTest {
 @Test void plateNormalizationPreservesIdentity() {
  assertEquals("04RDBD", LicensePlate.normalize(" 04r dbd "));
  assertEquals("AO1BA3A", LicensePlate.normalize("ao1-ba3a"));
  assertNull(LicensePlate.normalize("  "));
  assertThrows(IllegalArgumentException.class, () -> LicensePlate.normalize("<script>"));
  assertThrows(IllegalArgumentException.class, () -> LicensePlate.normalize("A".repeat(21)));
 }
 @Test void revalidatesTenantIdAndPlateFromCurrentAuthorizedRows() throws Exception {
  var vehicle=new VehicleCandidate(11,42,"a89eo19","JAC <test>");
  assertEquals(vehicle, VehicleCandidate.requireSelected(11,42,List.of(vehicle)));
  assertThrows(SQLException.class,()->VehicleCandidate.requireSelected(12,42,List.of(vehicle)));
  assertThrows(SQLException.class,()->VehicleCandidate.requireSelected(11,42,List.of()));
  assertThrows(SQLException.class,()->VehicleCandidate.requireSelected(11,42,List.of(vehicle,vehicle)));
  assertNull(VehicleCandidate.requireSelected(11,0,List.of()));
 }
 @Test void selectedVehicleDoesNotChangeNestingOrClaimCapacity() {
  var summary=ProposalLoadSummary.simulation(java.math.BigDecimal.TEN,3)
    .withVehicle(new VehicleCandidate(11,42,"a89eo19","JAC <test>"));
  assertEquals(3,summary.physicalHandlingUnits());
  assertTrue(summary.displayText().contains("A89EO19"));
  assertTrue(summary.displayText().contains("&lt;test&gt;"));
  assertTrue(summary.displayText().contains("No valida cabida"));
  assertFalse(summary.displayText().contains("pendiente de entregas y vehículo"));
 }
}
