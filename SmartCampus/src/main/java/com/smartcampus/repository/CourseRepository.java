package com.smartcampus.repository;

import com.smartcampus.entity.Course;
import com.smartcampus.entity.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    Optional<Course> findByCode(String code);
    
    boolean existsByCode(String code);
    
    Optional<Course> findByCodeAndIdNot(String code, Long id);
    
    List<Course> findByProfessorId(Long professorId);
    
    List<Course> findByStatus(CourseStatus status);
    
    List<Course> findBySemester(String semester);
    
    List<Course> findByAcademicYear(Integer academicYear);
    
    List<Course> findByProfessorIdAndStatus(Long professorId, CourseStatus status);
    
    List<Course> findBySemesterAndAcademicYear(String semester, Integer academicYear);
    
    @Query("SELECT c FROM Course c WHERE c.name LIKE %:name% OR c.code LIKE %:code%")
    List<Course> findByNameOrCodeContaining(@Param("name") String name, @Param("code") String code);
    
    @Query("SELECT c FROM Course c WHERE c.professor.department = :department")
    List<Course> findByDepartment(@Param("department") String department);
    
    @Query("SELECT c FROM Course c WHERE c.professor.department = :department AND c.status = :status")
    List<Course> findByDepartmentAndStatus(@Param("department") String department, @Param("status") CourseStatus status);
    
    @Query("SELECT c FROM Course c WHERE c.currentEnrollment < c.maxStudents OR c.maxStudents IS NULL")
    List<Course> findAvailableCourses();
    
    @Query("SELECT c FROM Course c WHERE c.professor.id = :professorId AND (c.name LIKE %:searchTerm% OR c.code LIKE %:searchTerm%)")
    List<Course> findByProfessorIdAndSearchTerm(@Param("professorId") Long professorId, @Param("searchTerm") String searchTerm);
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.professor.id = :professorId")
    Long countByProfessorId(@Param("professorId") Long professorId);
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.status = :status")
    Long countByStatus(@Param("status") CourseStatus status);
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.semester = :semester AND c.academicYear = :academicYear")
    Long countBySemesterAndAcademicYear(@Param("semester") String semester, @Param("academicYear") Integer academicYear);
} 