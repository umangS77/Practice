package Concurrency.RateLimiter;

import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingWindowRateLimiter {

    private int limit;
    private int windowInMs;

    private Deque<Long> times = new ArrayDeque<>();

    public SlidingWindowRateLimiter(int limit, int windowInMs) {
        this.limit = limit;
        this.windowInMs = windowInMs;
    }

    public synchronized boolean allow() {
        long now = System.currentTimeMillis();
        while(!times.isEmpty() && now - times.peekFirst() > windowInMs) {
            times.pollFirst();
        }
        if (times.size() < limit) {
            times.addLast(now);
            return true;
        }
        return false;
    }

}
