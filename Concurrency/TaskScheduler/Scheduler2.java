package Concurrency.TaskScheduler;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
+ Return Value (Future-like)
 */

class SimpleFuture<T> {

    private T value;
    private boolean done = false;

    private ReentrantLock lock = new ReentrantLock();
    private Condition finished = lock.newCondition();

    public void set(T v) {
        lock.lock();
        try {
            value = v;
            done = true;
            finished.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T get() throws InterruptedException {
        lock.lock();
        try {
            while(!done) {
                finished.await();
            }
            return value;
        } finally {
            lock.unlock();
        }
    }
}

public class Scheduler2<T> {

    private final int maxParallel;
    private int running;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition isAvailable = lock.newCondition();

    public Scheduler2(int maxParallel) {
        this.maxParallel = maxParallel;
    }

    public <T> SimpleFuture <T> submit(java.util.concurrent.Callable<T> task) {
        SimpleFuture<T> future = new SimpleFuture<>();

        Thread t = new Thread(() -> {
            acquireSlot();
            try {
                future.set(task.call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                releaseSlot();
            }
        });

        t.start();

        return future;
    }

    private void acquireSlot() {
        lock.lock();
        try {
            while(running == maxParallel) {
                isAvailable.awaitUninterruptibly();
            }
            running++;
        } finally {
            lock.unlock();
        }
    }

    private void releaseSlot() {
        lock.lock();
        try {
            running--;
            isAvailable.signal();
        } finally {
            lock.unlock();
        }
    }

}
