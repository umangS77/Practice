package Concurrency.PriorityJobScheduler;

/*

enhancement - Priority Change for RUNNING Jobs
we do Cooperative Preemption => Running job periodically checks shouldYield flag.
Scheduler can :
    raise priority → requeue
    lower priority → let finish

True thread preemption is unsafe in Java,
so I implement cooperative preemption where running jobs periodically check a yield flag and reschedule themselves.


 */


public class PriorityScheduler4 extends PriorityScheduler3 {

    public PriorityScheduler4(int n) {
        super(n);
    }

    private void workerAction() {
        while (true) {
            try {
                PJob job = take();
                if (job instanceof PreemptibleJob) {
                    PreemptibleJob pj = (PreemptibleJob) job;
                    pj.yield = false;
                    pj.task.run();

                    if (pj.yield) {
                        submit(pj);
                    }
                } else {
                    job.task.run();
                }

            } catch (Exception e) {
                //
            }
        }
    }

    public void boostRunning(PreemptibleJob job, int newPriority) {
        job.priority = newPriority;
        job.requestYield();
    }

}
