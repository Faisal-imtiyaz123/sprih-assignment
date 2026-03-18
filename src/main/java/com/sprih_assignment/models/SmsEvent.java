package com.sprih_assignment.models;


import com.sprih_assignment.utils.enums.EventType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsEvent extends BaseEvent {
    private String phoneNumber;
    private String message;
    
    public SmsEvent(String callbackUrl, String phoneNumber, String message) {
        super(EventType.SMS, callbackUrl);
        this.phoneNumber = phoneNumber;
        this.message = message;
    }
} 
