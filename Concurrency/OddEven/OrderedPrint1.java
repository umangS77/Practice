package Concurrency.OddEven;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
Thread A → odd
Thread B → even
Print in order

 */
public class OrderedPrint1 {
    private int MAX;
    private int num = 1;

    private ReentrantLock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();

    public OrderedPrint1(int max) {
        this.MAX = max;
    }

    public void printOdd() throws Exception {
        while (true) {
            lock.lock();
            try {
                while (num <= MAX && num % 2 == 0) {
                    cond.awaitUninterruptibly();
                }
                if (num > MAX) {
                    return;
                }

                System.out.println("Odd : " + num);
                num++;
                cond.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    public void printEven() {
        while(true) {
            lock.lock();
            try {
                while (num <= MAX && num %2 == 1) {
                    cond.awaitUninterruptibly();
                }
                if (num > MAX) {
                    return;
                }
                System.out.println("Even : " + num);
                num++;
                cond.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
}
