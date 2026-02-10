package Concurrency.TaskScheduler;

/*

support to cancel task

 */

import java.util.concurrent.Callable;

class CancellableFuture<T> extends SimpleFuture<T> {

    private volatile boolean cancelled = false;

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

}

public class Scheduler5 extends Scheduler4 {

    public Scheduler5(int maxParallel) {
        super(maxParallel);
    }

    public <T> CancellableFuture<T> submitCancellable(Callable<T> task) {
        CancellableFuture<T> future = new CancellableFuture<>();

        Runnable runnable = () -> {
            if (future.isCancelled()) {
                return;
            }
            try {
                future.set(task.call());
            } catch (Exception e) {
                //
            }
        };

        submit(runnable);
        return future;
    }
}
