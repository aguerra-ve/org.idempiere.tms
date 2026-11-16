package org.idempiere.tms.nesting;

import java.time.LocalDate;
import java.util.Objects;

/** Exact boundary within which structural grouping may be evaluated. */
public record LoadScope(long tenantId, DeliveryKey deliveryKey, String routeKey,
		LocalDate loadDate, long candidateVehicleId) {

	public LoadScope {
		if (tenantId <= 0) throw new IllegalArgumentException("tenantId must be positive");
		Objects.requireNonNull(deliveryKey, "deliveryKey");
		if (tenantId != deliveryKey.tenantId())
			throw new IllegalArgumentException("delivery tenant must match scope tenant");
		Objects.requireNonNull(routeKey, "routeKey");
		routeKey = routeKey.trim();
		if (routeKey.isEmpty()) throw new IllegalArgumentException("routeKey must not be blank");
		Objects.requireNonNull(loadDate, "loadDate");
		if (candidateVehicleId <= 0)
			throw new IllegalArgumentException("candidateVehicleId must be positive");
	}
}
