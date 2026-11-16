package org.idempiere.tms.nesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/** Read-only snapshot of completed QTanque shipment M_InOut_ID=1056427. */
class RealShipmentReplayTest {

	private static final long TENANT = 1000000;
	private static final long SHIPMENT = 1056427;
	// id-04 has no TMS route/vehicle tables. These scope values are replay markers only.
	private static final String REPLAY_ROUTE = "REPLAY-M_INOUT-1056427";
	private static final long REPLAY_VEHICLE = Long.MAX_VALUE;
	private static final LocalDate MOVEMENT_DATE = LocalDate.of(2026, 9, 18);
	private static final DeliveryKey DELIVERY = new DeliveryKey(TENANT, OrderType.MMS, SHIPMENT);
	private static final LoadScope SCOPE = new LoadScope(TENANT, DELIVERY, REPLAY_ROUTE,
			MOVEMENT_DATE, REPLAY_VEHICLE);

	@Test
	void completedShipmentKeepsThreeRealLineIdentitiesAndWeightInTwoFootprints() {
		List<CommercialLine> lines = List.of(
				line(1157079, 1000230, "TLL-0700", 17),
				line(1157080, 1000223, "TLL-0370", 12),
				line(1157081, 1000223, "TLL-0370", 12));
		RuleSnapshot rule = confirmedTllRule();

		List<NestingResult> result = NestingPolicy.evaluate(SCOPE, lines, List.of(rule));
		assertEquals(2, result.size());
		List<LoadGroup> groups = result.stream().map(LoadGroup.class::cast).toList();
		StructuralGroup chain = assertInstanceOf(StructuralGroup.class,
				groups.stream().filter(StructuralGroup.class::isInstance).findFirst().orElseThrow());
		UnnestedGroup remainder = assertInstanceOf(UnnestedGroup.class,
				groups.stream().filter(UnnestedGroup.class::isInstance).findFirst().orElseThrow());
		assertEquals(List.of("TLL-0700", "TLL-0370"),
				chain.units().stream().map(CommercialUnit::classification).toList());
		assertEquals(List.of("TLL-0370"),
				remainder.units().stream().map(CommercialUnit::classification).toList());
		assertEquals(2, groups.stream().mapToInt(LoadGroup::footprintCount).sum());
		assertEquals(1, groups.stream().mapToInt(LoadGroup::savedFootprints).sum());
		assertEquals(Set.of(SCOPE), groups.stream().map(LoadGroup::scope).collect(Collectors.toSet()));

		List<CommercialUnit> units = groups.stream().flatMap(group -> group.units().stream()).toList();
		assertEquals(3, units.size());
		assertEquals(Set.of(
				new UnitIdentity(TENANT, SHIPMENT, 1157079, 1),
				new UnitIdentity(TENANT, SHIPMENT, 1157080, 1),
				new UnitIdentity(TENANT, SHIPMENT, 1157081, 1)),
				units.stream().map(CommercialUnit::identity).collect(Collectors.toSet()));
		assertEquals(new BigDecimal("41"), units.stream().map(CommercialUnit::unitWeight)
				.reduce(BigDecimal.ZERO, BigDecimal::add));
	}

	@Test
	void fourCompletedShipmentsKeepTheirOwnUnitsWeightAndFootprints() {
		List<ReplayShipment> shipments = List.of(
				new ReplayShipment(1056427, LocalDate.of(2026, 9, 18), List.of(
						new ReplayLine(1157079, 1000230, "TLL-0700", 1, 17),
						new ReplayLine(1157080, 1000223, "TLL-0370", 1, 12),
						new ReplayLine(1157081, 1000223, "TLL-0370", 1, 12)), 3, 41, 2),
				new ReplayShipment(1056240, LocalDate.of(2026, 9, 15), List.of(
						new ReplayLine(1156542, 1000222, "TLL-0370", 1, 12),
						new ReplayLine(1156543, 1000231, "TLL-0700", 1, 17)), 2, 29, 1),
				new ReplayShipment(1056083, LocalDate.of(2026, 9, 10), List.of(
						new ReplayLine(1156213, 1000232, "TLL-0700", 1, 17),
						new ReplayLine(1156216, 1000229, "TLL-0550", 2, 14)), 3, 45, 2),
				new ReplayShipment(1056002, LocalDate.of(2026, 9, 9), List.of(
						new ReplayLine(1156023, 1000229, "TLL-0550", 4, 14),
						new ReplayLine(1156024, 1000232, "TLL-0700", 4, 17)), 8, 124, 4));

		int totalFootprints = 0;
		Set<UnitIdentity> allIdentities = new HashSet<>();
		for (ReplayShipment shipment : shipments) {
			LoadScope scope = replayScope(shipment.id(), shipment.movementDate());
			List<CommercialLine> lines = replayLines(scope, shipment.lines());
			List<LoadGroup> groups = NestingPolicy.evaluate(scope, lines, List.of(confirmedTllRule()))
					.stream().map(LoadGroup.class::cast).toList();
			List<CommercialUnit> units = groups.stream().flatMap(group -> group.units().stream()).toList();
			Set<UnitIdentity> expectedIdentities = shipment.lines().stream().flatMap(line ->
					java.util.stream.IntStream.rangeClosed(1, line.quantity()).mapToObj(ordinal ->
							new UnitIdentity(TENANT, shipment.id(), line.id(), ordinal)))
					.collect(Collectors.toSet());

			assertEquals(Set.of(scope), groups.stream().map(LoadGroup::scope).collect(Collectors.toSet()));
			assertEquals(shipment.expectedUnits(), units.size());
			assertEquals(expectedIdentities, units.stream().map(CommercialUnit::identity)
					.collect(Collectors.toSet()));
			assertEquals(new BigDecimal(shipment.expectedWeightKg()), units.stream()
					.map(CommercialUnit::unitWeight).reduce(BigDecimal.ZERO, BigDecimal::add));
			assertEquals(shipment.expectedFootprints(), groups.stream()
					.mapToInt(LoadGroup::footprintCount).sum());
			assertEquals(shipment.expectedUnits() - shipment.expectedFootprints(), groups.stream()
					.mapToInt(LoadGroup::savedFootprints).sum());
			assertTrue(allIdentities.addAll(expectedIdentities));
			totalFootprints += shipment.expectedFootprints();
		}
		assertEquals(16, allIdentities.size());
		assertEquals(9, totalFootprints);

		LoadScope firstScope = replayScope(shipments.get(0).id(), shipments.get(0).movementDate());
		List<CommercialLine> mixed = new ArrayList<>(replayLines(firstScope, shipments.get(0).lines()));
		DeliveryKey otherDelivery = new DeliveryKey(TENANT, OrderType.MMS, shipments.get(1).id());
		mixed.add(new CommercialLine(TENANT, shipments.get(1).id(), 1156542, otherDelivery,
				firstScope.routeKey(), firstScope.loadDate(), 1000222, "TLL-0370", BigDecimal.ONE,
				BigDecimal.valueOf(12), List.of("M_Product.Weight:current-master")));
		assertInstanceOf(InvalidComposition.class,
				NestingPolicy.evaluate(firstScope, mixed, List.of(confirmedTllRule())).get(0));
	}

	private static LoadScope replayScope(long shipmentId, LocalDate movementDate) {
		return new LoadScope(TENANT, new DeliveryKey(TENANT, OrderType.MMS, shipmentId),
				"REPLAY-M_INOUT-" + shipmentId, movementDate, REPLAY_VEHICLE);
	}

	private static List<CommercialLine> replayLines(LoadScope scope, List<ReplayLine> lines) {
		return lines.stream().map(line -> new CommercialLine(TENANT, scope.deliveryKey().documentId(),
				line.id(), scope.deliveryKey(), scope.routeKey(), scope.loadDate(), line.productId(),
				line.classification(), BigDecimal.valueOf(line.quantity()), BigDecimal.valueOf(line.weightKg()),
				List.of("M_Product.Weight:current-master"))).toList();
	}

	private static RuleSnapshot confirmedTllRule() {
		return new RuleSnapshot("TLL",
				Map.of("TLL-0370", 370, "TLL-0500", 500, "TLL-0550", 550,
						"TLL-0700", 700, "TLL-0900", 900, "TLL-1060", 1060),
				NestingStructure.CHAIN, EvidenceState.CONFIRMED, "DD-NESTING-001");
	}

	private record ReplayLine(long id, long productId, String classification, int quantity, long weightKg) { }
	private record ReplayShipment(long id, LocalDate movementDate, List<ReplayLine> lines,
			int expectedUnits, long expectedWeightKg, int expectedFootprints) { }

	private static CommercialLine line(long shipmentLineId, long productId,
			String classification, long weightKg) {
		return new CommercialLine(TENANT, SHIPMENT, shipmentLineId, DELIVERY, REPLAY_ROUTE,
				MOVEMENT_DATE, productId, classification, BigDecimal.ONE,
				BigDecimal.valueOf(weightKg), List.of("M_Product.Weight:current-master"));
	}
}
