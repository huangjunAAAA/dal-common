package com.boring.dal.dao;

public interface TxFlusher {
    void txClean(Object transaction);

    void markDirty(String region, String key);
}
