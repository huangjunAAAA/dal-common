package com.boring.dal.cache;

public interface EntityCache<T> {
    int inspect();
    void markDirty();
    void flush();
    T get();
    void update(T val);
    boolean updateIfAbsent(T val);
}
