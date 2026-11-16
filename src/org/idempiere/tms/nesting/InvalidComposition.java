package org.idempiere.tms.nesting;

import java.util.Objects;
import java.util.Set;

/** Closed result for input that cannot preserve scope or unit identity. */
public record InvalidComposition(LoadScope scope, String reason,
		Set<UnitIdentity> duplicateUnits) implements NestingResult {

	public InvalidComposition {
		Objects.requireNonNull(scope, "scope");
		Objects.requireNonNull(reason, "reason");
		if (reason.isBlank()) throw new IllegalArgumentException("reason must not be blank");
		duplicateUnits = duplicateUnits == null ? Set.of() : Set.copyOf(duplicateUnits);
	}
}
