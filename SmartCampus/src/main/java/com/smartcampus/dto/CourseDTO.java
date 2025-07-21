package com.smartcampus.dto;

import com.smartcampus.entity.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    private Long id;

    @NotBlank(message = "Course name is required")
    @Size(min = 2, max = 100, message = "Course name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Course code is required")
    @Size(min = 3, max = 20, message = "Course code must be between 3 and 20 characters")
    private String code;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Professor ID is required")
    private Long professorId;

    // Professor details for response
    private String professorName;
    private String professorEmail;

    @Size(max = 50, message = "Semester must not exceed 50 characters")
    private String semester;

    private Integer academicYear;

    private Integer credits;

    @Size(max = 20, message = "Schedule must not exceed 20 characters")
    private String schedule;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    private CourseStatus status;

    private Integer maxStudents;

    private Integer currentEnrollment;

    private Integer availableSeats;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Helper method to check if course is active
    public boolean isActive() {
        return status == CourseStatus.ACTIVE;
    }

    // Helper method to check if course has available seats
    public boolean hasAvailableSeats() {
        if (maxStudents == null) {
            return true; // No limit set
        }
        return currentEnrollment == null || currentEnrollment < maxStudents;
    }
} 