package org.idempiere.tms.mapview.map;

import java.util.List;
import java.util.Set;

import org.idempiere.tms.mapview.form.PredispatchMapDocument;
import org.idempiere.tms.mapview.model.OrderCandidate;

/** Default map provider, packaged with the bundle and backed by OpenStreetMap tiles. */
public final class MapLibreProvider implements MapProvider {
	@Override
	public String createDocument(List<OrderCandidate> candidates, Set<Integer> selectedOrderIds) {
		return PredispatchMapDocument.create(candidates, selectedOrderIds);
	}
}
