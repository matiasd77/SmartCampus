package com.smartcampus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "courses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course name is required")
    @Size(min = 2, max = 100, message = "Course name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Course code is required")
    @Size(min = 3, max = 20, message = "Course code must be between 3 and 20 characters")
    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Professor is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @Size(max = 50, message = "Semester must not exceed 50 characters")
    @Column(name = "semester")
    private String semester;

    @Column(name = "academic_year")
    private Integer academicYear;

    @Column(name = "credits")
    private Integer credits;

    @Size(max = 20, message = "Schedule must not exceed 20 characters")
    @Column(name = "schedule")
    private String schedule;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CourseStatus status;

    @Column(name = "max_students")
    private Integer maxStudents;

    @Column(name = "current_enrollment")
    private Integer currentEnrollment;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendanceRecords;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
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

    // Helper method to get available seats
    public Integer getAvailableSeats() {
        if (maxStudents == null) {
            return null; // No limit set
        }
        return maxStudents - (currentEnrollment != null ? currentEnrollment : 0);
    }

    // Helper method to increment enrollment
    public void incrementEnrollment() {
        if (currentEnrollment == null) {
            currentEnrollment = 0;
        }
        currentEnrollment++;
    }

    // Helper method to decrement enrollment
    public void decrementEnrollment() {
        if (currentEnrollment != null && currentEnrollment > 0) {
            currentEnrollment--;
        }
    }
} 