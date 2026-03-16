package com.sprih_assignment.controllers.eventController;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sprih_assignment.dto.request.event.EventRequest;
import com.sprih_assignment.dto.response.event.AddEventResponse;
import com.sprih_assignment.dto.response.event.EventResponse;
import com.sprih_assignment.models.BaseEvent;



import com.sprih_assignment.services.eventQueueManagerService.EventQueueManager;
import com.sprih_assignment.services.eventRequestParsingService.EventRequestParsingService;





@Slf4j
@RestController
@RequestMapping("/api/events")
public class EventController {
    
    private final EventQueueManager queueManager;
    private final EventRequestParsingService eventRequestParsingService;
    public EventController(EventQueueManager queueManager, EventRequestParsingService eventRequestParsingService) {
        this.queueManager = queueManager;
        this.eventRequestParsingService = eventRequestParsingService;
    }
    
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        log.info("Received event request: {}", request.getEventType());
            BaseEvent event = eventRequestParsingService.createEventFromRequest(request);
            AddEventResponse resp = queueManager.addEvent(event);
            EventResponse response = new EventResponse(
                    resp.getEventId(), 
                    "Event accepted for processing."
                );
            return ResponseEntity.ok(response);
    }
}
