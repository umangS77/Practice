package Concurrency.TaskScheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/*
+ Cache Support

 */
public class Scheduler6 extends Scheduler5 {

    private ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    public Scheduler6(int maxParallel) {
        super(maxParallel);
    }

    public <T> SimpleFuture<T> submitCached(String key, Callable<T> task) {
        if (cache.containsKey(key)) {
            SimpleFuture<T> cachedFuture = new SimpleFuture<>();
            cachedFuture.set((T) cache.get(key));
            return cachedFuture;
        }

        SimpleFuture<T> future = new SimpleFuture<>();
        Runnable runnable = () -> {
            try {
                T result = task.call();
                cache.put(key, result);
                future.set(result);
            } catch (Exception e) {
                //
            }
        };
        return future;
    }
}
