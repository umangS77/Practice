package Concurrency.TaskScheduler;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*

Enhancement 3 — + Task Delay Support

 */
public class Scheduler3 {
    private int maxParallel;
    private int running;

    private ReentrantLock lock = new ReentrantLock();
    private Condition isAvailable = lock.newCondition();

    public Scheduler3(int maxParallel) {
        this.maxParallel = maxParallel;
    }

    public void submitDelayed(Runnable task, long delayInMs) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(delayInMs);
            } catch (InterruptedException e) {
                //
            }
            submit(task);
        });
        t.start();
    }

    private void submit(Runnable task) {
        try {
            acquireSlot(task);
            task.run();
        } finally {
            releaseSlot();
        }
    }

    private void acquireSlot(Runnable task) {
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
