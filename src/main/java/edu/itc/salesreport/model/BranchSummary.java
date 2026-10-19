package edu.itc.salesreport.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public record BranchSummary(
        String branch, int receipts, BigDecimal revenue,
        List<ProductTotal> topProducts) {

    public BranchSummary {
        Objects.requireNonNull(branch, "branch");
        Objects.requireNonNull(revenue, "revenue");
        topProducts = List.copyOf(Objects.requireNonNull(topProducts, "topProducts"));
    }

    public BigDecimal averageBasket() {
        if (receipts <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return revenue.divide(BigDecimal.valueOf(receipts), 2, RoundingMode.HALF_UP);
    }
}