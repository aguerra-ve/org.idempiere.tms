package org.idempiere.tms.nesting;

import java.util.List;

/** A valid structural or independent group within one exact load scope. */
public sealed interface LoadGroup extends NestingResult permits StructuralGroup, UnnestedGroup {

	LoadScope scope();

	List<CommercialUnit> units();

	int footprintCount();

	int savedFootprints();
}
