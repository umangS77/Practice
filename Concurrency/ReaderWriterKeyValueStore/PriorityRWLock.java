package Concurrency.ReaderWriterKeyValueStore;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PriorityRWLock {

    private ReentrantLock lock = new ReentrantLock();
    private Condition canRead = lock.newCondition();
    private Condition canWrite = lock.newCondition();

    private int readers = 0;
    private int writersWaiting = 0;
    private boolean writing = false;

    private boolean writerPriority;

    public PriorityRWLock(boolean writerPriority) {
        this.writerPriority = writerPriority;
    }

    public void readLock() throws Exception {
        lock.lock();
        try {
            while (writing || (writerPriority && writersWaiting > 0)) {
                canRead.await();
            }
            readers++;
        } finally {
            lock.unlock();
        }
    }

    public void readUnlock() {
        lock.lock();
        try {
            readers--;
            if (readers == 0) {
                canWrite.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public void writeLock() throws Exception {
        lock.lock();
        try {
            writersWaiting++;
            if (writing || readers > 0) {
                canWrite.await();
            }
            writersWaiting--;
            writing = true;
        } finally {
            lock.unlock();
        }
    }

    public void writeUnlock() {
        lock.lock();
        try {
            writing = false;
            canWrite.signal();
            canRead.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
