package com.sprih_assignment.services.validation.request;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.sprih_assignment.dto.request.event.EventRequest;
import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.models.EmailEvent;
import com.sprih_assignment.models.PushEvent;
import com.sprih_assignment.models.SmsEvent;
import com.sprih_assignment.utils.exceptions.event.EventErrorMessages;
import com.sprih_assignment.utils.exceptions.event.EventException;
import com.sprih_assignment.utils.interfaces.EventDto;


@Service
public class EventRequestParsingService {

     public static BaseEvent createEventFromRequest(EventRequest request) {
        Map<String, Object> payload = request.getPayload();
        if(payload==null)throw new EventException(EventErrorMessages.NULL_PAYLOAD);
        switch (request.getEventType()) {
            case EMAIL:
                String recipient = (String) payload.get("recipient");
                String emailMessage = (String) payload.get("message");
                
                if (recipient == null || emailMessage == null) {
                    throw new EventException(EventDto.EMAIL_ERROR_MSG);
                }
                
                return new EmailEvent(
                    request.getCallbackUrl(),
                    recipient,
                    emailMessage
                );
                
            case SMS:
                String phoneNumber = (String) payload.get("phoneNumber");
                String smsMessage = (String) payload.get("message");
                
                if (phoneNumber == null || smsMessage == null) {
                    throw new EventException(EventDto.SMS_ERROR_MSG);
                }
                
                return new SmsEvent(
                    request.getCallbackUrl(),
                    phoneNumber,
                    smsMessage
                );
                
            case PUSH:
                String deviceId = (String) payload.get("deviceId");
                String pushMessage = (String) payload.get("message");
                
                if (deviceId == null || pushMessage == null) {
                    throw new EventException(EventDto.PUSH_ERROR_MSG);
                }
                
                return new PushEvent(
                    request.getCallbackUrl(),
                    deviceId,
                    pushMessage
                );
                
            default:
                throw new EventException("Unsupported event type: " + request.getEventType());
        }
    }
}
