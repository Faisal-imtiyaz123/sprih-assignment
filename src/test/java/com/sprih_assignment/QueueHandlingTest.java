package com.sprih_assignment;

import com.sprih_assignment.models.EmailEvent;
import com.sprih_assignment.models.PushEvent;
import com.sprih_assignment.models.SmsEvent;
import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.services.core.events.EventProcessorService;
import com.sprih_assignment.services.core.events.EventQueueManager;
import com.sprih_assignment.utils.enums.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
public class QueueHandlingTest {

    @Autowired
    private EventQueueManager eventQueueManager;

    @MockitoBean
    private EventProcessorService eventProcessorService;

    private List<String> processingOrder;
    private CountDownLatch latch;

    @BeforeEach
    void setUp() {
        processingOrder = new ArrayList<>();
        // Reset queue state if needed
    }

    @Test
    void testFIFOOrder_WithSameEventType() throws InterruptedException {
        // Arrange
        int eventCount = 5;
        latch = new CountDownLatch(eventCount);
        
        // Mock the processor to capture order
        doAnswer(invocation -> {
            BaseEvent event = invocation.getArgument(0);
            processingOrder.add(event.getEventId());
            latch.countDown();
            return null;
        }).when(eventProcessorService).process(any(BaseEvent.class));

        // Act - Add events in sequence
        List<String> expectedOrder = new ArrayList<>();
        for (int i = 1; i <= eventCount; i++) {
            EmailEvent event = new EmailEvent(
                "http://callback.com", 
                "test" + i + "@example.com", 
                "Message " + i
            );
            String eventId = eventQueueManager.addEvent(event).getEventId();
            expectedOrder.add(eventId);
        }

        // Wait for processing to complete
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "Events should process within timeout");

        // Assert - Verify FIFO order
        assertEquals(expectedOrder, processingOrder, 
            "Events should be processed in the same order they were added");
    }

    @Test
    void testFIFOOrder_WithMultipleEventTypes() throws InterruptedException {
        // Arrange
        int eventsPerType = 3;
        latch = new CountDownLatch(eventsPerType * 3); // 3 event types
        
        // Track order per event type
        List<String> emailOrder = new ArrayList<>();
        List<String> smsOrder = new ArrayList<>();
        List<String> pushOrder = new ArrayList<>();
        
        doAnswer(invocation -> {
            BaseEvent event = invocation.getArgument(0);
            switch (event.getEventType()) {
                case EMAIL:
                    emailOrder.add(event.getEventId());
                    break;
                case SMS:
                    smsOrder.add(event.getEventId());
                    break;
                case PUSH:
                    pushOrder.add(event.getEventId());
                    break;
            }
            latch.countDown();
            return null;
        }).when(eventProcessorService).process(any(BaseEvent.class));

        // Act - Add events in interleaved order
        List<String> expectedEmailOrder = new ArrayList<>();
        List<String> expectedSmsOrder = new ArrayList<>();
        List<String> expectedPushOrder = new ArrayList<>();
        
        for (int i = 1; i <= eventsPerType; i++) {
            // Add email
            EmailEvent email = new EmailEvent(
                "http://callback.com", 
                "email" + i + "@test.com", 
                "Email " + i
            );
            expectedEmailOrder.add(eventQueueManager.addEvent(email).getEventId());
            
            // Add SMS
            SmsEvent sms = new SmsEvent(
                "http://callback.com", 
                "+1234567" + i, 
                "SMS " + i
            );
            expectedSmsOrder.add(eventQueueManager.addEvent(sms).getEventId());
            
            // Add Push
            PushEvent push = new PushEvent(
                "http://callback.com", 
                "device" + i, 
                "Push " + i
            );
            expectedPushOrder.add(eventQueueManager.addEvent(push).getEventId());
        }

        // Wait for processing
        boolean completed = latch.await(15, TimeUnit.SECONDS);
        assertTrue(completed, "Events should process within timeout");

        // Assert - Each queue maintains FIFO independently
        assertEquals(expectedEmailOrder, emailOrder, 
            "Email events should maintain FIFO order");
        assertEquals(expectedSmsOrder, smsOrder, 
            "SMS events should maintain FIFO order");
        assertEquals(expectedPushOrder, pushOrder, 
            "Push events should maintain FIFO order");
    }

    @Test
    void testFIFOOrder_WithDelayedProcessing() throws InterruptedException {
        // Arrange
        latch = new CountDownLatch(3);
        List<String> orderWithDelay = new ArrayList<>();
        
        // Simulate varying processing times
        doAnswer(invocation -> {
            BaseEvent event = invocation.getArgument(0);
            // Simulate different processing times
            if (event.getEventId().contains("slow")) {
                Thread.sleep(500);
            }
            orderWithDelay.add(event.getEventId());
            latch.countDown();
            return null;
        }).when(eventProcessorService).process(any(BaseEvent.class));

        // Act - Add events with one "slow" event
        EmailEvent event1 = new EmailEvent("http://callback.com", "fast1@test.com", "Fast 1");
        EmailEvent event2 = new EmailEvent("http://callback.com", "slow@test.com", "Slow");
        EmailEvent event3 = new EmailEvent("http://callback.com", "fast2@test.com", "Fast 2");
        
        String id1 = eventQueueManager.addEvent(event1).getEventId();
        String id2 = eventQueueManager.addEvent(event2).getEventId();
        String id3 = eventQueueManager.addEvent(event3).getEventId();
        
        List<String> expectedOrder = List.of(id1, id2, id3);

        // Wait for processing
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "Events should process within timeout");

        // Assert - Order should be preserved even with slow processing
        assertEquals(expectedOrder, orderWithDelay,
            "FIFO order should be preserved even with varying processing times");
    }

    // @Test
    // void testFIFOOrder_WithQueueFull() {
    //     // Arrange
    //     int queueSize = eventQueueManager.getQueueSize(EventType.EMAIL);
        
    //     // Act & Assert - Add more events than queue capacity
    //     assertThrows(RuntimeException.class, () -> {
    //         for (int i = 0; i <= queueSize+1; i++) {
    //             EmailEvent event = new EmailEvent(
    //                 "http://callback.com", 
    //                 "test@example.com", 
    //                 "Message " + i
    //             );
    //             eventQueueManager.addEvent(event);
    //         }
    //     });
    // }

    // @Test
    // void testFIFOOrder_WithMixedSuccessAndFailure() throws InterruptedException {
    //     // Arrange
    //     latch = new CountDownLatch(4);
    //     List<String> completedOrder = new ArrayList<>();
    //     List<String> failedEvents = new ArrayList<>();
        
    //     doAnswer(invocation -> {
    //         BaseEvent event = invocation.getArgument(0);
    //         if (event.getEventId().contains("fail")) {
    //             failedEvents.add(event.getEventId());
    //             throw new RuntimeException("Simulated failure");
    //         }
    //         completedOrder.add(event.getEventId());
    //         latch.countDown();
    //         return null;
    //     }).when(eventProcessorService).process(any(BaseEvent.class));

    //     // Act
    //     EmailEvent event1 = new EmailEvent("http://callback.com", "success1@test.com", "Success 1");
    //     EmailEvent event2 = new EmailEvent("http://callback.com", "fail@test.com", "Fail");
    //     EmailEvent event3 = new EmailEvent("http://callback.com", "success2@test.com", "Success 2");
    //     EmailEvent event4 = new EmailEvent("http://callback.com", "success3@test.com", "Success 3");
        
    //     String id1 = eventQueueManager.addEvent(event1).getEventId();
    //     String id2 = eventQueueManager.addEvent(event2).getEventId();
    //     String id3 = eventQueueManager.addEvent(event3).getEventId();
    //     String id4 = eventQueueManager.addEvent(event4).getEventId();

    //     // Wait for processing
    //     Thread.sleep(2000); // Wait for failures to be handled

    //     // Assert
    //     assertTrue(failedEvents.contains(id2), "Event 2 should have failed");
    //     assertEquals(List.of(id1, id3, id4), completedOrder,
    //         "Successful events should maintain order despite failures");
    // }

    @Test
    void testFIFOOrder_WithEmptyQueue() {
        // Act
        int size = eventQueueManager.getQueueSize(EventType.EMAIL);
        
        // Assert
        assertEquals(0, size, "Queue should be empty initially");
    }

    @Test
    void testFIFOOrder_VerifyEventIds() {
        // Arrange
        EmailEvent event1 = new EmailEvent("http://callback.com", "test1@example.com", "Hello 1");
        EmailEvent event2 = new EmailEvent("http://callback.com", "test2@example.com", "Hello 2");
        
        // Act
        String eventId1 = eventQueueManager.addEvent(event1).getEventId();
        String eventId2 = eventQueueManager.addEvent(event2).getEventId();
        
        // Assert
        assertNotNull(eventId1, "Event ID should not be null");
        assertNotNull(eventId2, "Event ID should not be null");
        assertNotEquals(eventId1, eventId2, "Event IDs should be unique");
    }

    @Test
    void testFIFOOrder_ConcurrentAccess() throws InterruptedException {
        // Arrange
        int threadCount = 10;
        int eventsPerThread = 5;
        latch = new CountDownLatch(threadCount * eventsPerThread);
        List<String> allEvents = new ArrayList<>();
        
        doAnswer(invocation -> {
            BaseEvent event = invocation.getArgument(0);
            synchronized (allEvents) {
                allEvents.add(event.getEventId());
            }
            latch.countDown();
            return null;
        }).when(eventProcessorService).process(any(BaseEvent.class));

        // Act - Multiple threads adding events
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < eventsPerThread; j++) {
                    EmailEvent event = new EmailEvent(
                        "http://callback.com", 
                        "thread" + threadNum + "@test.com", 
                        "Message " + j
                    );
                    eventQueueManager.addEvent(event);
                }
            });
            threads[i].start();
        }

        // Wait for all threads to finish adding
        for (Thread thread : threads) {
            thread.join();
        }

        // Wait for processing
        boolean completed = latch.await(20, TimeUnit.SECONDS);
        assertTrue(completed, "All events should process");

        // Assert - Verify no data corruption
        assertEquals(threadCount * eventsPerThread, allEvents.size(),
            "All events should be processed");
    }
}