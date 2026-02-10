package Concurrency;

import java.util.HashMap;
import java.util.Map;

public class ThreadSafeKeyValueStore {

    private final Map<String, String> store = new HashMap<>();

    synchronized void setValue(String key, String value) {
        this.store.put(key, value);
    }

    private synchronized String getValue(String key) {
        return this.store.get(key);
    }

    private synchronized void deleteValue(String key) {
        this.store.remove(key);
    }

    private synchronized int size() {
        return this.store.size();
    }


}
