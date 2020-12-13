package com.boring.dal.cache.construct;

public class VersionedValue<T> {
    public long version;
    public T val;
}
