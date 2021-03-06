package com.boring.dal.cache.impl.daoevents.handling;

public interface EventHandler {
    void handleEvent(EventContext env);

    String EVENT_NEWENTITYSAVED="NewEntitySaved";
    String EVENT_ENTITYUPDATED ="EntityUpdated";
    String EVENT_ENTITYDELETED="EntityRemoved";
}
