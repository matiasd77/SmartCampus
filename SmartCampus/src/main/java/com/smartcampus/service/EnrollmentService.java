package com.smartcampus.service;

import com.smartcampus.dto.EnrollmentDTO;
import com.smartcampus.entity.EnrollmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface EnrollmentService {
    List<EnrollmentDTO> getAllEnrollments();
    EnrollmentDTO getEnrollmentById(Long id);
    EnrollmentDTO createEnrollment(EnrollmentDTO enrollmentDTO);
    EnrollmentDTO updateEnrollment(Long id, EnrollmentDTO enrollmentDTO);
    void deleteEnrollment(Long id);
    List<EnrollmentDTO> getEnrollmentsByStudentId(Long studentId);
    List<EnrollmentDTO> getEnrollmentsByCourseId(Long courseId);
    List<EnrollmentDTO> getEnrollmentsByStatus(EnrollmentStatus status);
    List<EnrollmentDTO> getActiveEnrollments();
    List<EnrollmentDTO> getEnrollmentsByStudentIdAndStatus(Long studentId, EnrollmentStatus status);
    List<EnrollmentDTO> getEnrollmentsByCourseIdAndStatus(Long courseId, EnrollmentStatus status);
    List<EnrollmentDTO> getEnrollmentsByProfessorId(Long professorId);
    List<EnrollmentDTO> getEnrollmentsByProfessorIdAndCourseId(Long professorId, Long courseId);
    List<EnrollmentDTO> getEnrollmentsByStudentIdAndSemester(Long studentId, String semester, Integer academicYear);
    List<EnrollmentDTO> getEnrollmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<EnrollmentDTO> getEnrollmentsByStudentIdAndDateRange(Long studentId, LocalDateTime startDate, LocalDateTime endDate);
    List<EnrollmentDTO> getEnrollmentsByCourseIdAndDateRange(Long courseId, LocalDateTime startDate, LocalDateTime endDate);
    EnrollmentDTO dropEnrollment(Long enrollmentId, String reason);
    boolean isStudentEnrolledInCourse(Long studentId, Long courseId);
    Long getEnrollmentCountByStudentIdAndStatus(Long studentId, EnrollmentStatus status);
    Long getEnrollmentCountByCourseIdAndStatus(Long courseId, EnrollmentStatus status);
    Long getEnrollmentCountByProfessorIdAndStatus(Long professorId, EnrollmentStatus status);
    Long getActiveEnrollmentCountByStudentId(Long studentId);
    Long getActiveEnrollmentCountByCourseId(Long courseId);
    List<EnrollmentDTO> getActiveEnrollmentsByStudentAndSemester(Long studentId, String semester, Integer academicYear);
    List<EnrollmentDTO> getGradedEnrollmentsByStudentId(Long studentId);
    List<EnrollmentDTO> getUngradedActiveEnrollmentsByStudentId(Long studentId);
} 