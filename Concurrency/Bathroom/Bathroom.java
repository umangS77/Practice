package Concurrency.Bathroom;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Bathroom {
    private int menInside = 0;
    private int womenInside = 0;

    private ReentrantLock lock = new ReentrantLock();
    private Condition menWait = lock.newCondition();
    private Condition womenWait = lock.newCondition();

    public void manEnter() throws InterruptedException {
        lock.lock();
        try {
            while(womenInside > 0) {
                menWait.await();
            }
            menInside++;
        } finally {
            lock.unlock();
        }
    }

    public void womanEnter() throws InterruptedException {
        lock.lock();
        try {
            while(menInside > 0) {
                womenWait.await();
            }
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
                womenWait.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public void womanExit() throws InterruptedException {
        lock.lock();
        try {
            womenInside--;
            if(womenInside == 0) {
                menWait.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }



}
