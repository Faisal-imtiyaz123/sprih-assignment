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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; 
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@Import(JacksonAutoConfiguration.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ CORRECT: Use @MockitoBean for all dependencies
    @MockitoBean
    private EventQueueManager queueManager;

    @MockitoBean
    private EventRequestParsingService parsingService;

    @MockitoBean
    private CallBackService callBackService;

    @MockitoBean
    private EventProcessorService processorService;

    private EventRequest validEmailRequest;
    private EventRequest validSmsRequest;
    private EventRequest validPushRequest;
    private BaseEvent emailEvent;
    private BaseEvent smsEvent;
    private BaseEvent pushEvent;
    private AddEventResponse successResponse;

    @BeforeEach
    void setUp() {
        validEmailRequest = createEventRequest(EventType.EMAIL, 
            Map.of("recipient", "test@example.com", "message", "Hello Email"));
        
        validSmsRequest = createEventRequest(EventType.SMS,
            Map.of("phoneNumber", "+1234567890", "message", "Hello SMS"));
        
        validPushRequest = createEventRequest(EventType.PUSH,
            Map.of("deviceId", "device-123", "message", "Hello Push"));

        emailEvent = new EmailEvent("http://callback.com", "test@example.com", "Hello Email");
        smsEvent = new SmsEvent("http://callback.com", "+1234567890", "Hello SMS");
        pushEvent = new PushEvent("http://callback.com", "device-123", "Hello Push");

        successResponse = new AddEventResponse("event-123");
    }

    @Test
    void createEvent_WithValidEmailRequest_ShouldReturnSuccess() throws Exception {
        // ✅ CORRECT: Mock instance methods, not static
        when(parsingService.createEventFromRequest(any(EventRequest.class)))
            .thenReturn(emailEvent);
        when(queueManager.addEvent(any(BaseEvent.class)))
            .thenReturn(successResponse);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEmailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("event-123"))
                .andExpect(jsonPath("$.message").value("Event accepted for processing."));

        verify(parsingService).createEventFromRequest(any(EventRequest.class));
        verify(queueManager).addEvent(any(BaseEvent.class));
    }

    @Test
    void createEvent_ShouldHandleProcessingFailures() throws Exception {
        // ✅ CORRECT: Use the mocked instance
        when(parsingService.createEventFromRequest(any(EventRequest.class)))
            .thenReturn(emailEvent);
        when(queueManager.addEvent(any(BaseEvent.class)))
            .thenReturn(successResponse);

        // Mock the processor to simulate failure
        doAnswer(invocation -> {
            BaseEvent event = invocation.getArgument(0);
            event.setStatus(EventStatus.FAILED);
            event.setErrorMessage(EventErrorMessages.PROCESSING_FAILED);
            return null;
        }).when(processorService).process(any(BaseEvent.class));

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEmailRequest)))
                .andExpect(status().isOk());

        verify(parsingService).createEventFromRequest(any(EventRequest.class));
        verify(queueManager).addEvent(any(BaseEvent.class));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRequests")
    void createEvent_WithInvalidPayloads_ShouldReturnBadRequest(EventRequest request, String expectedErrorMessage) 
            throws Exception {
        when(parsingService.createEventFromRequest(any(EventRequest.class)))
            .thenThrow(new EventException(expectedErrorMessage));

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(expectedErrorMessage));
    }

    private static Stream<Arguments> provideInvalidRequests() {
        return Stream.of(
            Arguments.of(
                createEventRequest(EventType.EMAIL, Map.of("message", "Hello")),
                "Missing required fields for EMAIL"
            ),
            Arguments.of(
                createEventRequest(EventType.EMAIL, Map.of("recipient", "test@example.com")),
                "Missing required fields for EMAIL"
            ),
            Arguments.of(
                createEventRequest(EventType.SMS, Map.of("message", "Hello")),
                "Missing required fields for SMS"
            ),
            Arguments.of(
                createEventRequest(EventType.SMS, Map.of("phoneNumber", "+1234567890")),
                "Missing required fields for SMS"
            ),
            Arguments.of(
                createEventRequest(EventType.PUSH, Map.of("message", "Hello")),
                "Missing required fields for PUSH"
            ),
            Arguments.of(
                createEventRequest(EventType.PUSH, Map.of("deviceId", "device-123")),
                "Missing required fields for PUSH"
            )
        );
    }

    private static EventRequest createEventRequest(EventType type, Map<String, Object> payload) {
        EventRequest request = new EventRequest();
        request.setEventType(type.name());  // ✅ Set enum directly, not type.name()
        request.setCallbackUrl("http://callback.com");
        request.setPayload(payload);
        return request;
    }
}