package org.idempiere.tms.mapview.model;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;

/** Approximate state points used until the Ship-To stores validated coordinates. */
public final class StateCoordinates {
	private static final Map<String, Point> POINTS = Map.ofEntries(
			Map.entry("AMAZONAS", new Point(-65.55, 3.50)),
			Map.entry("ANZOATEGUI", new Point(-64.68, 9.00)),
			Map.entry("APURE", new Point(-67.47, 7.05)),
			Map.entry("ARAGUA", new Point(-67.47, 10.24)),
			Map.entry("BARINAS", new Point(-70.20, 8.63)),
			Map.entry("BOLIVAR", new Point(-63.55, 8.13)),
			Map.entry("CARABOBO", new Point(-68.00, 10.17)),
			Map.entry("COJEDES", new Point(-68.33, 9.38)),
			Map.entry("DELTA AMACURO", new Point(-61.60, 8.80)),
			Map.entry("DISTRITO CAPITAL", new Point(-66.90, 10.49)),
			Map.entry("FALCON", new Point(-69.67, 11.18)),
			Map.entry("GUARICO", new Point(-66.59, 8.75)),
			Map.entry("LA GUAIRA", new Point(-66.93, 10.60)),
			Map.entry("LARA", new Point(-69.32, 10.07)),
			Map.entry("MERIDA", new Point(-71.16, 8.60)),
			Map.entry("MIRANDA", new Point(-66.42, 10.25)),
			Map.entry("MONAGAS", new Point(-63.18, 9.75)),
			Map.entry("NUEVA ESPARTA", new Point(-63.93, 10.96)),
			Map.entry("PORTUGUESA", new Point(-69.20, 9.10)),
			Map.entry("SUCRE", new Point(-63.25, 10.45)),
			Map.entry("TACHIRA", new Point(-72.25, 7.77)),
			Map.entry("TRUJILLO", new Point(-70.43, 9.37)),
			Map.entry("VARGAS", new Point(-66.93, 10.60)),
			Map.entry("YARACUY", new Point(-68.74, 10.34)),
			Map.entry("ZULIA", new Point(-71.64, 10.64)));

	private StateCoordinates() {
	}

	public static Point find(String state) {
		if (state == null) {
			return null;
		}
		String key = Normalizer.normalize(state, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
				.toUpperCase(Locale.ROOT).trim();
		return POINTS.get(key);
	}

	public record Point(double longitude, double latitude) {
	}
}
