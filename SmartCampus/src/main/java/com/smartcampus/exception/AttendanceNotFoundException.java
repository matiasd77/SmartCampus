package com.smartcampus.exception;

import java.time.LocalDate;

public class AttendanceNotFoundException extends RuntimeException {
    
    public AttendanceNotFoundException(String message) {
        super(message);
    }
    
    public AttendanceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static AttendanceNotFoundException withId(Long id) {
        return new AttendanceNotFoundException("Attendance not found with id: " + id);
    }
    
    public static AttendanceNotFoundException withStudentAndCourseAndDate(Long studentId, Long courseId, LocalDate date) {
        return new AttendanceNotFoundException("Attendance not found for student: " + studentId + ", course: " + courseId + ", date: " + date);
    }
} 