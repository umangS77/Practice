package Concurrency.Bathroom;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
Enhancement 3 - Fair + Multi-Group + Multi-Bathroom
 */

public class BathroomEnhancement3 {
    private BathroomUnit[] bathrooms;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public BathroomEnhancement3(int n) {
        bathrooms = new BathroomUnit[n];
        for (int i=0;i<n;i++) {
            bathrooms[i] = new BathroomUnit();
        }
    }

    public int enter(String group) throws InterruptedException {
        lock.lock();
        try {
            int n = bathrooms.length;
            while(true) {
                for (int i=0;i<n;i++) {
                    BathroomUnit b = bathrooms[i];
                    if (b.getInside() == 0 || group.equals(b.getGroup())) {
                        b.setGroup(group);
                        b.addInside();
                        return i;
                    }
                }
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public void exit(int bathroomId) {
        lock.lock();
        try {
            BathroomUnit b = bathrooms[bathroomId];
            b.removeInside();
            if (b.getInside() == 0) {
                b.setGroup(null);
            }
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

}
