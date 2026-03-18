package com.sprih_assignment.utils.annotations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.lang.annotation.*;

import com.sprih_assignment.config.swagger.EventRequestExamples;



@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Create and queue a new event")
@RequestBody(
    description = "Event request with type-specific payload",
    required = true,
    content = @Content(
        mediaType = "application/json",
        examples = {
            @ExampleObject(name = "📧 Email", value = EventRequestExamples.EMAIL_EVENT),
            @ExampleObject(name = "📱 SMS", value = EventRequestExamples.SMS_EVENT),
            @ExampleObject(name = "🔔 Push", value = EventRequestExamples.PUSH_EVENT)
        }
    )
)
// @ApiResponses({
//     @ApiResponse(
//         responseCode = "200",
//         description = "Event created successfully",
//         content = @Content(examples = @ExampleObject(value = SUCCESS_RESPONSE))
//     ),
//     @ApiResponse(
//         responseCode = "400",
//         description = "Invalid request",
//         content = @Content(examples = @ExampleObject(value = ERROR_RESPONSE))
//     ),
//     @ApiResponse(
//         responseCode = "503",
//         description = "Service unavailable"
//     )
// })
public @interface ReqExampleDocs {
}