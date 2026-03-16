package com.sprih_assignment;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.sprih_assignment.models.EmailEvent;
import com.sprih_assignment.models.PushEvent;
import com.sprih_assignment.models.SmsEvent;
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

    @Test
    public void testSmsEventCreeation(){
        String eventId = UUID.randomUUID().toString();
        SmsEvent event = new SmsEvent("http://callback.com", "+911234567890", "Hello");
        
        assertEquals(eventId, event.getEventId());
        assertEquals(EventType.SMS, event.getEventType());
        assertEquals("+911234567890", event.getPhoneNumber());
        assertEquals(EventStatus.PENDING, event.getStatus());
    }
    @Test
    public void testPushEventCreeation(){
        String eventId = UUID.randomUUID().toString();
        PushEvent event = new PushEvent("http://callback.com", "abc-123-xyz", "Hello");
        
        assertEquals(eventId, event.getEventId());
        assertEquals(EventType.PUSH, event.getEventType());
        assertEquals("abc-123-xyz", event.getDeviceId());
        assertEquals(EventStatus.PENDING, event.getStatus());
    }
}