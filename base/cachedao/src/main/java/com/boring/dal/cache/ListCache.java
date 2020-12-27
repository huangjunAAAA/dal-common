package com.boring.dal.cache;

import java.util.List;

public interface ListCache<T> {
    int inspect();
    void markDirty();
    void flush();
    boolean merge(int start, int end, List<T> part);
    RangeResult<T> findRange(Integer start, Integer count);

    class RangeResult<T>{
        /**
         * -1 未命中，0 完全命中，1 部分命中（前半部），2 部分命中（后半部）
         */
        public int status;
        public List<T> actual;
    }
}
