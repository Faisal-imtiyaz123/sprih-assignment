package com.sprih_assignment.dto.response.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class EventErrorResponse {
    private final Status status;
    private final String errorMessgae;
    public enum Status{
        ERROR, FAILED, VALIDATION_ERROR, SYSTEM_ERROR,
    }
}
