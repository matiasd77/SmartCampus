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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id", "date"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Attendance {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @NotNull(message = "Date is required")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Size(max = 100, message = "Session name must not exceed 100 characters")
    @Column(name = "session_name")
    private String sessionName;

    @Size(max = 50, message = "Session type must not exceed 50 characters")
    @Column(name = "session_type")
    private String sessionType;

    @Column(name = "session_duration")
    private Integer sessionDuration; // in minutes

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "late_minutes")
    private Integer lateMinutes;

    @Column(name = "early_departure_minutes")
    private Integer earlyDepartureMinutes;

    @Column(name = "is_makeup")
    private Boolean isMakeup;

    @Column(name = "makeup_date")
    private LocalDate makeupDate;

    @Size(max = 200, message = "Makeup reason must not exceed 200 characters")
    @Column(name = "makeup_reason")
    private String makeupReason;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Size(max = 100, message = "Verified by must not exceed 100 characters")
    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    @Size(max = 200, message = "Verification notes must not exceed 200 characters")
    @Column(name = "verification_notes")
    private String verificationNotes;

    @Column(name = "is_excused")
    private Boolean isExcused;

    @Size(max = 200, message = "Excuse reason must not exceed 200 characters")
    @Column(name = "excuse_reason")
    private String excuseReason;

    @Size(max = 100, message = "Excuse approved by must not exceed 100 characters")
    @Column(name = "excuse_approved_by")
    private String excuseApprovedBy;

    @Column(name = "excuse_approval_date")
    private LocalDateTime excuseApprovalDate;

    @Size(max = 200, message = "Excuse notes must not exceed 200 characters")
    @Column(name = "excuse_notes")
    private String excuseNotes;

    @Column(name = "attendance_percentage")
    private Double attendancePercentage;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "is_counted_towards_grade")
    private Boolean isCountedTowardsGrade;

    @Column(name = "grade_impact")
    private Double gradeImpact;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
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
        if (student != null) {
            return student.getStudentId() + " - " + student.getFirstName() + " " + student.getLastName();
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

    // Helper method to get professor info
    public String getProfessorInfo() {
        if (course != null && course.getProfessor() != null) {
            return course.getProfessor().getFullName();
        }
        return null;
    }

    // Helper method to get semester info
    public String getSemesterInfo() {
        if (course != null) {
            String semester = course.getSemester();
            Integer year = course.getAcademicYear();
            if (semester != null && year != null) {
                return semester + " " + year;
            }
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