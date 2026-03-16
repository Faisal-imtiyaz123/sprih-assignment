package com.sprih_assignment.models;
import com.sprih_assignment.utils.enums.Events.EventType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailEvent extends BaseEvent {
    private String recipient;
    private String message;
    
    public EmailEvent(String callbackUrl, String recipient, String message) {
        super(EventType.EMAIL, callbackUrl);
        this.recipient = recipient;
        this.message = message;
    }
}

