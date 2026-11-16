import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.idempiere.tms.mapview.model.*;

/** Runs against the deployed binary with synthetic input only; no ERP access or persistence. */
public final class ProposalNestingRuntimeSmoke {
    public static void main(String[] args) {
        verify(1000000, 9);
        verify(11, 25);
        System.out.println("ProposalNestingRuntimeSmoke OK: QTanque 25 -> 9; GardenWorld 25 -> 25; no vehicle or capacity claim");
    }
    private static void verify(long tenant, int groups) {
        OrderCandidate order = new OrderCandidate(1,"SYNTHETIC-RUNTIME","Synthetic",1,"","","","",
                Instant.EPOCH,AgingBand.GREEN,"A","","",new BigDecimal("25"),
                null,null,null,false,false,"Synthetic",false,false);
        List<OrderProductLine> lines = List.of(
                new OrderProductLine(tenant,1,1,1,10,"TLL-0500",new BigDecimal("8")),
                new OrderProductLine(tenant,1,2,2,10,"TLL-0550",new BigDecimal("8")),
                new OrderProductLine(tenant,1,3,3,10,"TLL-1060",new BigDecimal("9")));
        ProposalLoadSummary summary = ProposalNestingPreview.evaluate(tenant,List.of(order),lines);
        if (!Integer.valueOf(groups).equals(summary.physicalHandlingUnits()) || !summary.vehiclePending()
                || !summary.provisional() || !summary.displayText().contains("No valida cabida"))
            throw new AssertionError("Unexpected runtime preview for tenant " + tenant);
    }
}
