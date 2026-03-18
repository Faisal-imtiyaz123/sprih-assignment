package com.sprih_assignment.utils.exceptions.callBack;

import com.sprih_assignment.dto.request.callback.CallBackRequest;

import lombok.Data;

@Data
public class CallBackException extends RuntimeException{
    private  CallBackRequest callBackRequest;

    public CallBackException(String message){
        super(message);
    }
    public CallBackException(String message, CallBackRequest callBackRequest){
        super(message);
        this.callBackRequest = callBackRequest;
    }
    
}
