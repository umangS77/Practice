package Concurrency.ProducerConsumer;

public class Pipeline1 {
    public static void main(String args[]) {
        int queueCapacity = 5;
        SimpleBlockingQueue <Task> queue = new SimpleBlockingQueue<>(queueCapacity);

        Thread producer = new Thread( () -> {
            for (int i=0;i<20;i++) {
                try {
                    queue.put(new Task(i));
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
