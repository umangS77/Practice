package Concurrency.Bathroom;


/*

Enhancement 4 - Fair + Multi-Group + Multi-Bathroom + VIP Priority
                i.e., multi priority bathroom

                VIP users should go before normal users.

 */

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BathroomEnhancement4 {
    private BathroomUnit[] bathrooms;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private int vipWaiting = 0;

    public BathroomEnhancement4(int n) {
        bathrooms = new BathroomUnit[n];
        for (int i=0;i<n;i++) {
            bathrooms[i] = new BathroomUnit();
        }
    }

    public int enter(String group, Boolean vip) throws InterruptedException {
        lock.lock();
        try {
            int n = bathrooms.length;

            if (vip) {
                vipWaiting++;
            }

            while(true) {
                if (!vip && vipWaiting > 0) {
                    condition.await();
                    continue;
                }
                for (int i=0;i<n;i++) {
                    BathroomUnit b = bathrooms[i];
                    if (b.getInside() == 0 || group.equals(b.getGroup())) {
                        b.setGroup(group);
                        b.addInside();
                        if (vip) {
                            vipWaiting--;
                        }
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
