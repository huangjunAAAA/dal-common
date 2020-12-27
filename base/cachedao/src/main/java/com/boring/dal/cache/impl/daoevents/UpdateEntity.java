package com.boring.dal.cache.impl.daoevents;

import com.boring.dal.cache.impl.daoevents.handling.EventContext;
import com.boring.dal.cache.impl.daoevents.handling.EventHandler;
import org.springframework.stereotype.Component;

import static com.boring.dal.cache.impl.daoevents.handling.EventHandler.EVENT_ENTITYUPDATED;

@Component(EVENT_ENTITYUPDATED)
public class UpdateEntity implements EventHandler {
    @Override
    public void handleEvent(EventContext env) {

    }
}
