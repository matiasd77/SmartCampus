package com.smartcampus.exception;

public class CourseNotFoundException extends RuntimeException {
    
    public CourseNotFoundException(String message) {
        super(message);
    }
    
    public CourseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static CourseNotFoundException withId(Long id) {
        return new CourseNotFoundException("Course not found with id: " + id);
    }
    
    public static CourseNotFoundException withCode(String code) {
        return new CourseNotFoundException("Course not found with code: " + code);
    }
} 