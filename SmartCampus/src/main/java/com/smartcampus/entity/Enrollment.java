package com.smartcampus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull(message = "Course is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotNull(message = "Enrollment date is required")
    @Column(name = "enrollment_date", nullable = false)
    private LocalDateTime enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EnrollmentStatus status;

    @Column(name = "drop_date")
    private LocalDateTime dropDate;

    @Column(name = "withdrawal_reason")
    private String withdrawalReason;

    @Column(name = "grade_letter")
    private String gradeLetter;

    @Column(name = "grade_points")
    private Double gradePoints;

    @Column(name = "attendance_percentage")
    private Double attendancePercentage;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "notes")
    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper method to check if enrollment is active
    public boolean isActiveEnrollment() {
        return isActive != null && isActive && status == EnrollmentStatus.ENROLLED;
    }

    // Helper method to check if student can drop the course
    public boolean canDrop() {
        return isActiveEnrollment() && dropDate == null;
    }

    // Helper method to mark enrollment as dropped
    public void dropEnrollment(String reason) {
        this.status = EnrollmentStatus.DROPPED;
        this.dropDate = LocalDateTime.now();
        this.withdrawalReason = reason;
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    // Helper method to calculate GPA contribution
    public Double getGpaContribution() {
        if (gradePoints == null || course.getCredits() == null) {
            return 0.0;
        }
        return gradePoints * course.getCredits();
    }

    // Helper method to get semester info
    public String getSemesterInfo() {
        if (course.getSemester() != null && course.getAcademicYear() != null) {
            return course.getSemester() + " " + course.getAcademicYear();
        }
        return null;
    }

    // Helper method to get course info
    public String getCourseInfo() {
        if (course != null) {
            return course.getCode() + " - " + course.getName();
        }
        return null;
    }

    // Helper method to get student info
    public String getStudentInfo() {
        if (student != null) {
            return student.getStudentId() + " - " + student.getFirstName() + " " + student.getLastName();
        }
        return null;
    }
} 