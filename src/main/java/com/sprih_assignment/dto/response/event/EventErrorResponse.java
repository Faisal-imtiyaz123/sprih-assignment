package com.sprih_assignment.dto.response.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Event Error Response")
@Data
@AllArgsConstructor

public class EventErrorResponse {
    private final Status status;
    private final String errorMessgae;
    public enum Status{
        ERROR, FAILED, VALIDATION_ERROR, SYSTEM_ERROR,
    }
}
