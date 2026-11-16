package org.idempiere.tms.mapview.model;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/** Seven local calendar dates, counting the completion date as day one. */
public final class HistoryWindow {
	public static final ZoneId CARACAS = ZoneId.of("America/Caracas");

	private HistoryWindow() {
	}

	public static Instant startInclusive(Clock clock) {
		return LocalDate.now(clock.withZone(CARACAS)).minusDays(6)
				.atStartOfDay(CARACAS).toInstant();
	}

	public static boolean contains(Instant completedAt, Clock clock) {
		Instant now = clock.instant();
		return completedAt != null && !completedAt.isBefore(startInclusive(clock))
				&& !completedAt.isAfter(now);
	}
}
