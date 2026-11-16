package org.idempiere.tms.nesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

class SourceScopeTest {

	private static final LocalDate LOAD_DATE = LocalDate.of(2026, 9, 23);

	@Test
	void resolvesShipmentAndPreservesEverySourceField() {
		OTRRef ref = ref(11, 501, 9001, OrderType.MMS, 701L, true,
				"RUTA-A", LOAD_DATE, new BigDecimal("3"));

		CommercialLine line = assertInstanceOf(CommercialLine.class, SourceAdapter.resolve(ref));

		assertEquals(11, line.tenantId());
		assertEquals(501, line.originDocumentId());
		assertEquals(9001, line.originLineId());
		assertEquals(new DeliveryKey(11, OrderType.MMS, 701), line.deliveryKey());
		assertEquals("RUTA-A", line.routeKey());
		assertEquals(LOAD_DATE, line.loadDate());
		assertEquals(1000226, line.productId());
		assertEquals("TLL-0500", line.classification());
		assertEquals(new BigDecimal("3"), line.quantity());
		assertEquals(new BigDecimal("13"), line.unitWeight());
		assertEquals(List.of("M_Product.Weight"), line.measurementRefs());
	}

	@Test
	void resolvesReceiptOnlyWhenItsDirectionMatches() {
		OTRRef receipt = ref(11, 502, 9002, OrderType.MMR, 702L, false,
				"RUTA-B", LOAD_DATE, BigDecimal.ONE);
		OTRRef wrongDirection = ref(11, 502, 9002, OrderType.MMR, 702L, true,
				"RUTA-B", LOAD_DATE, BigDecimal.ONE);

		CommercialLine line = assertInstanceOf(CommercialLine.class, SourceAdapter.resolve(receipt));
		assertEquals(new DeliveryKey(11, OrderType.MMR, 702), line.deliveryKey());
		assertInstanceOf(MissingDeliveryIdentity.class, SourceAdapter.resolve(wrongDirection));
	}

	@Test
	void rejectsMoreThanOneActiveSourceReference() {
		OTRRef ambiguous = ref(11, 501, 9001, 2, OrderType.MMS, 701L, true,
				"RUTA-A", LOAD_DATE, BigDecimal.ONE);

		assertInstanceOf(MissingDeliveryIdentity.class, SourceAdapter.resolve(ambiguous));
	}

	@Test
	void distinctOriginsCannotExchangeTenantDeliveryRouteOrDate() {
		OTRRef firstRef = ref(11, 501, 9001, OrderType.MMS, 701L, true,
				"RUTA-A", LOAD_DATE, BigDecimal.ONE);
		OTRRef secondRef = ref(12, 601, 9101, OrderType.MMS, 801L, true,
				"RUTA-B", LOAD_DATE.plusDays(1), BigDecimal.ONE);

		CommercialLine first = assertInstanceOf(CommercialLine.class, SourceAdapter.resolve(firstRef));
		CommercialLine second = assertInstanceOf(CommercialLine.class, SourceAdapter.resolve(secondRef));

		assertNotEquals(first, second);
		assertEquals(11, first.tenantId());
		assertEquals(new DeliveryKey(11, OrderType.MMS, 701), first.deliveryKey());
		assertEquals("RUTA-A", first.routeKey());
		assertEquals(LOAD_DATE, first.loadDate());
		assertEquals(12, second.tenantId());
		assertEquals(new DeliveryKey(12, OrderType.MMS, 801), second.deliveryKey());
		assertEquals("RUTA-B", second.routeKey());
		assertEquals(LOAD_DATE.plusDays(1), second.loadDate());
	}

	@ParameterizedTest
	@EnumSource(value = OrderType.class, names = { "SOO", "POO", "DOO", "MMM" })
	void doesNotInferDeliveryFromCustomerOrClassification(OrderType orderType) {
		OTRRef unresolved = ref(11, 503, 9003, orderType, 703L, true,
				"RUTA-A", LOAD_DATE, BigDecimal.ONE);

		assertInstanceOf(MissingDeliveryIdentity.class, SourceAdapter.resolve(unresolved));
	}

	@ParameterizedTest
	@ValueSource(strings = { "0", "-1", "1.5" })
	void rejectsAQuantityThatCannotRepresentTllCommercialUnits(String quantity) {
		OTRRef invalidQuantity = ref(11, 501, 9001, OrderType.MMS, 701L, true,
				"RUTA-A", LOAD_DATE, new BigDecimal(quantity));

		assertInstanceOf(InvalidQuantity.class, SourceAdapter.resolve(invalidQuantity));
	}

	@Test
	void tenantDeliveryRouteDateAndCandidateAreAllScopeDimensions() {
		DeliveryKey delivery = new DeliveryKey(11, OrderType.MMS, 701);
		LoadScope base = new LoadScope(11, delivery, "RUTA-A", LOAD_DATE, 41);

		assertNotEquals(base, new LoadScope(12, new DeliveryKey(12, OrderType.MMS, 701),
				"RUTA-A", LOAD_DATE, 41), "tenant must isolate the scope");
		assertNotEquals(base, new LoadScope(11, new DeliveryKey(11, OrderType.MMS, 702),
				"RUTA-A", LOAD_DATE, 41), "delivery must isolate the scope");
		assertNotEquals(base, new LoadScope(11, delivery, "RUTA-B", LOAD_DATE, 41),
				"route must isolate the scope");
		assertNotEquals(base, new LoadScope(11, delivery, "RUTA-A", LOAD_DATE.plusDays(1), 41),
				"load date must isolate the scope");
		assertNotEquals(base, new LoadScope(11, delivery, "RUTA-A", LOAD_DATE, 42),
				"candidate vehicle must isolate the scope");
	}

	private static OTRRef ref(long tenantId, long originDocumentId, long originLineId,
			OrderType orderType, Long physicalDeliveryId, boolean outbound, String routeKey,
			LocalDate loadDate, BigDecimal quantity) {
		return ref(tenantId, originDocumentId, originLineId, 1, orderType, physicalDeliveryId,
				outbound, routeKey, loadDate, quantity);
	}

	private static OTRRef ref(long tenantId, long originDocumentId, long originLineId,
			int sourceReferenceCount, OrderType orderType, Long physicalDeliveryId,
			boolean outbound, String routeKey, LocalDate loadDate, BigDecimal quantity) {
		return new OTRRef(tenantId, originDocumentId, originLineId, sourceReferenceCount, orderType,
				physicalDeliveryId, outbound, 1000001, routeKey, loadDate, 1000226,
				"TLL-0500", quantity, new BigDecimal("13"), List.of("M_Product.Weight"));
	}
}
