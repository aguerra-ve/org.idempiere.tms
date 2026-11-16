package org.idempiere.tms.mapview.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public final class TemporaryRouteProposalSmoke {
	public static void main(String[] args) {
		OrderCandidate second = candidate(2, "IQP-MAP-02", "Cliente <B>", "Almacén Norte", "3.5", false);
		OrderCandidate first = candidate(1, "IQP-MAP-01", "Cliente A", "Almacén Norte", "2", false);
		OrderCandidate historical = candidate(3, "IQP-MAP-03", "Cliente C", "Almacén Sur", "0", true);

		TemporaryRouteProposal empty = TemporaryRouteProposal.from(List.of(first), Set.of());
		check(empty.orders().isEmpty(), "empty selection stays empty");
		check(empty.totalPendingQty().compareTo(BigDecimal.ZERO) == 0, "empty total is zero");

		TemporaryRouteProposal proposal = TemporaryRouteProposal.from(
				List.of(second, historical, first), Set.of(1, 2, 3, 999));
		check(proposal.orders().size() == 2, "only selected, non-historical orders are included");
		check(proposal.orders().get(0).documentNo().equals("IQP-MAP-01"), "orders are deterministic");
		check(proposal.totalPendingQty().compareTo(new BigDecimal("5.5")) == 0, "pending quantity is summed");
		check(proposal.customerCount() == 2, "customers are counted");
		check(proposal.warehouseCount() == 1, "warehouses are counted once");
		check(proposal.loadSummary().commercialUnits().compareTo(new BigDecimal("5.5")) == 0,
				"commercial units preserve the selected pending quantity");
		check(proposal.loadSummary().nestingPending(), "missing nesting result is modeled as pending");
		check(proposal.loadSummary().physicalHandlingUnits() == null,
				"missing nesting result is not silently converted to zero");
		check(proposal.loadSummary().vehiclePending(), "vehicle stays pending until explicitly selected");
		check(proposal.displayText().contains("NO GUARDADO"), "temporary status is explicit");
		check(proposal.displayText().contains("Unidades comerciales: 5.5"),
				"commercial quantity is shown as the first capacity stage");
		check(proposal.displayText().contains("Unidades después del nesting: pendiente"),
				"unknown nesting result is explicit and is not shown as zero");
		check(proposal.displayText().contains("Vehículo candidato: no seleccionado"),
				"vehicle selection remains explicit and read-only");
		check(proposal.displayText().contains("Cliente &lt;B&gt;"), "ERP text is escaped for the HTML dialog");
		check(!proposal.displayText().contains("Cliente <B>"), "raw HTML is not emitted");
		System.out.println("TemporaryRouteProposalSmoke OK: selection, totals and temporary warning");
	}

	private static OrderCandidate candidate(int id, String documentNo, String customer, String warehouse,
			String pending, boolean historical) {
		return new OrderCandidate(id, documentNo, customer, 0, "", "Portuguesa", "Turén", "VILLA BRUZUAL",
				Instant.parse("2026-09-21T12:00:00Z"), AgingBand.GREEN, "A", "Disponibilidad",
				"", new BigDecimal(pending), -69.1, 9.2, historical ? Instant.parse("2026-09-22T12:00:00Z") : null,
				historical, false, warehouse, false, false);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
