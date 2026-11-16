package org.idempiere.tms.nesting;

import java.util.List;
import java.util.Objects;

/** Units retained independently because no confirmed reduction applies. */
public record UnnestedGroup(LoadScope scope, List<CommercialUnit> units) implements LoadGroup {

	public UnnestedGroup {
		Objects.requireNonNull(scope, "scope");
		units = List.copyOf(units);
		if (units.isEmpty()) throw new IllegalArgumentException("units must not be empty");
	}

	@Override
	public int footprintCount() {
		return units.size();
	}

	@Override
	public int savedFootprints() {
		return 0;
	}
}
