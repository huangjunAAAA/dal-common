package com.boring.dal.cache;

import java.util.List;

public interface ListCache<T> {
    int inspect();
    boolean merge();
    int getVersion();
    RangeResult<T> findRange(Integer start, Integer count);

    class RangeResult<T>{
        /**
         * -1 未命中，0 完全命中，1 部分命中（前半部），2 部分命中（后半部）
         */
        int status;
        List<T> actual;
    }
}
