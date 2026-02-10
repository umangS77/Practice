package Concurrency.RateLimiter.TokenBucketRateLimiter;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/*

VIP requests go first. =>

vipQueue
normalQueue

 */

public class Enhancement3 {

    private int capacity;
    private double refillPerMs;

    private double tokens;
    private long lastRefill;

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private final Queue<Thread> vipQueue = new ArrayDeque<>();
    private final Queue<Thread> normalQueue = new ArrayDeque<>();


    public Enhancement3(int perSecond, int capacity) {
        this.refillPerMs = perSecond/1000.0;
        this.capacity = capacity;
        this.tokens = capacity;
        this.lastRefill = System.currentTimeMillis();
    }

    public void acquire(boolean vip) throws InterruptedException {
        lock.lock();
        try {
            Thread me = Thread.currentThread();
            if(vip) {
                vipQueue.add(me);
            } else {
                normalQueue.add(me);
            }

            while(true) {
                refill();

                Queue<Thread> currentQueue = !vipQueue.isEmpty() ? vipQueue : normalQueue;

                boolean isHead = currentQueue.peek() == me;
                if (isHead && tokens >= 1) {
                    tokens--;
                    currentQueue.poll();
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
