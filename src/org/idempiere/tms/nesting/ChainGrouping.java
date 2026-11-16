package org.idempiere.tms.nesting;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Shared structural count, without unit expansion or any delivery/vehicle feasibility claim. */
public final class ChainGrouping {
	private ChainGrouping() { }

	public record Count(BigInteger chains, BigInteger independentUnits) {
		public BigInteger footprints() { return chains.add(independentUnits); }
	}

	public static Count count(Map<String, BigInteger> quantities, RuleSnapshot rule) {
		Objects.requireNonNull(quantities, "quantities");
		boolean confirmed = rule != null && rule.evidenceState() == EvidenceState.CONFIRMED
				&& rule.structure() == NestingStructure.CHAIN;
		Map<Integer, BigInteger> ranks = new HashMap<>();
		BigInteger independent = BigInteger.ZERO;
		for (Map.Entry<String, BigInteger> entry : quantities.entrySet()) {
			BigInteger qty = Objects.requireNonNull(entry.getValue(), "quantity");
			if (qty.signum() < 0) throw new IllegalArgumentException("quantity must not be negative");
			Integer rank = confirmed ? rule.ranks().get(entry.getKey()) : null;
			if (rank == null) independent = independent.add(qty);
			else ranks.merge(rank, qty, BigInteger::add);
		}
		BigInteger chains = ranks.values().stream().max(BigInteger::compareTo).orElse(BigInteger.ZERO);
		return new Count(chains, independent);
	}
}
