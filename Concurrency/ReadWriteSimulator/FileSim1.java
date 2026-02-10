package Concurrency.ReadWriteSimulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*

many readers
single writer at a time
whole-file lock

 */
public class FileSim1 {
    private List<Chunk> chunks = new ArrayList<>();
    private SimpleRWLock lock = new SimpleRWLock();

    public FileSim1(int chunkCount) {
        for(int i=0;i<chunkCount;i++) {
            chunks.add(new Chunk(""+ i));
        }
    }

    public String readChunk(int i) throws Exception {
        lock.readLock();
        try {
            return chunks.get(i).data;
        } finally {
            lock.readUnlock();
        }
    }

    public void writeChunk(int i, String data) throws Exception {
        lock.writeLock();
        try {
            Chunk c = chunks.get(i);
            c.data = data;
            c.version++;
        } finally {
            lock.writeUnlock();
        }
    }
}
