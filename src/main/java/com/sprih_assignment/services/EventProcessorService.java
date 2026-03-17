package com.sprih_assignment.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.utils.enums.Events.EventStatus;
import com.sprih_assignment.utils.enums.Events.EventType;
import com.sprih_assignment.utils.error.services.EventProcessorErrorHandler;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EventProcessorService {
    
    private static final Random RANDOM = new Random();
    private static final double FAILURE_RATE = 0.1; 
    
    private static final Map<EventType, Integer> PROCESSING_TIMES = Map.of(
        EventType.EMAIL, 5,
        EventType.SMS, 3,
        EventType.PUSH, 2
    );
    
    private final CallBackService callbackService;
    private final EventProcessorErrorHandler errorHandler;
    
    public EventProcessorService(CallBackService callbackService, EventProcessorErrorHandler errorHandler) {
        this.callbackService = callbackService;
        this.errorHandler = errorHandler;
    }
    
    public void process(BaseEvent event) {
        event.setStatus(EventStatus.PROCESSING);
        log.info("Processing event: {} of type: {}", event.getEventId(), event.getEventType());
        
        try {

            int processingTime = PROCESSING_TIMES.get(event.getEventType());
            
            log.info("PROCESSING" + event.getEventType() + "event");
            TimeUnit.SECONDS.sleep(processingTime);
            
            // Simulate random failure
            if (RANDOM.nextDouble() < FAILURE_RATE) {
                throw new RuntimeException("Simulated processing failure");
            }
            
            event.setStatus(EventStatus.COMPLETED);
            event.setProcessedAt(Instant.now());
            log.info("Event {} completed successfully", event.getEventId());
            
        } catch (InterruptedException e) {
            errorHandler.handleInterruptedException(event, e);
            
        } catch (Exception e) {
            errorHandler.handleException(event, e);
        }
        // Send callback notification
        // callbackService.sendCallback(event);
    }
}