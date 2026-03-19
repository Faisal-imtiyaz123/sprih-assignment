package com.sprih_assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.models.EmailEvent;
import com.sprih_assignment.services.core.callback.CallBackService;
import com.sprih_assignment.services.core.events.EventProcessorService;
import com.sprih_assignment.utils.enums.EventStatus;

@SpringBootTest 
public class CallBackServiceTest {

    @MockitoSpyBean
    private EventProcessorService eventProcessorService;
    
    @MockitoSpyBean
    private CallBackService callBackService;
    
    private final String callBackUrl = "http://localhost:8080/api/mock-callback";

    @Test
    void callBackIsSent_inFailure_orSuccess() {
        BaseEvent event = new EmailEvent(callBackUrl, "test@example.com", "Hello");
        eventProcessorService.process(event);
        verify(callBackService).sendCallback(event);
    }
}