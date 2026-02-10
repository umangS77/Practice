package Concurrency.ReadWriteSimulator;


/*
Enhancement 4 : Version Conflict Detection

detect write-after-read conflict
optimistic concurrency

Reader records version → writer verifies.
This is optimistic concurrency control using version checks.

 */

public class FileSim4 extends FileSim3 {

    public FileSim4(int chunkCount) {
        super(chunkCount);
    }

    public ReadResult readWithVersion(int i) throws Exception {
        LockedChunk c = chunks.get(i);
        c.lock.readLock();
        try {
            return new ReadResult(c.data, c.version);
        } finally {
            c.lock.readUnlock();
        }
    }

    public boolean writeIfVersion(int i, String data, long expectedVersion) throws Exception {
        LockedChunk chunk = chunks.get(i);
        chunk.lock.writeLock();
        try {
            if (chunk.version != expectedVersion) {
                return false;
            }

            chunk.data = data;
            chunk.version++;

            return true;
        } finally {
            chunk.lock.writeUnlock();
        }
    }

}
