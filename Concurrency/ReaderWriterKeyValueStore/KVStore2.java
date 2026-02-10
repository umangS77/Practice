package Concurrency.ReaderWriterKeyValueStore;

import java.util.HashMap;
import java.util.Map;

/*

enhancements : Multiple Readers, Single Writer
Allow concurrent reads but exclusive writes.

using custom SimpleRWLock here instead of ReentrantReadWriteLock

 */

public class KVStore2<K,V> {
    private Map<K,V> map = new HashMap<>();

//    private final ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
    private SimpleRWLock lock = new SimpleRWLock();

    public void put(K k, V v) throws Exception {
        lock.writeLock();
//        rw.writeLock().lock();
        try {
            map.put(k,v);
        } finally {
            lock.writeUnlock();
            //        rw.writeLock().unlock();

        }
    }

    public V get(K k) throws Exception {
        lock.readLock();
        //        rw.readLock().lock();

        try {
            return map.get(k);
        } finally {
            lock.readUnlock();
            //        rw.readLock().unlock();

        }
    }

}
