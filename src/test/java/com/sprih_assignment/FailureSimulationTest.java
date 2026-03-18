package com.sprih_assignment;

import com.sprih_assignment.dto.response.event.AddEventResponse;
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
import java.util.concurrent.atomic.AtomicInteger;

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
    void test10PercentFailureRate() throws InterruptedException {
        // Arrange
        int totalEvents = 100; // Run 100 events to see the 10% failure rate
        CountDownLatch latch = new CountDownLatch(totalEvents);
        
        List<String> successfulEvents = new ArrayList<>();
        List<String> failedEvents = new ArrayList<>();
        
        // Create a custom processor to track results
        EventProcessorService trackingProcessor = new EventProcessorService(callBackService) {
            @Override
            public void process(BaseEvent event) {
                try {
                    super.process(event);
                    if (event.getStatus() == EventStatus.COMPLETED) {
                        successfulEvents.add(event.getEventId());
                    } else if (event.getStatus() == EventStatus.FAILED) {
                        failedEvents.add(event.getEventId());
                    }
                } finally {
                    latch.countDown();
                }
            }
        };
        
        // Inject our tracking processor (you might need to modify your code to allow this)
        // For testing, you could use ReflectionTestUtils or redesign to be more testable
        
        // Act - Add 100 events
        for (int i = 0; i < totalEvents; i++) {
            EmailEvent event = new EmailEvent(
                "http://callback.com",
                "user" + i + "@example.com",
                "Message " + i
            );
            eventQueueManager.addEvent(event);
        }
        
        // Wait for all events to process
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        assertTrue(completed, "All events should process within timeout");
        
        // Assert - Check that approximately 10% failed
        int totalProcessed = successfulEvents.size() + failedEvents.size();
        assertEquals(totalEvents, totalProcessed, "All events should be processed");
        
        double failureRate = (double) failedEvents.size() / totalProcessed;
        System.out.println("Success: " + successfulEvents.size());
        System.out.println("Failed: " + failedEvents.size());
        System.out.println("Failure rate: " + (failureRate * 100) + "%");
        
        // Should be around 10% (allow some margin for randomness)
        assertTrue(failureRate >= 0.05 && failureRate <= 0.15, 
            "Failure rate should be around 10% (±5%)");
    }
    
    @Test
    void testFailureRateOverMultipleRuns() {
        // Run multiple times to verify the 10% average
        int runs = 10;
        int eventsPerRun = 100;
        AtomicInteger totalFailures = new AtomicInteger(0);
        AtomicInteger totalSuccess = new AtomicInteger(0);
        
        for (int run = 0; run < runs; run++) {
            // This is simplified - in reality you'd need to reset state between runs
            List<String> successfulEvents = new ArrayList<>();
            List<String> failedEvents = new ArrayList<>();
            
            // Add events (simplified - you'd need proper async handling)
            for (int i = 0; i < eventsPerRun; i++) {
                EmailEvent event = new EmailEvent(
                    "http://callback.com",
                    "user" + i + "@example.com",
                    "Message " + i
                );
                eventQueueManager.addEvent(event);
            }
            
            // In real test, you'd wait for processing
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}
            
            // Record results (simplified)
            totalFailures.addAndGet(failedEvents.size());
            totalSuccess.addAndGet(successfulEvents.size());
        }
        
        int totalEvents = runs * eventsPerRun;
        double overallFailureRate = (double) totalFailures.get() / totalEvents;
        
        System.out.println("Total events: " + totalEvents);
        System.out.println("Total failures: " + totalFailures.get());
        System.out.println("Overall failure rate: " + (overallFailureRate * 100) + "%");
        
        // Should be close to 10%
        assertTrue(overallFailureRate >= 0.08 && overallFailureRate <= 0.12,
            "Overall failure rate should be around 10%");
    }
    
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