package com.smartcampus.mapper.impl;

import com.smartcampus.dto.GradeDTO;
import com.smartcampus.entity.Grade;
import com.smartcampus.mapper.GradeMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GradeMapperImpl implements GradeMapper {

    @Override
    public GradeDTO toDto(Grade grade) {
        if (grade == null) {
            return null;
        }

        return GradeDTO.builder()
                .id(grade.getId())
                .enrollmentId(grade.getEnrollment() != null ? grade.getEnrollment().getId() : null)
                .studentId(grade.getEnrollment() != null && grade.getEnrollment().getStudent() != null ? 
                        grade.getEnrollment().getStudent().getId() : null)
                .studentName(grade.getEnrollment() != null && grade.getEnrollment().getStudent() != null ? 
                        grade.getEnrollment().getStudent().getFirstName() + " " + grade.getEnrollment().getStudent().getLastName() : null)
                .studentIdNumber(grade.getEnrollment() != null && grade.getEnrollment().getStudent() != null ? 
                        grade.getEnrollment().getStudent().getStudentId() : null)
                .courseId(grade.getEnrollment() != null && grade.getEnrollment().getCourse() != null ? 
                        grade.getEnrollment().getCourse().getId() : null)
                .courseName(grade.getEnrollment() != null && grade.getEnrollment().getCourse() != null ? 
                        grade.getEnrollment().getCourse().getName() : null)
                .courseCode(grade.getEnrollment() != null && grade.getEnrollment().getCourse() != null ? 
                        grade.getEnrollment().getCourse().getCode() : null)
                .professorName(grade.getEnrollment() != null && grade.getEnrollment().getCourse() != null && 
                        grade.getEnrollment().getCourse().getProfessor() != null ? 
                        grade.getEnrollment().getCourse().getProfessor().getFirstName() + " " + grade.getEnrollment().getCourse().getProfessor().getLastName() : null)
                .gradeValue(grade.getGradeValue())
                .comment(grade.getComment())
                .dateAssigned(grade.getDateAssigned())
                .gradeType(grade.getGradeType())
                .gradeLetter(grade.getGradeLetter())
                .gradePoints(grade.getGradePoints())
                .status(grade.getStatus())
                .isFinal(grade.getIsFinal())
                .weight(grade.getWeight())
                .assignmentName(grade.getAssignmentName())
                .assignmentType(grade.getAssignmentType())
                .maxPoints(grade.getMaxPoints())
                .curveApplied(grade.getCurveApplied())
                .curveValue(grade.getCurveValue())
                .feedback(grade.getFeedback())
                .createdAt(grade.getCreatedAt())
                .updatedAt(grade.getUpdatedAt())
                .build();
    }

    @Override
    public Grade toEntity(GradeDTO gradeDTO) {
        if (gradeDTO == null) {
            return null;
        }

        return Grade.builder()
                .id(gradeDTO.getId())
                .gradeValue(gradeDTO.getGradeValue())
                .comment(gradeDTO.getComment())
                .dateAssigned(gradeDTO.getDateAssigned())
                .gradeType(gradeDTO.getGradeType())
                .gradeLetter(gradeDTO.getGradeLetter())
                .gradePoints(gradeDTO.getGradePoints())
                .status(gradeDTO.getStatus())
                .isFinal(gradeDTO.getIsFinal())
                .weight(gradeDTO.getWeight())
                .assignmentName(gradeDTO.getAssignmentName())
                .assignmentType(gradeDTO.getAssignmentType())
                .maxPoints(gradeDTO.getMaxPoints())
                .curveApplied(gradeDTO.getCurveApplied())
                .curveValue(gradeDTO.getCurveValue())
                .feedback(gradeDTO.getFeedback())
                .build();
    }

    @Override
    public List<GradeDTO> toDtoList(List<Grade> grades) {
        if (grades == null) {
            return null;
        }

        return grades.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
} 