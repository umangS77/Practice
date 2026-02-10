package Concurrency.TaskScheduler;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Scheduler1 {

    private int maxParallel;
    private int running;

    private ReentrantLock lock = new ReentrantLock();
    private Condition isAvailable = lock.newCondition();

    public Scheduler1(int maxParallel) {
        this.maxParallel = maxParallel;
    }

    public void submit(Runnable task) {
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
