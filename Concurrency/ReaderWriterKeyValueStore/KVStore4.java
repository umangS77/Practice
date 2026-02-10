package Concurrency.ReaderWriterKeyValueStore;

/*

Snapshot Reads (Versioned Data)

Readers get consistent snapshot view.

Snapshot taken under read lock gives consistent view without blocking other readers.

 */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Versioned<V> {
    final long version;
    final V value;

    Versioned(long v, V val) {
        version = v;
        value = val;
    }
}

public class KVStore4<K,V> {

    private Map<K, Versioned<V>> map = new HashMap<>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private long version = 0;

    public void put(K k, V v) {
        lock.writeLock().lock();
        try {
            map.put(k, new Versioned<>(++version, v));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Map<K,V> snapshot() {
        lock.readLock().lock();
        try {
            Map<K,V> snap = new HashMap<>();
            for (Map.Entry<K, Versioned<V>> e : map.entrySet()) {
                snap.put(e.getKey(), e.getValue().value);
            }
            return snap;
        } finally { lock.readLock().unlock(); }
    }


}
