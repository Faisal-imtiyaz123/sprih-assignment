package com.sprih_assignment.utils.exceptions.event;


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
}