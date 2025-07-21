package com.smartcampus.repository;

import com.smartcampus.entity.Student;
import com.smartcampus.entity.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    Optional<Student> findByStudentId(String studentId);
    
    Optional<Student> findByUser_Id(Long userId);
    
    // Alternative method name for convenience
    @Query("SELECT s FROM Student s WHERE s.user.id = :userId")
    Optional<Student> findByUserId(@Param("userId") Long userId);
    
    boolean existsByStudentId(String studentId);
    
    boolean existsByUser_Id(Long userId);
    
    // Convenience method with simplified naming
    @Query("SELECT COUNT(s) > 0 FROM Student s WHERE s.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
    
    List<Student> findByStatus(StudentStatus status);
    
    List<Student> findByMajor(String major);
    
    List<Student> findByYearOfStudy(Integer yearOfStudy);
    
    @Query("SELECT s FROM Student s WHERE s.user.email = :email")
    Optional<Student> findByUserEmail(@Param("email") String email);
    
    @Query("SELECT s FROM Student s WHERE s.major = :major AND s.status = :status")
    List<Student> findByMajorAndStatus(@Param("major") String major, @Param("status") StudentStatus status);
    
    // Additional methods that might be called by services
    @Query("SELECT s FROM Student s WHERE s.firstName LIKE %:name% OR s.lastName LIKE %:name%")
    List<Student> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT s FROM Student s WHERE s.major = :major AND s.yearOfStudy = :year")
    List<Student> findByMajorAndYearOfStudy(@Param("major") String major, @Param("year") Integer year);
    
    @Query("SELECT s FROM Student s WHERE s.status = :status AND s.yearOfStudy = :year")
    List<Student> findByStatusAndYearOfStudy(@Param("status") StudentStatus status, @Param("year") Integer year);
    
    @Query("SELECT s FROM Student s WHERE s.gpa >= :minGpa")
    List<Student> findByGpaGreaterThanEqual(@Param("minGpa") Double minGpa);
    
    @Query("SELECT s FROM Student s WHERE s.gpa <= :maxGpa")
    List<Student> findByGpaLessThanEqual(@Param("maxGpa") Double maxGpa);
    
    @Query("SELECT s FROM Student s WHERE s.gpa BETWEEN :minGpa AND :maxGpa")
    List<Student> findByGpaBetween(@Param("minGpa") Double minGpa, @Param("maxGpa") Double maxGpa);
    
    // Count methods
    @Query("SELECT COUNT(s) FROM Student s WHERE s.major = :major")
    Long countByMajor(@Param("major") String major);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.status = :status")
    Long countByStatus(@Param("status") StudentStatus status);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.yearOfStudy = :year")
    Long countByYearOfStudy(@Param("year") Integer year);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.major = :major AND s.status = :status")
    Long countByMajorAndStatus(@Param("major") String major, @Param("status") StudentStatus status);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.major = :major AND s.yearOfStudy = :year")
    Long countByMajorAndYearOfStudy(@Param("major") String major, @Param("year") Integer year);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.status = :status AND s.yearOfStudy = :year")
    Long countByStatusAndYearOfStudy(@Param("status") StudentStatus status, @Param("year") Integer year);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.gpa >= :minGpa")
    Long countByGpaGreaterThanEqual(@Param("minGpa") Double minGpa);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.gpa BETWEEN :minGpa AND :maxGpa")
    Long countByGpaBetween(@Param("minGpa") Double minGpa, @Param("maxGpa") Double maxGpa);
    
    // Statistical methods
    @Query("SELECT AVG(s.gpa) FROM Student s WHERE s.major = :major")
    Double getAverageGpaByMajor(@Param("major") String major);
    
    @Query("SELECT AVG(s.gpa) FROM Student s WHERE s.yearOfStudy = :year")
    Double getAverageGpaByYearOfStudy(@Param("year") Integer year);
    
    @Query("SELECT AVG(s.gpa) FROM Student s WHERE s.status = :status")
    Double getAverageGpaByStatus(@Param("status") StudentStatus status);
    
    @Query("SELECT MAX(s.gpa) FROM Student s WHERE s.major = :major")
    Double getMaxGpaByMajor(@Param("major") String major);
    
    @Query("SELECT MIN(s.gpa) FROM Student s WHERE s.major = :major")
    Double getMinGpaByMajor(@Param("major") String major);
    
    @Query("SELECT MAX(s.gpa) FROM Student s WHERE s.yearOfStudy = :year")
    Double getMaxGpaByYearOfStudy(@Param("year") Integer year);
    
    @Query("SELECT MIN(s.gpa) FROM Student s WHERE s.yearOfStudy = :year")
    Double getMinGpaByYearOfStudy(@Param("year") Integer year);
    
    // Top performers
    @Query("SELECT s FROM Student s WHERE s.major = :major ORDER BY s.gpa DESC")
    List<Student> findByMajorOrderByGpaDesc(@Param("major") String major);
    
    @Query("SELECT s FROM Student s WHERE s.yearOfStudy = :year ORDER BY s.gpa DESC")
    List<Student> findByYearOfStudyOrderByGpaDesc(@Param("year") Integer year);
    
    @Query("SELECT s FROM Student s ORDER BY s.gpa DESC")
    List<Student> findAllOrderByGpaDesc();
    
    @Query("SELECT s FROM Student s WHERE s.status = StudentStatus.ACTIVE ORDER BY s.gpa DESC")
    List<Student> findActiveStudentsOrderByGpaDesc();
} 