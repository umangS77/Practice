package Concurrency;

public class Runner {

    public static void main(String args[]) {
        ThreadSafeKeyValueStore store = new ThreadSafeKeyValueStore();
        Runnable writer = () -> {
            for (int i=0;i<1000;i++) {
                store.setValue("k"+i, "v"+i);
            }
        };

        Thread t1 = new Thread(writer);
        Thread t2 = new Thread(writer);
        t1.start();
        t2.start();
    }
}
