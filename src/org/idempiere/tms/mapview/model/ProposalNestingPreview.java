package org.idempiere.tms.mapview.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.idempiere.tms.nesting.ChainGrouping;
import org.idempiere.tms.nesting.EvidenceState;
import org.idempiere.tms.nesting.NestingStructure;
import org.idempiere.tms.nesting.RuleSnapshot;

/** Non-operational preview: no shipment, route, date or vehicle identity is invented. */
public final class ProposalNestingPreview {
	// Evidence is specific to GRUPO QTANQUE; matching classifications in other tenants do not authorize it.
	private static final long QTANQUE_CLIENT_ID = 1000000L;
	private static final RuleSnapshot TLL = new RuleSnapshot("TLL", Map.of(
			"TLL-0370", 1, "TLL-0500", 2, "TLL-0550", 3, "TLL-0700", 4,
			"TLL-0900", 5, "TLL-1060", 6), NestingStructure.CHAIN, EvidenceState.CONFIRMED,
			"DD-NESTING-001; operator reaffirmation 2026-10-13");
	private record Bucket(int orderId, int warehouseId) { }

	private ProposalNestingPreview() { }

	public static ProposalLoadSummary evaluate(long tenantId, List<OrderCandidate> orders,
			List<OrderProductLine> lines) {
		BigDecimal total = orders.stream().map(OrderCandidate::pendingQty).reduce(BigDecimal.ZERO, BigDecimal::add);
		if (tenantId <= 0 || orders.isEmpty()) return pending(total, "Seleccione pedidos pendientes para simular.");
		Map<Integer, BigDecimal> expected = new HashMap<>();
		for (OrderCandidate order : orders) {
			if (order.orderId() <= 0 || order.historical() || order.pendingQty().signum() <= 0
					|| expected.putIfAbsent(order.orderId(), order.pendingQty()) != null)
				return pending(total, "La selección cambió; actualice el mapa y revise los pedidos.");
			if (order.prepared())
				return pending(total, "Hay entregas en preparación; revise su contenido antes de simular.");
		}
		Map<Integer, BigDecimal> actual = new HashMap<>();
		Map<Bucket, Map<String, BigInteger>> quantities = new HashMap<>();
		Set<Integer> lineIds = new HashSet<>();
		for (OrderProductLine line : lines) {
			if (line.tenantId() != tenantId || !expected.containsKey(line.orderId())
					|| line.lineId() <= 0 || line.productId() <= 0 || line.warehouseId() <= 0
					|| !lineIds.add(line.lineId()))
				return pending(total, "No se pudo verificar el contenido completo de la selección.");
			if (line.pendingQty() == null || line.pendingQty().signum() <= 0)
				return pending(total, "Hay cantidades pendientes inválidas; actualice los pedidos.");
			BigInteger whole;
			try {
				whole = line.pendingQty().toBigIntegerExact();
			} catch (ArithmeticException exception) {
				return pending(total, "La simulación requiere cantidades enteras por producto.");
			}
			actual.merge(line.orderId(), line.pendingQty(), BigDecimal::add);
			quantities.computeIfAbsent(new Bucket(line.orderId(), line.warehouseId()), ignored -> new HashMap<>())
					.merge(line.classification(), whole, BigInteger::add);
		}
		for (Map.Entry<Integer, BigDecimal> entry : expected.entrySet()) {
			if (!actual.containsKey(entry.getKey()) || actual.get(entry.getKey()).compareTo(entry.getValue()) != 0)
				return pending(total, "Las cantidades cambiaron o faltan líneas; actualice el mapa y vuelva a revisar.");
		}
		BigInteger footprints = BigInteger.ZERO;
		for (Map<String, BigInteger> bucket : quantities.values())
			footprints = footprints.add(ChainGrouping.count(bucket, tenantId == QTANQUE_CLIENT_ID ? TLL : null).footprints());
		try {
			return ProposalLoadSummary.simulation(total, footprints.intValueExact());
		} catch (ArithmeticException exception) {
			return pending(total, "La cantidad de grupos supera el límite de esta simulación.");
		}
	}

	private static ProposalLoadSummary pending(BigDecimal quantity, String reason) {
		return ProposalLoadSummary.pending(quantity, reason);
	}
}
