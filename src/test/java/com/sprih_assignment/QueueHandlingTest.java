// package com.sprih_assignment;

// import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.context.SpringBootTest;

// import com.sprih_assignment.models.EmailEvent;
// import com.sprih_assignment.services.EventQueueManager;

// @SpringBootTest
// public class QueueHandlingTest {

//     @Test
//     public void testFIFOOrder() {
//         EventQueueManager queueManager = new EventQueueManager(mockProcessor);
        
//         String eventId1 = queueManager.addEvent(new EmailEvent("url", "a@b.com", "msg1"));
//         String eventId2 = queueManager.addEvent(new EmailEvent( "url", "a@b.com", "msg2"));
        
//         // Verify order (implementation depends on how you expose queues for testing)
//         // This would need additional test hooks
//     }
// }