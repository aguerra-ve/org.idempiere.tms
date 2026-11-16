package org.idempiere.tms.nesting;

import java.util.List;
import java.util.Objects;

/** One confirmed outer-to-inner chain representing one theoretical footprint. */
public record StructuralGroup(LoadScope scope, List<CommercialUnit> units) implements LoadGroup {

	public StructuralGroup {
		Objects.requireNonNull(scope, "scope");
		units = List.copyOf(units);
		if (units.size() < 2)
			throw new IllegalArgumentException("a structural group requires at least two units");
	}

	@Override
	public int footprintCount() {
		return 1;
	}

	@Override
	public int savedFootprints() {
		return units.size() - 1;
	}
}
