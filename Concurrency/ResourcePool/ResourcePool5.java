package Concurrency.ResourcePool;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.function.Supplier;

/*

Enhancement :

 */

public class ResourcePool5<T extends Resource> {

    // ---------- wrapper ----------
    private static class Pooled<R> {
        final R resource;
        long lastUsed;

        Pooled(R r) {
            this.resource = r;
            this.lastUsed = System.currentTimeMillis();
        }
    }

    // ---------- config ----------
    private final int maxSize;
    private final Supplier<T> factory;

    // ---------- state ----------
    private int created = 0;
    private final Deque<Pooled<T>> free = new ArrayDeque<>();
    private final Map<T, Long> inUseSince = new HashMap<>();

    // ---------- sync ----------
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();

    // ---------- ctor ----------
    public ResourcePool5(int maxSize, Supplier<T> factory) {
        this.maxSize = maxSize;
        this.factory = factory;
    }

    // =====================================================
    // ACQUIRE (blocking)
    // =====================================================

    public T acquire() throws InterruptedException {
        lock.lock();
        try {
            while (free.isEmpty() && created == maxSize) {
                notEmpty.await();
            }

            T r;
            if (!free.isEmpty()) {
                r = free.removeFirst().resource;
            } else {
                created++;
                r = factory.get();
            }

            inUseSince.put(r, System.currentTimeMillis());
            return r;

        } finally {
            lock.unlock();
        }
    }

    // =====================================================
    // ACQUIRE (timeout)
    // =====================================================

    public T acquire(long timeoutMs) throws InterruptedException {

        long nanos = TimeUnit.MILLISECONDS.toNanos(timeoutMs);

        lock.lock();
        try {
            while (free.isEmpty() && created == maxSize) {
                if (nanos <= 0) return null;
                nanos = notEmpty.awaitNanos(nanos);
            }

            T r;
            if (!free.isEmpty()) {
                r = free.removeFirst().resource;
            } else {
                created++;
                r = factory.get();
            }

            inUseSince.put(r, System.currentTimeMillis());
            return r;

        } finally {
            lock.unlock();
        }
    }

    // =====================================================
    // RELEASE
    // =====================================================

    public void release(T r) {
        lock.lock();
        try {
            Long start = inUseSince.remove(r);
            if (start == null) {
                System.out.println("WARN: double release or foreign resource");
                return;
            }

            r.reset();
            free.addLast(new Pooled<>(r));
            notEmpty.signal();

        } finally {
            lock.unlock();
        }
    }

    // =====================================================
    // IDLE EVICTION
    // =====================================================

    public void startIdleEvictor(long idleMs, long intervalMs) {

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

        t.setDaemon(true);
        t.start();
    }

    private void evictIdle(long idleMs) {
        long now = System.currentTimeMillis();

        lock.lock();
        try {
            Iterator<Pooled<T>> it = free.iterator();
            while (it.hasNext()) {
                Pooled<T> p = it.next();
                if (now - p.lastUsed > idleMs) {
                    it.remove();
                    created--;
                    System.out.println("Evicted idle resource");
                }
            }
        } finally {
            lock.unlock();
        }
    }

    // =====================================================
    // LEAK DETECTION
    // =====================================================

    public void startLeakChecker(long maxUsageMs, long intervalMs) {

        Thread t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(intervalMs);
                    checkLeaks(maxUsageMs);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        t.setDaemon(true);
        t.start();
    }

    private void checkLeaks(long maxUsageMs) {
        long now = System.currentTimeMillis();

        lock.lock();
        try {
            for (var e : inUseSince.entrySet()) {
                long held = now - e.getValue();
                if (held > maxUsageMs) {
                    System.out.println("LEAK SUSPECTED: "
                            + e.getKey() + " held " + held + " ms");
                }
            }
        } finally {
            lock.unlock();
        }
    }

    // =====================================================
    // METRICS
    // =====================================================

    public int freeCount() {
        lock.lock();
        try { return free.size(); }
        finally { lock.unlock(); }
    }

    public int inUseCount() {
        lock.lock();
        try { return inUseSince.size(); }
        finally { lock.unlock(); }
    }

    public int totalCreated() {
        lock.lock();
        try { return created; }
        finally { lock.unlock(); }
    }
}

