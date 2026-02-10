package Concurrency.TaskScheduler;

public class Scheduler7 extends Scheduler6 {

    private volatile boolean failed = false;

    public Scheduler7(int maxParallel) {
        super(maxParallel);
    }

    public void submit(Runnable task) {
        if (failed) {
            throw new IllegalStateException("Task failed");
        }

        super.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                failed = true;
                throw e;
            }
        });
    }

}
