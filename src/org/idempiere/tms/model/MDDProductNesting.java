/**
 * Copyright (C) 2003-2017, e-Evolution Consultants S.A. , http://www.e-evolution.com
 * This program is free software, you can redistribute it and/or modify it
 * under the terms version 2 of the GNU General Public License as published
 * or (at your option) any later version.
 * by the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along
 * with this program, if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * For the text or an alternative of this public license, you may reach us
 * or via info@adempiere.net or http://www.adempiere.net/license.html
 */

package org.idempiere.tms.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Domain Model for Product Nesting.
 *
 * Models which tank sizes ("Classification") can nest inside which other sizes of the same
 * "NestingFamily" (e.g. CILINDRICO, CONICO). Any smaller SeqNo can nest inside any larger SeqNo
 * of the same family, not only the immediately adjacent size (confirmed against a real shipment,
 * see ANIDADO_TANQUES_SAP.md in this repo, section 8).
 */
public class MDDProductNesting extends X_DD_ProductNesting {

    public MDDProductNesting(Properties ctx, int dd_ProductNesting_ID, String trxName) {
        super(ctx, dd_ProductNesting_ID, trxName);
    }

    public MDDProductNesting(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

    /**
     * How many physical "footprints" (truck slots) a family needs for a given mix of quantities:
     * the max quantity present across any single size tier of that family. A footprint can carry
     * at most one unit per tier nested inside it, so no family needs more footprints than its
     * busiest tier, and it never needs fewer (that tier's units have nowhere else to go).
     *
     * @param qtyBySeqNo quantity present for each SeqNo tier of ONE family (tiers with 0/absent qty may be omitted)
     * @return the minimum number of footprints required
     */
    public static BigDecimal getFootprintCount(Map<Integer, BigDecimal> qtyBySeqNo) {
        BigDecimal max = BigDecimal.ZERO;
        for (BigDecimal qty : qtyBySeqNo.values()) {
            if (qty != null && qty.compareTo(max) > 0)
                max = qty;
        }
        return max;
    }

    /**
     * Allocates the effective volume-per-unit for every Classification in ONE NestingFamily,
     * given how many units of each are present together (e.g. in one createFreightUnit run).
     *
     * Algorithm (verified by hand against a real order, IVS_PreShipment_ID=1005978, in
     * ANIDADO_TANQUES_SAP.md §08): walk tiers from the largest SeqNo down. Each footprint needs
     * exactly one "anchor" (outermost tank); assign anchors greedily from the largest tier first,
     * since a bigger tank can always serve as an anchor for a footprint that a smaller one could
     * also anchor, but not vice versa. Once every footprint has an anchor, every remaining unit of
     * every smaller tier is assumed to nest for free inside an already-anchored footprint.
     *
     * IMPORTANT — interim placeholder: NestedVolume has not been measured yet for any model
     * (pending real-world measurement). Units that end up "nested for free" are valued at 0 here,
     * not at their real marginal volume. This UNDER-counts total volume slightly (a nested tank
     * does take a small amount of extra space) but never under-counts the number of footprints —
     * the anchor count is exact. It is therefore a safe, conservative placeholder for capacity
     * planning: it will not cause a load to be under-booked in terms of how many truck "slots" are
     * needed, only in exactly how full each slot is estimated to be. TODO: once NestedVolume is
     * measured and populated, replace the 0 contribution of nested units with their own
     * NestedVolume so the estimate becomes exact instead of conservative.
     *
     * @param qtyByClassification   quantity present for each Classification of this family
     * @param seqNoByClassification SeqNo (size rank) for each Classification of this family
     * @param fullVolumeByClassification M_Product.Volume for each Classification of this family
     * @return effective volume-per-unit to use for each Classification, for THIS batch only
     */
    public static Map<String, BigDecimal> allocateFootprintVolume(
            Map<String, BigDecimal> qtyByClassification,
            Map<String, Integer> seqNoByClassification,
            Map<String, BigDecimal> fullVolumeByClassification) {

        Map<String, BigDecimal> result = new HashMap<String, BigDecimal>();

        List<String> classifications = new ArrayList<String>(qtyByClassification.keySet());
        // largest SeqNo first
        classifications.sort(new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                Integer seqA = seqNoByClassification.get(a);
                Integer seqB = seqNoByClassification.get(b);
                return Integer.compare(seqB == null ? 0 : seqB, seqA == null ? 0 : seqA);
            }
        });

        BigDecimal remainingFootprints = getFootprintCount(toSeqNoKeyedMap(qtyByClassification, seqNoByClassification));

        for (String classification : classifications) {
            BigDecimal qty = qtyByClassification.get(classification);
            result.put(classification, BigDecimal.ZERO);

            if (qty == null || qty.signum() <= 0 || remainingFootprints.signum() <= 0)
                continue;

            BigDecimal anchorsUsed = qty.min(remainingFootprints);
            BigDecimal fullVolume = fullVolumeByClassification.get(classification);
            if (fullVolume != null && anchorsUsed.signum() > 0) {
                // spread the anchor cost evenly across every unit of this classification present —
                // individual units of the same product are interchangeable, so it does not matter
                // which specific one is "the anchor".
                BigDecimal perUnit = fullVolume.multiply(anchorsUsed).divide(qty, 6, RoundingMode.HALF_UP);
                result.put(classification, perUnit);
            }
            remainingFootprints = remainingFootprints.subtract(anchorsUsed);
        }

        return result;
    }

    private static Map<Integer, BigDecimal> toSeqNoKeyedMap(Map<String, BigDecimal> qtyByClassification,
            Map<String, Integer> seqNoByClassification) {
        Map<Integer, BigDecimal> bySeqNo = new HashMap<Integer, BigDecimal>();
        for (Map.Entry<String, BigDecimal> entry : qtyByClassification.entrySet()) {
            Integer seqNo = seqNoByClassification.get(entry.getKey());
            if (seqNo == null)
                continue;
            BigDecimal existing = bySeqNo.get(seqNo);
            bySeqNo.put(seqNo, existing == null ? entry.getValue() : existing.add(entry.getValue()));
        }
        return bySeqNo;
    }
}
