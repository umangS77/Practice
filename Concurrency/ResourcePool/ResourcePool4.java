package Concurrency.ResourcePool;

/*


Enhancement : Idle Eviction Thread
=> remove unused resources after idle time

 */


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

class Pooled<T extends Resource> {
    final T res;
    long lastUsed;

    Pooled(T r) {
        res = r;
        lastUsed = System.currentTimeMillis();
    }

}

public class ResourcePool4 <T extends Resource> {

    private Deque<Pooled<T>> free = new ArrayDeque<>();
    private ReentrantLock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();

    private Supplier<T> factory;
    private int maxSize;
    private int created = 0;

    public ResourcePool4(Supplier<T> factory, int maxSize) {
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
                Pooled<T> r = free.removeFirst();
                return r.res;
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
            free.addLast(new Pooled<>(r));
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    // Eviction Thread :

    private void evictIdle(long idleMs) {
        long now = System.currentTimeMillis();

        lock.lock();
        try {
            Pooled<T> p = free.peek();
            if (now - p.lastUsed > idleMs) {
                created--;
                free.removeFirst();
            }
        } finally {
            lock.unlock();
        }
    }

    public void startEviction(long idleMs, long intervalMs) {
        Thread t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(intervalMs);
                    evictIdle(idleMs);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        t.setDaemon(true); // Background daemon thread evicts idle resources to control memory and connection usage.
        t.start();
    }


}
