package Concurrency.PriorityJobScheduler;

/*

submit(job, priority)
workers fetch highest priority job
changePriority(jobId, newPriority)
thread-safe



=> changing priority of running jobs — true CPU-style preemption is complex —
we implement cooperative preemption (job checks flag / gets rescheduled)

 */

import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PriorityScheduler1 {
    protected PriorityQueue<PJob> pq = new PriorityQueue<>();
    protected ReentrantLock lock = new ReentrantLock();
    protected Condition notEmpty = lock.newCondition();

    public void submit(PJob job) {
        lock.lock();

        try {
            pq.add(job);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public PJob take() throws Exception {
        lock.lock();
        try {
            while (pq.isEmpty()) {
                notEmpty.await();
            }
            return pq.poll();
        } finally {
            lock.unlock();
        }
    }

}
