package com.boring.dal.cache.impl;

@FunctionalInterface
public interface DBDataFetcher<T, R> {
    R apply(T t) throws Exception;
}
