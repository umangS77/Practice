package Concurrency.ReaderWriterKeyValueStore;

import java.util.HashMap;
import java.util.Map;

/*

Enhancement : Reader vs Writer Priority Modes

What if readers starve writers?
-> We implement custom priority based RW lock with mode switch.

reader-priority mode
writer-priority mode


 */
public class KVStore3<K, V> {

    private Map<K, V> map = new HashMap<>();
    private PriorityRWLock lock;

    public KVStore3(boolean writerPriority) {
        this.lock = new PriorityRWLock(writerPriority);
    }

    public void put(K k, V v) throws Exception {
        lock.writeLock();
        try {
            map.put(k, v);
        } finally {
            lock.writeUnlock();
        }
    }

    public V get(K k) throws Exception {
        lock.readLock();
        try {
            return map.get(k);
        } finally {
            lock.readUnlock();
        }
    }

}
