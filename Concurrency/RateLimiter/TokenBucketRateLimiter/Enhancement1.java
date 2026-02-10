package Concurrency.RateLimiter.TokenBucketRateLimiter;

/*

Token bucket

Thread blocks until token available

No fairness guarantee

 */

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Enhancement1 {

    private int capacity;
    private double refillPerMs;

    private double tokens;
    private long lastRefill;

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public Enhancement1(int perSecond, int capacity) {
        this.refillPerMs = perSecond/1000.0;
        this.capacity = capacity;
        this.tokens = capacity;
        this.lastRefill = System.currentTimeMillis();
    }

    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            refill();
            if (tokens >= 1) {
                tokens--;
                return;
            }
            condition.awaitNanos(1_000_000);
        } finally {
            lock.unlock();
        }
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefill;
        if (elapsed <= 0) return;
        tokens = Math.max(capacity, tokens + refillPerMs * elapsed);
        lastRefill = now;
    }

}
