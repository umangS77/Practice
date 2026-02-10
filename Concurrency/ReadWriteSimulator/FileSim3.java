package Concurrency.ReadWriteSimulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/*

Enhacement : Write Batching

combine multiple writes
apply atomically
reduce lock churn

method : Acquire locks in index(sorted) order (deadlock safe).

 */
public class FileSim3 extends FileSim2 {

    public FileSim3(int chunkCount) {
        super(chunkCount);
    }

    private void writeBatch(Map<Integer, String> updates) throws Exception {
        List<Integer> ids = new ArrayList<>(updates.keySet());
        Collections.sort(ids);

        for (int id : ids) {
            chunks.get(id).lock.writeLock();
        }
        try {
            for (int id : ids) {
                LockedChunk chunk = chunks.get(id);
                chunk.data = updates.get(id);
                chunk.version++;
            }
        } finally {
            for (int i = ids.size()-1; i>=0 ;i--) {
                chunks.get(i).lock.writeUnlock();
            }
        }
    }

}
