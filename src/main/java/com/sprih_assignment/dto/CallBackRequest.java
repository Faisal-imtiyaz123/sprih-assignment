package com.sprih_assignment.dto;



import lombok.Data;

import java.time.Instant;


import com.sprih_assignment.utils.enums.Events.EventStatus;
import com.sprih_assignment.utils.enums.Events.EventType;

@Data
public class CallBackRequest{
    private String eventId;
    private EventStatus status;
    private EventType eventType;
    private Instant processedAt;
    private String errorMessage;
}