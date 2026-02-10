package Concurrency.JobQueue;

/*

Enhancement :
shutdown support
metrics
reject after shutdown


 */
public class JobQueue5 extends JobQueue2 {

    private volatile boolean shutdown = false;
    private long added = 0;
    private long taken = 0;

    @Override
    public Job takeJob() throws Exception {
        Job j = super.takeJob();
        taken++;
        return j;
    }

    @Override
    public void addJob(Job job) throws Exception {
        if (shutdown) {
            throw new Exception("shutdown");
        }
        super.addJob(job);
        added++;
    }

    public void shutdown() {
        shutdown = true;
    }

    public long getAdded() { return added; }
    public long getTaken() { return taken; }

}
