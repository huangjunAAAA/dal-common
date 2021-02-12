package com.boring.dal.cache.impl;

public class FetchOp<T> {
    public final String listName;
    public final Object[] params;
    public final Integer start;
    public final Integer count;
    public final boolean masterAccess;

    public FetchOp(String listName, Object[] params, Integer start, Integer count, boolean masterAccess) {
        this.listName = listName;
        this.params = params;
        this.start = start;
        this.count = count;
        this.masterAccess = masterAccess;
    }


}
