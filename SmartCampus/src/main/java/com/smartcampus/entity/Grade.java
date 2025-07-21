package com.smartcampus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Enrollment is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true)
    private Enrollment enrollment;

    @NotNull(message = "Grade value is required")
    @DecimalMin(value = "0.0", message = "Grade must be at least 0.0")
    @DecimalMax(value = "100.0", message = "Grade must be at most 100.0")
    @Column(name = "grade_value", nullable = false)
    private Double gradeValue;

    @Size(max = 500, message = "Comment must not exceed 500 characters")
    @Column(columnDefinition = "TEXT")
    private String comment;

    @NotNull(message = "Date assigned is required")
    @Column(name = "date_assigned", nullable = false)
    private LocalDateTime dateAssigned;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_type")
    private GradeType gradeType;

    @Size(max = 10, message = "Grade letter must not exceed 10 characters")
    @Column(name = "grade_letter")
    private String gradeLetter;

    @DecimalMin(value = "0.0", message = "Grade points must be at least 0.0")
    @DecimalMax(value = "4.0", message = "Grade points must be at most 4.0")
    @Column(name = "grade_points")
    private Double gradePoints;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private GradeStatus status;

    @Column(name = "is_final")
    private Boolean isFinal;

    @Column(name = "weight")
    private Double weight;

    @Size(max = 100, message = "Assignment name must not exceed 100 characters")
    @Column(name = "assignment_name")
    private String assignmentName;

    @Size(max = 50, message = "Assignment type must not exceed 50 characters")
    @Column(name = "assignment_type")
    private String assignmentType;

    @Column(name = "max_points")
    private Double maxPoints;

    @Column(name = "curve_applied")
    private Boolean curveApplied;

    @Column(name = "curve_value")
    private Double curveValue;

    @Size(max = 200, message = "Feedback must not exceed 200 characters")
    @Column(name = "feedback")
    private String feedback;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
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
        if (enrollment != null && enrollment.getStudent() != null) {
            return enrollment.getStudent().getStudentId() + " - " + enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName();
        }
        return null;
    }

    // Helper method to get course info
    public String getCourseInfo() {
        if (enrollment != null && enrollment.getCourse() != null) {
            return enrollment.getCourse().getCode() + " - " + enrollment.getCourse().getName();
        }
        return null;
    }

    // Helper method to get professor info
    public String getProfessorInfo() {
        if (enrollment != null && enrollment.getCourse() != null && enrollment.getCourse().getProfessor() != null) {
            return enrollment.getCourse().getProfessor().getFullName();
        }
        return null;
    }

    // Helper method to get semester info
    public String getSemesterInfo() {
        if (enrollment != null && enrollment.getCourse() != null) {
            String semester = enrollment.getCourse().getSemester();
            Integer year = enrollment.getCourse().getAcademicYear();
            if (semester != null && year != null) {
                return semester + " " + year;
            }
        }
        return null;
    }
} 