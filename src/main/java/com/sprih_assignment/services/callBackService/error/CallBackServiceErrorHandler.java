package com.sprih_assignment.services.callBackService.error;



import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CallBackServiceErrorHandler {
    
    public void handleException(Exception e){
        log.error(e.getMessage());
        throw new RuntimeException(e.getMessage());
    }
}