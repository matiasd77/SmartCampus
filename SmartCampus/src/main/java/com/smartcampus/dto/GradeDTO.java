package com.smartcampus.dto;

import com.smartcampus.entity.GradeStatus;
import com.smartcampus.entity.GradeType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeDTO {

    private Long id;

    @NotNull(message = "Enrollment ID is required")
    private Long enrollmentId;

    // Enrollment details for response
    private Long studentId;
    private String studentName;
    private String studentIdNumber;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private String professorName;

    @NotNull(message = "Grade value is required")
    @DecimalMin(value = "0.0", message = "Grade must be at least 0.0")
    @DecimalMax(value = "100.0", message = "Grade must be at most 100.0")
    private Double gradeValue;

    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;

    private LocalDateTime dateAssigned;

    private GradeType gradeType;

    @Size(max = 10, message = "Grade letter must not exceed 10 characters")
    private String gradeLetter;

    @DecimalMin(value = "0.0", message = "Grade points must be at least 0.0")
    @DecimalMax(value = "4.0", message = "Grade points must be at most 4.0")
    private Double gradePoints;

    private GradeStatus status;

    private Boolean isFinal;

    private Double weight;

    @Size(max = 100, message = "Assignment name must not exceed 100 characters")
    private String assignmentName;

    @Size(max = 50, message = "Assignment type must not exceed 50 characters")
    private String assignmentType;

    private Double maxPoints;

    private Boolean curveApplied;

    private Double curveValue;

    @Size(max = 200, message = "Feedback must not exceed 200 characters")
    private String feedback;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Helper method to calculate percentage
    public Double getPercentage() {
        if (gradeValue == null || maxPoints == null || maxPoints == 0) {
            return null;
        }
        return (gradeValue / maxPoints) * 100.0;
    }

    // Helper method to get letter grade based on percentage
    public String calculateLetterGrade() {
        if (gradeValue == null || maxPoints == null || maxPoints == 0) {
            return null;
        }
        
        double percentage = getPercentage();
        if (percentage >= 93.0) return "A";
        else if (percentage >= 90.0) return "A-";
        else if (percentage >= 87.0) return "B+";
        else if (percentage >= 83.0) return "B";
        else if (percentage >= 80.0) return "B-";
        else if (percentage >= 77.0) return "C+";
        else if (percentage >= 73.0) return "C";
        else if (percentage >= 70.0) return "C-";
        else if (percentage >= 67.0) return "D+";
        else if (percentage >= 63.0) return "D";
        else if (percentage >= 60.0) return "D-";
        else return "F";
    }

    // Helper method to get grade points based on letter grade
    public Double calculateGradePoints() {
        String letter = gradeLetter != null ? gradeLetter : calculateLetterGrade();
        if (letter == null) return null;
        
        switch (letter.toUpperCase()) {
            case "A": return 4.0;
            case "A-": return 3.7;
            case "B+": return 3.3;
            case "B": return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C": return 2.0;
            case "C-": return 1.7;
            case "D+": return 1.3;
            case "D": return 1.0;
            case "D-": return 0.7;
            case "F": return 0.0;
            default: return null;
        }
    }

    // Helper method to check if grade is passing
    public boolean isPassing() {
        if (gradeValue == null || maxPoints == null || maxPoints == 0) {
            return false;
        }
        double percentage = getPercentage();
        return percentage >= 60.0;
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
} 