package com.sprih_assignment.dto.request.event;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

import com.sprih_assignment.utils.enums.EventType;
import com.sprih_assignment.utils.exceptions.event.EventErrorMessages;
import com.sprih_assignment.utils.exceptions.event.EventException;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for creating a new Event")
@Data   
public class EventRequest {
    @NotNull
    private String eventType;
    
    @NotNull
    private Map<String, Object> payload;
    
    @NotNull
    private String callbackUrl;

    public EventType getEventType(){
        try{
            return EventType.valueOf(eventType);
        }catch(Exception e){      
            throw new EventException(EventErrorMessages.INVALID_EVENT_TYPE + " " +   eventType);
        }
    }
}