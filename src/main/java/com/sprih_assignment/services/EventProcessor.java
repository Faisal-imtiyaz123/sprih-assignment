package com.sprih_assignment.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.utils.enums.Events.EventStatus;
import com.sprih_assignment.utils.enums.Events.EventType;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EventProcessor {
    
    private static final Random RANDOM = new Random();
    private static final double FAILURE_RATE = 0.1; // 10% failure rate
    
    private static final Map<EventType, Integer> PROCESSING_TIMES = Map.of(
        EventType.EMAIL, 5,
        EventType.SMS, 3,
        EventType.PUSH, 2
    );
    
    private final CallBackService callbackService;
    
    public EventProcessor(CallBackService callbackService) {
        this.callbackService = callbackService;
    }
    
    public void process(BaseEvent event) {
        event.setStatus(EventStatus.PROCESSING);
        log.info("Processing event: {} of type: {}", event.getEventId(), event.getEventType());
        
        try {
            // Get processing time for event type
            int processingTime = PROCESSING_TIMES.get(event.getEventType());
            
            // Simulate processing time
            TimeUnit.SECONDS.sleep(processingTime);
            
            // Simulate random failure
            if (RANDOM.nextDouble() < FAILURE_RATE) {
                throw new RuntimeException("Simulated processing failure");
            }
            
            event.setStatus(EventStatus.COMPLETED);
            event.setProcessedAt(Instant.now());
            log.info("Event {} completed successfully", event.getEventId());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            event.setStatus(EventStatus.FAILED);
            event.setErrorMessage("Processing interrupted");
            event.setProcessedAt(Instant.now());
            log.error("Event {} processing interrupted", event.getEventId());
            
        } catch (Exception e) {
            event.setStatus(EventStatus.FAILED);
            event.setErrorMessage(e.getMessage());
            event.setProcessedAt(Instant.now());
            log.error("Event {} failed: {}", event.getEventId(), e.getMessage());
        }
        
        // Send callback notification
        callbackService.sendCallback(event);
    }
}