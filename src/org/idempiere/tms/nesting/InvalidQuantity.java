package org.idempiere.tms.nesting;

import java.math.BigDecimal;
import java.util.Objects;

/** Closed result used when the commercial quantity cannot be grouped safely. */
public record InvalidQuantity(BigDecimal quantity, String reason) implements SourceResolution {

	public InvalidQuantity {
		Objects.requireNonNull(reason, "reason");
		if (reason.isBlank()) throw new IllegalArgumentException("reason must not be blank");
	}
}
