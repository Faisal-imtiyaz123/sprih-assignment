package com.sprih_assignment.models;

import java.time.Instant;
import java.util.UUID;

import com.sprih_assignment.utils.enums.EventStatus;
import com.sprih_assignment.utils.enums.EventType;

import lombok.Data;


@Data
public class BaseEvent {
    private String eventId;
    private EventType eventType;
    private EventStatus status;
    private String callbackUrl;
    private Instant createdAt;
    private Instant processedAt;
    private String errorMessage;
    
    public BaseEvent(EventType eventType, String callbackUrl) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.callbackUrl = callbackUrl;
        this.status = EventStatus.PENDING;
        this.createdAt = Instant.now();
    }
}