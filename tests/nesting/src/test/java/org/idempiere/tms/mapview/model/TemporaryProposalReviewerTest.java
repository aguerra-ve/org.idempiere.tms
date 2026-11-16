package org.idempiere.tms.mapview.model;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TemporaryProposalReviewerTest {
	private static OrderCandidate order(String qty, boolean historical) {
		return new OrderCandidate(1,"P1","Cliente",1,"","","","",Instant.EPOCH,
				AgingBand.GREEN,"A","","",new BigDecimal(qty),null,null,null,historical,false,"Almacén",false,false);
	}
	private static final class Source implements TemporaryProposalReviewer.Source {
		List<OrderCandidate> orders=List.of(order("2",false));
		List<OrderProductLine> lines=List.of(
				new OrderProductLine(1000000,1,1,9,10,"TLL-0500",BigDecimal.ONE),
				new OrderProductLine(1000000,1,2,10,10,"TLL-1060",BigDecimal.ONE));
		int reads; Set<Integer> requested;
		@Override public List<OrderCandidate> findCandidates() { return orders; }
		@Override public List<OrderProductLine> findPendingProductLines(Set<Integer> ids) throws SQLException {
			reads++;requested=ids;return lines;
		}
	}
	@Test void rereadsCurrentQuantitiesAndOnlyRequestsAuthorizedSelection() throws Exception {
		Source source=new Source();
		TemporaryRouteProposal proposal=TemporaryProposalReviewer.review(1000000,Set.of(1),source);
		assertEquals(new BigDecimal("2"),proposal.totalPendingQty());
		assertEquals(1,proposal.loadSummary().physicalHandlingUnits());
		assertEquals(Set.of(1),source.requested);
		assertTrue(proposal.displayText().contains("No valida cabida"));
	}
	@Test void invisibleMissingOrHistoricalOrderStopsBeforeReadingLines() {
		for (List<OrderCandidate> orders:List.of(List.<OrderCandidate>of(),List.of(order("0",true)))) {
			Source source=new Source();source.orders=orders;
			assertThrows(SQLException.class,()->TemporaryProposalReviewer.review(1000000,Set.of(1),source));
			assertEquals(0,source.reads);
		}
		Source source=new Source();
		assertThrows(SQLException.class,()->TemporaryProposalReviewer.review(1000000,Set.of(1,999),source));
		assertEquals(0,source.reads);
	}
	@Test void snapshotChangingBetweenReadsNeverYieldsPartialPhysicalCount() throws Exception {
		Source source=new Source();source.orders=List.of(order("3",false));
		TemporaryRouteProposal result=TemporaryProposalReviewer.review(1000000,Set.of(1),source);
		assertEquals(new BigDecimal("3"),result.totalPendingQty());
		assertTrue(result.loadSummary().nestingPending());
	}
	@Test void databaseFailureCannotBecomeZeroOrSuccessfulPreview() {
		TemporaryProposalReviewer.Source source=new TemporaryProposalReviewer.Source() {
			public List<OrderCandidate> findCandidates() { return List.of(order("2",false)); }
			public List<OrderProductLine> findPendingProductLines(Set<Integer> ids) throws SQLException {
				throw new SQLException("fixture failure");
			}
		};
		assertThrows(SQLException.class,()->TemporaryProposalReviewer.review(1000000,Set.of(1),source));
	}

 @Test void vehicleIsRecheckedAndChangedPlateStopsProposal() throws Exception {
  Source source=new Source();
  var selected=new VehicleCandidate(1000000,42,"AO1BA3A","FVR");
  var result=TemporaryProposalReviewer.review(1000000,Set.of(1),source,selected,()->List.of(selected));
  assertEquals(1,result.loadSummary().physicalHandlingUnits());
  assertTrue(result.loadSummary().displayText().contains("AO1BA3A"));
  assertThrows(SQLException.class,()->TemporaryProposalReviewer.review(1000000,Set.of(1),source,
   selected,()->List.of()));
  assertThrows(SQLException.class,()->TemporaryProposalReviewer.review(1000000,Set.of(1),source,
   selected,()->List.of(new VehicleCandidate(1000000,42,"OTHER","FVR"))));
  assertThrows(SQLException.class,()->TemporaryProposalReviewer.review(11,Set.of(1),source,
   selected,()->List.of(new VehicleCandidate(11,42,"AO1BA3A","FVR"))));
 }
 @Test void missingVehicleIsOptionalAndNeverReadsFleet() throws Exception {
  var result=TemporaryProposalReviewer.review(1000000,Set.of(1),new Source(),null,
   ()->{ throw new AssertionError("No vehicle selected"); });
  assertTrue(result.loadSummary().vehiclePending());
 }

 @Test void associationChangeOrLostPlatformStopsReview() throws Exception {
  var platform=new PlatformCandidate(1000000,8,"04RDBD",new BigDecimal("1270"),new BigDecimal("240"),"cm");
  var selected=new VehicleCandidate(1000000,42,"55EJAG","Cargo",platform);
  var changed=new VehicleCandidate(1000000,42,"55EJAG","Cargo",
   new PlatformCandidate(1000000,9,"OTHER",BigDecimal.ONE,BigDecimal.ONE,"m"));
  for(var current:List.of(changed,new VehicleCandidate(1000000,42,"55EJAG","Cargo")))
   assertThrows(SQLException.class,()->TemporaryProposalReviewer.review(1000000,Set.of(1),new Source(),selected,()->List.of(current)));
  var fresh=new VehicleCandidate(1000000,42,"55EJAG","Cargo",
   new PlatformCandidate(1000000,8,"04RDBD",new BigDecimal("1300"),new BigDecimal("240"),"cm"));
  var result=TemporaryProposalReviewer.review(1000000,Set.of(1),new Source(),selected,()->List.of(fresh));
  assertTrue(result.displayText().contains("13,00 × 2,40 m"));
 }
}
