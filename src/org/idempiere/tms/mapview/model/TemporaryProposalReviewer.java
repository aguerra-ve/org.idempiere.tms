package org.idempiere.tms.mapview.model;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/** Re-reads authorized selection and lines before producing an in-memory preview. */
public final class TemporaryProposalReviewer {
	public interface Source {
		List<OrderCandidate> findCandidates() throws SQLException;
		List<OrderProductLine> findPendingProductLines(Set<Integer> orderIds) throws SQLException;
	}
	@FunctionalInterface
	public interface VehicleSource { List<VehicleCandidate> findCandidates() throws SQLException; }
	public static TemporaryRouteProposal review(long tenantId, Set<Integer> selectedOrderIds,
			Source source, VehicleCandidate selectedVehicle, VehicleSource vehicles) throws SQLException {
		VehicleCandidate current = selectedVehicle == null ? null : VehicleCandidate.requireSelected(
				tenantId, selectedVehicle.vehicleId(), vehicles.findCandidates());
		if (selectedVehicle != null && (selectedVehicle.tenantId() != tenantId
				|| !selectedVehicle.plate().equals(current.plate())
				|| !selectedVehicle.samePlatformIdentity(current)))
			throw new SQLException("La identidad del vehículo cambió; vuelva a seleccionarlo.");
		TemporaryRouteProposal proposal = review(tenantId, selectedOrderIds, source);
		return proposal.withLoadSummary(proposal.loadSummary().withVehicle(current));
	}

	private TemporaryProposalReviewer() { }
	public static TemporaryRouteProposal review(long tenantId, Set<Integer> selectedOrderIds, Source source)
			throws SQLException {
		Set<Integer> selection = Set.copyOf(selectedOrderIds);
		TemporaryRouteProposal proposal = TemporaryRouteProposal.from(source.findCandidates(), selection);
		if (proposal.orders().size() != selection.size())
			throw new SQLException("La selección cambió; actualice el mapa antes de revisar la propuesta.");
		if (selection.isEmpty()) return proposal;
		return proposal.withLoadSummary(ProposalNestingPreview.evaluate(tenantId, proposal.orders(),
				source.findPendingProductLines(selection)));
	}
}
