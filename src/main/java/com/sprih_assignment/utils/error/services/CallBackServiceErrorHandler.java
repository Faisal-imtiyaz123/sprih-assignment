package com.sprih_assignment.utils.error.services;

import org.springframework.stereotype.Component;
import com.sprih_assignment.models.BaseEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CallBackServiceErrorHandler {
    public void handleException(Exception e, BaseEvent event){
        log.error(e.getMessage()+"HELLO ERROR");
        event.setErrorMessage(event.getErrorMessage());
    }
}