package Concurrency.JobQueue;


/*
enhancement :
Workers block if queue empty
Producers wake them

Consumers wait on a condition variable and producers signal when jobs arrive.

*/

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class JobQueue2 {
    private Deque<Job> queue = new ArrayDeque<>();
    private ReentrantLock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();

    public void addJob(Job job) throws Exception {
        lock.lock();
        try {
            queue.addLast(job);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public Job takeJob() throws Exception {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            Job job = queue.removeFirst();
            return job;
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


}
