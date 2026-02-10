package Concurrency.ProducerConsumer;


/*

enhancement : Multi-Stage Pipeline

Stages : Producer → Parse → Transform → Write
Each stage = thread + queue.

 */
public class Pipeline2 {
    public static void main(String args[]) {
        int capacity = 5;
        SimpleBlockingQueue<Task> q1 = new SimpleBlockingQueue<>(capacity);
        SimpleBlockingQueue<Task> q2 = new SimpleBlockingQueue<>(capacity);
        SimpleBlockingQueue<Task> q3 = new SimpleBlockingQueue<>(capacity);

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 20; i++) {
                try {
                    q1.put(new Task(i));
                    System.out.println("Produced " + i);
                } catch (Exception ignored) {}
            }
        });

        Thread parse = new Thread(() -> {
            while(true) {
                try {
                    Task t1 = q1.take();
                    q2.put(t1);
                    System.out.println("Parsed : " + t1.id);
                } catch (Exception e) {
                    //
                }
            }
        });

        Thread transform = new Thread(() -> {
            while(true) {
                try {
                    Task t1 = q2.take();
                    q3.put(t1);
                    System.out.println("Transformed : " + t1.id);
                } catch (Exception e) {
                    //
                }
            }
        });

        Thread write = new Thread(() -> {
           while(true) {
               try {
                   Task t1 = q3.take();
                   System.out.println("Written : " + t1.id);
               } catch (Exception e) {
                   //
               }
           }
        });

        producer.start();
        parse.start();
        transform.start();
        write.start();
    }
}
