package org.idempiere.tms.nesting;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/** Pure policy that applies only confirmed chain rules inside one load scope. */
public final class NestingPolicy {

	private static final Comparator<CommercialUnit> UNIT_ORDER = Comparator
			.comparingLong((CommercialUnit unit) -> unit.identity().tenantId())
			.thenComparingLong(unit -> unit.identity().originDocumentId())
			.thenComparingLong(unit -> unit.identity().originLineId())
			.thenComparingInt(unit -> unit.identity().ordinal());

	private NestingPolicy() {
	}

	public static List<NestingResult> evaluate(LoadScope scope, List<CommercialLine> lines,
			List<RuleSnapshot> snapshots) {
		Objects.requireNonNull(scope, "scope");
		Objects.requireNonNull(lines, "lines");
		Objects.requireNonNull(snapshots, "snapshots");
		if (lines.isEmpty()) return invalid(scope, "commercial lines must not be empty", Set.of());

		List<CommercialUnit> units = new ArrayList<>();
		for (CommercialLine line : lines) {
			Objects.requireNonNull(line, "line");
			if (!matches(scope, line))
				return invalid(scope, "every commercial line must match the load scope", Set.of());
			Integer quantity = wholeQuantity(line.quantity());
			if (quantity == null)
				return invalid(scope, "quantity must contain whole commercial units", Set.of());
			for (int ordinal = 1; ordinal <= quantity; ordinal++) {
				units.add(new CommercialUnit(
						new UnitIdentity(line.tenantId(), line.originDocumentId(), line.originLineId(), ordinal),
						line.productId(), line.classification(), line.unitWeight(), line.measurementRefs()));
			}
		}

		Set<UnitIdentity> duplicates = duplicateIdentities(units);
		if (!duplicates.isEmpty())
			return invalid(scope, "commercial unit identity must be unique", duplicates);

		Map<String, RuleSnapshot> confirmedRules = new HashMap<>();
		for (RuleSnapshot snapshot : snapshots) {
			Objects.requireNonNull(snapshot, "snapshot");
			if (snapshot.evidenceState() != EvidenceState.CONFIRMED
					|| snapshot.structure() != NestingStructure.CHAIN)
				continue;
			if (confirmedRules.putIfAbsent(snapshot.family(), snapshot) != null)
				return invalid(scope, "more than one confirmed rule exists for a family", Set.of());
		}

		Map<String, List<CommercialUnit>> unitsByFamily = new TreeMap<>();
		for (CommercialUnit unit : units)
			unitsByFamily.computeIfAbsent(familyOf(unit.classification()), ignored -> new ArrayList<>()).add(unit);

		List<NestingResult> result = new ArrayList<>();
		for (Map.Entry<String, List<CommercialUnit>> entry : unitsByFamily.entrySet()) {
			List<CommercialUnit> familyUnits = entry.getValue();
			familyUnits.sort(UNIT_ORDER);
			RuleSnapshot rule = confirmedRules.get(entry.getKey());
			if (rule == null) {
				result.add(new UnnestedGroup(scope, familyUnits));
				continue;
			}
			appendConfirmedChains(scope, familyUnits, rule, result);
		}
		return List.copyOf(result);
	}

	private static void appendConfirmedChains(LoadScope scope, List<CommercialUnit> familyUnits,
			RuleSnapshot rule, List<NestingResult> result) {
		Map<Integer, List<CommercialUnit>> unitsByRank = new TreeMap<>(Comparator.reverseOrder());
		List<CommercialUnit> independent = new ArrayList<>();
		for (CommercialUnit unit : familyUnits) {
			Integer rank = rule.ranks().get(unit.classification());
			if (rank == null) independent.add(unit);
			else unitsByRank.computeIfAbsent(rank, ignored -> new ArrayList<>()).add(unit);
		}

		Map<String, BigInteger> quantities = new HashMap<>();
		for (CommercialUnit unit : familyUnits)
			quantities.merge(unit.classification(), BigInteger.ONE, BigInteger::add);
		int chainCount = ChainGrouping.count(quantities, rule).chains().intValueExact();
		List<List<CommercialUnit>> chains = new ArrayList<>();
		for (int index = 0; index < chainCount; index++) chains.add(new ArrayList<>());
		for (List<CommercialUnit> sameRank : unitsByRank.values()) {
			sameRank.sort(UNIT_ORDER);
			for (int index = 0; index < sameRank.size(); index++) chains.get(index).add(sameRank.get(index));
		}

		for (List<CommercialUnit> chain : chains) {
			if (chain.size() > 1) result.add(new StructuralGroup(scope, chain));
			else independent.addAll(chain);
		}
		if (!independent.isEmpty()) {
			independent.sort(UNIT_ORDER);
			result.add(new UnnestedGroup(scope, independent));
		}
	}

	private static boolean matches(LoadScope scope, CommercialLine line) {
		return scope.tenantId() == line.tenantId()
				&& scope.deliveryKey().equals(line.deliveryKey())
				&& scope.routeKey().equals(line.routeKey())
				&& scope.loadDate().equals(line.loadDate());
	}

	private static Integer wholeQuantity(BigDecimal quantity) {
		try {
			return quantity.intValueExact();
		} catch (ArithmeticException exception) {
			return null;
		}
	}

	private static Set<UnitIdentity> duplicateIdentities(List<CommercialUnit> units) {
		Set<UnitIdentity> seen = new HashSet<>();
		Set<UnitIdentity> duplicates = new LinkedHashSet<>();
		for (CommercialUnit unit : units) {
			if (!seen.add(unit.identity())) duplicates.add(unit.identity());
		}
		return duplicates;
	}

	private static String familyOf(String classification) {
		int separator = classification.indexOf('-');
		return separator < 0 ? classification : classification.substring(0, separator);
	}

	private static List<NestingResult> invalid(LoadScope scope, String reason,
			Set<UnitIdentity> duplicateUnits) {
		return List.of(new InvalidComposition(scope, reason, duplicateUnits));
	}
}
