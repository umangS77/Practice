package Concurrency.HitCounter;


/*
add waitUntilHitsAtLeast(N) => Thread blocks until hit count reaches threshold
 */

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Enhancement3 {

    private int WINDOW = 300; // 5 minutes window
    private int times[] = new int[WINDOW];
    private int counts[] = new int[WINDOW];

    private int totalHits = 0;

    private Lock lock = new ReentrantLock();
    private Condition thresholdReached = lock.newCondition();

    public void hit(int timestamp) {
        lock.lock();
        try {
            int idx = timestamp % WINDOW;
            if (times[idx] != timestamp) {
                times[idx] = timestamp; // bucket is stale => reset
                counts[idx] = 1;
            } else {
                counts[idx]++;
            }
            totalHits++;
            thresholdReached.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void waitForHits(int n) throws InterruptedException {
        lock.lock();
        try {
            while(totalHits < n) {
                thresholdReached.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public int getHits(int timestamp) {
        lock.lock();
        try {
            int total = 0;
            for (int i = 0; i < WINDOW; i++) {
                if (timestamp - times[i] < WINDOW) {
                    total += counts[i];
                }
            }
            return total;
        } finally {
            lock.unlock();
        }
    }


}
