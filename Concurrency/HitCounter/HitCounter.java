package Concurrency.HitCounter;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HitCounter {

    private int WINDOW = 300; // 5 minutes window
    private int times[] = new int[WINDOW];
    private int counts[] = new int[WINDOW];

    private Lock lock = new ReentrantLock();

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
        } finally {
            lock.unlock();
        }
    }

    public int getHits(int timestamp) {
        lock.lock();
        try {
            int total=0;
            for (int i=0; i<WINDOW; i++) {
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
