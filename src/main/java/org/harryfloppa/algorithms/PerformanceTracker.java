package org.harryfloppa.algorithms;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Tracks performance metrics for heap operations with CSV export capability
 */
public class PerformanceTracker {
    public long comparisons = 0;
    public long swaps = 0;
    public long arrayAccesses = 0;
    public long allocations = 0;
    private long startTime = 0;
    private long endTime = 0;

    public void startTimer() {
        startTime = System.nanoTime();
    }

    public void stopTimer() {
        endTime = System.nanoTime();
    }

    public long getElapsedTimeNanos() {
        return endTime - startTime;
    }

    public double getElapsedTimeMillis() {
        return (endTime - startTime) / 1_000_000.0;
    }

    public void reset() {
        comparisons = 0;
        swaps = 0;
        arrayAccesses = 0;
        allocations = 0;
        startTime = 0;
        endTime = 0;
    }

    public PerformanceTracker snapshot() {
        PerformanceTracker copy = new PerformanceTracker();
        copy.comparisons = this.comparisons;
        copy.swaps = this.swaps;
        copy.arrayAccesses = this.arrayAccesses;
        copy.allocations = this.allocations;
        copy.startTime = this.startTime;
        copy.endTime = this.endTime;
        return copy;
    }

    /**
     * Export metrics to CSV file
     * @param filename Output CSV file name
     * @param operation The operation being measured (e.g., "insert", "extractMin")
     * @param dataSize Size of data set
     * @param append Whether to append to existing file or create new
     */
    public void exportToCSV(String filename, String operation, int dataSize, boolean append) {
        try {
            boolean writeHeader = !append || !Files.exists(Paths.get(filename));

            try (PrintWriter writer = new PrintWriter(new FileWriter(filename, append))) {
                if (writeHeader) {
                    writer.println("timestamp,operation,dataSize,comparisons,swaps,arrayAccesses,allocations,timeMs");
                }

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                writer.printf("%s,%s,%d,%d,%d,%d,%d,%.3f%n",
                        timestamp, operation, dataSize,
                        comparisons, swaps, arrayAccesses, allocations,
                        getElapsedTimeMillis());
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }

    /**
     * Export batch of metrics to CSV
     */
    public static void exportBatchToCSV(String filename, String operation,
                                        int[] dataSizes, PerformanceTracker[] trackers) {
        try {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                writer.println("operation,dataSize,comparisons,swaps,arrayAccesses,allocations,timeMs");

                for (int i = 0; i < dataSizes.length && i < trackers.length; i++) {
                    PerformanceTracker t = trackers[i];
                    writer.printf("%s,%d,%d,%d,%d,%d,%.3f%n",
                            operation, dataSizes[i],
                            t.comparisons, t.swaps, t.arrayAccesses, t.allocations,
                            t.getElapsedTimeMillis());
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing batch to CSV: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return String.format(
                "PerformanceTracker{comparisons=%d, swaps=%d, arrayAccesses=%d, allocations=%d, timeMs=%.3f}",
                comparisons, swaps, arrayAccesses, allocations, getElapsedTimeMillis()
        );
    }

    /**
     * Pretty print metrics
     */
    public void printReport(String operation, int dataSize) {
        System.out.println("\n=== Performance Report ===");
        System.out.println("Operation: " + operation);
        System.out.println("Data Size: " + dataSize);
        System.out.println("Comparisons: " + comparisons);
        System.out.println("Swaps: " + swaps);
        System.out.println("Array Accesses: " + arrayAccesses);
        System.out.println("Allocations: " + allocations);
        System.out.printf("Time: %.3f ms%n", getElapsedTimeMillis());
        System.out.println("========================\n");
    }
}