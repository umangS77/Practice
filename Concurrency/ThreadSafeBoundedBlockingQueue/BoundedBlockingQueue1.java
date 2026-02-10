package Concurrency.ThreadSafeBoundedBlockingQueue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBlockingQueue1<T> {

    protected int capacity;
    protected Deque<T> queue = new ArrayDeque<>();
    protected ReentrantLock lock = new ReentrantLock();
    protected Condition notFull = lock.newCondition();
    protected Condition notEmpty = lock.newCondition();

    public BoundedBlockingQueue1(int capacity) {
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
            while(queue.isEmpty()) {
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
