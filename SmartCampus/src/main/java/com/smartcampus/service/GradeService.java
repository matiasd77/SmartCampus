package com.smartcampus.service;

import com.smartcampus.dto.GradeDTO;
import com.smartcampus.entity.GradeStatus;
import com.smartcampus.entity.GradeType;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface GradeService {
    List<GradeDTO> getAllGrades();
    Page<GradeDTO> getAllGrades(int page, int size);
    GradeDTO getGradeById(Long id);
    GradeDTO getGradeByEnrollmentId(Long enrollmentId);
    GradeDTO createGrade(GradeDTO gradeDTO);
    GradeDTO updateGrade(Long id, GradeDTO gradeDTO);
    void deleteGrade(Long id);
    List<GradeDTO> getGradesByStudentId(Long studentId);
    List<GradeDTO> getGradesByCourseId(Long courseId);
    List<GradeDTO> getGradesByProfessorId(Long professorId);
    List<GradeDTO> getGradesByStudentIdAndCourseId(Long studentId, Long courseId);
    List<GradeDTO> getGradesByStudentIdAndGradeType(Long studentId, GradeType gradeType);
    List<GradeDTO> getGradesByCourseIdAndGradeType(Long courseId, GradeType gradeType);
    List<GradeDTO> getGradesByStatus(GradeStatus status);
    List<GradeDTO> getFinalGrades();
    List<GradeDTO> getGradesByStudentIdAndIsFinal(Long studentId, Boolean isFinal);
    List<GradeDTO> getGradesByCourseIdAndIsFinal(Long courseId, Boolean isFinal);
    List<GradeDTO> getGradesByStudentIdAndSemester(Long studentId, String semester, Integer academicYear);
    List<GradeDTO> getGradesByProfessorIdAndSemester(Long professorId, String semester, Integer academicYear);
    List<GradeDTO> getGradesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<GradeDTO> getGradesByStudentIdAndDateRange(Long studentId, LocalDateTime startDate, LocalDateTime endDate);
    List<GradeDTO> getGradesByCourseIdAndDateRange(Long courseId, LocalDateTime startDate, LocalDateTime endDate);
    List<GradeDTO> getGradesByProfessorIdAndDateRange(Long professorId, LocalDateTime startDate, LocalDateTime endDate);
    List<GradeDTO> getGradesByStudentIdAndGradeRange(Long studentId, Double minGrade, Double maxGrade);
    List<GradeDTO> getGradesByCourseIdAndGradeRange(Long courseId, Double minGrade, Double maxGrade);
    List<GradeDTO> getGradesByStudentIdAndGradeLetter(Long studentId, String gradeLetter);
    List<GradeDTO> getGradesByCourseIdAndGradeLetter(Long courseId, String gradeLetter);
    List<GradeDTO> getGradedEnrollmentsByStudentId(Long studentId);
    List<GradeDTO> getUngradedEnrollmentsByStudentId(Long studentId);
    List<GradeDTO> getGradedEnrollmentsByCourseId(Long courseId);
    List<GradeDTO> getUngradedEnrollmentsByCourseId(Long courseId);
    Long getGradeCountByStudentId(Long studentId);
    Long getGradeCountByCourseId(Long courseId);
    Long getGradeCountByProfessorId(Long professorId);
    Long getGradeCountByStudentIdAndStatus(Long studentId, GradeStatus status);
    Long getGradeCountByCourseIdAndStatus(Long courseId, GradeStatus status);
    Long getGradeCountByProfessorIdAndStatus(Long professorId, GradeStatus status);
    Double getAverageGradeByStudentId(Long studentId);
    Double getAverageGradeByCourseId(Long courseId);
    Double getAverageGradeByProfessorId(Long professorId);
    Double getMaxGradeByStudentId(Long studentId);
    Double getMinGradeByStudentId(Long studentId);
    Double getMaxGradeByCourseId(Long courseId);
    Double getMinGradeByCourseId(Long courseId);
    boolean existsByEnrollmentId(Long enrollmentId);
} 