package com.sprih_assignment.utils.error.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sprih_assignment.controllers.EventController;
import com.sprih_assignment.dto.request.callback.CallBackRequest;
import com.sprih_assignment.dto.request.event.EventRequest;
import com.sprih_assignment.dto.response.event.EventErrorResponse;
import com.sprih_assignment.dto.response.event.EventResponse;
import com.sprih_assignment.dto.response.event.EventErrorResponse.Status;
import com.sprih_assignment.models.BaseEvent;
import com.sprih_assignment.models.EventFactory;
import com.sprih_assignment.services.core.callback.CallBackService;
import com.sprih_assignment.utils.enums.EventType;
import com.sprih_assignment.utils.exceptions.callBack.CallBackException;
import com.sprih_assignment.utils.exceptions.event.EventException;

import jakarta.servlet.http.HttpServletRequest;


@Slf4j
@ControllerAdvice(assignableTypes = EventController.class)
@AllArgsConstructor
public class EventControllerErrorHandler {

    private final CallBackService callBackService;
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<EventResponse> handleIllegalStateException(IllegalStateException e){
        log.error("System not accepting events: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new EventResponse(null, e.getMessage()));
    }
    @ExceptionHandler(EventException.class)
    public ResponseEntity<EventErrorResponse> handleEventExceptions(EventException e){
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(new EventErrorResponse(Status.ERROR,e.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<EventErrorResponse> handleException(RuntimeException e){
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(new EventErrorResponse(Status.ERROR, e.getMessage()));
    }
    @ExceptionHandler(CallBackException.class)
    public ResponseEntity<CallBackRequest> handleCallBackException(CallBackException e){
        CallBackRequest callBackRequest = e.getCallBackRequest();
        callBackRequest.setErrorMessage(e.getMessage());
        callBackRequest.setStatus(null);
        return ResponseEntity.badRequest().body(callBackRequest);
    }
    public BaseEvent handleIllegalArgumentException(String errorMsg, EventType type){
        return new EventFactory().createEventWithErrorMsg(type);
    }
}
