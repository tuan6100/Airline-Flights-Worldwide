package online.anhht.airline.util.collection;

import org.jspecify.annotations.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BidirectionalHashMap <K1, K2>
        implements BidirectionalMap<K1, K2>,
        Serializable {

    @Serial
    private static final long serialVersionUID = 3527331175160540995L;
    protected final Map<K1, K2> forwardMap;
    protected final Map<K2, K1> backwardMap;

    public BidirectionalHashMap() {
        this.forwardMap = new HashMap<>();
        this.backwardMap = new HashMap<>();
    }

    @Override
    public int size() {
        return forwardMap.size();
    }

    @Override
    public boolean isEmpty() {
        return forwardMap.isEmpty() || backwardMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return forwardMap.containsKey(key) || backwardMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.containsKey(value);
    }

    @Override
    public Object get(Object key) {
        K2 value = forwardMap.get(key);
        if (value != null) {
            return value;
        }
        return backwardMap.get(key);

    }

    @Override
    public void put(K1 key1, K2 key2) {
        forwardMap.put(key1, key2);
        backwardMap.put(key2, key1);
    }

    @Override
    public void putAll(@NonNull Map<K1, K2> map) {
        this.forwardMap.putAll(map);
        map.forEach((key, value) -> {
            Objects.requireNonNull(value);
            backwardMap.put(value, key);
        });
    }

    @Override
    public void remove(Object key) {
        if (forwardMap.containsKey(key)) {
            forwardMap.remove(key);
        } else {
            backwardMap.remove(key);
        }
    }

    @Override
    public void clear() {
        forwardMap.clear();
        backwardMap.clear();
    }
}
