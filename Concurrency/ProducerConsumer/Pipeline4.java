package Concurrency.ProducerConsumer;

/*

Enhancement : Retry + Dead Letter Queue

Failed tasks are retried with bounded attempts and then routed to a dead letter queue.

 */
public class Pipeline4 {

    static final Task POISON = new Task(-1);

    public static void main(String args[]) {
        int MAX_RETRY = 3;
        int queueCapacity = 5;
        SimpleBlockingQueue <Task> dlq = new SimpleBlockingQueue<>(100);
        SimpleBlockingQueue <Task> queue = new SimpleBlockingQueue<>(queueCapacity);

        Thread producer = new Thread( () -> {
            for (int i=0;i<20;i++) {
                try {
                    queue.put(new Task(i));

                    if (i==13) {
                        queue.put(POISON);
                    }


                    System.out.println("Produced : " + i);
                } catch (Exception e) {
                    //
                }
            }
        });

        Thread consumer = new Thread( () -> {
            while(true) {
                try {

                    Task t = queue.take();

                    if (t == POISON) {
                        break;
                    }
                    try {
                        // some process
                        System.out.println("Consumed : " + t.id);
                    } catch (Exception e) {
                        t.retries++;
                        if (t.retries <= MAX_RETRY) {
                            // retry
                        } else {
                            dlq.put(t);
                            System.out.println("DLQ : " + t.id);
                        }
                    }
                } catch (Exception e) {
                }
            }
        });

        producer.start();
        consumer.start();


    }
}
