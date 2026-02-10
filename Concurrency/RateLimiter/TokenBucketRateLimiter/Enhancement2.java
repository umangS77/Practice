package Concurrency.RateLimiter.TokenBucketRateLimiter;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*

Guarantee arrival order. =>

Queue<Thread>
Only head thread can consume token

 */

public class Enhancement2 {

    private int capacity;
    private double refillPerMs;

    private double tokens;
    private long lastRefill;

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private final Queue<Thread> queue = new ArrayDeque<>();

    public Enhancement2(int perSecond, int capacity) {
        this.refillPerMs = perSecond/1000.0;
        this.capacity = capacity;
        this.tokens = capacity;
        this.lastRefill = System.currentTimeMillis();
    }

    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            Thread me = Thread.currentThread();
            queue.add(me);

            while(true) {
                refill();

                boolean isHead = queue.peek() == me;
                if (isHead && tokens >= 1) {
                    tokens--;
                    queue.poll();
                    condition.signalAll();
                    return;
                }
                condition.await();
            }
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
