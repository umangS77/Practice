package Concurrency.ReadWriteSimulator;

/*
Enhancement : Rollback Support (Mini Transactions)

multi-chunk update
rollback if conflict


 */

import java.util.*;

class Txn {
    // Transaction keeps before-images and restores them on failure — simple rollback model.
    final Map<Integer,String> before = new HashMap<>();
    final Map<Integer,String> after = new HashMap<>();
}

public class FileSim5 extends FileSim4 {
    public FileSim5(int chunkCount) {
        super(chunkCount);
    }

    public boolean writeTxn(Txn txn) throws Exception {
        List<Integer> ids = new ArrayList<>(txn.after.keySet());
        Collections.sort(ids);

        for (int i : ids) {
            chunks.get(i).lock.writeLock();
        }

        try {
            // capture before images
            for (int i : ids) {
                txn.before.put(i, chunks.get(i).data);
            }

            // apply
            for (int i : ids) {
                LockedChunk chunk = chunks.get(i);
                chunk.data = txn.after.get(i);
                chunk.version++;
            }

            return true;
        } catch (Exception e) {
            for (int i : ids) {
                chunks.get(i).data = txn.before.get(i);
            }
            return false;
        } finally {
            for (int i = ids.size()-1 ; i>=0; i--) {
                chunks.get(i).lock.writeUnlock();
            }
        }
    }
}
