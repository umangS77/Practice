package Concurrency.RateLimiter;

public class FixedWindowRateLimiter {
    private int limit;
    private long windowInMs;

    private int count = 0;
    private long windowStart = System.currentTimeMillis();

    public FixedWindowRateLimiter(long windowInMs, int limit) {
        this.windowInMs = windowInMs;
        this.limit = limit;
    }

    public synchronized boolean allow() {
        long now = System.currentTimeMillis();

        if (now - windowStart >= windowInMs) {
            windowStart = now;
            count = 0;
        }

        if (count < limit) {
            count++;
            return true;
        }
        return false;
    }
}
