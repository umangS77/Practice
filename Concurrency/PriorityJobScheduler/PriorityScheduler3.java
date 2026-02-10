package Concurrency.PriorityJobScheduler;

/*

Enhancement = Dynamic Priority Change (Queued Jobs)

PriorityQueue does NOT auto-reorder
We must remove + reinsert

explanation : Since PriorityQueue doesn’t reorder automatically, I remove and reinsert the job after priority change.


 */
public class PriorityScheduler3 extends PriorityScheduler2 {


    public PriorityScheduler3(int n) {
        super(n);
    }

    public void changePriority(String jobId, int newPriority) {
        lock.lock();
        try {
            for (PJob job : pq) {
                if (job.id.equals(jobId)) {
                    pq.remove(job);
                    job.priority = newPriority;
                    pq.add(job);
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
