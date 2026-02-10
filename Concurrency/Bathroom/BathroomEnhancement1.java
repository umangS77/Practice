package Concurrency.Bathroom;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*

ENHANCEMENT 1 — Fairness (Men/Women, No Starvation)

 */
public class BathroomEnhancement1 {

    private int menInside = 0;
    private int womenInside = 0;
    private int menWaiting = 0;
    private int womenWaiting = 0;

    private String turn = "WOMEN";

    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void manEnter() throws InterruptedException {
        lock.lock();
        try {
            while(womenInside > 0 || ("WOMEN".equals(turn) && womenWaiting > 0 )) {
                condition.await();
            }
            menInside++;
            menWaiting--;
        } finally {
            lock.unlock();
        }
    }

    public void womanEnter() throws InterruptedException {
        lock.lock();
        try {
            while(menInside > 0 || ("MEN".equals(turn) && menWaiting > 0)) {
                condition.await();
            }
            womenWaiting--;
            womenInside++;
        } finally {
            lock.unlock();
        }
    }

    public void manExit() throws InterruptedException {
        lock.lock();
        try {
            menInside--;
            if(menInside == 0) {
                turn = "WOMEN";
            }
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void womanExit() throws InterruptedException {
        lock.lock();
        try {
            womenInside--;
            if(womenInside == 0) {
                turn = "MEN";
            }
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }



}
