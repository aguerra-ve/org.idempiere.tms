package org.idempiere.tms.nesting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/** Resolved commercial quantity with stable source and delivery identity. */
public record CommercialLine(long tenantId, long originDocumentId, long originLineId,
		DeliveryKey deliveryKey, String routeKey, LocalDate loadDate, long productId,
		String classification, BigDecimal quantity, BigDecimal unitWeight,
		List<String> measurementRefs) implements SourceResolution {

	public CommercialLine {
		if (tenantId <= 0) throw new IllegalArgumentException("tenantId must be positive");
		if (originDocumentId <= 0) throw new IllegalArgumentException("originDocumentId must be positive");
		if (originLineId <= 0) throw new IllegalArgumentException("originLineId must be positive");
		Objects.requireNonNull(deliveryKey, "deliveryKey");
		if (tenantId != deliveryKey.tenantId())
			throw new IllegalArgumentException("delivery tenant must match line tenant");
		routeKey = requireText(routeKey, "routeKey");
		Objects.requireNonNull(loadDate, "loadDate");
		if (productId <= 0) throw new IllegalArgumentException("productId must be positive");
		classification = requireText(classification, "classification");
		Objects.requireNonNull(quantity, "quantity");
		if (quantity.signum() <= 0) throw new IllegalArgumentException("quantity must be positive");
		measurementRefs = measurementRefs == null ? List.of() : List.copyOf(measurementRefs);
	}

	private static String requireText(String value, String field) {
		Objects.requireNonNull(value, field);
		String normalized = value.trim();
		if (normalized.isEmpty()) throw new IllegalArgumentException(field + " must not be blank");
		return normalized;
	}
}
