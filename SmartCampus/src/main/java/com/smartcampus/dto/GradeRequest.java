package com.smartcampus.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Professor ID is required")
    private Long professorId;

    private String letterGrade;

    @DecimalMin(value = "0.0", message = "Grade points must be at least 0.0")
    @DecimalMax(value = "4.0", message = "Grade points must be at most 4.0")
    private Double gradePoints;

    @DecimalMin(value = "0.0", message = "Percentage score must be at least 0.0")
    @DecimalMax(value = "100.0", message = "Percentage score must be at most 100.0")
    private Double percentageScore;

    @DecimalMin(value = "0.0", message = "Assignment score must be at least 0.0")
    private Double assignmentScore;

    @DecimalMin(value = "0.0", message = "Midterm score must be at least 0.0")
    private Double midtermScore;

    @DecimalMin(value = "0.0", message = "Final exam score must be at least 0.0")
    private Double finalExamScore;

    @DecimalMin(value = "0.0", message = "Participation score must be at least 0.0")
    private Double participationScore;

    private String comments;
} 