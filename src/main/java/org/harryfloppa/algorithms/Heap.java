package org.harryfloppa.algorithms;

import org.harryfloppa.metrics.PerformanceTracker;

import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * Abstract base class for Heap implementation
 */
public abstract class Heap<T extends Comparable<T>> implements IHeap<T> {
    protected T[] heap;
    protected int position = -1;
    protected final HashMap<T, Integer> elementIndexMap;
    protected final PerformanceTracker metrics;

    @SuppressWarnings("unchecked")
    public Heap(PerformanceTracker metrics) {
        this.heap = (T[]) new Comparable[2];
        this.elementIndexMap = new HashMap<>();
        this.metrics = metrics != null ? metrics : new PerformanceTracker();
    }

    public Heap() {
        this(new PerformanceTracker());
    }


    @Override
    public boolean isEmpty() {
        return position == -1;
    }

    @Override
    public int size() {
        return position + 1;
    }

    protected boolean isFull() {
        return position == heap.length - 1;
    }

    @SuppressWarnings("unchecked")
    protected void resize(int capacity) {
        metrics.allocations++;
        T[] newHeap = (T[]) new Comparable[capacity];
        System.arraycopy(heap, 0, newHeap, 0, position + 1);
        heap = newHeap;
        metrics.arrayAccesses += (position + 1);
    }
    protected void swap(int i, int j) {
        if (i == j) return;

        metrics.swaps++;
        metrics.arrayAccesses += 2;

        T temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;

        metrics.arrayAccesses += 2;

        elementIndexMap.put(heap[i], i);
        elementIndexMap.put(heap[j], j);
    }
    protected abstract void fixUpward(int index);

    protected abstract void fixDownward(int index, int endIndex);
    protected abstract boolean shouldSwap(T child, T parent);


    @Override
    public IHeap<T> insert(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot insert null element");
        }

        if (isFull()) {
            resize(2 * heap.length);
        }

        metrics.allocations++;
        heap[++position] = element;
        metrics.arrayAccesses++;

        elementIndexMap.put(element, position);
        fixUpward(position);

        return this;
    }


    @Override
    public T extractRoot() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }

        metrics.arrayAccesses++;
        T result = heap[0];
        elementIndexMap.remove(result);

        if (position == 0) {
            heap[0] = null;
            position = -1;
            metrics.arrayAccesses++;
            return result;
        }

        heap[0] = heap[position];
        heap[position] = null;
        position--;
        metrics.arrayAccesses += 3;

        if (position >= 0) {
            elementIndexMap.put(heap[0], 0);
            fixDownward(0, position);
        }

        return result;
    }

    @Override
    public T peekRoot() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        metrics.arrayAccesses++;
        return heap[0];
    }

    @Override
    public T getRoot() {
        return extractRoot();
    }

    @Override
    public void decreaseKey(T oldValue, T newValue) {
        if (oldValue == null || newValue == null) {
            throw new IllegalArgumentException("Values cannot be null");
        }

        Integer index = elementIndexMap.get(oldValue);
        if (index == null) {
            throw new IllegalArgumentException("Element not found in heap");
        }

        metrics.comparisons++;
        if (!isValidDecreaseKey(oldValue, newValue)) {
            throw new IllegalArgumentException("Invalid decrease key operation");
        }

        elementIndexMap.remove(oldValue);
        heap[index] = newValue;
        elementIndexMap.put(newValue, index);
        metrics.arrayAccesses++;

        fixUpward(index);
    }

    protected abstract boolean isValidDecreaseKey(T oldValue, T newValue);

    @Override
    public void merge(IHeap<T> other) {
        if (other == null || other.isEmpty()) {
            return;
        }

        if (!(other instanceof Heap)) {
            throw new IllegalArgumentException("Can only merge with same heap type");
        }

        Heap<T> otherHeap = (Heap<T>) other;
        int newSize = this.size() + other.size();

        // Resize if needed
        while (newSize > heap.length) {
            resize(2 * heap.length);
        }

        for (int i = 0; i <= otherHeap.position; i++) {
            metrics.arrayAccesses++;
            T element = otherHeap.heap[i];
            heap[++position] = element;
            elementIndexMap.put(element, position);
            metrics.arrayAccesses++;
        }

        // Floyd's buildHeap: O(n) instead of O(n log n)
        buildHeap();
    }

    protected void buildHeap() {
        for (int i = (position - 1) / 2; i >= 0; i--) {
            fixDownward(i, position);
        }
    }


    @Override
    public void sort() {
        int originalPosition = position;

        for (int i = 0; i <= originalPosition; i++) {
            swap(0, position - i);
            fixDownward(0, position - i - 1);
        }

        System.out.println("Sorted array:");
        for (int i = 0; i <= originalPosition; i++) {
            System.out.println(heap[i]);
        }
    }

    // utils

    public PerformanceTracker getMetrics() {
        return metrics;
    }

    public void resetMetrics() {
        metrics.reset();
    }

    @Override
    public String toString() {
        if (isEmpty()) return getClass().getSimpleName() + "[]";
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append("[");
        for (int i = 0; i <= position; i++) {
            sb.append(heap[i]);
            if (i < position) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}