package Concurrency.JobQueue;

/*
Higher priority jobs executed first
=> PriorityQueue
 */


import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class PriorityJob implements Job, Comparable<PriorityJob> {

    int priority;
    Runnable task;

    public PriorityJob(int priority, Runnable task) {
        this.priority = priority;
        this.task = task;
    }

    public void execute() {
        task.run();
    }

    public int compareTo(PriorityJob o) {
        return Integer.compare(o.priority, this.priority);
    }
}

public class JobQueue3 {

    private PriorityQueue<PriorityJob> pq = new PriorityQueue<>();
    private ReentrantLock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();

    public void addJob(PriorityJob job) {
        lock.lock();
        try {
            pq.add(job);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public PriorityJob takeJob() throws Exception {
        lock.lock();
        try {
            while (pq.isEmpty()) {
                notEmpty.await();
            }
            PriorityJob job = pq.poll();
            return job;
        } finally {
            lock.unlock();
        }
    }

}
