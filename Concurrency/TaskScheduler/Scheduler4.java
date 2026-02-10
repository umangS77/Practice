package Concurrency.TaskScheduler;

/*

Enhancement 4 — + Shutdown Support

 */

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Scheduler4 {

    private int maxParallel;
    private int running;

    private ReentrantLock lock = new ReentrantLock();
    private Condition isAvailable = lock.newCondition();

    private volatile boolean shutdown = false;

    public Scheduler4(int maxParallel) {
        this.maxParallel = maxParallel;
    }

    public void shutdown() {
        shutdown = true;
    }

    public void submit(Runnable task) {

        if (shutdown) {
            throw new IllegalStateException("Scheduler shutdown");
        }

        Thread t = new Thread( () -> {
            try {
                acquireSlot();
                task.run();
            } catch (InterruptedException e) {
                //
            } finally {
                releaseSlot();
            }
        });
        t.start();
    }

    private void acquireSlot() throws InterruptedException{
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

