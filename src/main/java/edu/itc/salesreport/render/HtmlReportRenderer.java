package edu.itc.salesreport.render;

import edu.itc.salesreport.model.BranchSummary;
import edu.itc.salesreport.model.MonthlyReport;

import java.nio.charset.StandardCharsets;

public final class HtmlReportRenderer implements ReportRenderer {

    @Override
    public String extension() {
        return "html";
    }

    @Override
    public byte[] render(MonthlyReport report) {
        var sb = new StringBuilder();
        sb.append("<table>\n");
        sb.append("  <tr><th>Branch</th><th>Revenue</th><th>Receipts</th><th>Avg basket</th></tr>\n");
        for (BranchSummary b : report.branches().values()) {
            sb.append(String.format("  <tr><td>%s</td><td>%s</td><td>%d</td><td>%s</td></tr>\n",
                    escape(b.branch()), b.revenue(), b.receipts(), b.averageBasket()));
        }
        var chain = report.chain();
        sb.append(String.format("  <tr><td>%s</td><td>%s</td><td>%d</td><td>%s</td></tr>\n",
                escape(chain.branch()), chain.revenue(), chain.receipts(), chain.averageBasket()));
        sb.append("</table>");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
