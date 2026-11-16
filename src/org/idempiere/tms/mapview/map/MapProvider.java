package org.idempiere.tms.mapview.map;

import java.util.List;
import java.util.Set;

import org.idempiere.tms.mapview.model.OrderCandidate;

/** OSGi extension point for rendering the predispatch map. */
public interface MapProvider {
	String createDocument(List<OrderCandidate> candidates, Set<Integer> selectedOrderIds);
}
