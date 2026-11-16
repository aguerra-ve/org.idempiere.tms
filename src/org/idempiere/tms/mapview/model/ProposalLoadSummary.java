package org.idempiere.tms.mapview.model;

import java.math.BigDecimal;
import java.util.Objects;

/** Read-only capacity stages for a temporary proposal; null means not evaluated, never zero. */
public record ProposalLoadSummary(BigDecimal commercialUnits, Integer physicalHandlingUnits,
		String candidateVehicle, boolean provisional, String evaluationNote) {

	public ProposalLoadSummary(BigDecimal commercialUnits, Integer physicalHandlingUnits, String candidateVehicle) {
		this(commercialUnits, physicalHandlingUnits, candidateVehicle, false, null);
	}

	public ProposalLoadSummary {
		Objects.requireNonNull(commercialUnits, "commercialUnits");
		if (commercialUnits.signum() < 0) throw new IllegalArgumentException("commercialUnits must not be negative");
		if (physicalHandlingUnits != null && physicalHandlingUnits < 0)
			throw new IllegalArgumentException("physicalHandlingUnits must not be negative");
		candidateVehicle = candidateVehicle == null || candidateVehicle.isBlank() ? null : candidateVehicle.trim();
	}

	public static ProposalLoadSummary pending(BigDecimal commercialUnits) {
		return new ProposalLoadSummary(commercialUnits, null, null);
	}

	public static ProposalLoadSummary pending(BigDecimal commercialUnits, String reason) {
		return new ProposalLoadSummary(commercialUnits, null, null, false, reason);
	}

	public static ProposalLoadSummary simulation(BigDecimal commercialUnits, int handlingUnits) {
		return new ProposalLoadSummary(commercialUnits, handlingUnits, null, true,
				"Simulación por pedido y almacén; pendiente de entregas. No valida cabida.");
	}

	public ProposalLoadSummary withVehicle(VehicleCandidate vehicle) {
		return new ProposalLoadSummary(commercialUnits, physicalHandlingUnits,
				vehicle == null ? null : vehicle.reviewText(), provisional, evaluationNote);
	}

	public boolean nestingPending() {
		return physicalHandlingUnits == null;
	}

	public boolean vehiclePending() {
		return candidateVehicle == null;
	}

	public String displayText() {
		return "CAPACIDAD FÍSICA\n"
				+ "Unidades comerciales: " + commercialUnits.stripTrailingZeros().toPlainString() + "\n"
				+ "Unidades después del nesting: "
				+ (nestingPending() ? "pendiente" : physicalHandlingUnits)
				+ (provisional ? " (simulación por pedido y almacén)" : "") + "\n"
				+ "Vehículo candidato: "
				+ (vehiclePending() ? "no seleccionado" : escapeHtml(candidateVehicle))
				+ (evaluationNote == null || evaluationNote.isBlank() ? "" : "\n" + escapeHtml(evaluationNote));
	}

	private static String escapeHtml(String value) {
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&#39;");
	}
}
