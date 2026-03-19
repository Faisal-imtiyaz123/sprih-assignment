package com.sprih_assignment.services.core.events;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.services.core.callback.CallBackService;
import com.sprih_assignment.utils.enums.EventStatus;
import com.sprih_assignment.utils.enums.EventType;
import com.sprih_assignment.utils.error.services.EventProcessorErrorHandler;
import com.sprih_assignment.utils.exceptions.event.EventErrorMessages;
import com.sprih_assignment.utils.exceptions.event.EventException;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Setter
public class EventProcessorService {
    
    private static final Random RANDOM = new Random();
    private static final double FAILURE_RATE = 0.1;
    private boolean forceEventFail = false; 
    
    private static final Map<EventType, Integer> PROCESSING_TIMES = Map.of(
        EventType.EMAIL, 5,
        EventType.SMS, 3,
        EventType.PUSH, 2
    );
    
    private final CallBackService callbackService;

    @Autowired
    private EventProcessorErrorHandler errorHandler;
    
    public EventProcessorService(CallBackService callbackService) {
        this.callbackService = callbackService;
    }
    
    public void process(BaseEvent event) {
        event.setStatus(EventStatus.PROCESSING);
        log.info("PROCESSING EVENT: {} OF TYPE: {}", event.getEventId(), event.getEventType());
        try {
            if(forceEventFail==true){
                throw new EventException(EventErrorMessages.PROCESSING_FAILED);
            }
            int processingTime = PROCESSING_TIMES.get(event.getEventType());
            
            log.info("PROCESSING {} EVENT",event.getEventType());
            TimeUnit.SECONDS.sleep(processingTime);
            
            // Simulate random failure
            if (RANDOM.nextDouble() < FAILURE_RATE) {
                throw new EventException(EventErrorMessages.PROCESSING_FAILED);
            }
            
            event.setStatus(EventStatus.COMPLETED);
            event.setProcessedAt(Instant.now());
            log.info("Event {} completed successfully", event.getEventId());
            
        } catch (InterruptedException e) {
            errorHandler.handleInterruptedException(event, e);
            
        } catch (Exception e) {
            errorHandler.handleException(event, e);
        }finally{
            // Send callback notification
            callbackService.sendCallback(event);
        }
    }
}