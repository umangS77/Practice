package Concurrency.RateLimiter.TokenBucketRateLimiter;


/*

Limiter per user / key =>

ConcurrentHashMap + computeIfAbsent

 */

import java.util.concurrent.ConcurrentHashMap;

public class Enhancement4 {

    private int capacity;
    private int perSecond;

    private ConcurrentHashMap<String, Enhancement3> map = new ConcurrentHashMap<>();

    public Enhancement4(int perSecond, int capacity) {
        this.capacity = capacity;
        this.perSecond = perSecond;
    }

    public void acquire(String key, boolean vip) throws InterruptedException {
        Enhancement3 limiter = map.computeIfAbsent(key, k -> new Enhancement3(perSecond, capacity));
        limiter.acquire(vip);
    }
}
