package com.sprih_assignment.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sprih_assignment.dto.CallBackRequest;
import com.sprih_assignment.models.BaseEvent;

@Slf4j
@Service
public class CallBackService {
    
    private final RestTemplate restTemplate;
    
    public CallBackService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public void sendCallback(BaseEvent event) {
        CallBackRequest callback = new CallBackRequest();
        callback.setEventId(event.getEventId());
        callback.setEventType(event.getEventType());
        callback.setStatus(event.getStatus());
        callback.setProcessedAt(event.getProcessedAt());
        callback.setErrorMessage(event.getErrorMessage());
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<CallBackRequest> request = new HttpEntity<>(callback, headers);
            
            restTemplate.postForEntity(event.getCallbackUrl(), request, String.class);
            log.info("Callback sent successfully for event: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Failed to send callback for event: {}", event.getEventId(), e);
        }
    }
}