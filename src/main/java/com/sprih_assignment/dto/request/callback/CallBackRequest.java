package com.sprih_assignment.dto.request.callback;



import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.Instant;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sprih_assignment.utils.enums.EventStatus;
import com.sprih_assignment.utils.enums.EventType;
import com.sprih_assignment.utils.exceptions.callBack.CallBackException;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class CallBackRequest {
    
    @NotBlank(message = "Event ID cannot be null or empty")
    private String eventId;
    
    @NotBlank(message = "Status cannot be null or empty")
    private String status; 
    
    @NotNull(message = "Event type is required")
    private EventType eventType;
    
    private Instant processedAt;
    
    private String errorMessage;
    
    public boolean hasValidStatus() {
    if (status == null) {
        return false;
    }
    
    try {
        EventStatus.valueOf(status.toUpperCase()); 
        return true; 
    } catch (IllegalArgumentException e) {
        return false; 
    }
}
    public EventStatus getStatus(String eventStatus) {
        try {
            return EventStatus.valueOf(eventStatus);
        } catch (Exception e) {
            throw new CallBackException("Invalid status value: " + status + 
                ". Allowed values: " + Arrays.toString(EventStatus.values()));
        }
    }
}