package com.smartcampus.repository;

import com.smartcampus.entity.Enrollment;
import com.smartcampus.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    List<Enrollment> findByStudentId(Long studentId);
    
    List<Enrollment> findByCourseId(Long courseId);
    
    List<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);
    
    List<Enrollment> findByCourseIdAndStatus(Long courseId, EnrollmentStatus status);
    
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    
    List<Enrollment> findByStatus(EnrollmentStatus status);
    
    List<Enrollment> findByIsActive(Boolean isActive);
    
    List<Enrollment> findByStudentIdAndIsActive(Long studentId, Boolean isActive);
    
    List<Enrollment> findByCourseIdAndIsActive(Long courseId, Boolean isActive);
    
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.course.semester = :semester AND e.course.academicYear = :academicYear")
    List<Enrollment> findByStudentIdAndSemesterAndAcademicYear(@Param("studentId") Long studentId, @Param("semester") String semester, @Param("academicYear") Integer academicYear);
    
    @Query("SELECT e FROM Enrollment e WHERE e.course.professor.id = :professorId")
    List<Enrollment> findByProfessorId(@Param("professorId") Long professorId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.course.professor.id = :professorId AND e.course.id = :courseId")
    List<Enrollment> findByProfessorIdAndCourseId(@Param("professorId") Long professorId, @Param("courseId") Long courseId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.course.professor.id = :professorId AND e.status = :status")
    List<Enrollment> findByProfessorIdAndStatus(@Param("professorId") Long professorId, @Param("status") EnrollmentStatus status);
    
    @Query("SELECT e FROM Enrollment e WHERE e.enrollmentDate BETWEEN :startDate AND :endDate")
    List<Enrollment> findByEnrollmentDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.enrollmentDate BETWEEN :startDate AND :endDate")
    List<Enrollment> findByStudentIdAndEnrollmentDateBetween(@Param("studentId") Long studentId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.enrollmentDate BETWEEN :startDate AND :endDate")
    List<Enrollment> findByCourseIdAndEnrollmentDateBetween(@Param("courseId") Long courseId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.student.id = :studentId AND e.status = :status")
    Long countByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") EnrollmentStatus status);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = :status")
    Long countByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") EnrollmentStatus status);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.professor.id = :professorId AND e.status = :status")
    Long countByProfessorIdAndStatus(@Param("professorId") Long professorId, @Param("status") EnrollmentStatus status);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.student.id = :studentId AND e.isActive = true")
    Long countActiveEnrollmentsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.isActive = true")
    Long countActiveEnrollmentsByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.course.semester = :semester AND e.course.academicYear = :academicYear AND e.isActive = true")
    List<Enrollment> findActiveEnrollmentsByStudentAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester, @Param("academicYear") Integer academicYear);
    
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.gradeLetter IS NOT NULL")
    List<Enrollment> findGradedEnrollmentsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.gradeLetter IS NULL AND e.isActive = true")
    List<Enrollment> findUngradedActiveEnrollmentsByStudentId(@Param("studentId") Long studentId);
} 