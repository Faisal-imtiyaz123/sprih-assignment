package com.sprih_assignment.dto.response.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Response upon a successful event creation")
@Data
@AllArgsConstructor
public class EventResponse {
    private String eventId;
    private String message;
}