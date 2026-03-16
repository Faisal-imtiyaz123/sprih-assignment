package com.sprih_assignment.models;

import com.sprih_assignment.dto.enums.EventDto;
import com.sprih_assignment.utils.enums.Events.EventType;
import lombok.Data;

@Data
public class EventFactory {
    
    public BaseEvent createEventWithErrorMsg(EventType type) {
        BaseEvent event;
        
        switch (type) {
            case EMAIL:
                event = new EmailEvent(null,null,null);
                event.setErrorMessage(EventDto.EMAIL_ERROR_MSG);
                break;
            case SMS:
                event = new SmsEvent(null,null,null);
                event.setErrorMessage(EventDto.SMS_ERROR_MSG);
                break;
            case PUSH:
                event = new PushEvent(null,null,null);
                event.setErrorMessage(EventDto.PUSH_ERROR_MSG);
                break;
            default:
                throw new IllegalArgumentException("Unknown event type: " + type);
        }
        return event;
    }
}