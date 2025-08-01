package online.anhht.airline.util.collection;

import org.jspecify.annotations.NonNull;

import java.util.Map;

public interface BidirectionalMap<K1, K2> {
    int size();
    boolean isEmpty();
    boolean containsKey(Object key);
    boolean containsValue(Object value);
    Object get(Object key);
    void put(K1 key1, K2 key2);
    void putAll(@NonNull Map<K1, K2> map);
    void remove(Object key);
    void clear();

}
