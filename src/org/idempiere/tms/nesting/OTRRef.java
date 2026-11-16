package org.idempiere.tms.nesting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/** Immutable input captured at the iDempiere boundary before physical grouping. */
public record OTRRef(long tenantId, long originDocumentId, long originLineId,
		int sourceReferenceCount, OrderType orderType, Long physicalDeliveryId,
		boolean outbound, long customerId,
		String routeKey, LocalDate loadDate, long productId, String classification,
		BigDecimal quantity, BigDecimal unitWeight, List<String> measurementRefs) {

	public OTRRef {
		Objects.requireNonNull(orderType, "orderType");
		routeKey = requireText(routeKey, "routeKey");
		Objects.requireNonNull(loadDate, "loadDate");
		classification = requireText(classification, "classification");
		measurementRefs = measurementRefs == null ? List.of() : List.copyOf(measurementRefs);
	}

	private static String requireText(String value, String field) {
		Objects.requireNonNull(value, field);
		String normalized = value.trim();
		if (normalized.isEmpty()) throw new IllegalArgumentException(field + " must not be blank");
		return normalized;
	}
}
