package org.idempiere.tms.nesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.idempiere.tms.mapview.model.ProposalLoadSummary;
import org.junit.jupiter.api.Test;

class NestingSmokeTest {

	@Test
	void productSourceIsCompiledAndUnknownNestingRemainsPending() {
		ProposalLoadSummary summary = ProposalLoadSummary.pending(new BigDecimal("21"));

		assertEquals(new BigDecimal("21"), summary.commercialUnits());
		assertTrue(summary.nestingPending());
		assertTrue(summary.vehiclePending());
		assertTrue(summary.displayText().contains("Unidades después del nesting: pendiente"));
	}

	@Test
	void productInvariantRejectsNegativeCommercialUnits() {
		assertThrows(IllegalArgumentException.class,
				() -> ProposalLoadSummary.pending(BigDecimal.ONE.negate()));
	}
}
