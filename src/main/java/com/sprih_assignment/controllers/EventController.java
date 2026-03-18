package com.sprih_assignment.controllers;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sprih_assignment.dto.request.callback.CallBackRequest;
import com.sprih_assignment.dto.request.event.EventRequest;
import com.sprih_assignment.dto.response.event.AddEventResponse;
import com.sprih_assignment.dto.response.event.EventResponse;
import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.services.EventQueueManager;
import com.sprih_assignment.services.EventRequestParsingService;
import com.sprih_assignment.utils.annotations.ReqExampleDocs;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@Slf4j
@RestController
@RequestMapping("/api")

public class EventController {
 
    private final EventQueueManager queueManager;
    private final EventRequestParsingService eventRequestParsingService;
    public EventController(EventQueueManager queueManager, EventRequestParsingService eventRequestParsingService) {
        this.queueManager = queueManager;
        this.eventRequestParsingService = eventRequestParsingService;
    }
    
    @PostMapping("/events")
    @ReqExampleDocs
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        log.info("Received event request: {}", request.getEventType().name());
            BaseEvent event = eventRequestParsingService.createEventFromRequest(request);
            AddEventResponse resp = queueManager.addEvent(event);
            EventResponse response = new EventResponse(
                    resp.getEventId(), 
                    "Event accepted for processing."
                );
            return ResponseEntity.ok(response);
    }

    @PostMapping("/mock-callback")
    public ResponseEntity<?> mockCallbackResponse(@RequestBody CallBackRequest request) {
        return ResponseEntity.ok(request);
    }
    
}
