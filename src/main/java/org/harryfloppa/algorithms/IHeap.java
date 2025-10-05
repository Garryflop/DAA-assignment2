package org.harryfloppa.algorithms;


public interface IHeap<T extends Comparable<T>> {
    IHeap<T> insert(T element);
    T getRoot();
    T extractRoot();
    T peekRoot();
    boolean isEmpty();
    int size();
    void decreaseKey(T oldValue, T newValue);
    void merge(IHeap<T> other);
    void sort();
}