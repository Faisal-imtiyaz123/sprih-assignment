package com.sprih_assignment.services.eventProcessorService.error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.utils.enums.Events.EventStatus;

import java.time.Instant;

@Slf4j
@Component
public class EventProcessorErrorHandler {
    
    public void handleInterruptedException(BaseEvent event, InterruptedException e) {
        Thread.currentThread().interrupt();
        updateEventStatus(event, EventStatus.FAILED, "Processing interrupted");
        log.error("Event {} processing interrupted", event.getEventId());
    }
    
    public void handleException(BaseEvent event, Exception e) {
        updateEventStatus(event, EventStatus.FAILED, e.getMessage());
        log.error("Event {} failed: {}", event.getEventId(), e.getMessage());
    }
    
    private void updateEventStatus(BaseEvent event, EventStatus status, String errorMessage) {
        event.setStatus(status);
        event.setErrorMessage(errorMessage);
        event.setProcessedAt(Instant.now());
    }
}