package com.lucene;

import org.apache.lucene.util.ArrayUtil;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;


public abstract class MyPriorityQueue<T> implements Iterable<T> {
    private int size = 0;
    private final T[] heap;

    public MyPriorityQueue(int maxSize, boolean prepopulate) {
        final int heapSize;
        if (0 == maxSize) {
            heapSize = 2;
        } else {
            heapSize = maxSize + 1;

            if (heapSize > ArrayUtil.MAX_ARRAY_LENGTH) {
                throw new IllegalArgumentException("maxSize must be <= " + (ArrayUtil.MAX_ARRAY_LENGTH - 1) + "; got: " + maxSize);
            }
        }
        @SuppressWarnings("unchecked") final T[] h = (T[]) new Object[heapSize];
        this.heap = h;

        if (prepopulate) {
            T sentinel = getSentinelObject();
            if (sentinel != null) {
                heap[1] = sentinel;
                for (int i = 2; i < heap.length; i++) {
                    heap[i] = getSentinelObject();
                }
                size = maxSize;
            }
        }
    }

    protected abstract boolean lessThan(T a, T b);

    protected abstract boolean moreThan(T a, T b);

    protected T getSentinelObject() {
        return null;
    }

    public final T top() {
        return heap[1];
    }

    public final T popSmall() {
        System.out.println("****************small**************");
        Arrays.asList(heap).forEach(System.out::println);
        T result = heap[1];       // save first value
        heap[1] = heap[size];     // move last to first
        heap[size] = null;        // permit GC of objects
        size--;
        downHeap(1);              // adjust heap
        System.out.println("******************************");
        Arrays.asList(heap).forEach(System.out::println);
        System.out.println("****************small**************");
        return result;
    }


    public final T popBig() {
        System.out.println("****************big**************");
        Arrays.asList(heap).forEach(System.out::println);
        heap[1] = heap[size];     // move last to first
        heap[size] = null;        // permit GC of objects
        size--;
        upHeap(1);              // adjust heap
        T result = heap[1];       // save first value
        System.out.println("******************************");
        Arrays.asList(heap).forEach(System.out::println);
        System.out.println("****************big**************");
        return result;
    }

    public final T updateTop() {
        downHeap(1);
        return heap[1];
    }

    public final int size() {
        return size;
    }


    private final void upHeap(int i) {

        T node = heap[i];          // save top node
        int j = i << 1;            // find bigger child
        int k = j + 1;
        if (k <= size && moreThan(heap[k], heap[j])) {
            j = k;
        }
        while (j <= size && moreThan(heap[j], node)) {
            heap[i] = heap[j];       // shift up child
            i = j;
            j = i << 1;
            k = j + 1;
            if (k <= size && moreThan(heap[k], heap[j])) {
                j = k;
            }
        }
        heap[i] = node;            // install saved node
    }

    private final void downHeap(int i) {
        T node = heap[i];          // save top node
        int j = i << 1;            // find smaller child
        int k = j + 1;
        if (k <= size && lessThan(heap[k], heap[j])) {
            j = k;
        }
        while (j <= size && lessThan(heap[j], node)) {
            heap[i] = heap[j];       // shift up child
            i = j;
            j = i << 1;
            k = j + 1;
            if (k <= size && lessThan(heap[k], heap[j])) {
                j = k;
            }
        }
        heap[i] = node;            // install saved node
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            int i = 1;

            @Override
            public boolean hasNext() {
                return i <= size;
            }

            @Override
            public T next() {
                if (hasNext() == false) {
                    throw new NoSuchElementException();
                }
                return heap[i++];
            }

        };
    }
}

