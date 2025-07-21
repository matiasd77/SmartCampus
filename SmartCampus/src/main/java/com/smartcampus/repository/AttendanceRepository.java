package com.smartcampus.repository;

import com.smartcampus.entity.Attendance;
import com.smartcampus.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    Optional<Attendance> findByStudentIdAndCourseIdAndDate(Long studentId, Long courseId, LocalDate date);
    
    boolean existsByStudentIdAndCourseIdAndDate(Long studentId, Long courseId, LocalDate date);
    
    List<Attendance> findByStudentId(Long studentId);
    
    List<Attendance> findByCourseId(Long courseId);
    
    List<Attendance> findByCourseProfessorId(Long professorId);
    
    List<Attendance> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    List<Attendance> findByStatus(AttendanceStatus status);
    
    List<Attendance> findByStudentIdAndStatus(Long studentId, AttendanceStatus status);
    
    List<Attendance> findByCourseIdAndStatus(Long courseId, AttendanceStatus status);
    
    List<Attendance> findByCourseProfessorIdAndStatus(Long professorId, AttendanceStatus status);
    
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.course.semester = :semester AND a.course.academicYear = :academicYear")
    List<Attendance> findByStudentIdAndSemesterAndAcademicYear(@Param("studentId") Long studentId, @Param("semester") String semester, @Param("academicYear") Integer academicYear);
    
    @Query("SELECT a FROM Attendance a WHERE a.course.professor.id = :professorId AND a.course.semester = :semester AND a.course.academicYear = :academicYear")
    List<Attendance> findByProfessorIdAndSemesterAndAcademicYear(@Param("professorId") Long professorId, @Param("semester") String semester, @Param("academicYear") Integer academicYear);
    
    List<Attendance> findByDate(LocalDate date);
    
    List<Attendance> findByStudentIdAndDate(Long studentId, LocalDate date);
    
    List<Attendance> findByCourseIdAndDate(Long courseId, LocalDate date);
    
    List<Attendance> findByCourseProfessorIdAndDate(Long professorId, LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findByStudentIdAndDateBetween(@Param("studentId") Long studentId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Attendance a WHERE a.course.id = :courseId AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findByCourseIdAndDateBetween(@Param("courseId") Long courseId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Attendance a WHERE a.course.professor.id = :professorId AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findByProfessorIdAndDateBetween(@Param("professorId") Long professorId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    List<Attendance> findByIsMakeup(Boolean isMakeup);
    
    List<Attendance> findByStudentIdAndIsMakeup(Long studentId, Boolean isMakeup);
    
    List<Attendance> findByCourseIdAndIsMakeup(Long courseId, Boolean isMakeup);
    
    List<Attendance> findByIsVerified(Boolean isVerified);
    
    List<Attendance> findByStudentIdAndIsVerified(Long studentId, Boolean isVerified);
    
    List<Attendance> findByCourseIdAndIsVerified(Long courseId, Boolean isVerified);
    
    List<Attendance> findByIsExcused(Boolean isExcused);
    
    List<Attendance> findByStudentIdAndIsExcused(Long studentId, Boolean isExcused);
    
    List<Attendance> findByCourseIdAndIsExcused(Long courseId, Boolean isExcused);
    
    @Query("SELECT a FROM Attendance a WHERE a.lateMinutes > 0")
    List<Attendance> findLateAttendances();
    
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.lateMinutes > 0")
    List<Attendance> findLateAttendancesByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT a FROM Attendance a WHERE a.course.id = :courseId AND a.lateMinutes > 0")
    List<Attendance> findLateAttendancesByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT a FROM Attendance a WHERE a.earlyDepartureMinutes > 0")
    List<Attendance> findEarlyDepartureAttendances();
    
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.earlyDepartureMinutes > 0")
    List<Attendance> findEarlyDepartureAttendancesByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT a FROM Attendance a WHERE a.course.id = :courseId AND a.earlyDepartureMinutes > 0")
    List<Attendance> findEarlyDepartureAttendancesByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT a FROM Attendance a WHERE a.checkInTime IS NOT NULL AND a.checkOutTime IS NULL")
    List<Attendance> findActiveCheckIns();
    
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.checkInTime IS NOT NULL AND a.checkOutTime IS NULL")
    List<Attendance> findActiveCheckInsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT a FROM Attendance a WHERE a.course.id = :courseId AND a.checkInTime IS NOT NULL AND a.checkOutTime IS NULL")
    List<Attendance> findActiveCheckInsByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId")
    Long countByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.course.id = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.course.professor.id = :professorId")
    Long countByProfessorId(@Param("professorId") Long professorId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.status = :status")
    Long countByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") AttendanceStatus status);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.course.id = :courseId AND a.status = :status")
    Long countByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") AttendanceStatus status);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.course.professor.id = :professorId AND a.status = :status")
    Long countByProfessorIdAndStatus(@Param("professorId") Long professorId, @Param("status") AttendanceStatus status);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.status = 'PRESENT'")
    Long countPresentByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.course.id = :courseId AND a.status = 'PRESENT'")
    Long countPresentByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.course.professor.id = :professorId AND a.status = 'PRESENT'")
    Long countPresentByProfessorId(@Param("professorId") Long professorId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.status = 'ABSENT'")
    Long countAbsentByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.course.id = :courseId AND a.status = 'ABSENT'")
    Long countAbsentByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.course.professor.id = :professorId AND a.status = 'ABSENT'")
    Long countAbsentByProfessorId(@Param("professorId") Long professorId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND (a.status = 'EXCUSED' OR a.isExcused = true)")
    Long countExcusedByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.course.id = :courseId AND (a.status = 'EXCUSED' OR a.isExcused = true)")
    Long countExcusedByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.course.professor.id = :professorId AND (a.status = 'EXCUSED' OR a.isExcused = true)")
    Long countExcusedByProfessorId(@Param("professorId") Long professorId);
    
    @Query("SELECT AVG(a.attendancePercentage) FROM Attendance a WHERE a.student.id = :studentId")
    Double getAverageAttendancePercentageByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT AVG(a.attendancePercentage) FROM Attendance a WHERE a.course.id = :courseId")
    Double getAverageAttendancePercentageByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT AVG(a.attendancePercentage) FROM Attendance a WHERE a.course.professor.id = :professorId")
    Double getAverageAttendancePercentageByProfessorId(@Param("professorId") Long professorId);
    
    @Query("SELECT MAX(a.attendancePercentage) FROM Attendance a WHERE a.student.id = :studentId")
    Double getMaxAttendancePercentageByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT MIN(a.attendancePercentage) FROM Attendance a WHERE a.student.id = :studentId")
    Double getMinAttendancePercentageByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT MAX(a.attendancePercentage) FROM Attendance a WHERE a.course.id = :courseId")
    Double getMaxAttendancePercentageByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT MIN(a.attendancePercentage) FROM Attendance a WHERE a.course.id = :courseId")
    Double getMinAttendancePercentageByCourseId(@Param("courseId") Long courseId);
} 