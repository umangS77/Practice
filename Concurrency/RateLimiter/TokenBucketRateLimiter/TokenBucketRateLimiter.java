package Concurrency.RateLimiter.TokenBucketRateLimiter;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TokenBucketRateLimiter {

    private int capacity;
    private double refillRatePerMs;

    private double tokens;
    private long lastRefillTime;

    private Lock lock = new ReentrantLock();

    public TokenBucketRateLimiter(int capacity, int tokensPerSecond) {
        this.capacity = capacity;
        this.refillRatePerMs = tokensPerSecond /1000.0;

        this.tokens = capacity;
        this.lastRefillTime = System.currentTimeMillis();
    }

    public boolean allow() {
        lock.lock();
        try {
            refillTokens();
            if (tokens >= 1) {
                tokens--;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void refillTokens() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;

        if (elapsed <= 0) return;

        double newTokens = elapsed/refillRatePerMs;
        tokens = Math.min(capacity, tokens + newTokens);
        lastRefillTime = now;
    }

}
