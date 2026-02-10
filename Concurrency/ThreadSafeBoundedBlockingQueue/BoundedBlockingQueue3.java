package Concurrency.ThreadSafeBoundedBlockingQueue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
Enhancement = Fairness => ReentrantLock(true)
 */

public class BoundedBlockingQueue3<T> {

    private int capacity;
    private Deque<T> queue = new ArrayDeque<>();

    private ReentrantLock lock = new ReentrantLock(true);
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    public BoundedBlockingQueue3(int capacity) {
        this.capacity = capacity;
    }

    public void put(T item) throws Exception {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();
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
                notEmpty.await();
            }
            T item = queue.removeFirst();
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }

}
