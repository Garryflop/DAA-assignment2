package org.harryfloppa.benchmarks;

import org.harryfloppa.algorithms.MinHeap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * JMH Benchmark for MinHeap operations
 * Run with: mvn clean install
 *           java -jar target/benchmarks.jar
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@Fork(value = 1, warmups = 1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class MinHeapJMHBenchmark {

    @Param({"100", "1000", "10000"})
    private int size;

    private Integer[] data;
    private MinHeap<Integer> prepopulatedHeap;

    @Setup(Level.Trial)
    public void setup() {
        Random rand = new Random(42);
        data = new Integer[size];
        for (int i = 0; i < size; i++) {
            data[i] = rand.nextInt(size * 10);
        }

        // Prepopulate heap for extract/decreaseKey tests
        prepopulatedHeap = new MinHeap<>();
        for (Integer value : data) {
            prepopulatedHeap.insert(value);
        }
    }

    @Benchmark
    public void benchmarkInsert(Blackhole blackhole) {
        MinHeap<Integer> heap = new MinHeap<>();
        for (Integer value : data) {
            heap.insert(value);
        }
        blackhole.consume(heap);
    }

    @Benchmark
    public void benchmarkExtractMin(Blackhole blackhole) {
        MinHeap<Integer> heap = new MinHeap<>();
        for (Integer value : data) {
            heap.insert(value);
        }

        for (int i = 0; i < size; i++) {
            blackhole.consume(heap.extractMin());
        }
    }

    @Benchmark
    public void benchmarkPeekMin(Blackhole blackhole) {
        blackhole.consume(prepopulatedHeap.peekMin());
    }

    @Benchmark
    public void benchmarkMerge(Blackhole blackhole) {
        MinHeap<Integer> heap1 = new MinHeap<>();
        MinHeap<Integer> heap2 = new MinHeap<>();

        int half = size / 2;
        for (int i = 0; i < half; i++) {
            heap1.insert(data[i]);
            heap2.insert(data[i + half]);
        }

        heap1.merge(heap2);
        blackhole.consume(heap1);
    }

    @Benchmark
    public void benchmarkDecreaseKey(Blackhole blackhole) {
        MinHeap<Integer> heap = new MinHeap<>();

        // Insert sequential values for reliable decreaseKey
        for (int i = 0; i < size; i++) {
            heap.insert(i * 10);
        }

        // Decrease every 10th key
        for (int i = 0; i < size; i += 10) {
            try {
                heap.decreaseKey(i * 10, i * 10 - 5);
            } catch (IllegalArgumentException e) {
                // Ignore if key already decreased
            }
        }

        blackhole.consume(heap);
    }

    @Benchmark
    public void benchmarkInsertExtractMixed(Blackhole blackhole) {
        MinHeap<Integer> heap = new MinHeap<>();

        // Mixed insert/extract pattern
        for (int i = 0; i < size; i++) {
            heap.insert(data[i]);

            if (i % 5 == 0 && !heap.isEmpty()) {
                blackhole.consume(heap.extractMin());
            }
        }

        blackhole.consume(heap);
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}