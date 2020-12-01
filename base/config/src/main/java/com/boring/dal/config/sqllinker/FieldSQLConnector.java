package com.boring.dal.config.sqllinker;

import com.boring.dal.config.DataEntry;

import java.util.List;

public interface FieldSQLConnector {
    void linkDataEntry(DataEntry de, List<Class> clazz);
}
