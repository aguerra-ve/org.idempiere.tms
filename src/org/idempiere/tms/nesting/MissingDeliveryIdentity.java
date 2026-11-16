package org.idempiere.tms.nesting;

import java.util.Objects;

/** Closed result used when an OTR cannot prove one physical delivery. */
public record MissingDeliveryIdentity(String reason) implements SourceResolution {

	public MissingDeliveryIdentity {
		Objects.requireNonNull(reason, "reason");
		if (reason.isBlank()) throw new IllegalArgumentException("reason must not be blank");
	}
}
