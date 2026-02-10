package Concurrency.Bathroom;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
    ENHANCEMENT 2 — Fairness + Multi-Group (Caste/Category)
 */

public class BathroomEnhancement2 {

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private String currentGroup = null;
    private int inside = 0;

    private Map<String, Integer> waiting = new HashMap<>();
    private String turnGroup = null;

    public void enter(String group) throws InterruptedException {
        lock.lock();
        try {
            waiting.put(group, waiting.getOrDefault(group, 0) + 1);
            while((inside > 0 && !group.equals(currentGroup)) || (inside == 0 && turnGroup != null && !turnGroup.equals(group))) {
                condition.await();
            }
            waiting.put(group, waiting.get(group) - 1);
            if (inside == 0) {
                currentGroup = group;
            }
            inside++;
        } finally {
            lock.unlock();
        }
    }

    public void exit() {
        lock.lock();
        try {
            inside--;
            if (inside == 0) {
                currentGroup = null;
                turnGroup = nextGroup();
            }
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private String nextGroup() {
        for (Map.Entry<String, Integer> group : waiting.entrySet()) {
            if (group.getValue() > 0) {
                return group.getKey();
            }
        }
        return null;
    }

}
