package com.sprih_assignment.utils.shutdown;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import com.sprih_assignment.services.eventQueueManagerService.EventQueueManager;

@Slf4j
@Component
public class ShutdownHandler implements ApplicationListener<ContextClosedEvent> {
    
    private final EventQueueManager queueManager;
    
    public ShutdownHandler(EventQueueManager queueManager) {
        this.queueManager = queueManager;
    }
    
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("Received shutdown signal. Initiating graceful shutdown...");
        queueManager.shutdown();
        log.info("Shutdown handler completed.");
    }
}