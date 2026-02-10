package Concurrency.ThreadSafeBoundedBlockingQueue;


/*
enhancement = metrics
current size
waiting producers
waiting consumers
timeout failures

 */

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBlockingQueue4<T> {
    private int capacity;
    private Deque<T> queue = new ArrayDeque<>();

    private ReentrantLock lock = new ReentrantLock(true);
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    private int waitingProducers = 0;
    private int waitingConsumers = 0;
    private long rejectedOffers = 0;
    private long timeoutPolls = 0;

    public BoundedBlockingQueue4(int capacity) {
        this.capacity = capacity;
    }

    public void put(T item) throws Exception {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                waitingProducers++;
                try {
                    notFull.await();
                } finally {
                    waitingProducers--;
                }
            }
            queue.addLast(item);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T get() throws Exception {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                waitingConsumers++;
                try {
                    notEmpty.await();
                } finally {
                    waitingConsumers--;
                }
            }
            T item = queue.removeFirst();
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }
}
