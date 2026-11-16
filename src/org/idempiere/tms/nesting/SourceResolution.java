package org.idempiere.tms.nesting;

/** Closed result of resolving an OTR reference into a physical source line. */
public sealed interface SourceResolution
		permits CommercialLine, MissingDeliveryIdentity, InvalidQuantity {
}
