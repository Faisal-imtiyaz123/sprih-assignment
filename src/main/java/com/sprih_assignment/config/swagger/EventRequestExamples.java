package com.sprih_assignment.config.swagger;

public final class EventRequestExamples {
    
    private EventRequestExamples() {} 
    
    public static final String EMAIL_EVENT = """
        {
            "eventType": "EMAIL",
            "callbackUrl": "https://webhook.site/your-unique-id",
            "payload": {
                "recipient": "john.doe@example.com",
                "message": "Welcome to our platform!",
                "subject": "Welcome Email"
            }
        }
        """;
    
    public static final String SMS_EVENT = """
        {
            "eventType": "SMS",
            "callbackUrl": "https://webhook.site/your-unique-id",
            "payload": {
                "phoneNumber": "+1234567890",
                "message": "Your verification code is 123456"
            }
        }
        """;
    
    public static final String PUSH_EVENT = """
        {
            "eventType": "PUSH",
            "callbackUrl": "https://webhook.site/your-unique-id",
            "payload": {
                "deviceToken": "f7d8e9a0b1c2d3e4f5g6h7i8j9k0l1m2",
                "message": "You have a new message",
                "title": "New Alert"
            }
        }
        """;
    
    public static final String SUCCESS_RESPONSE = """
        {
            "eventId": "evt_123abc456def",
            "message": "Event accepted for processing."
        }
        """;
    
    public static final String ERROR_RESPONSE = """
        {
            "status": "ERROR",
            "errorMessage": "Missing required fields for EMAIL"
        }
        """;
}