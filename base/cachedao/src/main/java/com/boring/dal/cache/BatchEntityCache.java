package com.boring.dal.cache;

import java.util.List;

public interface BatchEntityCache<T> {
    List<EntityCache<T>> get();
    EntityCache<T> getByKey(String key);
}
