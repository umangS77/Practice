package Concurrency.PriorityJobScheduler;

public class PreemptibleJob extends PJob {
    volatile boolean yield = false;
    PreemptibleJob(String id, int priority, Runnable task) {
        super(id, priority, task);
    }

    public void requestYield() {
        this.yield = true;
    }
}
