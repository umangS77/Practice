package Concurrency.ReaderWriterKeyValueStore;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleRWLock {

    private ReentrantLock lock = new ReentrantLock();
    private Condition canRead = lock.newCondition();
    private Condition canWrite = lock.newCondition();

    private int activeReaders = 0;
    private boolean activeWriter = false;
    private int waitingWriters = 0;

    public void readLock() throws InterruptedException {
        lock.lock();
        try {
            while (activeWriter || waitingWriters > 0) {
                canRead.await();
            }
            activeReaders++;
        } finally {
            lock.unlock();
        }
    }

    public void readUnlock() {
        lock.lock();
        try {
            activeReaders--;
            if (activeReaders == 0) {
                canWrite.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public void writeLock() throws Exception {
        lock.lock();
        try {
            waitingWriters++;
            while (activeReaders > 0 || activeWriter) {
                canWrite.await();
            }
            waitingWriters--;
            activeWriter = true;
        } finally {
            lock.unlock();
        }
    }

    public void writeUnlock() {
        lock.lock();
        try {
            activeWriter = false;
            // writers first if waiting
            if (waitingWriters > 0) {
                canWrite.signal();
            } else {
                canRead.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}
