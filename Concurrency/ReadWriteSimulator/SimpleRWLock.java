package Concurrency.ReadWriteSimulator;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleRWLock {

    private ReentrantLock lock = new ReentrantLock();
    private Condition canRead = lock.newCondition();
    private Condition canWrite = lock.newCondition();


    private int waitingWriters = 0;
    private int readers = 0;
    private boolean activeWriter = false;

    public void readLock() throws Exception {
        lock.lock();
        try {

            while (activeWriter || waitingWriters > 0) {
                canRead.await();
            }
            readers++;
        } finally {
            lock.unlock();
        }
    }

    public void readUnlock() throws Exception {
        lock.lock();
        try {
            readers--;
            while (readers > 0) {
                canWrite.await();
            }
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
            waitingWriters++;
            while (readers > 0 || activeWriter) {
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
