package Concurrency.PriorityJobScheduler;

/*

Multi-Worker Execution
=> Workers continuously pull highest-priority jobs from a protected priority queue.

 */

import java.util.ArrayList;
import java.util.List;

public class PriorityScheduler2 extends PriorityScheduler1 {
    private List<Thread> workers = new ArrayList<>();

    private void workerAction() {
        while(true) {
            try {
                PJob job = take();
                job.task.run();
            } catch (Exception e) {
                //
            }
        }
    }

    public PriorityScheduler2(int n) {
        for (int i=0;i<n;i++) {
            Thread t = new Thread(this::workerAction);
            t.start();
            workers.add(t);

        }
    }
}
