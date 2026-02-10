package Concurrency.ReaderWriterKeyValueStore;

import java.util.*;
import java.util.concurrent.locks.*;

/*

Enhancement :
entries expire automatically
background cleaner removes them


 */

class TTLValue<V> {
    final V value;
    final long expireAt;

    TTLValue(V v, long exp) {
        value = v;
        expireAt = exp;
    }
}

public class KVStore5<K,V> {

    private final Map<K,TTLValue<V>> map = new HashMap<>();
    private final ReentrantReadWriteLock rw = new ReentrantReadWriteLock();

    public void put(K k, V v, long ttlMs) {
        rw.writeLock().lock();
        try {
            map.put(k, new TTLValue<>(v,
                    System.currentTimeMillis() + ttlMs));
        } finally { rw.writeLock().unlock(); }
    }

    public V get(K k) {
        rw.readLock().lock();
        try {
            TTLValue<V> tv = map.get(k);
            if (tv == null) return null;
            if (tv.expireAt < System.currentTimeMillis())
                return null;
            return tv.value;
        } finally { rw.readLock().unlock(); }
    }

    public void startCleaner(long intervalMs) {
        Thread t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(intervalMs);
                    cleanup();
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        rw.writeLock().lock();
        try {
            map.entrySet().removeIf(e -> e.getValue().expireAt < now);
        } finally { rw.writeLock().unlock(); }
    }
}

