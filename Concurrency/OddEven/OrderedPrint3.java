package Concurrency.OddEven;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*

Enhancement — Pause / Resume

external pause/resume control
Separate condition for pause state avoids mixing predicates.

 */
public class OrderedPrint3 {

    private int MAX;
    private int maxThreads;
    private int num = 1;

    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public OrderedPrint3(int MAX, int maxThreads) {
        this.MAX = MAX;
        this.maxThreads = maxThreads;
    }

    public void worker(int id) {
        while (true) {
            lock.lock();
            try {

                while (paused)
                    pauseCond.awaitUninterruptibly();

                while (num <= MAX && (num-1) % maxThreads == id) {
                    condition.awaitUninterruptibly();
                }

                if (num > MAX) return;

                System.out.println("ThreadId : " + id + " --- num : " + num);
                num++;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    private boolean paused = false;
    private final Condition pauseCond = lock.newCondition();

    public void pause() {
        lock.lock();
        try { paused = true; }
        finally { lock.unlock(); }
    }

    public void resume() {
        lock.lock();
        try {
            paused = false;
            pauseCond.signalAll();
        } finally { lock.unlock(); }
    }

}
