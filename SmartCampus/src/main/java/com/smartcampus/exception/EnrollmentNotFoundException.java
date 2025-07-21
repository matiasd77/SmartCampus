package com.smartcampus.exception;

public class EnrollmentNotFoundException extends RuntimeException {
    
    public EnrollmentNotFoundException(String message) {
        super(message);
    }
    
    public EnrollmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static EnrollmentNotFoundException withId(Long id) {
        return new EnrollmentNotFoundException("Enrollment not found with id: " + id);
    }
    
    public static EnrollmentNotFoundException withStudentAndCourse(Long studentId, Long courseId) {
        return new EnrollmentNotFoundException("Enrollment not found for student: " + studentId + " and course: " + courseId);
    }
} 