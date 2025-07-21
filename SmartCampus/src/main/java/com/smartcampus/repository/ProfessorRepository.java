package com.smartcampus.repository;

import com.smartcampus.entity.AcademicRank;
import com.smartcampus.entity.Professor;
import com.smartcampus.entity.ProfessorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    
    Optional<Professor> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<Professor> findByEmailAndIdNot(String email, Long id);
    
    List<Professor> findByDepartment(String department);
    
    List<Professor> findByStatus(ProfessorStatus status);
    
    List<Professor> findByDepartmentAndStatus(String department, ProfessorStatus status);
    
    @Query("SELECT p FROM Professor p WHERE p.firstName LIKE %:name% OR p.lastName LIKE %:name%")
    List<Professor> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT p FROM Professor p WHERE p.department = :department AND (p.firstName LIKE %:name% OR p.lastName LIKE %:name%)")
    List<Professor> findByDepartmentAndNameContaining(@Param("department") String department, @Param("name") String name);
    
    @Query("SELECT COUNT(p) FROM Professor p WHERE p.department = :department")
    Long countByDepartment(@Param("department") String department);
    
    @Query("SELECT COUNT(p) FROM Professor p WHERE p.status = :status")
    Long countByStatus(@Param("status") ProfessorStatus status);
    
    @Query("SELECT p FROM Professor p WHERE p.user.id = :userId")
    Optional<Professor> findByUser_Id(@Param("userId") Long userId);
    
    // Alternative method name for convenience
    @Query("SELECT p FROM Professor p WHERE p.user.id = :userId")
    Optional<Professor> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT p FROM Professor p WHERE p.user.email = :email")
    Optional<Professor> findByUserEmail(@Param("email") String email);
    
    // Convenience method to check if professor exists by user ID
    @Query("SELECT COUNT(p) > 0 FROM Professor p WHERE p.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
    
    // Additional methods that might be called by services - using actual entity fields
    @Query("SELECT p FROM Professor p WHERE p.academicRank = :rank")
    List<Professor> findByAcademicRank(@Param("rank") AcademicRank rank);
    
    @Query("SELECT p FROM Professor p WHERE p.department = :department AND p.academicRank = :rank")
    List<Professor> findByDepartmentAndAcademicRank(@Param("department") String department, @Param("rank") AcademicRank rank);
    
    @Query("SELECT p FROM Professor p WHERE p.status = :status AND p.academicRank = :rank")
    List<Professor> findByStatusAndAcademicRank(@Param("status") ProfessorStatus status, @Param("rank") AcademicRank rank);
    
    @Query("SELECT p FROM Professor p WHERE p.officeLocation LIKE %:location%")
    List<Professor> findByOfficeLocationContaining(@Param("location") String location);
    
    @Query("SELECT p FROM Professor p WHERE p.department = :department AND p.officeLocation LIKE %:location%")
    List<Professor> findByDepartmentAndOfficeLocationContaining(@Param("department") String department, @Param("location") String location);
    
    @Query("SELECT p FROM Professor p WHERE p.phoneNumber LIKE %:phone%")
    List<Professor> findByPhoneNumberContaining(@Param("phone") String phone);
    
    @Query("SELECT p FROM Professor p WHERE p.bio LIKE %:keyword%")
    List<Professor> findByBioContaining(@Param("keyword") String keyword);
    
    @Query("SELECT p FROM Professor p WHERE p.department = :department AND p.bio LIKE %:keyword%")
    List<Professor> findByDepartmentAndBioContaining(@Param("department") String department, @Param("keyword") String keyword);
    
    // Additional count methods
    @Query("SELECT COUNT(p) FROM Professor p WHERE p.department = :department AND p.status = :status")
    Long countByDepartmentAndStatus(@Param("department") String department, @Param("status") ProfessorStatus status);
    
    @Query("SELECT COUNT(p) FROM Professor p WHERE p.academicRank = :rank")
    Long countByAcademicRank(@Param("rank") AcademicRank rank);
    
    @Query("SELECT COUNT(p) FROM Professor p WHERE p.department = :department AND p.academicRank = :rank")
    Long countByDepartmentAndAcademicRank(@Param("department") String department, @Param("rank") AcademicRank rank);
    
    @Query("SELECT COUNT(p) FROM Professor p WHERE p.status = :status AND p.academicRank = :rank")
    Long countByStatusAndAcademicRank(@Param("status") ProfessorStatus status, @Param("rank") AcademicRank rank);
    
    @Query("SELECT COUNT(p) FROM Professor p WHERE p.bio LIKE %:keyword%")
    Long countByBioContaining(@Param("keyword") String keyword);
    
    // Search methods - using actual entity fields
    @Query("SELECT p FROM Professor p WHERE p.firstName LIKE %:searchTerm% OR p.lastName LIKE %:searchTerm% OR p.email LIKE %:searchTerm% OR p.bio LIKE %:searchTerm%")
    List<Professor> findBySearchTerm(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT p FROM Professor p WHERE p.department = :department AND (p.firstName LIKE %:searchTerm% OR p.lastName LIKE %:searchTerm% OR p.email LIKE %:searchTerm% OR p.bio LIKE %:searchTerm%)")
    List<Professor> findByDepartmentAndSearchTerm(@Param("department") String department, @Param("searchTerm") String searchTerm);
    
    // Active professors
    @Query("SELECT p FROM Professor p WHERE p.status = 'ACTIVE'")
    List<Professor> findActiveProfessors();
    
    @Query("SELECT p FROM Professor p WHERE p.department = :department AND p.status = 'ACTIVE'")
    List<Professor> findActiveProfessorsByDepartment(@Param("department") String department);
    
    @Query("SELECT COUNT(p) FROM Professor p WHERE p.status = 'ACTIVE'")
    Long countActiveProfessors();
    
    @Query("SELECT COUNT(p) FROM Professor p WHERE p.department = :department AND p.status = 'ACTIVE'")
    Long countActiveProfessorsByDepartment(@Param("department") String department);
} 