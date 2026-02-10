package Concurrency.ReaderWriterKeyValueStore;

/*

one lock
serialize all reads + writes
simple but safe

I start with a single mutex to ensure atomic map operations.
This is correct but limits read concurrency.

 */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class KVStore1<K,V> {
    private Map<K,V> map = new HashMap<>();
    private ReentrantLock lock = new ReentrantLock();

    public void put(K key, V value) {
        lock.lock();
        try {
            map.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    public V get(K key) {
        lock.lock();
        try {
            return map.computeIfAbsent(key, null);
        } finally {
            lock.unlock();
        }
    }

    public void delete(K key) {
        lock.lock();
        try {
            map.remove(key);
        } finally {
            lock.unlock();
        }
    }

}
