package Concurrency.PriorityJobScheduler;


/*

Enhancement = Metrics + Shutdown


 */
public class PriorityScheduler5 extends PriorityScheduler4 {

    private volatile boolean shutdown = false;
    private long exectued = 0;

    public PriorityScheduler5(int n) {
        super(n);
    }

    public void submit(PJob job) {
        if (shutdown) {
            throw new IllegalStateException("Down");
        }
        super.submit(job);
    }

    private void recordExec() {
        lock.lock();
        try {
            exectued++;
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        shutdown = true;
    }

    public long getExectued() {
        return exectued;
    }
}
