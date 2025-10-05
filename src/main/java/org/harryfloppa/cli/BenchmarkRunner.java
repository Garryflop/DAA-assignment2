package org.harryfloppa.cli;

import org.harryfloppa.algorithms.MinHeap;
import org.harryfloppa.metrics.PerformanceTracker;

import java.util.Random;
import java.util.Scanner;

/**
 * CLI Benchmark Runner for MinHeap operations
 */
public class BenchmarkRunner {

    private static final int[] DEFAULT_SIZES = {100, 500, 1000, 5000, 10000};
    private static final String CSV_FILE = "benchmark-results.csv";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   MinHeap Benchmark Runner v1.0        ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        while (true) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Run Insert Benchmark");
            System.out.println("2. Run ExtractMin Benchmark");
            System.out.println("3. Run DecreaseKey Benchmark");
            System.out.println("4. Run Merge Benchmark");
            System.out.println("5. Run All Benchmarks");
            System.out.println("6. Custom Size Benchmark");
            System.out.println("0. Exit");
            System.out.print("\nChoice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> benchmarkInsert(DEFAULT_SIZES);
                case 2 -> benchmarkExtractMin(DEFAULT_SIZES);
                case 3 -> benchmarkDecreaseKey(DEFAULT_SIZES);
                case 4 -> benchmarkMerge(DEFAULT_SIZES);
                case 5 -> runAllBenchmarks(DEFAULT_SIZES);
                case 6 -> customSizeBenchmark(scanner);
                case 0 -> {
                    System.out.println("\nExiting... Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private static void benchmarkInsert(int[] sizes) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║      INSERT OPERATION BENCHMARK        ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        PerformanceTracker[] trackers = new PerformanceTracker[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            int size = sizes[i];
            System.out.printf("Testing with %d elements... ", size);

            PerformanceTracker tracker = new PerformanceTracker();
            MinHeap<Integer> heap = new MinHeap<>(tracker);
            Random rand = new Random(42); // Fixed seed for reproducibility

            tracker.startTimer();
            for (int j = 0; j < size; j++) {
                heap.insert(rand.nextInt(size * 10));
            }
            tracker.stopTimer();

            trackers[i] = tracker;
            System.out.printf("Done (%.2f ms)%n", tracker.getElapsedTimeMillis());
        }

        printSummaryTable("INSERT", sizes, trackers);
        PerformanceTracker.exportBatchToCSV(CSV_FILE, "insert", sizes, trackers);
        System.out.println("\n✓ Results exported to " + CSV_FILE);
    }

    private static void benchmarkExtractMin(int[] sizes) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    EXTRACT-MIN OPERATION BENCHMARK     ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        PerformanceTracker[] trackers = new PerformanceTracker[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            int size = sizes[i];
            System.out.printf("Testing with %d elements... ", size);

            // First, build the heap
            MinHeap<Integer> heap = new MinHeap<>();
            Random rand = new Random(42);
            for (int j = 0; j < size; j++) {
                heap.insert(rand.nextInt(size * 10));
            }

            // Now measure extractMin
            PerformanceTracker tracker = new PerformanceTracker();
            tracker.startTimer();
            for (int j = 0; j < size; j++) {
                heap.extractMin();
            }
            tracker.stopTimer();

            trackers[i] = tracker;
            System.out.printf("Done (%.2f ms)%n", tracker.getElapsedTimeMillis());
        }

        printSummaryTable("EXTRACT-MIN", sizes, trackers);
        PerformanceTracker.exportBatchToCSV(CSV_FILE, "extractMin", sizes, trackers);
        System.out.println("\n✓ Results exported to " + CSV_FILE);
    }

    private static void benchmarkDecreaseKey(int[] sizes) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   DECREASE-KEY OPERATION BENCHMARK     ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        PerformanceTracker[] trackers = new PerformanceTracker[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            int size = sizes[i];
            System.out.printf("Testing with %d elements... ", size);

            PerformanceTracker tracker = new PerformanceTracker();
            MinHeap<Integer> heap = new MinHeap<>(tracker);

            // Insert elements
            for (int j = 0; j < size; j++) {
                heap.insert(j * 10);
            }

            tracker.reset();
            tracker.startTimer();

            // Decrease random keys
            Random rand = new Random(42);
            for (int j = 0; j < Math.min(size, 1000); j++) {
                int oldVal = rand.nextInt(size) * 10;
                int newVal = oldVal - rand.nextInt(5) - 1;
                try {
                    heap.decreaseKey(oldVal, newVal);
                } catch (IllegalArgumentException e) {
                    // Element might have been decreased already
                }
            }

            tracker.stopTimer();
            trackers[i] = tracker;
            System.out.printf("Done (%.2f ms)%n", tracker.getElapsedTimeMillis());
        }

        printSummaryTable("DECREASE-KEY", sizes, trackers);
        PerformanceTracker.exportBatchToCSV(CSV_FILE, "decreaseKey", sizes, trackers);
        System.out.println("\n✓ Results exported to " + CSV_FILE);
    }

    private static void benchmarkMerge(int[] sizes) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║      MERGE OPERATION BENCHMARK         ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        PerformanceTracker[] trackers = new PerformanceTracker[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            int size = sizes[i];
            System.out.printf("Testing with %d elements... ", size);

            PerformanceTracker tracker = new PerformanceTracker();

            // Create two heaps of equal size
            MinHeap<Integer> heap1 = new MinHeap<>(tracker);
            MinHeap<Integer> heap2 = new MinHeap<>();

            Random rand = new Random(42);
            for (int j = 0; j < size / 2; j++) {
                heap1.insert(rand.nextInt(size * 10));
                heap2.insert(rand.nextInt(size * 10));
            }

            tracker.reset();
            tracker.startTimer();
            heap1.merge(heap2);
            tracker.stopTimer();

            trackers[i] = tracker;
            System.out.printf("Done (%.2f ms)%n", tracker.getElapsedTimeMillis());
        }

        printSummaryTable("MERGE", sizes, trackers);
        PerformanceTracker.exportBatchToCSV(CSV_FILE, "merge", sizes, trackers);
        System.out.println("\n✓ Results exported to " + CSV_FILE);
    }

    private static void runAllBenchmarks(int[] sizes) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║      RUNNING ALL BENCHMARKS            ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        benchmarkInsert(sizes);
        benchmarkExtractMin(sizes);
        benchmarkDecreaseKey(sizes);
        benchmarkMerge(sizes);

        System.out.println("\n✓ All benchmarks complete!");
    }

    private static void customSizeBenchmark(Scanner scanner) {
        System.out.print("\nEnter custom size: ");
        int size = scanner.nextInt();

        System.out.println("\nSelect operation:");
        System.out.println("1. Insert");
        System.out.println("2. ExtractMin");
        System.out.println("3. DecreaseKey");
        System.out.println("4. Merge");
        System.out.println("5. Run All");
        System.out.print("Choice: ");

        int choice = scanner.nextInt();
        int[] customSize = {size};

        switch (choice) {
            case 1 -> benchmarkInsert(customSize);
            case 2 -> benchmarkExtractMin(customSize);
            case 3 -> benchmarkDecreaseKey(customSize);
            case 4 -> benchmarkMerge(customSize);
            case 5 -> runAllBenchmarks(customSize);
            default -> System.out.println("Invalid choice!");
        }
    }

    private static void printSummaryTable(String operation, int[] sizes, PerformanceTracker[] trackers) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.printf("║                    %s BENCHMARK RESULTS%-20s║%n", operation, "");
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");
        System.out.println("║  Size  │  Time(ms)  │ Comparisons │   Swaps   │ Array Access      ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");

        for (int i = 0; i < sizes.length; i++) {
            PerformanceTracker t = trackers[i];
            System.out.printf("║ %6d │ %10.2f │ %11d │ %9d │ %12d      ║%n",
                    sizes[i], t.getElapsedTimeMillis(), t.comparisons, t.swaps, t.arrayAccesses);
        }

        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
    }
}