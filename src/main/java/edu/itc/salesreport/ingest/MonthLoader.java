package edu.itc.salesreport.ingest;

import edu.itc.salesreport.model.SaleTransaction;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;
/** The subject that loads monthly CSV files and notifies observers. */
public final class MonthLoader {

    private final TransactionParser parser;
    private final List<LoadListener> listeners = new CopyOnWriteArrayList<>();
    private final List<String> rejectedRows = new ArrayList<>();

    public MonthLoader(TransactionParser parser) {
        this.parser = parser;
    }

    public Runnable subscribe(LoadListener listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    public List<String> rejectedRows() {
        return List.copyOf(rejectedRows);
    }

    public List<SaleTransaction> load(YearMonth month, Path dataDir) {
        long startNanos = System.nanoTime();
        Path dir = dataDir.resolve(month.toString());
        List<SaleTransaction> acceptedTransactions = new ArrayList<>();
        rejectedRows.clear();

        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            Duration took = Duration.ofNanos(System.nanoTime() - startNanos);
            publish(new LoadEvent.LoadFinished(0, 0, took));
            return acceptedTransactions;
        }

        List<Path> files;
        try (Stream<Path> stream = Files.list(dir)) {
            files = stream.filter(p -> p.toString().endsWith(".csv"))
                          .sorted()
                          .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        int filesTotal = files.size();
        int filesDone = 0;
        int totalAccepted = 0;
        int totalRejected = 0;

        for (Path file : files) {
            int fileAccepted = 0;
            int fileRejected = 0;
            List<String> lines;
            try {
                lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                try {
                    SaleTransaction tx = parser.parse(line);
                    acceptedTransactions.add(tx);
                    fileAccepted++;
                } catch (InvalidRowException e) {
                    fileRejected++;
                    rejectedRows.add(file.getFileName() + ":" + (i + 1) + " " + e.getMessage());
                }
            }

            filesDone++;
            totalAccepted += fileAccepted;
            totalRejected += fileRejected;
            publish(new LoadEvent.FileLoaded(file, fileAccepted, fileRejected, filesDone, filesTotal));
        }

        Duration took = Duration.ofNanos(System.nanoTime() - startNanos);
        publish(new LoadEvent.LoadFinished(totalAccepted, totalRejected, took));
        return acceptedTransactions;
    }

    private void publish(LoadEvent event) {
        for (LoadListener l : listeners) {
            l.onEvent(event);
        }
    }
}
