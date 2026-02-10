package Concurrency.PriorityJobScheduler;

class PJob implements Comparable<PJob> {
    String id;
    volatile int priority;
    Runnable task;

    PJob(String id, int priority, Runnable task) {
        this.id = id;
        this.priority = priority;
        this.task = task;
    }

    public int compareTo(PJob o) {
        return Integer.compare(o.priority, this.priority);
    }
}