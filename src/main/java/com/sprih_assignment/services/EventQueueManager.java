package com.sprih_assignment.services;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.utils.enums.Events.EventType;

import java.util.Map;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class EventQueueManager {
    
    private final Map<EventType, BlockingQueue<BaseEvent>> queues = new ConcurrentHashMap<>();
    private final Map<EventType, ExecutorService> executors = new ConcurrentHashMap<>();
    private final EventProcessor eventProcessor;
    private final AtomicBoolean acceptingNewEvents = new AtomicBoolean(true);
    
    public EventQueueManager(EventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }
    
    @PostConstruct
    public void initialize() {
        // Initialize queues and executors for each event type
        for (EventType type : EventType.values()) {
            queues.put(type, new LinkedBlockingQueue<>());
            
            // Create single thread executor for FIFO processing
            ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
                Thread thread = new Thread(r);
                thread.setName("processor-" + type.name().toLowerCase());
                thread.setDaemon(false);
                return thread;
            });
            
            executors.put(type, executor);
            
            // Start processor for this queue
            startQueueProcessor(type, queues.get(type), executor);
        }
        
        log.info("Event queue manager initialized with {} queues", EventType.values().length);
    }
    
    private void startQueueProcessor(EventType type, 
                                     BlockingQueue<BaseEvent> queue, 
                                     ExecutorService executor) {
        executor.submit(() -> {
            log.info("Started processor for queue: {}", type);
            
            while (acceptingNewEvents.get() || !queue.isEmpty()) {
                try {
                    BaseEvent event = queue.poll(1, TimeUnit.SECONDS);
                    if (event != null) {
                        eventProcessor.process(event);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("Processor for {} interrupted", type);
                    break;
                } catch (Exception e) {
                    log.error("Error processing event in queue: {}", type, e);
                }
            }
            
            log.info("Processor for {} shutting down. Queue empty: {}", type, queue.isEmpty());
        });
    }
    
    public String addEvent(BaseEvent event) {
        if (!acceptingNewEvents.get()) {
            throw new IllegalStateException("System is shutting down. Not accepting new events.");
        }
        
        BlockingQueue<BaseEvent> queue = queues.get(event.getEventType());
        if (queue == null) {
            throw new IllegalArgumentException("No queue found for event type: " + event.getEventType());
        }
        
        queue.offer(event);
        log.info("Event {} added to {} queue. Queue size: {}", 
                 event.getEventId(), event.getEventType(), queue.size());
        
        return event.getEventId();
    }
    
    @PreDestroy
    public void shutdown() {
        log.info("Initiating graceful shutdown...");
        acceptingNewEvents.set(false);
        
        // Wait for queues to empty
        boolean allQueuesEmpty;
        int maxWaitTime = 60; // Maximum wait time in seconds
        
        for (int i = 0; i < maxWaitTime; i++) {
            allQueuesEmpty = queues.values().stream().allMatch(BlockingQueue::isEmpty);
            
            if (allQueuesEmpty) {
                log.info("All queues are empty. Proceeding with shutdown.");
                break;
            }
            
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        // Shutdown executors
        executors.values().forEach(executor -> {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        });
        
        log.info("Graceful shutdown completed");
    }
    
    // For testing purposes
    public int getQueueSize(EventType type) {
        BlockingQueue<BaseEvent> queue = queues.get(type);
        return queue != null ? queue.size() : 0;
    }
    
    public boolean isAcceptingNewEvents() {
        return acceptingNewEvents.get();
    }
}