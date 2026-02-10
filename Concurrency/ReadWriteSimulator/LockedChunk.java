package Concurrency.ReadWriteSimulator;

public class LockedChunk {
    String data;
    long version = 0;
    SimpleRWLock lock = new SimpleRWLock();

    public LockedChunk(String data) {
        this.data = data;
    }
}