package com.sprih_assignment.exceptions.event;


import lombok.Getter;

@Getter
public class EventException extends RuntimeException {
    
    private final String details;
    
    // Constructor with just message
    public EventException(String message) {
        super(message);
        this.details = null;
    }
    
    
    // Constructor with all details
    public EventException(String message, String details) {
        super(message);
        this.details = details;
    }
    
    // Constructor with cause
    public EventException(String message, Throwable cause) {
        super(message, cause);
        this.details = null;
    }
    
    @Getter
    public enum EventErrorMessages {
    
        MISSING_REQUIRED_FIELDS("Missing required fields"),
        INVALID_EVENT_TYPE("Invalid event type"),
        EVENT_VALIDATION_FAILED("Event validation failed"),
        EVENT_ADD_ERROR("Failed to add event"),
        
 
        QUEUE_FULL("Queue is full"),
        QUEUE_TIMEOUT( "Queue operation timed out"),
        QUEUE_SHUTDOWN( "System is shutting down"),
        

        PROCESSING_FAILED("Event processing failed"),
        CALLBACK_FAILED( "Callback failed"),
        
  
        GENERAL_ERROR( "An error occurred");
        
        private final String errorMsg;
        
        EventErrorMessages(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }
}