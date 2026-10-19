package edu.itc.salesreport.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;
import java.util.Objects;

public record MonthlyReport(
        YearMonth month, int receipts, BigDecimal revenue,
        Map<String, BranchSummary> branches) {

    public MonthlyReport {
        Objects.requireNonNull(month, "month");
        Objects.requireNonNull(revenue, "revenue");
        branches = Map.copyOf(Objects.requireNonNull(branches, "branches"));
    }
}
