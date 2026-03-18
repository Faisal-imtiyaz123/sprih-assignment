package com.sprih_assignment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.models.EmailEvent;
import com.sprih_assignment.services.CallBackService;
import com.sprih_assignment.services.EventProcessorService;
import com.sprih_assignment.utils.enums.EventStatus;

public class CallBackServiceTest{

    @Autowired
    EventProcessorService eventProcessorService;
    @Autowired
    CallBackService callBackService;
@Test
void process_WithSuccess_ShouldSendCallbackWithSuccessStatus() {
    
    // Arrange
    BaseEvent event = new EmailEvent("http://callback.com", "test@example.com", "Hello");
    
    // Act
    eventProcessorService.process(event);
    
    // Assert
    verify(callBackService).sendCallback(event);
    assertEquals(EventStatus.COMPLETED, event.getStatus());
    assertNull(event.getErrorMessage());
}

@Test
void process_WithFailure_ShouldSendCallbackWithErrorStatus() {
    // Arrange
    BaseEvent event = new EmailEvent("http://callback.com", "test@example.com", "Hello");
    
    // Force failure (you might need to mock RANDOM or use reflection)
    
    // Act
    eventProcessorService.process(event);
    
    // Assert
    verify(callBackService).sendCallback(event);
    assertEquals(EventStatus.FAILED, event.getStatus());
    assertNotNull(event.getErrorMessage());
}
}
