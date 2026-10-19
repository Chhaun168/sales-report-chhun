package edu.itc.salesreport.ingest;

import edu.itc.salesreport.model.PaymentMethod;
import edu.itc.salesreport.model.SaleTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.function.Function;

/** Parses the canonical Angkor Mart POS export (10 fields, no quoting). */
public final class CsvTransactionParser implements TransactionParser {

    public static final String HEADER = "branch,date,receipt_no,sku,"
            + "product_name,category,quantity,unit_price,discount,"
            + "payment_method";

    private static final Set<String> BRANCHES = Set.of("PNH", "REP", "BTB");
    private static final Set<String> CATEGORIES = Set.of(
        "Grocery", "Fresh Produce", "Beverages", "Household", "Personal Care", "Bakery", "Stationery", "Electronics"
);

    @Override
    public SaleTransaction parse(String line) throws InvalidRowException {
        if (line == null || line.isBlank()) {
            throw new InvalidRowException("empty line");
        }
        String[] f = line.split(",", -1);
        if (f.length != 10) {
            throw new InvalidRowException("expected 10 fields but found " + f.length);
        }

        String branch = f[0].strip();
        if (!BRANCHES.contains(branch)) {
            throw new InvalidRowException("unknown branch: " + branch);
        }

        LocalDate date = field("date", f[1], LocalDate::parse);
        String receiptNo = f[2].strip();
        if (receiptNo.isEmpty()) {
            throw new InvalidRowException("empty receiptNo");
        }

        String sku = f[3].strip();
        if (sku.isEmpty()) {
            throw new InvalidRowException("empty sku");
        }

        String productName = f[4].strip();
        if (productName.isEmpty()) {
            throw new InvalidRowException("empty productName");
        }

        String category = f[5].strip();
        if (!CATEGORIES.contains(category)) {
            throw new InvalidRowException("unknown category: " + category);
        }

        int quantity = field("quantity", f[6], Integer::parseInt);
        BigDecimal unitPrice = field("unitPrice", f[7], BigDecimal::new);
        BigDecimal discount = field("discount", f[8], BigDecimal::new);
        
        PaymentMethod paymentMethod = field("paymentMethod", f[9], s -> PaymentMethod.valueOf(s.strip()));

        try {
            return new SaleTransaction(
                    branch, date, receiptNo, sku, productName,
                    category, quantity, unitPrice, discount, paymentMethod
            );
        } catch (IllegalArgumentException e) {
            throw new InvalidRowException("invalid transaction rule: " + e.getMessage(), e);
        }
    }

    /** Converts one field; any RuntimeException becomes a checked error. */
    private static <T> T field(String name, String raw,
                               Function<String, T> convert) throws InvalidRowException {
        try {
            return convert.apply(raw.strip());
        } catch (RuntimeException e) {
            throw new InvalidRowException(
                    "bad " + name + " \"" + raw + "\"", e);
            }
        }
}  
