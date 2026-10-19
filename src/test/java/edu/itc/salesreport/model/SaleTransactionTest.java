package edu.itc.salesreport.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaleTransactionTest {

    @Test
    void roundsRevenueCorrectly() {
        SaleTransaction tx = new SaleTransaction(
                "PNH", LocalDate.of(2026, 9, 1), "R-1", "SKU-1",
                "Item", "Grocery", 3, new BigDecimal("0.335"),
                BigDecimal.ZERO, PaymentMethod.CASH
        );
        assertEquals(new BigDecimal("1.01"), tx.revenue());
    }

    @Test
    void branchSummaryTopProductsIsUnmodifiable() {
        ProductTotal pt = new ProductTotal("SKU-1", "Item", 2, new BigDecimal("10.00"));
        BranchSummary summary = new BranchSummary("PNH", 1, new BigDecimal("10.00"), List.of(pt));
        
        assertThrows(UnsupportedOperationException.class, () -> {
            summary.topProducts().add(new ProductTotal("SKU-2", "Item2", 1, new BigDecimal("5.00")));
        });
    }
}
