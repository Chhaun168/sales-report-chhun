package edu.itc.salesreport.render;

import edu.itc.salesreport.model.MonthlyReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ReportRendererTest {

    @ParameterizedTest
    @ValueSource(strings = {"txt", "html", "csv"})
    void rendersAllFormats(String ext) {
        MonthlyReport report = TestReports.september();
        ReportRenderer renderer = ReportRenderer.forExtension(ext);
        assertEquals(ext, renderer.extension());
        byte[] bytes = renderer.render(report);
        String content = new String(bytes, StandardCharsets.UTF_8);

        assertTrue(content.contains("PNH"));
        assertTrue(content.contains("REP"));
        assertTrue(content.contains("BTB"));
        assertTrue(content.contains("2300.50"));
    }

    @Test
    void csvExactLineFormat() {
        MonthlyReport report = TestReports.september();
        ReportRenderer renderer = ReportRenderer.forExtension("csv");
        String content = new String(renderer.render(report), StandardCharsets.UTF_8);
        assertTrue(content.contains("2026-09,REP,800.50,0.00,50,16.01"));
    }

    @Test
    void invalidExtensionThrows() {
        assertThrows(IllegalArgumentException.class, () -> ReportRenderer.forExtension("pdf"));
    }
}
