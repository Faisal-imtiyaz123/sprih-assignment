package com.sprih_assignment.controllers;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sprih_assignment.dto.EventRequest;
import com.sprih_assignment.dto.EventResponse;
import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.models.EmailEvent;
import com.sprih_assignment.models.PushEvent;
import com.sprih_assignment.models.SmsEvent;
import com.sprih_assignment.services.EventQueueManager;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/events")
public class EventController {
    
    private final EventQueueManager queueManager;
    
    public EventController(EventQueueManager queueManager) {
        this.queueManager = queueManager;
    }
    
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        log.info("Received event request: {}", request.getEventType());
        
        try {
            BaseEvent event = createEventFromRequest(request);
            String eventId = queueManager.addEvent(event);
            
            EventResponse response = new EventResponse(
                eventId, 
                "Event accepted for processing."
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (IllegalStateException e) {
            log.error("System not accepting events: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new EventResponse(null, e.getMessage()));
        }
    }
    
    private BaseEvent createEventFromRequest(EventRequest request) {
        Map<String, Object> payload = request.getPayload();
        
        switch (request.getEventType()) {
            case EMAIL:
                String recipient = (String) payload.get("recipient");
                String emailMessage = (String) payload.get("message");
                
                if (recipient == null || emailMessage == null) {
                    throw new IllegalArgumentException("Missing required fields for EMAIL");
                }
                
                return new EmailEvent(
                    request.getCallbackUrl(),
                    recipient,
                    emailMessage
                );
                
            case SMS:
                String phoneNumber = (String) payload.get("phoneNumber");
                String smsMessage = (String) payload.get("message");
                
                if (phoneNumber == null || smsMessage == null) {
                    throw new IllegalArgumentException("Missing required fields for SMS");
                }
                
                return new SmsEvent(
                    request.getCallbackUrl(),
                    phoneNumber,
                    smsMessage
                );
                
            case PUSH:
                String deviceId = (String) payload.get("deviceId");
                String pushMessage = (String) payload.get("message");
                
                if (deviceId == null || pushMessage == null) {
                    throw new IllegalArgumentException("Missing required fields for PUSH");
                }
                
                return new PushEvent(
                    request.getCallbackUrl(),
                    deviceId,
                    pushMessage
                );
                
            default:
                throw new IllegalArgumentException("Unsupported event type: " + request.getEventType());
        }
    }
}
