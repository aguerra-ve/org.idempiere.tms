package org.idempiere.tms.mapview.model;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProposalNestingPreviewTest {
	private static final int TENANT = 1000000;
	private static OrderCandidate order(int id, String qty) {
		return new OrderCandidate(id, "P-" + id, "Cliente", 1, "", "", "", "",
				Instant.parse("2026-10-13T12:00:00Z"), AgingBand.GREEN, "A", "", "",
				new BigDecimal(qty), null, null, null, false, false, "Almacén", false, false);
	}
	private static OrderProductLine line(int order, int id, int warehouse, String classification, String qty) {
		return new OrderProductLine(TENANT, order, id, 99, warehouse, classification, new BigDecimal(qty));
	}
	private static ProposalLoadSummary evaluate(List<OrderCandidate> orders, OrderProductLine... lines) {
		return ProposalNestingPreview.evaluate(TENANT, orders, List.of(lines));
	}
	@Test void confirmedTransitiveSizesProduceProvisionalCountWithoutVehicle() {
		ProposalLoadSummary result = evaluate(List.of(order(1,"25")),
				line(1,1,10,"TLL-0500","8"),line(1,2,10,"TLL-0550","8"),line(1,3,10,"TLL-1060","9"));
		assertEquals(9, result.physicalHandlingUnits());
		assertTrue(result.vehiclePending());
		assertTrue(result.displayText().contains("simulación por pedido y almacén"));
		assertTrue(result.displayText().contains("No valida cabida"));
	}
	@Test void cannotBorrowInteriorsFromAnotherOrderOrWarehouse() {
		assertEquals(2,evaluate(List.of(order(1,"1"),order(2,"1")),
				line(1,1,10,"TLL-0500","1"),line(2,2,10,"TLL-1060","1")).physicalHandlingUnits());
		assertEquals(2,evaluate(List.of(order(1,"2")),
				line(1,1,10,"TLL-0500","1"),line(1,2,20,"TLL-1060","1")).physicalHandlingUnits());
	}
	@Test void mixedUnsupportedFamiliesAndMissingClassificationRemainIndependent() {
		assertEquals(5,evaluate(List.of(order(1,"6")),
				line(1,1,10,"TLL-0500","1"),line(1,2,10,"TLL-1060","1"),
				line(1,3,10,"ORI-0550","2"),line(1,4,10,"","1"),
				line(1,5,10,"TLL-9999","1")).physicalHandlingUnits());
	}
	@Test void duplicatedLinesDoNotGiveAFalseResultEvenIfTotalMatches() {
		OrderProductLine first=line(1,1,10,"TLL-0500","1");
		assertTrue(evaluate(List.of(order(1,"2")),first,first).nestingPending());
	}
	@Test void missingChangedOrUnexpectedLinesKeepTheWholeResultPending() {
		assertTrue(evaluate(List.of(order(1,"2")),line(1,1,10,"TLL-0500","1")).nestingPending());
		assertTrue(evaluate(List.of(order(1,"1")),line(2,1,10,"TLL-0500","1")).nestingPending());
		assertTrue(evaluate(List.of(order(1,"1"))).nestingPending());
		assertTrue(evaluate(List.of(order(1,"2"),order(2,"1")),
				line(1,1,10,"TLL-0500","1"),line(2,2,10,"TLL-1060","2")).nestingPending());
	}
	@Test void foreignTenantAndInvalidIdentitiesKeepResultPending() {
		assertTrue(evaluate(List.of(order(1,"1")),
				new OrderProductLine(22,1,1,99,10,"TLL-0500",BigDecimal.ONE)).nestingPending());
		assertTrue(evaluate(List.of(order(1,"1")),
				new OrderProductLine(TENANT,1,0,99,10,"TLL-0500",BigDecimal.ONE)).nestingPending());
	}
	@Test void fractionalQuantitiesAreIndeterminateAndNeverTruncated() {
		ProposalLoadSummary result=evaluate(List.of(order(1,"1.5")),line(1,1,10,"TLL-0500","1.5"));
		assertTrue(result.nestingPending());
		assertEquals(new BigDecimal("1.5"),result.commercialUnits());
		assertTrue(result.displayText().contains("cantidades enteras"));
	}
	@Test void nonPositiveMissingAndOversizedQuantitiesDoNotCrashOrAllocateUnits() {
		assertTrue(evaluate(List.of(order(1,"0")),line(1,1,10,"TLL-0500","0")).nestingPending());
		assertTrue(evaluate(List.of(order(1,"1")),
				new OrderProductLine(TENANT,1,1,99,10,"TLL-0500",null)).nestingPending());
		assertTrue(evaluate(List.of(order(1,"2147483648")),
				line(1,1,10,"TLL-0500","2147483648")).nestingPending());
	}
	@Test void veryLargeCommercialQuantityCanStillHaveRepresentableGroupCount() {
		assertEquals(1500000000,evaluate(List.of(order(1,"3000000000")),
				line(1,1,10,"TLL-0500","1500000000"),
				line(1,2,10,"TLL-1060","1500000000")).physicalHandlingUnits());
	}
	@Test void orderingAndSplittingLinesDoNotChangeCount() {
		OrderProductLine a=line(1,1,10,"TLL-0500","2"),b=line(1,2,10,"TLL-1060","1");
		assertEquals(evaluate(List.of(order(1,"3")),a,b),evaluate(List.of(order(1,"3")),b,a));
		assertEquals(2,evaluate(List.of(order(1,"3")),
				line(1,3,10,"TLL-0500","1"),line(1,4,10,"TLL-0500","1"),b).physicalHandlingUnits());
	}
	@Test void anotherTenantsMatchingClassificationsCannotInheritQtanqueRules() {
		ProposalLoadSummary result=ProposalNestingPreview.evaluate(22,List.of(order(1,"2")),List.of(
				new OrderProductLine(22,1,1,9,10,"TLL-0500",BigDecimal.ONE),
				new OrderProductLine(22,1,2,10,10,"TLL-1060",BigDecimal.ONE)));
		assertEquals(2,result.physicalHandlingUnits());
	}
	@Test void temporaryProposalOnlyAcceptsMatchingCommercialTotalAndEscapesReasons() {
		TemporaryRouteProposal proposal=TemporaryRouteProposal.from(List.of(order(1,"3")),Set.of(1));
		ProposalLoadSummary summary=evaluate(proposal.orders(),
				line(1,1,10,"TLL-0500","2"),line(1,2,10,"TLL-1060","1"));
		assertEquals(2,proposal.withLoadSummary(summary).loadSummary().physicalHandlingUnits());
		assertThrows(IllegalArgumentException.class,()->proposal.withLoadSummary(ProposalLoadSummary.pending(BigDecimal.ONE)));
		assertTrue(ProposalLoadSummary.pending(BigDecimal.ONE,"<script>").displayText().contains("&lt;script&gt;"));
	}
}
