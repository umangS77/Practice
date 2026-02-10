package Concurrency.ProducerConsumer;

/*

enhancement : Graceful Shutdown (Poison Pill)

 */

public class Pipeline3 {

    static final Task POISON = new Task(-1);

    public static void main(String args[]) {
        int queueCapacity = 5;
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
                    System.out.println("Consumed : " + t.id);
                } catch (Exception e) {
                    //
                }
            }
        });

        producer.start();
        consumer.start();

    }
}
