package com.smartcampus.exception;

import com.smartcampus.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Resource not found", ex.getMessage()));
    }

    @ExceptionHandler(ProfessorNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleProfessorNotFoundException(ProfessorNotFoundException ex) {
        log.warn("Professor not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Professor not found", ex.getMessage()));
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleCourseNotFoundException(CourseNotFoundException ex) {
        log.warn("Course not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Course not found", ex.getMessage()));
    }

    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleEnrollmentNotFoundException(EnrollmentNotFoundException ex) {
        log.warn("Enrollment not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Enrollment not found", ex.getMessage()));
    }

    @ExceptionHandler(GradeNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleGradeNotFoundException(GradeNotFoundException ex) {
        log.warn("Grade not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Grade not found", ex.getMessage()));
    }

    @ExceptionHandler(AttendanceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleAttendanceNotFoundException(AttendanceNotFoundException ex) {
        log.warn("Attendance not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Attendance not found", ex.getMessage()));
    }

    @ExceptionHandler(AnnouncementNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleAnnouncementNotFoundException(AnnouncementNotFoundException ex) {
        log.warn("Announcement not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Announcement not found", ex.getMessage()));
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotificationNotFoundException(NotificationNotFoundException ex) {
        log.warn("Notification not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Notification not found", ex.getMessage()));
    }

    @ExceptionHandler(PasswordChangeException.class)
    public ResponseEntity<ApiResponse<String>> handlePasswordChangeException(PasswordChangeException ex) {
        log.warn("Password change failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Password change failed", ex.getMessage()));
    }

    @ExceptionHandler(ProfileUpdateException.class)
    public ResponseEntity<ApiResponse<String>> handleProfileUpdateException(ProfileUpdateException ex) {
        log.warn("Profile update failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Profile update failed", ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Bad credentials: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid email or password", "Please check your credentials and try again."));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.warn("Username not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid email or password", "Please check your credentials and try again."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error", "An unexpected error occurred"));
    }
} 