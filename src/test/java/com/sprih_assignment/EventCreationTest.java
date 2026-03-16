package com.sprih_assignment;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.sprih_assignment.models.EmailEvent;
import com.sprih_assignment.utils.enums.Events.EventStatus;
import com.sprih_assignment.utils.enums.Events.EventType;

@SpringBootTest
public class EventCreationTest {
    
    @Test
    public void testEmailEventCreation() {
        String eventId = UUID.randomUUID().toString();
        EmailEvent event = new EmailEvent("http://callback.com", "test@example.com", "Hello");
        
        assertEquals(eventId, event.getEventId());
        assertEquals(EventType.EMAIL, event.getEventType());
        assertEquals("test@example.com", event.getRecipient());
        assertEquals(EventStatus.PENDING, event.getStatus());
    }
}