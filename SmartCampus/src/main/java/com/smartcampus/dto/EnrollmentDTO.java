package com.smartcampus.dto;

import com.smartcampus.entity.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {

    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    // Student details for response
    private String studentName;
    private String studentIdNumber;
    private String studentEmail;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    // Course details for response
    private String courseName;
    private String courseCode;
    private String courseDescription;
    private Integer courseCredits;
    private String courseSemester;
    private Integer courseAcademicYear;
    private String courseSchedule;
    private String courseLocation;
    private String professorName;

    private LocalDateTime enrollmentDate;

    private EnrollmentStatus status;

    private LocalDateTime dropDate;

    private String withdrawalReason;

    private String gradeLetter;

    private Double gradePoints;

    private Double attendancePercentage;

    private Boolean isActive;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Helper method to check if enrollment is active
    public boolean isActiveEnrollment() {
        return isActive != null && isActive && status == EnrollmentStatus.ENROLLED;
    }

    // Helper method to check if student can drop the course
    public boolean canDrop() {
        return isActiveEnrollment() && dropDate == null;
    }

    // Helper method to get semester info
    public String getSemesterInfo() {
        if (courseSemester != null && courseAcademicYear != null) {
            return courseSemester + " " + courseAcademicYear;
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

    // Helper method to get student info
    public String getStudentInfo() {
        if (studentIdNumber != null && studentName != null) {
            return studentIdNumber + " - " + studentName;
        }
        return null;
    }
} 