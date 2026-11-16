package org.idempiere.tms.mapview.model;

import java.math.BigDecimal;
import java.time.Instant;

/** Read-only projection used by the native predispatch form. */
public record OrderCandidate(int orderId, String documentNo, String customer, int locationId,
		String address, String state, String municipality, String parish, Instant completedAt,
		AgingBand agingBand, String deliveryRule, String deliveryRuleLabel,
		String deliveryRuleExplanation, BigDecimal pendingQty, Double longitude, Double latitude,
		Instant dispatchedAt, boolean historical, boolean prepared, String warehouseName,
		boolean specialAddress, boolean rma) {

	public boolean hasCoordinates() {
		return longitude != null && latitude != null && longitude >= -180 && longitude <= 180
				&& latitude >= -90 && latitude <= 90;
	}

	public String territorialLabel() {
		return join(parish, municipality, state);
	}

	public String mapsLocation() {
		String value = join(address, parish, municipality, state, "Venezuela");
		return value.isBlank() ? customer : value;
	}

	private static String join(String... values) {
		StringBuilder result = new StringBuilder();
		for (String value : values) {
			if (value == null || value.isBlank()) {
				continue;
			}
			if (result.length() > 0) {
				result.append(", ");
			}
			result.append(value.trim());
		}
		return result.toString();
	}
}
