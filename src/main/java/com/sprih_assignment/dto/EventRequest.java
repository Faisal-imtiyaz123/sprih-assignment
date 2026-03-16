package com.sprih_assignment.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

import com.sprih_assignment.utils.enums.Events.EventType;

@Data
public class EventRequest {
    @NotNull
    private EventType eventType;
    
    @NotNull
    private Map<String, Object> payload;
    
    @NotNull
    private String callbackUrl;

}