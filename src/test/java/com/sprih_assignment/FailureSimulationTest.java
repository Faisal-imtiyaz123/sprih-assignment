package com.sprih_assignment;

import com.sprih_assignment.models.EmailEvent;
import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.services.core.callback.CallBackService;
import com.sprih_assignment.services.core.events.EventProcessorService;
import com.sprih_assignment.services.core.events.EventQueueManager;
import com.sprih_assignment.utils.enums.EventStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FailureSimulationTest {
    @Autowired
    private CallBackService callBackService;
    @Autowired
    private EventQueueManager eventQueueManager;

    @Autowired
    private EventProcessorService eventProcessorService;

    @Test
    void testEventsHaveCorrectStatusAfterFailure() throws InterruptedException {
        // Arrange
        CountDownLatch latch = new CountDownLatch(10);
        List<EventStatus> finalStatuses = new ArrayList<>();
        
        // Create a processor that captures final status
        EventProcessorService statusProcessor = new EventProcessorService(null) {
            @Override
            public void process(BaseEvent event) {
                try {
                    super.process(event);
                } finally {
                    finalStatuses.add(event.getStatus());
                    latch.countDown();
                }
            }
        };
        
        // Act
        for (int i = 0; i < 10; i++) {
            EmailEvent event = new EmailEvent(
                "http://callback.com",
                "user" + i + "@example.com",
                "Message " + i
            );
            eventQueueManager.addEvent(event);
        }
        
        // Wait for processing
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(completed);
        
        // Assert
        long completedCount = finalStatuses.stream()
            .filter(status -> status == EventStatus.COMPLETED)
            .count();
        long failedCount = finalStatuses.stream()
            .filter(status -> status == EventStatus.FAILED)
            .count();
        
        System.out.println("COMPLETED: " + completedCount);
        System.out.println("FAILED: " + failedCount);
        
        // Verify all events have a final status
        assertEquals(10, finalStatuses.size(), "All events should have a status");
        assertTrue(failedCount > 0 || completedCount > 0, "Should have some results");
    }
}