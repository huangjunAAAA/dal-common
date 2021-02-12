package com.boring.dal.cache.impl.daoevents.handling;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class PostHandler implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public void handleEvent(String eventName,EventContext params){
        EventHandler handler = (EventHandler) applicationContext.getBean(eventName);
        if(handler!=null){
            handler.handleEvent(params);
        }
    }
}
