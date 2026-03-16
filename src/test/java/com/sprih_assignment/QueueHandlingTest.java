// package com.sprih_assignment;

// import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.context.SpringBootTest;

// import com.sprih_assignment.models.EmailEvent;
// import com.sprih_assignment.services.eventQueueManagerService.EventQueueManager;

// @SpringBootTest
// public class QueueHandlingTest {
//     private EventQueueManager eventQueueManager;

//     QueueHandlingTest(EventQueueManager eventQueueManager){
//         this.eventQueueManager = eventQueueManager;
//     }

//     @Test
//     public void testFIFOOrder() {
//         EmailEvent event_1 = new EmailEvent("http://callback.com", "test@example.com", "Hello");
//         EmailEvent event_2 = new EmailEvent("http://callback.com", "test@example.com", "Hello");
//         String eventId_1 = eventQueueManager.addEvent(event_1);
//         String eventId_2 = eventQueueManager.addEvent(event_2);
//         System.out.println(eventId_1);
//         System.out.println(eventId_1);
//     }
// }