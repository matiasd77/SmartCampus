package com.smartcampus.dto;

import com.smartcampus.entity.AttendanceStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {

    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    // Student details for response
    private String studentName;
    private String studentIdNumber;
    private String studentEmail;

    // Course details for response
    private String courseName;
    private String courseCode;
    private String professorName;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Status is required")
    private AttendanceStatus status;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    @Size(max = 100, message = "Session name must not exceed 100 characters")
    private String sessionName;

    @Size(max = 50, message = "Session type must not exceed 50 characters")
    private String sessionType;

    private Integer sessionDuration; // in minutes

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private Integer lateMinutes;

    private Integer earlyDepartureMinutes;

    private Boolean isMakeup;

    private LocalDate makeupDate;

    @Size(max = 200, message = "Makeup reason must not exceed 200 characters")
    private String makeupReason;

    private Boolean isVerified;

    @Size(max = 100, message = "Verified by must not exceed 100 characters")
    private String verifiedBy;

    private LocalDateTime verificationDate;

    @Size(max = 200, message = "Verification notes must not exceed 200 characters")
    private String verificationNotes;

    private Boolean isExcused;

    @Size(max = 200, message = "Excuse reason must not exceed 200 characters")
    private String excuseReason;

    @Size(max = 100, message = "Excuse approved by must not exceed 100 characters")
    private String excuseApprovedBy;

    private LocalDateTime excuseApprovalDate;

    @Size(max = 200, message = "Excuse notes must not exceed 200 characters")
    private String excuseNotes;

    private Double attendancePercentage;

    private Boolean isRequired;

    private Boolean isCountedTowardsGrade;

    private Double gradeImpact;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Helper method to check if attendance is present
    public boolean isPresent() {
        return AttendanceStatus.PRESENT.equals(status);
    }

    // Helper method to check if attendance is absent
    public boolean isAbsent() {
        return AttendanceStatus.ABSENT.equals(status);
    }

    // Helper method to check if attendance is excused
    public boolean isExcused() {
        return AttendanceStatus.EXCUSED.equals(status) || Boolean.TRUE.equals(isExcused);
    }

    // Helper method to check if attendance is late
    public boolean isLate() {
        return lateMinutes != null && lateMinutes > 0;
    }

    // Helper method to check if attendance is early departure
    public boolean isEarlyDeparture() {
        return earlyDepartureMinutes != null && earlyDepartureMinutes > 0;
    }

    // Helper method to check if attendance is makeup
    public boolean isMakeup() {
        return Boolean.TRUE.equals(isMakeup);
    }

    // Helper method to check if attendance is verified
    public boolean isVerified() {
        return Boolean.TRUE.equals(isVerified);
    }

    // Helper method to check if attendance is required
    public boolean isRequired() {
        return Boolean.TRUE.equals(isRequired);
    }

    // Helper method to check if attendance counts towards grade
    public boolean isCountedTowardsGrade() {
        return Boolean.TRUE.equals(isCountedTowardsGrade);
    }

    // Helper method to get student info
    public String getStudentInfo() {
        if (studentIdNumber != null && studentName != null) {
            return studentIdNumber + " - " + studentName;
        }
        return null;
    }

    // Helper method to get course info
    public String getCourseInfo() {
        if (courseCode != null && courseName != null) {
            return courseCode + " - " + courseName;
        }
        return null;
    }

    // Helper method to calculate attendance duration
    public Integer getAttendanceDuration() {
        if (checkInTime != null && checkOutTime != null) {
            return (int) java.time.Duration.between(checkInTime, checkOutTime).toMinutes();
        }
        return sessionDuration;
    }

    // Helper method to check if attendance is on time
    public boolean isOnTime() {
        return lateMinutes == null || lateMinutes == 0;
    }

    // Helper method to check if attendance is full session
    public boolean isFullSession() {
        if (sessionDuration == null) return true;
        Integer actualDuration = getAttendanceDuration();
        return actualDuration != null && actualDuration >= sessionDuration * 0.75; // 75% of session
    }
} 