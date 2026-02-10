package Concurrency.ResourcePool;

/*

Enhancement :
Max Size + Blocking Acquire

limit total resources
block when none free

Threads block when pool is exhausted and wake when a resource is released.

 */

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class ResourcePool2 <T extends Resource> {
    private Deque<T> free = new ArrayDeque<>();
    private ReentrantLock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();

    private Supplier<T> factory;
    private int maxSize;
    private int created = 0;

    public ResourcePool2(Supplier<T> factory, int maxSize) {
        this.factory = factory;
        this.maxSize = maxSize;
    }

    public T acquire() throws Exception {
        lock.lock();
        try {
            while(free.isEmpty() && created == maxSize) {
                notEmpty.await();
            }

            if (!free.isEmpty()) {
                return free.removeFirst();
            }

            created++;
            return factory.get();
        } finally {
            lock.unlock();
        }
    }

    public void release(T r) {
        lock.lock();
        try {
            r.reset();
            free.addLast(r);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

}
