package Concurrency.JobQueue;

/*

add job
remove job
getNext job
multi-thread safe
non-blocking


 */

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.ReentrantLock;

public class JobQueue1 {

    private Deque<Job> queue = new ArrayDeque<>();
    private ReentrantLock lock = new ReentrantLock(true);

    public void addJob(Job job) {
        lock.lock();
        try {
            queue.addLast(job);
        } finally {
            lock.unlock();
        }
    }

    public boolean removeJob(Job job) {
        lock.lock();
        try {
            return queue.remove(job);
        } finally {
            lock.unlock();
        }
    }

    public Job getNext() {
        lock.lock();
        try {
            return queue.pollFirst();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

}
