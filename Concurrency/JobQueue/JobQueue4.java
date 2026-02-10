package Concurrency.JobQueue;

/*
enhancement :
Job has scheduledTime
Only runnable after that

 */

import org.jetbrains.annotations.NotNull;

import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class DelayedJob implements Comparable<DelayedJob> {

    long runAt;
    Job job;

    public DelayedJob(long runAt, Job job) {
        this.runAt = runAt;
        this.job = job;
    }

    @Override
    public int compareTo(@NotNull DelayedJob o) {
        return Long.compare(this.runAt, o.runAt);
    }
}

public class JobQueue4 {
    private PriorityQueue<DelayedJob> pq = new PriorityQueue<>();

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();

    public void addJob(Job job, long delayInMs) {
        lock.lock();
        try {
            pq.add(new DelayedJob(delayInMs, job));
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public Job takeJob() throws Exception {
        lock.lock();
        try {
            while (true) {
                DelayedJob job = pq.peek();
                long wait = job.runAt - System.currentTimeMillis();

                if (wait <= 0) {
                    return pq.poll().job;
                }

                notEmpty.awaitNanos(wait*1_000_000);
            }
        } finally {
            lock.unlock();
        }
    }

}
