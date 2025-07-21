package com.smartcampus.exception;

public class ProfileUpdateException extends RuntimeException {
    
    public ProfileUpdateException(String message) {
        super(message);
    }
    
    public ProfileUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
} 