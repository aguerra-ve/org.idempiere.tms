package org.idempiere.tms.mapview.model;

import java.math.BigDecimal;

/** Read-only pending quantity; source order identity is never a physical delivery identity. */
public record OrderProductLine(long tenantId, int orderId, int lineId, int productId,
		int warehouseId, String classification, BigDecimal pendingQty) {
	public OrderProductLine {
		classification = classification == null ? "" : classification.trim();
	}
}
