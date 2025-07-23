package com.smartcampus.service.impl;

import com.smartcampus.dto.GradeDTO;
import com.smartcampus.entity.Enrollment;
import com.smartcampus.entity.Grade;
import com.smartcampus.entity.GradeStatus;
import com.smartcampus.entity.GradeType;
import com.smartcampus.exception.EnrollmentNotFoundException;
import com.smartcampus.exception.GradeNotFoundException;
import com.smartcampus.mapper.GradeMapper;
import com.smartcampus.repository.EnrollmentRepository;
import com.smartcampus.repository.GradeRepository;
import com.smartcampus.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeMapper gradeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getAllGrades() {
        List<Grade> grades = gradeRepository.findAll();
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GradeDTO> getAllGrades(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Grade> gradesPage = gradeRepository.findAll(pageable);
        return gradesPage.map(gradeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public GradeDTO getGradeById(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> GradeNotFoundException.withId(id));
        return gradeMapper.toDto(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public GradeDTO getGradeByEnrollmentId(Long enrollmentId) {
        Grade grade = gradeRepository.findByEnrollmentId(enrollmentId)
                .orElseThrow(() -> GradeNotFoundException.withEnrollmentId(enrollmentId));
        return gradeMapper.toDto(grade);
    }

    @Override
    public GradeDTO createGrade(GradeDTO gradeDTO) {
        // Validate enrollment exists
        Enrollment enrollment = enrollmentRepository.findById(gradeDTO.getEnrollmentId())
                .orElseThrow(() -> EnrollmentNotFoundException.withId(gradeDTO.getEnrollmentId()));

        // Check if grade already exists for this enrollment
        if (gradeRepository.existsByEnrollmentId(gradeDTO.getEnrollmentId())) {
            throw new RuntimeException("Grade already exists for this enrollment");
        }

        Grade grade = gradeMapper.toEntity(gradeDTO);
        grade.setEnrollment(enrollment);
        
        // Set default values
        if (grade.getDateAssigned() == null) {
            grade.setDateAssigned(LocalDateTime.now());
        }
        if (grade.getStatus() == null) {
            grade.setStatus(GradeStatus.PENDING);
        }
        if (grade.getIsFinal() == null) {
            grade.setIsFinal(false);
        }
        
        // Calculate letter grade and grade points if not provided
        if (grade.getGradeLetter() == null && grade.getGradeValue() != null && grade.getMaxPoints() != null) {
            grade.setGradeLetter(grade.calculateLetterGrade());
        }
        if (grade.getGradePoints() == null && grade.getGradeLetter() != null) {
            grade.setGradePoints(grade.calculateGradePoints());
        }
        
        // Set audit fields
        grade.setCreatedAt(LocalDateTime.now());
        grade.setUpdatedAt(LocalDateTime.now());
        
        Grade savedGrade = gradeRepository.save(grade);
        return gradeMapper.toDto(savedGrade);
    }

    @Override
    public GradeDTO updateGrade(Long id, GradeDTO gradeDTO) {
        Grade existingGrade = gradeRepository.findById(id)
                .orElseThrow(() -> GradeNotFoundException.withId(id));

        // Update fields
        if (gradeDTO.getGradeValue() != null) {
            existingGrade.setGradeValue(gradeDTO.getGradeValue());
        }
        if (gradeDTO.getComment() != null) {
            existingGrade.setComment(gradeDTO.getComment());
        }
        if (gradeDTO.getGradeType() != null) {
            existingGrade.setGradeType(gradeDTO.getGradeType());
        }
        if (gradeDTO.getGradeLetter() != null) {
            existingGrade.setGradeLetter(gradeDTO.getGradeLetter());
        }
        if (gradeDTO.getGradePoints() != null) {
            existingGrade.setGradePoints(gradeDTO.getGradePoints());
        }
        if (gradeDTO.getStatus() != null) {
            existingGrade.setStatus(gradeDTO.getStatus());
        }
        if (gradeDTO.getIsFinal() != null) {
            existingGrade.setIsFinal(gradeDTO.getIsFinal());
        }
        if (gradeDTO.getWeight() != null) {
            existingGrade.setWeight(gradeDTO.getWeight());
        }
        if (gradeDTO.getAssignmentName() != null) {
            existingGrade.setAssignmentName(gradeDTO.getAssignmentName());
        }
        if (gradeDTO.getAssignmentType() != null) {
            existingGrade.setAssignmentType(gradeDTO.getAssignmentType());
        }
        if (gradeDTO.getMaxPoints() != null) {
            existingGrade.setMaxPoints(gradeDTO.getMaxPoints());
        }
        if (gradeDTO.getCurveApplied() != null) {
            existingGrade.setCurveApplied(gradeDTO.getCurveApplied());
        }
        if (gradeDTO.getCurveValue() != null) {
            existingGrade.setCurveValue(gradeDTO.getCurveValue());
        }
        if (gradeDTO.getFeedback() != null) {
            existingGrade.setFeedback(gradeDTO.getFeedback());
        }
        
        // Recalculate letter grade and grade points if grade value or max points changed
        if ((gradeDTO.getGradeValue() != null || gradeDTO.getMaxPoints() != null) && 
            existingGrade.getGradeValue() != null && existingGrade.getMaxPoints() != null) {
            existingGrade.setGradeLetter(existingGrade.calculateLetterGrade());
            existingGrade.setGradePoints(existingGrade.calculateGradePoints());
        }
        
        existingGrade.setUpdatedAt(LocalDateTime.now());

        Grade updatedGrade = gradeRepository.save(existingGrade);
        return gradeMapper.toDto(updatedGrade);
    }

    @Override
    public void deleteGrade(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> GradeNotFoundException.withId(id));
        gradeRepository.delete(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByStudentId(Long studentId) {
        List<Grade> grades = gradeRepository.findByEnrollmentStudentId(studentId);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByCourseId(Long courseId) {
        List<Grade> grades = gradeRepository.findByEnrollmentCourseId(courseId);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByProfessorId(Long professorId) {
        List<Grade> grades = gradeRepository.findByEnrollmentCourseProfessorId(professorId);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByStudentIdAndCourseId(Long studentId, Long courseId) {
        List<Grade> grades = gradeRepository.findByEnrollmentStudentIdAndEnrollmentCourseId(studentId, courseId);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByStudentIdAndGradeType(Long studentId, GradeType gradeType) {
        List<Grade> grades = gradeRepository.findByEnrollmentStudentIdAndGradeType(studentId, gradeType);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByCourseIdAndGradeType(Long courseId, GradeType gradeType) {
        List<Grade> grades = gradeRepository.findByEnrollmentCourseIdAndGradeType(courseId, gradeType);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByStatus(GradeStatus status) {
        List<Grade> grades = gradeRepository.findByStatus(status);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getFinalGrades() {
        List<Grade> grades = gradeRepository.findByIsFinal(true);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByStudentIdAndIsFinal(Long studentId, Boolean isFinal) {
        List<Grade> grades = gradeRepository.findByEnrollmentStudentIdAndIsFinal(studentId, isFinal);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByCourseIdAndIsFinal(Long courseId, Boolean isFinal) {
        List<Grade> grades = gradeRepository.findByEnrollmentCourseIdAndIsFinal(courseId, isFinal);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByStudentIdAndSemester(Long studentId, String semester, Integer academicYear) {
        List<Grade> grades = gradeRepository.findByStudentIdAndSemesterAndAcademicYear(studentId, semester, academicYear);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByProfessorIdAndSemester(Long professorId, String semester, Integer academicYear) {
        List<Grade> grades = gradeRepository.findByProfessorIdAndSemesterAndAcademicYear(professorId, semester, academicYear);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Grade> grades = gradeRepository.findByDateAssignedBetween(startDate, endDate);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByStudentIdAndDateRange(Long studentId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Grade> grades = gradeRepository.findByStudentIdAndDateAssignedBetween(studentId, startDate, endDate);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByCourseIdAndDateRange(Long courseId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Grade> grades = gradeRepository.findByCourseIdAndDateAssignedBetween(courseId, startDate, endDate);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByProfessorIdAndDateRange(Long professorId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Grade> grades = gradeRepository.findByProfessorIdAndDateAssignedBetween(professorId, startDate, endDate);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByStudentIdAndGradeRange(Long studentId, Double minGrade, Double maxGrade) {
        List<Grade> grades = gradeRepository.findByStudentIdAndGradeValueGreaterThanEqual(studentId, minGrade);
        grades.retainAll(gradeRepository.findByStudentIdAndGradeValueLessThanEqual(studentId, maxGrade));
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByCourseIdAndGradeRange(Long courseId, Double minGrade, Double maxGrade) {
        List<Grade> grades = gradeRepository.findByCourseIdAndGradeValueGreaterThanEqual(courseId, minGrade);
        grades.retainAll(gradeRepository.findByCourseIdAndGradeValueLessThanEqual(courseId, maxGrade));
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByStudentIdAndGradeLetter(Long studentId, String gradeLetter) {
        List<Grade> grades = gradeRepository.findByStudentIdAndGradeLetter(studentId, gradeLetter);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByCourseIdAndGradeLetter(Long courseId, String gradeLetter) {
        List<Grade> grades = gradeRepository.findByCourseIdAndGradeLetter(courseId, gradeLetter);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradedEnrollmentsByStudentId(Long studentId) {
        List<Grade> grades = gradeRepository.findGradedEnrollmentsByStudentId(studentId);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getUngradedEnrollmentsByStudentId(Long studentId) {
        List<Grade> grades = gradeRepository.findUngradedEnrollmentsByStudentId(studentId);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getGradedEnrollmentsByCourseId(Long courseId) {
        List<Grade> grades = gradeRepository.findGradedEnrollmentsByCourseId(courseId);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> getUngradedEnrollmentsByCourseId(Long courseId) {
        List<Grade> grades = gradeRepository.findUngradedEnrollmentsByCourseId(courseId);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getGradeCountByStudentId(Long studentId) {
        return gradeRepository.countByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getGradeCountByCourseId(Long courseId) {
        return gradeRepository.countByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getGradeCountByProfessorId(Long professorId) {
        return gradeRepository.countByProfessorId(professorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getGradeCountByStudentIdAndStatus(Long studentId, GradeStatus status) {
        return gradeRepository.countByStudentIdAndStatus(studentId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getGradeCountByCourseIdAndStatus(Long courseId, GradeStatus status) {
        return gradeRepository.countByCourseIdAndStatus(courseId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getGradeCountByProfessorIdAndStatus(Long professorId, GradeStatus status) {
        return gradeRepository.countByProfessorIdAndStatus(professorId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageGradeByStudentId(Long studentId) {
        return gradeRepository.getAverageGradeByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageGradeByCourseId(Long courseId) {
        return gradeRepository.getAverageGradeByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageGradeByProfessorId(Long professorId) {
        return gradeRepository.getAverageGradeByProfessorId(professorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getMaxGradeByStudentId(Long studentId) {
        return gradeRepository.getMaxGradeByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getMinGradeByStudentId(Long studentId) {
        return gradeRepository.getMinGradeByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getMaxGradeByCourseId(Long courseId) {
        return gradeRepository.getMaxGradeByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getMinGradeByCourseId(Long courseId) {
        return gradeRepository.getMinGradeByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEnrollmentId(Long enrollmentId) {
        return gradeRepository.existsByEnrollmentId(enrollmentId);
    }
} 