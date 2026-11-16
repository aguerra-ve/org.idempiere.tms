package org.idempiere.tms.mapview.model;

/** Spanish operator-facing interpretation of C_Order.DeliveryRule. */
public final class DeliveryRulePolicy {
	private DeliveryRulePolicy() {
	}

	public static Result explain(String code) {
		return switch (code == null ? "" : code) {
			case "A" -> new Result("Según disponibilidad", "Permite preparar una cantidad parcial disponible");
			case "F" -> new Result("Forzar", "Requiere autorización si falta inventario");
			case "L" -> new Result("Línea completa", "Cada línea incluida debe prepararse completa");
			case "O" -> new Result("Pedido completo", "Todas las líneas del pedido deben estar disponibles");
			case "R" -> new Result("Después del cobro", "No preparar hasta confirmar la condición financiera");
			case "M" -> new Result("Manual", "El despachador debe tomar una decisión explícita");
			default -> new Result("Revisar", "Regla de entrega no reconocida: " + (code == null ? "vacía" : code));
		};
	}

	public record Result(String label, String explanation) {
	}
}
