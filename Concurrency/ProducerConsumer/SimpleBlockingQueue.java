package Concurrency.ProducerConsumer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleBlockingQueue<T> {
    private int capacity;
    private Deque<T> q = new ArrayDeque<>();

    private ReentrantLock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();
    private Condition notFull = lock.newCondition();

    public SimpleBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void put(T task) throws Exception {
        lock.lock();
        try {
            while(q.size() == capacity) {
                notFull.await();
            }
            q.addLast(task);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T take() throws Exception {
        lock.lock();
        try {
            while (q.isEmpty()) {
                notEmpty.await();
            }
            T task = q.removeFirst();
            notFull.signal();
            return task;
        } finally {
            lock.unlock();
        }
    }

}
