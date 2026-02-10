package Concurrency.HitCounter;

/*
Enhancement 1 — Per-user hit counter

=> ConcurrentHashMap<userId, HitCounter>
 */

import java.util.concurrent.ConcurrentHashMap;

public class Enhancement1 {
    private ConcurrentHashMap<String, HitCounter> map = new ConcurrentHashMap<>();

    public void hit(String key, int timeStamp) {
        map.computeIfAbsent(key, k-> new HitCounter()).hit(timeStamp);
    }

    public int getHits(String key, int timeStamp) {
        HitCounter counter = map.get(key);
        return counter == null ? 0 : counter.getHits(timeStamp);
    }


}
