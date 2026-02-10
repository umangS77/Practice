package Concurrency.ThreadSafeBoundedBlockingQueue;

import java.util.concurrent.TimeUnit;

/*

Enhancement = Timeout Versions
Timeout implemented using awaitNanos and remaining-time tracking loop.

 */

public class BoundedBlockingQueue2<T> extends BoundedBlockingQueue1<T> {

    public BoundedBlockingQueue2(int capacity) {
        super(capacity);
    }

    public void put(T item, long timeoutInMs) throws Exception {

        long nanos = TimeUnit.MILLISECONDS.toNanos(timeoutInMs);
        lock.lock();
        try {
            while (queue.size() == capacity) {
                if (nanos <= 0) {
                    return;
                }
                nanos = notFull.awaitNanos(nanos);
            }
            queue.addLast(item);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T get(Long timeoutInMs) throws Exception {
        long nanos = TimeUnit.MILLISECONDS.toNanos(timeoutInMs);

        lock.lock();
        try {
            while (queue.isEmpty()) {
                if (nanos <= 0) return null;

                nanos = notEmpty.awaitNanos(nanos);
            }
            T item = queue.removeFirst();
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }
}
