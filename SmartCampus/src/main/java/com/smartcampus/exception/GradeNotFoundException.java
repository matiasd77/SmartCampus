package com.smartcampus.exception;

public class GradeNotFoundException extends RuntimeException {
    
    public GradeNotFoundException(String message) {
        super(message);
    }
    
    public GradeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static GradeNotFoundException withId(Long id) {
        return new GradeNotFoundException("Grade not found with id: " + id);
    }
    
    public static GradeNotFoundException withEnrollmentId(Long enrollmentId) {
        return new GradeNotFoundException("Grade not found for enrollment: " + enrollmentId);
    }
} 