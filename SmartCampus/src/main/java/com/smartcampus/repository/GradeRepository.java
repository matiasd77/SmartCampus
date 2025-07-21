package com.smartcampus.repository;

import com.smartcampus.entity.Grade;
import com.smartcampus.entity.GradeStatus;
import com.smartcampus.entity.GradeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    
    Optional<Grade> findByEnrollmentId(Long enrollmentId);
    
    boolean existsByEnrollmentId(Long enrollmentId);
    
    List<Grade> findByEnrollmentStudentId(Long studentId);
    
    List<Grade> findByEnrollmentCourseId(Long courseId);
    
    List<Grade> findByEnrollmentCourseProfessorId(Long professorId);
    
    List<Grade> findByEnrollmentStudentIdAndEnrollmentCourseId(Long studentId, Long courseId);
    
    List<Grade> findByEnrollmentStudentIdAndGradeType(Long studentId, GradeType gradeType);
    
    List<Grade> findByEnrollmentCourseIdAndGradeType(Long courseId, GradeType gradeType);
    
    List<Grade> findByStatus(GradeStatus status);
    
    List<Grade> findByIsFinal(Boolean isFinal);
    
    List<Grade> findByEnrollmentStudentIdAndIsFinal(Long studentId, Boolean isFinal);
    
    List<Grade> findByEnrollmentCourseIdAndIsFinal(Long courseId, Boolean isFinal);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.student.id = :studentId AND g.enrollment.course.semester = :semester AND g.enrollment.course.academicYear = :academicYear")
    List<Grade> findByStudentIdAndSemesterAndAcademicYear(@Param("studentId") Long studentId, @Param("semester") String semester, @Param("academicYear") Integer academicYear);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.course.professor.id = :professorId AND g.enrollment.course.semester = :semester AND g.enrollment.course.academicYear = :academicYear")
    List<Grade> findByProfessorIdAndSemesterAndAcademicYear(@Param("professorId") Long professorId, @Param("semester") String semester, @Param("academicYear") Integer academicYear);
    
    @Query("SELECT g FROM Grade g WHERE g.dateAssigned BETWEEN :startDate AND :endDate")
    List<Grade> findByDateAssignedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.student.id = :studentId AND g.dateAssigned BETWEEN :startDate AND :endDate")
    List<Grade> findByStudentIdAndDateAssignedBetween(@Param("studentId") Long studentId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.course.id = :courseId AND g.dateAssigned BETWEEN :startDate AND :endDate")
    List<Grade> findByCourseIdAndDateAssignedBetween(@Param("courseId") Long courseId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.course.professor.id = :professorId AND g.dateAssigned BETWEEN :startDate AND :endDate")
    List<Grade> findByProfessorIdAndDateAssignedBetween(@Param("professorId") Long professorId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.student.id = :studentId AND g.gradeValue >= :minGrade")
    List<Grade> findByStudentIdAndGradeValueGreaterThanEqual(@Param("studentId") Long studentId, @Param("minGrade") Double minGrade);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.student.id = :studentId AND g.gradeValue <= :maxGrade")
    List<Grade> findByStudentIdAndGradeValueLessThanEqual(@Param("studentId") Long studentId, @Param("maxGrade") Double maxGrade);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.course.id = :courseId AND g.gradeValue >= :minGrade")
    List<Grade> findByCourseIdAndGradeValueGreaterThanEqual(@Param("courseId") Long courseId, @Param("minGrade") Double minGrade);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.course.id = :courseId AND g.gradeValue <= :maxGrade")
    List<Grade> findByCourseIdAndGradeValueLessThanEqual(@Param("courseId") Long courseId, @Param("maxGrade") Double maxGrade);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.student.id = :studentId AND g.gradeLetter = :gradeLetter")
    List<Grade> findByStudentIdAndGradeLetter(@Param("studentId") Long studentId, @Param("gradeLetter") String gradeLetter);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.course.id = :courseId AND g.gradeLetter = :gradeLetter")
    List<Grade> findByCourseIdAndGradeLetter(@Param("courseId") Long courseId, @Param("gradeLetter") String gradeLetter);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.student.id = :studentId AND g.gradeLetter IS NOT NULL")
    List<Grade> findGradedEnrollmentsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.student.id = :studentId AND g.gradeLetter IS NULL")
    List<Grade> findUngradedEnrollmentsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.course.id = :courseId AND g.gradeLetter IS NOT NULL")
    List<Grade> findGradedEnrollmentsByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.course.id = :courseId AND g.gradeLetter IS NULL")
    List<Grade> findUngradedEnrollmentsByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.enrollment.student.id = :studentId")
    Long countByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.enrollment.course.id = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.enrollment.course.professor.id = :professorId")
    Long countByProfessorId(@Param("professorId") Long professorId);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.enrollment.student.id = :studentId AND g.status = :status")
    Long countByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") GradeStatus status);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.enrollment.course.id = :courseId AND g.status = :status")
    Long countByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") GradeStatus status);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.enrollment.course.professor.id = :professorId AND g.status = :status")
    Long countByProfessorIdAndStatus(@Param("professorId") Long professorId, @Param("status") GradeStatus status);
    
    @Query("SELECT AVG(g.gradeValue) FROM Grade g WHERE g.enrollment.student.id = :studentId")
    Double getAverageGradeByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT AVG(g.gradeValue) FROM Grade g WHERE g.enrollment.course.id = :courseId")
    Double getAverageGradeByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT AVG(g.gradeValue) FROM Grade g WHERE g.enrollment.course.professor.id = :professorId")
    Double getAverageGradeByProfessorId(@Param("professorId") Long professorId);
    
    @Query("SELECT MAX(g.gradeValue) FROM Grade g WHERE g.enrollment.student.id = :studentId")
    Double getMaxGradeByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT MIN(g.gradeValue) FROM Grade g WHERE g.enrollment.student.id = :studentId")
    Double getMinGradeByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT MAX(g.gradeValue) FROM Grade g WHERE g.enrollment.course.id = :courseId")
    Double getMaxGradeByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT MIN(g.gradeValue) FROM Grade g WHERE g.enrollment.course.id = :courseId")
    Double getMinGradeByCourseId(@Param("courseId") Long courseId);
} 