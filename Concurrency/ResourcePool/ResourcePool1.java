package Concurrency.ResourcePool;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*

reuse objects
thread-safe
non-blocking

 */
public class ResourcePool1<T extends Resource> {
    private Deque<T> free = new ArrayDeque<>();
    private ReentrantLock lock = new ReentrantLock();

    public ResourcePool1(Deque<T> free) {
        this.free = free;
    }

    public T acquire() {
        lock.lock();
        try {
            return free.pollFirst();
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
