package org.idempiere.tms.nesting;

import java.util.Objects;

/** Tenant-qualified physical shipment/receipt identity. */
public record DeliveryKey(long tenantId, OrderType orderType, long documentId) {

	public DeliveryKey {
		if (tenantId <= 0) throw new IllegalArgumentException("tenantId must be positive");
		Objects.requireNonNull(orderType, "orderType");
		if (orderType != OrderType.MMS && orderType != OrderType.MMR)
			throw new IllegalArgumentException("delivery orderType must be MMS or MMR");
		if (documentId <= 0) throw new IllegalArgumentException("documentId must be positive");
	}
}
