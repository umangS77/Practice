package Concurrency.ResourcePool;

/*

Enhancement : Acquire With Timeout

acquire(timeout) - fail if wait too long

Timeout implemented using awaitNanos with remaining-time loop.

 */

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class ResourcePool3 <T extends Resource> {
    private Deque<T> free = new ArrayDeque<>();
    private ReentrantLock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();

    private Supplier<T> factory;
    private int maxSize;
    private int created = 0;

    public ResourcePool3(Supplier<T> factory, int maxSize) {
        this.factory = factory;
        this.maxSize = maxSize;
    }

    public T acquire(Long timeoutInMs) throws Exception {
        long nanos = TimeUnit.MILLISECONDS.toNanos(timeoutInMs);
        lock.lock();
        try {
            while (free.isEmpty() && created == maxSize) {
                if (nanos <= 0) {
                    return null;
                }
                nanos = notEmpty.awaitNanos(nanos);
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
        } finally {
            lock.unlock();
        }
    }
}
