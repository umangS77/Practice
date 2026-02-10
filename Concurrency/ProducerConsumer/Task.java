package Concurrency.ProducerConsumer;

public class Task {
    int id;
    int retries = 0;

    Task (int id) {
        this.id = id;
    }
}
