package Concurrency.ReadWriteSimulator;

public class Chunk {
    String data;
    long version = 0;

    public Chunk(String data) {
        this.data = data;
    }
}
