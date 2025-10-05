package org.harryfloppa.algorithms;

import org.harryfloppa.metrics.PerformanceTracker;

/**
 * Min-Heap concrete implementation
 * <p>
 * HEAP PROPERTY: For every node i (except root):
 *   heap[i] >= heap[(i-1)/2]  (child >= parent)
 * <p>
 * ROOT: Always the MINIMUM element
 * - Parent of i: (i-1)/2
 * - Left child of i: 2*i + 1
 * - Right child of i: 2*i + 2
 * nothing to optimization
 */
public class MinHeap<T extends Comparable<T>> extends Heap<T> {

    public MinHeap(PerformanceTracker metrics) {
        super(metrics);
    }

    public MinHeap() {
        super();
    }

    @Override
    protected void fixUpward(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;

            metrics.arrayAccesses += 2;
            metrics.comparisons++;

            if (heap[index].compareTo(heap[parentIndex]) < 0) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    @Override
    protected void fixDownward(int index, int endIndex) {
        if (endIndex == -1) return;

        while (index <= endIndex) {
            int leftChildIndex = index * 2 + 1;
            int rightChildIndex = index * 2 + 2;

            if (leftChildIndex > endIndex) break;

            int smallestIndex = leftChildIndex;

            if (rightChildIndex <= endIndex) {
                metrics.arrayAccesses += 2;
                metrics.comparisons++;

                if (heap[rightChildIndex].compareTo(heap[leftChildIndex]) < 0) {
                    smallestIndex = rightChildIndex;
                }
            }

            metrics.arrayAccesses += 2;
            metrics.comparisons++;

            if (heap[index].compareTo(heap[smallestIndex]) > 0) {
                swap(index, smallestIndex);
                index = smallestIndex; // Move down
            } else {
                break;
            }
        }
    }

    @Override
    protected boolean shouldSwap(T child, T parent) {
        metrics.comparisons++;
        return child.compareTo(parent) < 0;
    }

    @Override
    protected boolean isValidDecreaseKey(T oldValue, T newValue) {
        return newValue.compareTo(oldValue) <= 0;
    }

    public T peekMin() {
        return peekRoot();
    }

    public T extractMin() {
        return extractRoot();
    }

    public void printHeapStructure() {
        if (isEmpty()) {
            System.out.println("Empty heap");
            return;
        }

        int level = 0;
        int nodesInLevel = 1;
        int index = 0;

        System.out.println("\nHeap Structure:");
        while (index <= position) {
            System.out.print("Level " + level + ": [");

            for (int i = 0; i < nodesInLevel && index <= position; i++) {
                System.out.print(heap[index]);
                if (i < nodesInLevel - 1 && index < position) {
                    System.out.print(", ");
                }
                index++;
            }

            System.out.println("]");
            level++;
            nodesInLevel *= 2;
        }
        System.out.println();
    }
}