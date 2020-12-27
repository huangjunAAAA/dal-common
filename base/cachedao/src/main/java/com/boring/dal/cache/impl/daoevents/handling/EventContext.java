package com.boring.dal.cache.impl.daoevents.handling;

public class EventContext {
    public EventContext(Object id, Object newObj, Object oldObj) {
        this.id = id;
        this.newObj = newObj;
        this.oldObj = oldObj;
    }

    public Object id;
    public Object newObj;
    public Object oldObj;
}
