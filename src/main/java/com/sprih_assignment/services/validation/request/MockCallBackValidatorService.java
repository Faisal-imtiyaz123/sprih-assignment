package com.sprih_assignment.services.validation.request;

import org.springframework.stereotype.Service;

import com.sprih_assignment.dto.request.callback.CallBackRequest;
import com.sprih_assignment.utils.exceptions.callBack.CallBackException;
import com.sprih_assignment.utils.exceptions.callBack.CallBackExceptionMessages;

@Service
public class MockCallBackValidatorService {
    public static void validateAll(CallBackRequest callBackRequest){
        validate(callBackRequest);
    }
    public static void validate(CallBackRequest callback){
        boolean isStatusValid = callback.hasValidStatus();
        if(!isStatusValid)throw new CallBackException(CallBackExceptionMessages.INVALID_STATUS,callback);
    }
}
