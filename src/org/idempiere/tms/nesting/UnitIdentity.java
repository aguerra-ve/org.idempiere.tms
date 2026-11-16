package org.idempiere.tms.nesting;

/** Stable identity of one expanded commercial unit. */
public record UnitIdentity(long tenantId, long originDocumentId, long originLineId, int ordinal) {

	public UnitIdentity {
		if (tenantId <= 0) throw new IllegalArgumentException("tenantId must be positive");
		if (originDocumentId <= 0) throw new IllegalArgumentException("originDocumentId must be positive");
		if (originLineId <= 0) throw new IllegalArgumentException("originLineId must be positive");
		if (ordinal <= 0) throw new IllegalArgumentException("ordinal must be positive");
	}
}
