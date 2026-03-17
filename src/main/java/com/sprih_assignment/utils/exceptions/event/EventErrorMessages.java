package com.sprih_assignment.utils.exceptions.event;

public interface EventErrorMessages {
    
    String MISSING_REQUIRED_FIELDS = "Missing required fields";
    String INVALID_EVENT_TYPE = "Invalid event type";
    String EVENT_VALIDATION_FAILED = "Event validation failed";
    String EVENT_ADD_ERROR = "Failed to add event";
    
    String QUEUE_FULL = "Queue is full";
    String QUEUE_TIMEOUT = "Queue operation timed out";
    String QUEUE_SHUTDOWN = "System is shutting down";
    
    String PROCESSING_FAILED = "Event processing failed";
    String CALLBACK_FAILED = "Callback failed";
    
    String GENERAL_ERROR = "An error occurred";
}
