import java.time.Clock;
import java.time.Instant;

import org.idempiere.tms.mapview.model.HistoryWindow;

public final class HistoryWindowSmoke {
	private static void check(boolean condition, String caseName) {
		if (!condition) throw new AssertionError(caseName);
	}

	public static void main(String[] args) {
		Instant completed = Instant.parse("2026-09-21T12:00:00Z"); // 08:00 Caracas
		Clock endOfDaySeven = Clock.fixed(Instant.parse("2026-09-28T03:59:59Z"), HistoryWindow.CARACAS);
		Clock startOfDayEight = Clock.fixed(Instant.parse("2026-09-28T04:00:00Z"), HistoryWindow.CARACAS);
		check(HistoryWindow.contains(completed, endOfDaySeven), "visible through day seven");
		check(!HistoryWindow.contains(completed, startOfDayEight), "hidden at start of day eight");
		check(HistoryWindow.startInclusive(endOfDaySeven).equals(Instant.parse("2026-09-21T04:00:00Z")),
				"Caracas midnight on day one");
		check(HistoryWindow.startInclusive(startOfDayEight).equals(Instant.parse("2026-09-22T04:00:00Z")),
				"Caracas midnight advances at day eight");
		check(!HistoryWindow.contains(Instant.parse("2026-09-28T04:00:01Z"), startOfDayEight),
				"future completion excluded");
		System.out.println("HistoryWindowSmoke OK: Caracas day seven and day eight boundary");
	}
}
