package com.sprih_assignment.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sprih_assignment.dto.request.callback.CallBackRequest;
import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.utils.error.services.CallBackServiceErrorHandler;

@Slf4j
@Service
public class CallBackService {
    
    private final RestTemplate restTemplate;
    private final CallBackServiceErrorHandler callBackServiceErrorHandler;

    public CallBackService(RestTemplate restTemplate, CallBackServiceErrorHandler callBackServiceErrorHandler) {
        this.restTemplate = restTemplate;
        this.callBackServiceErrorHandler = callBackServiceErrorHandler;
    }
    
    public void sendCallback(BaseEvent event) {
        CallBackRequest callback = new CallBackRequest();
        callback.setEventId(event.getEventId());
        callback.setEventType(event.getEventType());
        callback.setStatus(event.getStatus());
        callback.setProcessedAt(event.getProcessedAt());
        
        boolean isError = event.getErrorMessage()!=null && !event.getErrorMessage().isEmpty();
        if(isError){
            callback.setErrorMessage(event.getErrorMessage());
        }
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
                
            HttpEntity<CallBackRequest> request = new HttpEntity<>(callback, headers);
                
            Object res = restTemplate.postForEntity(event.getCallbackUrl(), request, String.class);
            log.info(res.toString());
            log.info("Callback sent successfully for event: {}", event.getEventId());
        }catch(Exception e){
            callBackServiceErrorHandler.handleException(e);
        }
    }
}