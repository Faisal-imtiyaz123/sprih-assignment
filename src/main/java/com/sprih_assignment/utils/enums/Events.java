package com.sprih_assignment.utils.enums;

public class Events {

    public enum EventType{
        EMAIL,
        SMS,
        PUSH
    }

    public enum EventStatus{
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
    
}
