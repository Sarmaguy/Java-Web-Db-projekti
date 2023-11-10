package hr.fer.oprpp1.custom.scripting.exec;

import java.util.HashMap;
import java.util.Map;

public class ObjectMultistack {



    private Map<String, MultistackEntry> map = new HashMap<>();

    private static class MultistackEntry {
        private ValueWrapper value;
        private MultistackEntry next;

        public MultistackEntry(ValueWrapper value, MultistackEntry next) {
            this.value = value;
            this.next = next;
        }
    }


    public void push(String keyName, ValueWrapper valueWrapper) {
        MultistackEntry entry = map.get(keyName);
        map.put(keyName, new MultistackEntry(valueWrapper, entry));
    }
    public ValueWrapper pop(String keyName) {
        MultistackEntry entry = map.get(keyName);
        MultistackEntry next = entry.next;
        map.put(keyName, next);

        return entry.value;
    }
    public ValueWrapper peek(String keyName) {
        MultistackEntry entry = map.get(keyName);
        return entry.value;
    }
    public boolean isEmpty(String keyName) {
        return map.get(keyName) == null;
    }
}
