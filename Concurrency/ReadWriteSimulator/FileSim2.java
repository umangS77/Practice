package Concurrency.ReadWriteSimulator;

/*

Enhancement 2 : Chunk-Level Locking (Fine Grained)

reads/writes only lock target chunk
higher parallelism

Lock striping at chunk level improves concurrency.

 */

import java.util.ArrayList;
import java.util.List;

public class FileSim2 {

    protected List<LockedChunk> chunks = new ArrayList<>();

    public FileSim2(int chunkCount) {
        for (int i=0;i<chunkCount;i++) {
            chunks.add(new LockedChunk("" + i));
        }
    }

    public String read(int i) throws Exception {
        LockedChunk chunk = chunks.get(i);
        chunk.lock.readLock();
        try {
            return chunk.data;
        } finally {
            chunk.lock.readUnlock();
        }
    }

    public void write(int i, String data) throws Exception {
        LockedChunk chunk = chunks.get(i);
        chunk.lock.writeLock();
        try {
            chunk.data = data;
            chunk.version++;
        } finally {
            chunk.lock.writeUnlock();
        }
    }

}
