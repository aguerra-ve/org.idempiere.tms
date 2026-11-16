package org.idempiere.tms.nesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NestingPolicyTest {

	private static final LocalDate LOAD_DATE = LocalDate.of(2026, 9, 23);
	private static final DeliveryKey DELIVERY = new DeliveryKey(11, OrderType.MMS, 701);
	private static final LoadScope SCOPE = new LoadScope(11, DELIVERY, "RUTA-A", LOAD_DATE, 41);

	@Test
	void confirmedTransitiveTllChainCreatesOneFootprintAndKeepsEveryUnit() {
		List<CommercialLine> lines = List.of(
				line(9001, "TLL-0370", 1),
				line(9002, "TLL-0500", 1),
				line(9003, "TLL-0700", 1),
				line(9004, "TLL-1060", 1));

		List<NestingResult> result = NestingPolicy.evaluate(SCOPE, lines,
				List.of(confirmedTllChain()));

		StructuralGroup group = assertInstanceOf(StructuralGroup.class, only(result));
		assertEquals(SCOPE, group.scope());
		assertEquals(1, group.footprintCount());
		assertEquals(List.of("TLL-1060", "TLL-0700", "TLL-0500", "TLL-0370"),
				group.units().stream().map(CommercialUnit::classification).toList());
		assertEquals(Set.of(
				new UnitIdentity(11, 501, 9001, 1),
				new UnitIdentity(11, 501, 9002, 1),
				new UnitIdentity(11, 501, 9003, 1),
				new UnitIdentity(11, 501, 9004, 1)), identities(result));
	}

	@Test
	void equalTllRanksRemainIndependentWithoutSavings() {
		List<NestingResult> result = NestingPolicy.evaluate(SCOPE,
				List.of(line(9001, "TLL-0500", 1), line(9002, "TLL-0500", 1)),
				List.of(confirmedTllChain()));

		UnnestedGroup group = assertInstanceOf(UnnestedGroup.class, only(result));
		assertEquals(2, group.units().size());
		assertEquals(2, group.footprintCount());
		assertEquals(0, group.savedFootprints());
	}

	@Test
	void unequalCountsDoNotCreateUnconfirmedParallelInteriors() {
		List<NestingResult> result = NestingPolicy.evaluate(SCOPE,
				List.of(line(9001, "TLL-0370", 2), line(9002, "TLL-0500", 1)),
				List.of(confirmedTllChain()));

		assertEquals(2, result.size());
		StructuralGroup chain = result.stream()
				.filter(StructuralGroup.class::isInstance)
				.map(StructuralGroup.class::cast)
				.findFirst().orElseThrow();
		UnnestedGroup remainder = result.stream()
				.filter(UnnestedGroup.class::isInstance)
				.map(UnnestedGroup.class::cast)
				.findFirst().orElseThrow();
		assertEquals(2, chain.units().size());
		assertEquals(1, remainder.units().size());
		assertEquals(2, loadGroups(result).stream().mapToInt(LoadGroup::footprintCount).sum());
		assertEquals(3, identities(result).size());
	}

	@ParameterizedTest
	@ValueSource(strings = { "TCL-1100", "ORI-0250" })
	void familiesWithoutConfirmedRulesRemainIndependent(String classification) {
		List<NestingResult> result = NestingPolicy.evaluate(SCOPE,
				List.of(line(9001, classification, 2)), List.of(confirmedTllChain()));

		UnnestedGroup group = assertInstanceOf(UnnestedGroup.class, only(result));
		assertEquals(SCOPE, group.scope());
		assertEquals(2, group.units().size());
		assertEquals(2, group.footprintCount());
		assertEquals(0, group.savedFootprints());
	}

	@Test
	void mixedFamiliesKeepIndependentGroupsAndEveryUnitExactlyOnce() {
		List<CommercialLine> lines = List.of(
				line(9001, "TLL-0370", 1),
				line(9002, "TCL-1100", 1),
				line(9003, "TLL-0500", 1));

		List<NestingResult> result = NestingPolicy.evaluate(SCOPE, lines,
				List.of(confirmedTllChain()));

		assertEquals(2, result.size());
		assertEquals(1, result.stream().filter(StructuralGroup.class::isInstance).count());
		assertEquals(1, result.stream().filter(UnnestedGroup.class::isInstance).count());
		assertEquals(2, loadGroups(result).stream().mapToInt(LoadGroup::footprintCount).sum());
		assertEquals(Set.of(
				new UnitIdentity(11, 501, 9001, 1),
				new UnitIdentity(11, 501, 9002, 1),
				new UnitIdentity(11, 501, 9003, 1)), identities(result));
	}

	@Test
	void duplicateCommercialUnitIdentityIsInvalid() {
		CommercialLine duplicate = line(9001, "TLL-0500", 1);

		List<NestingResult> result = NestingPolicy.evaluate(SCOPE,
				List.of(duplicate, duplicate), List.of(confirmedTllChain()));

		InvalidComposition invalid = assertInstanceOf(InvalidComposition.class, only(result));
		assertEquals(Set.of(new UnitIdentity(11, 501, 9001, 1)), invalid.duplicateUnits());
	}

	@Test
	void candidateVehicleRemainsPartOfEveryResultScope() {
		List<CommercialLine> lines = List.of(
				line(9001, "TLL-0370", 1), line(9002, "TLL-0500", 1));
		LoadScope otherCandidate = new LoadScope(11, DELIVERY, "RUTA-A", LOAD_DATE, 42);

		List<NestingResult> first = NestingPolicy.evaluate(SCOPE, lines,
				List.of(confirmedTllChain()));
		List<NestingResult> second = NestingPolicy.evaluate(otherCandidate, lines,
				List.of(confirmedTllChain()));

		assertNotEquals(first, second);
		assertEquals(Set.of(SCOPE), loadGroups(first).stream().map(LoadGroup::scope).collect(Collectors.toSet()));
		assertEquals(Set.of(otherCandidate),
				loadGroups(second).stream().map(LoadGroup::scope).collect(Collectors.toSet()));
	}

	@Test
	void aLineOutsideTheScopeIsInvalidInsteadOfBeingRelabeled() {
		CommercialLine otherRoute = new CommercialLine(11, 501, 9001, DELIVERY,
				"RUTA-B", LOAD_DATE, 1009001, "TLL-0500", BigDecimal.ONE,
				BigDecimal.TEN, List.of("M_Product.Weight"));

		List<NestingResult> result = NestingPolicy.evaluate(SCOPE, List.of(otherRoute),
				List.of(confirmedTllChain()));

		InvalidComposition invalid = assertInstanceOf(InvalidComposition.class, only(result));
		assertEquals(SCOPE, invalid.scope());
		assertEquals(Set.of(), invalid.duplicateUnits());
	}

	@Test
	void inputPermutationsProduceTheSameNormalizedResult() {
		CommercialLine small = line(9001, "TLL-0370", 1);
		CommercialLine medium = line(9002, "TLL-0500", 1);
		CommercialLine large = line(9003, "TLL-0700", 1);
		List<List<CommercialLine>> permutations = List.of(
				List.of(small, medium, large),
				List.of(small, large, medium),
				List.of(medium, small, large),
				List.of(medium, large, small),
				List.of(large, small, medium),
				List.of(large, medium, small));

		List<NestingResult> expected = NestingPolicy.evaluate(SCOPE, permutations.get(0),
				List.of(confirmedTllChain()));
		for (List<CommercialLine> permutation : permutations) {
			assertEquals(expected, NestingPolicy.evaluate(SCOPE, permutation,
					List.of(confirmedTllChain())));
		}
	}

	@Test
	void unconfirmedTllSnapshotDoesNotAuthorizeSavings() {
		RuleSnapshot unknown = new RuleSnapshot("TLL", tllRanks(), NestingStructure.CHAIN,
				EvidenceState.UNKNOWN, "unconfirmed-fixture");

		List<NestingResult> result = NestingPolicy.evaluate(SCOPE,
				List.of(line(9001, "TLL-0370", 1), line(9002, "TLL-0500", 1)),
				List.of(unknown));

		UnnestedGroup group = assertInstanceOf(UnnestedGroup.class, only(result));
		assertEquals(2, group.footprintCount());
		assertEquals(0, group.savedFootprints());
	}

	private static RuleSnapshot confirmedTllChain() {
		return new RuleSnapshot("TLL", tllRanks(), NestingStructure.CHAIN,
				EvidenceState.CONFIRMED, "DD-NESTING-001");
	}

	private static Map<String, Integer> tllRanks() {
		return Map.of(
				"TLL-0370", 370,
				"TLL-0500", 500,
				"TLL-0550", 550,
				"TLL-0700", 700,
				"TLL-0900", 900,
				"TLL-1060", 1060);
	}

	private static CommercialLine line(long originLineId, String classification, int quantity) {
		return new CommercialLine(11, 501, originLineId, DELIVERY, "RUTA-A", LOAD_DATE,
				1000000 + originLineId, classification, BigDecimal.valueOf(quantity),
				BigDecimal.TEN, List.of("M_Product.Weight"));
	}

	private static NestingResult only(List<NestingResult> result) {
		assertEquals(1, result.size());
		return result.get(0);
	}

	private static List<LoadGroup> loadGroups(List<NestingResult> result) {
		return result.stream().map(LoadGroup.class::cast).toList();
	}

	private static Set<UnitIdentity> identities(List<NestingResult> result) {
		return loadGroups(result).stream()
				.flatMap(group -> group.units().stream())
				.map(CommercialUnit::identity)
				.collect(Collectors.toSet());
	}
}
