package com.sprih_assignment.dto.request.event;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

import com.sprih_assignment.exceptions.event.EventException;
import com.sprih_assignment.exceptions.event.EventException.EventErrorMessages;
import com.sprih_assignment.utils.enums.Events.EventType;

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
            throw new EventException(EventErrorMessages.INVALID_EVENT_TYPE.getErrorMsg() + " " +   eventType);
        }
    }
}