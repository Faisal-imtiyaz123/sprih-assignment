package com.sprih_assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprih_assignment.controllers.EventController;
import com.sprih_assignment.dto.request.event.EventRequest;
import com.sprih_assignment.dto.response.event.AddEventResponse;
import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.models.EmailEvent;
import com.sprih_assignment.models.SmsEvent;
import com.sprih_assignment.models.PushEvent;
import com.sprih_assignment.services.core.callback.CallBackService;
import com.sprih_assignment.services.core.events.EventProcessorService;
import com.sprih_assignment.services.core.events.EventQueueManager;
import com.sprih_assignment.services.validation.request.EventRequestParsingService;
import com.sprih_assignment.utils.enums.EventType;
import com.sprih_assignment.utils.enums.EventStatus;
import com.sprih_assignment.utils.exceptions.event.EventException;
import com.sprih_assignment.utils.exceptions.event.EventErrorMessages;
import com.sprih_assignment.utils.interfaces.EventDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private EventQueueManager queueManager;

    // Can't mock static method directly - need to use Mockito.mockStatic
    // We'll test the controller without mocking this service
    @MockitoBean
    private EventRequestParsingService parsingService;  // This won't work for static methods

    @MockitoBean
    private CallBackService callBackService;

    @MockitoBean
    private EventProcessorService processorService;

    private Map<String, Object> validEmailPayload;
    private Map<String, Object> validSmsPayload;
    private Map<String, Object> validPushPayload;
    private Map<String, Object> invalidEmailPayload;
    private Map<String, Object> invalidSmsPayload;
    private Map<String, Object> invalidPushPayload;

    @BeforeEach
    void setUp() {
        // Setup valid payloads
        validEmailPayload = new HashMap<>();
        validEmailPayload.put("recipient", "test@example.com");
        validEmailPayload.put("message", "Hello Email");

        validSmsPayload = new HashMap<>();
        validSmsPayload.put("phoneNumber", "+1234567890");
        validSmsPayload.put("message", "Hello SMS");

        validPushPayload = new HashMap<>();
        validPushPayload.put("deviceId", "device-123");
        validPushPayload.put("message", "Hello Push");

        // Setup invalid payloads (missing required fields)
        invalidEmailPayload = new HashMap<>();
        invalidEmailPayload.put("message", "Hello");  // Missing recipient

        invalidSmsPayload = new HashMap<>();
        invalidSmsPayload.put("phoneNumber", "+1234567890");  // Missing message

        invalidPushPayload = new HashMap<>();
        invalidPushPayload.put("deviceId", "device-123");  // Missing message
    }

    @Test
    void createEvent_WithValidEmailRequest_ShouldReturnSuccess() throws Exception {
        // Instead of mocking static method, test with real payload
        EventRequest request = new EventRequest();
        request.setEventType(EventType.EMAIL.name());  // Set as enum, not String
        request.setCallbackUrl("http://callback.com");
        request.setPayload(validEmailPayload);

        // Mock only the queue manager
        when(queueManager.addEvent(any(BaseEvent.class)))
            .thenReturn(new AddEventResponse("event-123"));

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("event-123"))
                .andExpect(jsonPath("$.message").value("Event accepted for processing."));
    }

    @Test
    void createEvent_WithValidSmsRequest_ShouldReturnSuccess() throws Exception {
        EventRequest request = new EventRequest();
        request.setEventType(EventType.SMS.name());
        request.setCallbackUrl("http://callback.com");
        request.setPayload(validSmsPayload);

        when(queueManager.addEvent(any(BaseEvent.class)))
            .thenReturn(new AddEventResponse("event-123"));

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("event-123"));
    }

    @Test
    void createEvent_WithValidPushRequest_ShouldReturnSuccess() throws Exception {
        EventRequest request = new EventRequest();
        request.setEventType(EventType.PUSH.name());
        request.setCallbackUrl("http://callback.com");
        request.setPayload(validPushPayload);

        when(queueManager.addEvent(any(BaseEvent.class)))
            .thenReturn(new AddEventResponse("event-123"));

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("event-123"));
    }

    @Test
    void createEvent_WithInvalidEmailPayload_ShouldReturnBadRequest() throws Exception {
        EventRequest request = new EventRequest();
        request.setEventType(EventType.EMAIL.name());
        request.setCallbackUrl("http://callback.com");
        request.setPayload(invalidEmailPayload);  // Missing recipient

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEvent_WithInvalidSmsPayload_ShouldReturnBadRequest() throws Exception {
        EventRequest request = new EventRequest();
        request.setEventType(EventType.SMS.name());
        request.setCallbackUrl("http://callback.com");
        request.setPayload(invalidSmsPayload);  // Missing message

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEvent_WithInvalidPushPayload_ShouldReturnBadRequest() throws Exception {
        EventRequest request = new EventRequest();
        request.setEventType(EventType.PUSH.name());
        request.setCallbackUrl("http://callback.com");
        request.setPayload(invalidPushPayload);  // Missing message

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEvent_WithNullPayload_ShouldReturnBadRequest() throws Exception {
        EventRequest request = new EventRequest();
        request.setEventType(EventType.EMAIL.name());
        request.setCallbackUrl("http://callback.com");
        request.setPayload(null);  // Null payload

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}