package com.sprih_assignment.services.eventQueueManagerService.error;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

import org.springframework.stereotype.Component;

import com.sprih_assignment.dto.response.event.AddEventResponse;
import com.sprih_assignment.exceptions.event.EventException;
import com.sprih_assignment.exceptions.event.EventException.EventErrorMessages;
import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.utils.enums.Events.EventType;

@Slf4j
@Component
public class EventQueueManagerErrorHandler {
    
    // Processor errors
    public void handleProcessorInterrupted(EventType type, InterruptedException e) {
        log.info("Processor for {} interrupted", type);
        Thread.currentThread().interrupt();
    }
    
    public void handleProcessorError(EventType type, Exception e) {
        log.error("Error processing event in queue: {}", type, e);
    }
    
    // Add event errors
    public void handleShutdownState(String eventId) {
        String message = "System is shutting down. Not accepting new events.";
        log.error("Attempted to add event during shutdown: {}", eventId);
        throw new IllegalStateException(message);
    }
    
    // public void handleInvalidEventType(EventType eventType) {
    //     String message = "No queue found for event type: " + eventType;
    //     log.error(message);
    //     throw new IllegalArgumentException(message);
    // }
    
    public AddEventResponse handleAddEventSuccess(BaseEvent event, int queueSize) {
        log.info("Event {} added to {} queue. Queue size: {}", 
                 event.getEventId(), event.getEventType(), queueSize);
        return new AddEventResponse(event.getEventId());
    }
    
    public AddEventResponse handleAddEventTimeout(BaseEvent event) {
        String message = EventErrorMessages.QUEUE_TIMEOUT.getErrorMsg() + "for" + event.getEventId();
        log.error(message);
        throw new EventException(message);
    }
    
    public AddEventResponse handleAddEventException(BaseEvent event, Exception e) {
        log.error("Failed to add event {}: {}", event.getEventId(), e.getMessage());
        throw new EventException(EventErrorMessages.EVENT_ADD_ERROR.getErrorMsg() + event.getEventId());
    }
    
    // Shutdown errors
    public void handleShutdownInterrupted(InterruptedException e) {
        Thread.currentThread().interrupt();
    }
    
    public void handleExecutorShutdownInterrupted(ExecutorService executor, InterruptedException e) {
        executor.shutdownNow();
        Thread.currentThread().interrupt();
    }
    
    // Queue size errors
    public int handleQueueSizeNotFound(EventType type) {
        log.warn("Queue size requested for non-existent type: {}", type);
        return 0;
    }
}