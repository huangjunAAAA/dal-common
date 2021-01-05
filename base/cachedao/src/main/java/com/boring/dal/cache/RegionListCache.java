package com.boring.dal.cache;

import java.util.List;

public interface RegionListCache {
    List<String> matchKey(String pattern);
    void addListKey(String key);
    void removeListKey(List<String> keys);
    void invalidateKey(String pattern);
}
