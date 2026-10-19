package edu.itc.salesreport.render;

import edu.itc.salesreport.model.BranchSummary;
import edu.itc.salesreport.model.MonthlyReport;
import edu.itc.salesreport.model.PaymentMethod;
import edu.itc.salesreport.model.ProductTotal;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

final class TestReports {

    private TestReports() {}

    static BranchSummary branch(String code, String revenue, long receipts) {
        var rev = new BigDecimal(revenue);
        return new BranchSummary(code, (int) receipts, rev,
                List.of(new ProductTotal("SKU-1001", "Jasmine Rice 5kg",
                        (int) receipts, rev)));
    }

    static MonthlyReport september() {
        var pnh = branch("PNH", "1200.00", 100);
        var rep = branch("REP", "800.50", 50);
        var btb = branch("BTB", "300.00", 40);
        var chain = branch("ALL", "2300.50", 190);

        Map<String, BranchSummary> map = Map.of(
                "PNH", pnh,
                "REP", rep,
                "BTB", btb
        );

        return new MonthlyReport(YearMonth.of(2026, 9), 190, new BigDecimal("2300.50"), map, chain);
    }
}