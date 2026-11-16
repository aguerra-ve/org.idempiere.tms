package org.idempiere.tms.nesting;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/** One traceable unit expanded from a commercial line. */
public record CommercialUnit(UnitIdentity identity, long productId, String classification,
		BigDecimal unitWeight, List<String> measurementRefs) {

	public CommercialUnit {
		Objects.requireNonNull(identity, "identity");
		if (productId <= 0) throw new IllegalArgumentException("productId must be positive");
		Objects.requireNonNull(classification, "classification");
		classification = classification.trim();
		if (classification.isEmpty())
			throw new IllegalArgumentException("classification must not be blank");
		measurementRefs = measurementRefs == null ? List.of() : List.copyOf(measurementRefs);
	}
}
