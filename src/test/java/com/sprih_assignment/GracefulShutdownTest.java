package com.sprih_assignment;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.event.ContextClosedEvent;

import com.sprih_assignment.services.core.events.EventQueueManager;
import com.sprih_assignment.utils.shutdown.ShutdownHandler;

import static org.mockito.Mockito.*;

@SpringBootTest
class GracefulShutdownTest {

    @Nested
    class UnitTests {
        
        @Test
        void shouldCallShutdownOnEvent() {
            EventQueueManager queueManager = mock(EventQueueManager.class);
            ShutdownHandler handler = new ShutdownHandler(queueManager);
            ContextClosedEvent event = mock(ContextClosedEvent.class);
            
            handler.onApplicationEvent(event);
            
            verify(queueManager).shutdown();
        }
        
        @Test
        void shouldHandleNullEvent() {
            EventQueueManager queueManager = mock(EventQueueManager.class);
            ShutdownHandler handler = new ShutdownHandler(queueManager);
            
            handler.onApplicationEvent(null);
            
            verify(queueManager).shutdown();
        }
        
        @Test
        void shouldHandleShutdownExceptions() {
            EventQueueManager queueManager = mock(EventQueueManager.class);
            doThrow(new RuntimeException("Shutdown failed")).when(queueManager).shutdown();
            ShutdownHandler handler = new ShutdownHandler(queueManager);
            ContextClosedEvent event = mock(ContextClosedEvent.class);
            
            // Should not throw exception
            handler.onApplicationEvent(event);
            
            verify(queueManager).shutdown();
        }
    }

    @Nested
    class IntegrationTests {

    }
}