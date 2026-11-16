package org.idempiere.tms.mapview.model;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/** In-memory, read-only review of the orders selected for a possible route. */
public record TemporaryRouteProposal(List<OrderCandidate> orders, BigDecimal totalPendingQty,
		long customerCount, long warehouseCount, ProposalLoadSummary loadSummary) {

	public TemporaryRouteProposal {
		orders = List.copyOf(orders);
		if (loadSummary == null) loadSummary = ProposalLoadSummary.pending(totalPendingQty);
	}

	public static TemporaryRouteProposal from(List<OrderCandidate> candidates, Set<Integer> selectedOrderIds) {
		List<OrderCandidate> selected = candidates.stream()
				.filter(candidate -> !candidate.historical() && selectedOrderIds.contains(candidate.orderId()))
				.sorted(Comparator.comparing(OrderCandidate::documentNo, String.CASE_INSENSITIVE_ORDER)
						.thenComparingInt(OrderCandidate::orderId))
				.toList();
		BigDecimal total = selected.stream().map(OrderCandidate::pendingQty)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		long customers = selected.stream().map(OrderCandidate::customer).distinct().count();
		long warehouses = selected.stream().map(OrderCandidate::warehouseName).distinct().count();
		return new TemporaryRouteProposal(selected, total, customers, warehouses,
				ProposalLoadSummary.pending(total));
	}

	public TemporaryRouteProposal withLoadSummary(ProposalLoadSummary summary) {
		if (summary == null || summary.commercialUnits().compareTo(totalPendingQty) != 0)
			throw new IllegalArgumentException("summary must preserve the proposal commercial quantity");
		return new TemporaryRouteProposal(orders, totalPendingQty, customerCount, warehouseCount, summary);
	}

	public String displayText() {
		StringBuilder text = new StringBuilder("BORRADOR TEMPORAL · NO GUARDADO\n")
				.append(orders.size()).append(" pedidos · ")
				.append(customerCount).append(" clientes · ")
				.append(warehouseCount).append(" almacenes · ")
				.append("cantidad pendiente: ").append(totalPendingQty.stripTrailingZeros().toPlainString())
				.append("\n\n").append(loadSummary.displayText());
		for (OrderCandidate order : orders) {
			text.append("\n\n").append(escapeHtml(order.documentNo())).append(" · ")
					.append(escapeHtml(order.customer()))
					.append("\n").append(escapeHtml(valueOr(order.territorialLabel(), "Ubicación pendiente")))
					.append(" · ").append(escapeHtml(valueOr(order.warehouseName(), "Sin almacén")))
					.append(" · pendiente: ").append(order.pendingQty().stripTrailingZeros().toPlainString());
		}
		return text.toString();
	}

	private static String valueOr(String value, String fallback) {
		return value == null || value.isBlank() ? fallback : value;
	}

	private static String escapeHtml(String value) {
		if (value == null) return "";
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&#39;");
	}
}
