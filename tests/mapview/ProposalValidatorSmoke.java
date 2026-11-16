package org.idempiere.tms.mapview.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public final class ProposalValidatorSmoke {
	public static void main(String[] args) {
		OrderCandidate accepted = candidate(1, "IQP-01", "10", "HQ", true, false, false);
		OrderCandidate approximate = candidate(2, "IQP-02", "4", "HQ", false, false, false);
		OrderCandidate prepared = candidate(3, "IQP-03", "3", "HQ", true, true, false);
		OrderCandidate noWarehouse = candidate(4, "IQP-04", "2", "", true, false, false);
		OrderCandidate noBalance = candidate(5, "IQP-05", "0", "HQ", true, false, true);
		OrderCandidate disappeared = candidate(6, "IQP-06", "1", "HQ", true, false, false);

		ProposalValidation result = ProposalValidator.validate(
				List.of(accepted, approximate, prepared, noWarehouse, noBalance, disappeared),
				List.of(accepted, approximate, prepared, noWarehouse, noBalance),
				Set.of(1, 2, 3, 4, 5, 6));

		check(result.acceptedCount() == 1, "one order is accepted");
		check(result.warnedCount() == 2, "approximate and prepared orders are warned");
		check(result.blockedCount() == 3, "warehouse, balance and disappeared orders are blocked");
		check(result.item(2).status() == ProposalValidation.Status.WARNED, "approximate location warns");
		check(result.item(3).reasons().contains("Tiene una entrega en preparación"), "prepared shipment is explained");
		check(result.item(4).reasons().contains("Almacén no definido"), "missing warehouse is explained");
		check(result.item(5).reasons().contains("Sin saldo pendiente"), "zero balance is explained");
		check(result.item(6).reasons().contains("Ya no cumple los criterios de elegibilidad"),
				"disappeared order is explained");
		check(result.displayText().contains("1 aceptado · 2 advertidos · 3 bloqueados"),
				"summary contains classification totals");
		System.out.println("ProposalValidatorSmoke OK: accepted, warned, blocked and disappeared orders");
	}

	private static OrderCandidate candidate(int id, String documentNo, String pending, String warehouse,
			boolean coordinates, boolean prepared, boolean historical) {
		return new OrderCandidate(id, documentNo, "Cliente " + id, 100 + id, "Calle " + id,
				"Barinas", "Barinas", "Centro", Instant.parse("2026-09-21T12:00:00Z"), AgingBand.GREEN,
				"A", "Según disponibilidad", "", new BigDecimal(pending), coordinates ? -70.1 : null,
				coordinates ? 8.6 : null, historical ? Instant.parse("2026-09-22T12:00:00Z") : null,
				historical, prepared, warehouse, false, false);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
