package Concurrency.ThreadSafeBoundedBlockingQueue;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

/*

 all combined


 */

public class BoundedBlockingQueue5<T> {

    private final int capacity;
    private final Deque<T> queue = new ArrayDeque<>();

    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    private int waitingProducers = 0;
    private int waitingConsumers = 0;
    private long rejectedOffers = 0;
    private long timeoutPolls = 0;

    public BoundedBlockingQueue5(int capacity) {
        this.capacity = capacity;
    }

    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                waitingProducers++;
                try { notFull.await(); }
                finally { waitingProducers--; }
            }
            queue.addLast(item);
            notEmpty.signal();
        } finally { lock.unlock(); }
    }

    public boolean offer(T item, long timeoutMs) throws InterruptedException {
        long nanos = TimeUnit.MILLISECONDS.toNanos(timeoutMs);

        lock.lock();
        try {
            while (queue.size() == capacity) {
                if (nanos <= 0) {
                    rejectedOffers++;
                    return false;
                }
                waitingProducers++;
                try { nanos = notFull.awaitNanos(nanos); }
                finally { waitingProducers--; }
            }
            queue.addLast(item);
            notEmpty.signal();
            return true;
        } finally { lock.unlock(); }
    }

    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                waitingConsumers++;
                try { notEmpty.await(); }
                finally { waitingConsumers--; }
            }
            T x = queue.removeFirst();
            notFull.signal();
            return x;
        } finally { lock.unlock(); }
    }

    public T poll(long timeoutMs) throws InterruptedException {
        long nanos = TimeUnit.MILLISECONDS.toNanos(timeoutMs);

        lock.lock();
        try {
            while (queue.isEmpty()) {
                if (nanos <= 0) {
                    timeoutPolls++;
                    return null;
                }
                waitingConsumers++;
                try { nanos = notEmpty.awaitNanos(nanos); }
                finally { waitingConsumers--; }
            }
            T x = queue.removeFirst();
            notFull.signal();
            return x;
        } finally { lock.unlock(); }
    }

    // -------- metrics --------

    public int size() {
        lock.lock();
        try { return queue.size(); }
        finally { lock.unlock(); }
    }

    public int getWaitingProducers() { return waitingProducers; }
    public int getWaitingConsumers() { return waitingConsumers; }
    public long getRejectedOffers() { return rejectedOffers; }
    public long getTimeoutPolls() { return timeoutPolls; }
}
