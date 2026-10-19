package edu.itc.salesreport.ingest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MonthLoaderTest {

    @Test
    void loaderPublishesEventsAndHandlesUnsubscribe(@TempDir Path tempDir) throws Exception {
        Path monthDir = tempDir.resolve("2026-09");
        Files.createDirectories(monthDir);
        Files.writeString(monthDir.resolve("PNH-2026-09-01.csv"),
                CsvTransactionParser.HEADER + "\n"
                + "PNH,2026-09-01,PNH-1,SKU-1,Rice,Grocery,2,6.50,0.00,KHQR\n"
                + "PNH,2026-09-01,PNH-2,SKU-1,Rice,Grocery,invalid,6.50,0.00,KHQR\n");

        MonthLoader loader = new MonthLoader(new CsvTransactionParser());
        List<LoadEvent> events = new ArrayList<>();
        Runnable unsubscribe = loader.subscribe(events::add);

        // Test unsubscribe before load
        unsubscribe.run();

        loader.load(YearMonth.of(2026, 9), tempDir);
        assertTrue(events.isEmpty(), "Unsubscribed listener should receive no events");

        // Test normal subscription
        loader.subscribe(events::add);
        loader.load(YearMonth.of(2026, 9), tempDir);
        assertEquals(2, events.size()); // FileLoaded + LoadFinished
    }
}