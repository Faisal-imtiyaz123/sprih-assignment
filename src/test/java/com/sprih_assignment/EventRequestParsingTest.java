package com.sprih_assignment;

import com.sprih_assignment.dto.request.event.EventRequest;
import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.models.EmailEvent;
import com.sprih_assignment.models.PushEvent;
import com.sprih_assignment.models.SmsEvent;
import com.sprih_assignment.services.validation.request.EventRequestParsingService;
import com.sprih_assignment.utils.enums.EventType;
import com.sprih_assignment.utils.exceptions.event.EventErrorMessages;
import com.sprih_assignment.utils.exceptions.event.EventException;
import com.sprih_assignment.utils.interfaces.EventDto;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;



import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventRequestEventRequestParsingServiceTest {


    @Test
    void createEventFromRequest_WithValidEmailRequest_ShouldReturnEmailEvent() {
        // Arrange
        EventRequest request = createEmailRequest(
            "http://callback.com/test",
            "test@example.com",
            "Hello Email"
        );

        // Act
        BaseEvent result = EventRequestParsingService.createEventFromRequest(request);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof EmailEvent);
        EmailEvent emailEvent = (EmailEvent) result;
        assertEquals("http://callback.com/test", emailEvent.getCallbackUrl());
        assertEquals("test@example.com", emailEvent.getRecipient());
        assertEquals("Hello Email", emailEvent.getMessage());
    }

    @Test
    void createEventFromRequest_WithValidSmsRequest_ShouldReturnSmsEvent() {
        // Arrange
        EventRequest request = createSmsRequest(
            "http://callback.com/test",
            "+1234567890",
            "Hello SMS"
        );

        // Act
        BaseEvent result = EventRequestParsingService.createEventFromRequest(request);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof SmsEvent);
        SmsEvent smsEvent = (SmsEvent) result;
        assertEquals("http://callback.com/test", smsEvent.getCallbackUrl());
        assertEquals("+1234567890", smsEvent.getPhoneNumber());
        assertEquals("Hello SMS", smsEvent.getMessage());
    }

    @Test
    void createEventFromRequest_WithValidPushRequest_ShouldReturnPushEvent() {
        // Arrange
        EventRequest request = createPushRequest(
            "http://callback.com/test",
            "device-123",
            "Hello Push"
        );

        // Act
        BaseEvent result = EventRequestParsingService.createEventFromRequest(request);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof PushEvent);
        PushEvent pushEvent = (PushEvent) result;
        assertEquals("http://callback.com/test", pushEvent.getCallbackUrl());
        assertEquals("device-123", pushEvent.getDeviceId());
        assertEquals("Hello Push", pushEvent.getMessage());
    }

    @Test
    void createEventFromRequest_WithEmailMissingRecipient_ShouldThrowException() {
        // Arrange
        EventRequest request = createEmailRequest(
            "http://callback.com/test",
            null,
            "Hello Email"
        );

        // Act & Assert
        EventException exception = assertThrows(
            EventException.class,
            () -> EventRequestParsingService.createEventFromRequest(request)
        );
        assertEquals(EventDto.EMAIL_ERROR_MSG, exception.getMessage());
    }

    @Test
    void createEventFromRequest_WithEmailMissingMessage_ShouldThrowException() {
        // Arrange
        EventRequest request = createEmailRequest(
            "http://callback.com/test",
            "test@example.com",
            null
        );

        // Act & Assert
        EventException exception = assertThrows(
            EventException.class,
            () -> EventRequestParsingService.createEventFromRequest(request)
        );
        assertEquals(EventDto.EMAIL_ERROR_MSG, exception.getMessage());
    }

    @Test
    void createEventFromRequest_WithSmsMissingPhoneNumber_ShouldThrowException() {
        // Arrange
        EventRequest request = createSmsRequest(
            "http://callback.com/test",
            null,
            "Hello SMS"
        );

        // Act & Assert
        EventException exception = assertThrows(
            EventException.class,
            () -> EventRequestParsingService.createEventFromRequest(request)
        );
        assertEquals(EventDto.SMS_ERROR_MSG, exception.getMessage());
    }

    @Test
    void createEventFromRequest_WithSmsMissingMessage_ShouldThrowException() {
        // Arrange
        EventRequest request = createSmsRequest(
            "http://callback.com/test",
            "+1234567890",
            null
        );

        // Act & Assert
        EventException exception = assertThrows(
            EventException.class,
            () -> EventRequestParsingService.createEventFromRequest(request)
        );
        assertEquals(EventDto.SMS_ERROR_MSG, exception.getMessage());
    }

    @Test
    void createEventFromRequest_WithPushMissingDeviceId_ShouldThrowException() {
        // Arrange
        EventRequest request = createPushRequest(
            "http://callback.com/test",
            null,
            "Hello Push"
        );

        // Act & Assert
        EventException exception = assertThrows(
            EventException.class,
            () -> EventRequestParsingService.createEventFromRequest(request)
        );
        assertEquals(EventDto.PUSH_ERROR_MSG, exception.getMessage());
    }

    @Test
    void createEventFromRequest_WithPushMissingMessage_ShouldThrowException() {
        // Arrange
        EventRequest request = createPushRequest(
            "http://callback.com/test",
            "device-123",
            null
        );

        // Act & Assert
        EventException exception = assertThrows(
            EventException.class,
            () -> EventRequestParsingService.createEventFromRequest(request)
        );
        assertEquals(EventDto.PUSH_ERROR_MSG, exception.getMessage());
    }

    @Test
    void createEventFromRequest_WithUnsupportedEventType_ShouldThrowException() {
        // Arrange
        EventRequest request = new EventRequest();
        request.setEventType(null); // This will cause unsupported type
        request.setCallbackUrl("http://callback.com/test");
        request.setPayload(new HashMap<>());

        // Act & Assert
        EventException exception = assertThrows(
            EventException.class,
            () -> EventRequestParsingService.createEventFromRequest(request)
        );
        assertTrue(exception.getMessage().contains("Unsupported event type: " + request.getEventType()));
    }

    @Test
    void createEventFromRequest_WithNullPayload_ShouldThrowException() {
        // Arrange
        EventRequest request = new EventRequest();
        request.setEventType(EventType.EMAIL.name());
        request.setCallbackUrl("http://callback.com/test");
        request.setPayload(null); // Null payload

        // Act & Assert
        EventException ex= assertThrows(
            EventException.class,
            () -> EventRequestParsingService.createEventFromRequest(request)
        );
        assertEquals(ex.getLocalizedMessage(),EventErrorMessages.NULL_PAYLOAD);
    }

    @Test
    void createEventFromRequest_WithEmptyPayload_ShouldThrowException() {
        // Arrange
        EventRequest request = new EventRequest();
        request.setEventType(EventType.EMAIL.name());
        request.setCallbackUrl("http://callback.com/test");
        request.setPayload(new HashMap<>()); // Empty payload

        // Act & Assert
        EventException exception = assertThrows(
            EventException.class,
            () -> EventRequestParsingService.createEventFromRequest(request)
        );
        assertEquals(EventDto.EMAIL_ERROR_MSG, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideEmailRequests")
    void createEventFromRequest_WithVariousEmailInputs_ShouldHandleCorrectly(
            String recipient, String message, boolean shouldSucceed) {
        
        // Arrange
        EventRequest request = createEmailRequest(
            "http://callback.com/test",
            recipient,
            message
        );

        // Act & Assert
        if (shouldSucceed) {
            BaseEvent result = EventRequestParsingService.createEventFromRequest(request);
            assertNotNull(result);
            assertTrue(result instanceof EmailEvent);
        } else {
            assertThrows(
                EventException.class,
                () -> EventRequestParsingService.createEventFromRequest(request)
            );
        }
    }

    private static Stream<Arguments> provideEmailRequests() {
        return Stream.of(
            Arguments.of("test@example.com", "Hello", true),
            Arguments.of(null, "Hello", false),
            Arguments.of("test@example.com", null, false),
            Arguments.of("", "Hello", true),  // Empty string is valid (not null)
            Arguments.of("test@example.com", "", true)  // Empty string is valid
        );
    }

    // Helper methods to create test requests
    private EventRequest createEmailRequest(String callbackUrl, String recipient, String message) {
        EventRequest request = new EventRequest();
        request.setEventType(EventType.EMAIL.name());
        request.setCallbackUrl(callbackUrl);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("recipient", recipient);
        payload.put("message", message);
        request.setPayload(payload);
        
        return request;
    }

    private EventRequest createSmsRequest(String callbackUrl, String phoneNumber, String message) {
        EventRequest request = new EventRequest();
        request.setEventType(EventType.SMS.name());
        request.setCallbackUrl(callbackUrl);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("phoneNumber", phoneNumber);
        payload.put("message", message);
        request.setPayload(payload);
        
        return request;
    }

    private EventRequest createPushRequest(String callbackUrl, String deviceId, String message) {
        EventRequest request = new EventRequest();
        request.setEventType(EventType.PUSH.name());
        request.setCallbackUrl(callbackUrl);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("deviceId", deviceId);
        payload.put("message", message);
        request.setPayload(payload);
        
        return request;
    }
}