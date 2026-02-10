package Concurrency.HitCounter;

import java.util.concurrent.locks.ReentrantLock;

/*
Single lock per counter can still be hot under heavy concurrency => Split buckets across multiple locks
 */
public class Enhancement2 {

    private int WINDOW = 300;
    private int STRIPES = 8;

    private int[] times = new int[WINDOW];
    private int[] counts = new int[WINDOW];

    private ReentrantLock[] locks = new ReentrantLock[STRIPES];

    public Enhancement2() {
        for(int i=0;i<STRIPES;i++) {
            locks[i] = new ReentrantLock();
        }
    }

    public void hit(int timestamp) {
        int i = timestamp % WINDOW;
        ReentrantLock lock = locks[i % STRIPES];
        lock.lock();
        try {
            if(times[i] != timestamp) {
                times[i] = timestamp;
                counts[i] = 1;
            } else {
                counts[i]++;
            }
        } finally {
            lock.unlock();
        }
    }

    public int getHits(int timestamp) {
        int total = 0;

        for (int i=0;i<STRIPES;i++) {
            locks[i].lock();
        }
        try {
            for (int i=0; i<WINDOW; i++) {
                if (timestamp - times[i] < WINDOW) {
                    total += counts[i];
                }
            }
            return total;
        } finally {
            for (int i=STRIPES-1;i>=0;i--) {
                locks[i].unlock();
            }
        }
    }

}
