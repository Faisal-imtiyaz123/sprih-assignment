package com.sprih_assignment.dto.request.callback;



import lombok.Data;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sprih_assignment.utils.enums.EventStatus;
import com.sprih_assignment.utils.enums.EventType;



@Data
@JsonInclude(Include.NON_NULL)
public class CallBackRequest{
    private String eventId;
    private EventStatus status;
    private EventType eventType;
    private Instant processedAt;
    private String errorMessage = null;
}