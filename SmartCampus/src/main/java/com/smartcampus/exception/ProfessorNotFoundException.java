package com.smartcampus.exception;

public class ProfessorNotFoundException extends RuntimeException {
    
    public ProfessorNotFoundException(String message) {
        super(message);
    }
    
    public ProfessorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static ProfessorNotFoundException withId(Long id) {
        return new ProfessorNotFoundException("Professor not found with id: " + id);
    }
    
    public static ProfessorNotFoundException withEmail(String email) {
        return new ProfessorNotFoundException("Professor not found with email: " + email);
    }
} 