package com.sprih_assignment.models;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushEvent extends BaseEvent {
    private String deviceId;
    private String message;
    
    public PushEvent(String callbackUrl, String deviceId, String message) {
        super(com.sprih_assignment.utils.enums.Events.EventType.PUSH, callbackUrl);
        this.deviceId = deviceId;
        this.message = message;
    }
}