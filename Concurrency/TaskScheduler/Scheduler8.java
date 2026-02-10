package Concurrency.TaskScheduler;

/*
+ Task Dependencies
 */

import java.util.concurrent.Callable;

public class Scheduler8 extends Scheduler7 {

    public Scheduler8(int maxParallel) {
        super(maxParallel);
    }

    public <T> SimpleFuture<T> submitAfter(
            Callable<T> task,
            SimpleFuture<?> dependency) {

        SimpleFuture<T> f = new SimpleFuture<>();

        new Thread(() -> {
            try {
                dependency.get();
                submit(() -> {
                    try {
                        f.set(task.call());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (InterruptedException ignored) {
                //
            }
        }).start();

        return f;
    }
}