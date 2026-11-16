package org.idempiere.tms.nesting;

import java.math.BigDecimal;
import java.util.Objects;

/** Pure resolver from an OTR boundary snapshot to a delivery-qualified commercial line. */
public final class SourceAdapter {

	private SourceAdapter() {
	}

	public static SourceResolution resolve(OTRRef ref) {
		Objects.requireNonNull(ref, "ref");

		BigDecimal quantity = ref.quantity();
		if (quantity == null || quantity.signum() <= 0)
			return new InvalidQuantity(quantity, "quantity must be positive");
		if (ref.classification().startsWith("TLL-") && quantity.stripTrailingZeros().scale() > 0)
			return new InvalidQuantity(quantity, "TLL quantity must contain whole commercial units");
		if (ref.sourceReferenceCount() != 1)
			return new MissingDeliveryIdentity("exactly one active source reference is required");

		if (ref.tenantId() <= 0 || ref.originDocumentId() <= 0 || ref.originLineId() <= 0)
			return new MissingDeliveryIdentity("tenant and source identities must be positive");

		if (ref.orderType() != OrderType.MMS && ref.orderType() != OrderType.MMR)
			return new MissingDeliveryIdentity("order type does not prove one physical delivery");

		if (ref.physicalDeliveryId() == null || ref.physicalDeliveryId() <= 0)
			return new MissingDeliveryIdentity("physical delivery is missing");

		boolean expectedOutbound = ref.orderType() == OrderType.MMS;
		if (ref.outbound() != expectedOutbound)
			return new MissingDeliveryIdentity("delivery direction does not match order type");

		DeliveryKey deliveryKey = new DeliveryKey(ref.tenantId(), ref.orderType(),
				ref.physicalDeliveryId());
		return new CommercialLine(ref.tenantId(), ref.originDocumentId(), ref.originLineId(),
				deliveryKey, ref.routeKey(), ref.loadDate(), ref.productId(), ref.classification(),
				quantity, ref.unitWeight(), ref.measurementRefs());
	}
}
