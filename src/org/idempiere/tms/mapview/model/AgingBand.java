package org.idempiere.tms.mapview.model;

import java.time.Duration;
import java.time.Instant;

/** Operational age measured from the first transition to Completed. */
public enum AgingBand {
	GREEN("Menos de 24 h", "#16a34a"),
	YELLOW("De 24 a 47 h", "#eab308"),
	RED("48 h o más", "#dc2626"),
	UNKNOWN("Fecha por conciliar", "#64748b");

	private final String label;
	private final String color;

	AgingBand(String label, String color) {
		this.label = label;
		this.color = color;
	}

	public String getLabel() {
		return label;
	}

	public String getColor() {
		return color;
	}

	public static AgingBand at(Instant completedAt, Instant now) {
		if (completedAt == null || completedAt.isAfter(now)) return UNKNOWN;
		long hours = Duration.between(completedAt, now).toHours();
		return hours >= 48 ? RED : hours >= 24 ? YELLOW : GREEN;
	}
}
