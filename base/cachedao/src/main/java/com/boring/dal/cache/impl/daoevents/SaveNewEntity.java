package com.boring.dal.cache.impl.daoevents;

import com.boring.dal.cache.impl.daoevents.handling.EventContext;
import com.boring.dal.cache.impl.daoevents.handling.EventHandler;
import org.springframework.stereotype.Component;

import static com.boring.dal.cache.impl.daoevents.handling.EventHandler.EVENT_NEWENTITYSAVED;

@Component(EVENT_NEWENTITYSAVED)
public class SaveNewEntity implements EventHandler {
    @Override
    public void handleEvent(EventContext env) {

    }
}
