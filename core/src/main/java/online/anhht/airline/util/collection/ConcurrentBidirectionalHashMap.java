package online.anhht.airline.util.collection;

import org.jspecify.annotations.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentBidirectionalHashMap<K1, K2>
        extends BidirectionalHashMap<K1, K2>
        implements BidirectionalMap<K1, K2>,
        Serializable {


    @Serial
    private static final long serialVersionUID = -6953450535854458364L;
    private final ReadWriteLock lock;

    public ConcurrentBidirectionalHashMap() {
        super();
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return super.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return super.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        lock.readLock().lock();
        try {
            return super.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        lock.readLock().lock();
        try {
            return super.containsValue(value);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Object get(Object key) {
        lock.readLock().lock();
        try {
            return super.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void put(K1 key1, K2 key2) {
        lock.writeLock().lock();
        try {
            super.put(key1, key2);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void putAll(@NonNull Map<K1, K2> map) {
        lock.writeLock().lock();
        try {
            super.putAll(map);
        } finally {
            lock.writeLock().unlock();
        }
    }


    @Override
    public void remove(Object key) {
        lock.writeLock().lock();
        try {
            super.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            super.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
