package com.smartcampus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@Configuration
@ControllerAdvice
public class OpenApiExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleOpenApiException(Exception e) {
        if (e.getMessage() != null && e.getMessage().contains("OpenAPI")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"OpenAPI documentation generation failed: " + e.getMessage() + "\"}");
        }
        throw new RuntimeException(e);
    }
} 