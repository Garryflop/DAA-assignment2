package org.harryfloppa.algorithms;

import org.harryfloppa.metrics.PerformanceTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapTest {

    private MinHeap<Integer> heap;
    private PerformanceTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new PerformanceTracker();
        heap = new MinHeap<>(tracker);
    }

    @Test
    @DisplayName("New heap should be empty")
    void testNewHeapIsEmpty() {
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
    }

    @Test
    @DisplayName("Insert single element")
    void testInsertSingleElement() {
        heap.insert(5);
        assertFalse(heap.isEmpty());
        assertEquals(1, heap.size());
        assertEquals(5, heap.peekMin());
    }

    @Test
    @DisplayName("Insert multiple elements maintains min heap property")
    void testInsertMultipleElements() {
        heap.insert(5).insert(3).insert(7).insert(1);

        assertEquals(4, heap.size());
        assertEquals(1, heap.peekMin());
    }

    @Test
    @DisplayName("Extract min returns elements in sorted order")
    void testExtractMin() {
        heap.insert(5).insert(3).insert(7).insert(1).insert(9).insert(2);

        assertEquals(1, heap.extractMin());
        assertEquals(2, heap.extractMin());
        assertEquals(3, heap.extractMin());
        assertEquals(5, heap.extractMin());
        assertEquals(7, heap.extractMin());
        assertEquals(9, heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    @DisplayName("Extract from empty heap throws exception")
    void testExtractFromEmptyHeap() {
        assertThrows(NoSuchElementException.class, () -> heap.extractMin());
    }

    @Test
    @DisplayName("Peek from empty heap throws exception")
    void testPeekFromEmptyHeap() {
        assertThrows(NoSuchElementException.class, () -> heap.peekMin());
    }

    @Test
    @DisplayName("DecreaseKey operation works correctly")
    void testDecreaseKey() {
        heap.insert(10).insert(20).insert(30);

        assertEquals(10, heap.peekMin());

        // Decrease 30 to 5 (should become new min)
        heap.decreaseKey(30, 5);
        assertEquals(5, heap.peekMin());

        // Decrease 20 to 3 (should become new min)
        heap.decreaseKey(20, 3);
        assertEquals(3, heap.peekMin());
    }

    @Test
    @DisplayName("DecreaseKey with invalid new value throws exception")
    void testDecreaseKeyInvalid() {
        heap.insert(10).insert(20);

        // Try to increase key (should fail)
        assertThrows(IllegalArgumentException.class,
                () -> heap.decreaseKey(10, 15));
    }

    @Test
    @DisplayName("DecreaseKey with non-existent element throws exception")
    void testDecreaseKeyNonExistent() {
        heap.insert(10).insert(20);

        assertThrows(IllegalArgumentException.class,
                () -> heap.decreaseKey(99, 5));
    }

    @Test
    @DisplayName("Merge two heaps correctly")
    void testMerge() {
        MinHeap<Integer> heap1 = new MinHeap<>();
        heap1.insert(5).insert(10).insert(15);

        MinHeap<Integer> heap2 = new MinHeap<>();
        heap2.insert(3).insert(7).insert(12);

        heap1.merge(heap2);

        assertEquals(6, heap1.size());
        assertEquals(3, heap1.extractMin());
        assertEquals(5, heap1.extractMin());
        assertEquals(7, heap1.extractMin());
        assertEquals(10, heap1.extractMin());
        assertEquals(12, heap1.extractMin());
        assertEquals(15, heap1.extractMin());
    }

    @Test
    @DisplayName("Merge with empty heap")
    void testMergeWithEmpty() {
        heap.insert(5).insert(10);
        MinHeap<Integer> emptyHeap = new MinHeap<>();

        heap.merge(emptyHeap);
        assertEquals(2, heap.size());
    }

    @Test
    @DisplayName("Merge empty heap with non-empty heap")
    void testMergeEmptyWithNonEmpty() {
        MinHeap<Integer> heap2 = new MinHeap<>();
        heap2.insert(5).insert(10);

        heap.merge(heap2);
        assertEquals(2, heap.size());
        assertEquals(5, heap.peekMin());
    }

    @Test
    @DisplayName("Heap handles duplicate values")
    void testDuplicateValues() {
        heap.insert(5).insert(5).insert(3).insert(3);

        assertEquals(3, heap.extractMin());
        assertEquals(3, heap.extractMin());
        assertEquals(5, heap.extractMin());
        assertEquals(5, heap.extractMin());
    }

    @Test
    @DisplayName("Heap resizes correctly")
    void testResize() {
        // Insert more than initial capacity (starts at 2)
        for (int i = 10; i >= 1; i--) {
            heap.insert(i);
        }

        assertEquals(10, heap.size());
        assertEquals(1, heap.peekMin());
    }

    @Test
    @DisplayName("Performance tracker counts operations")
    void testPerformanceTracking() {
        tracker.reset();

        heap.insert(5);
        heap.insert(3);
        heap.insert(7);

        assertTrue(tracker.comparisons > 0, "Should have comparisons");
        assertTrue(tracker.arrayAccesses > 0, "Should have array accesses");

        System.out.println("After 3 inserts: " + tracker);
    }

    @Test
    @DisplayName("Insert null throws exception")
    void testInsertNull() {
        assertThrows(IllegalArgumentException.class, () -> heap.insert(null));
    }

    @Test
    @DisplayName("String heap works correctly")
    void testStringHeap() {
        MinHeap<String> stringHeap = new MinHeap<>();
        stringHeap.insert("banana")
                .insert("apple")
                .insert("cherry")
                .insert("apricot");

        assertEquals("apple", stringHeap.extractMin());
        assertEquals("apricot", stringHeap.extractMin());
        assertEquals("banana", stringHeap.extractMin());
        assertEquals("cherry", stringHeap.extractMin());
    }

    // ===== EDGE CASES =====

    @Test
    @DisplayName("Edge case: Single element operations")
    void testSingleElement() {
        heap.insert(42);
        assertEquals(42, heap.peekMin());
        assertEquals(42, heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    @DisplayName("Edge case: Two elements")
    void testTwoElements() {
        heap.insert(10).insert(5);
        assertEquals(5, heap.extractMin());
        assertEquals(10, heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    @DisplayName("Edge case: Large dataset")
    void testLargeDataset() {
        int n = 10000;
        for (int i = n; i > 0; i--) {
            heap.insert(i);
        }

        assertEquals(n, heap.size());

        for (int i = 1; i <= n; i++) {
            assertEquals(i, heap.extractMin());
        }

        assertTrue(heap.isEmpty());
    }

    @Test
    @DisplayName("Edge case: All same values")
    void testAllSameValues() {
        heap.insert(5).insert(5).insert(5).insert(5);

        for (int i = 0; i < 4; i++) {
            assertEquals(5, heap.extractMin());
        }
    }

    @Test
    @DisplayName("Edge case: Already sorted ascending")
    void testAlreadySortedAscending() {
        for (int i = 1; i <= 10; i++) {
            heap.insert(i);
        }

        for (int i = 1; i <= 10; i++) {
            assertEquals(i, heap.extractMin());
        }
    }

    @Test
    @DisplayName("Edge case: Already sorted descending")
    void testAlreadySortedDescending() {
        for (int i = 10; i >= 1; i--) {
            heap.insert(i);
        }

        for (int i = 1; i <= 10; i++) {
            assertEquals(i, heap.extractMin());
        }
    }

    @Test
    @DisplayName("Edge case: Negative numbers")
    void testNegativeNumbers() {
        heap.insert(-5).insert(-10).insert(0).insert(-1);

        assertEquals(-10, heap.extractMin());
        assertEquals(-5, heap.extractMin());
        assertEquals(-1, heap.extractMin());
        assertEquals(0, heap.extractMin());
    }

    @Test
    @DisplayName("Edge case: Integer limits")
    void testIntegerLimits() {
        heap.insert(Integer.MAX_VALUE)
                .insert(Integer.MIN_VALUE)
                .insert(0);

        assertEquals(Integer.MIN_VALUE, heap.extractMin());
        assertEquals(0, heap.extractMin());
        assertEquals(Integer.MAX_VALUE, heap.extractMin());
    }

    @Test
    @DisplayName("Stress test: Multiple operations")
    void testStressMultipleOperations() {
        // Mix of operations
        heap.insert(5).insert(10).insert(3);
        assertEquals(3, heap.extractMin());

        heap.insert(1).insert(15);
        heap.decreaseKey(10, 2);

        assertEquals(1, heap.extractMin());
        assertEquals(2, heap.extractMin());
        assertEquals(5, heap.extractMin());
        assertEquals(15, heap.extractMin());
    }

    @Test
    @DisplayName("Edge case: Merge with different sizes")
    void testMergeDifferentSizes() {
        MinHeap<Integer> small = new MinHeap<>();
        small.insert(5);

        MinHeap<Integer> large = new MinHeap<>();
        for (int i = 10; i <= 20; i++) {
            large.insert(i);
        }

        small.merge(large);
        assertEquals(12, small.size());
        assertEquals(5, small.peekMin());
    }

    @Test
    @DisplayName("Performance: Track metrics for insert operations")
    void testInsertMetrics() {
        tracker.reset();
        tracker.startTimer();

        for (int i = 100; i > 0; i--) {
            heap.insert(i);
        }

        tracker.stopTimer();
        tracker.printReport("Insert 100 elements", 100);

        assertTrue(tracker.comparisons > 0);
        assertTrue(tracker.swaps > 0);
        assertTrue(tracker.getElapsedTimeMillis() >= 0);
    }

    @Test
    @DisplayName("Performance: Export metrics to CSV")
    void testCSVExport() {
        tracker.reset();
        tracker.startTimer();

        for (int i = 50; i > 0; i--) {
            heap.insert(i);
        }

        tracker.stopTimer();
        tracker.exportToCSV("target/test-metrics.csv", "insert", 50, false);

        // Verify file exists
        assertTrue(new java.io.File("target/test-metrics.csv").exists());
    }
}