package com.smartcampus.mapper.impl;

import com.smartcampus.dto.EnrollmentDTO;
import com.smartcampus.entity.Enrollment;
import com.smartcampus.mapper.EnrollmentMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EnrollmentMapperImpl implements EnrollmentMapper {

    @Override
    public EnrollmentDTO toDto(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }

        return EnrollmentDTO.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent() != null ? enrollment.getStudent().getId() : null)
                .studentName(enrollment.getStudent() != null ? enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName() : null)
                .studentIdNumber(enrollment.getStudent() != null ? enrollment.getStudent().getStudentId() : null)
                .studentEmail(enrollment.getStudent() != null && enrollment.getStudent().getUser() != null ? 
                        enrollment.getStudent().getUser().getEmail() : null)
                .courseId(enrollment.getCourse() != null ? enrollment.getCourse().getId() : null)
                .courseName(enrollment.getCourse() != null ? enrollment.getCourse().getName() : null)
                .courseCode(enrollment.getCourse() != null ? enrollment.getCourse().getCode() : null)
                .courseDescription(enrollment.getCourse() != null ? enrollment.getCourse().getDescription() : null)
                .courseCredits(enrollment.getCourse() != null ? enrollment.getCourse().getCredits() : null)
                .courseSemester(enrollment.getCourse() != null ? enrollment.getCourse().getSemester() : null)
                .courseAcademicYear(enrollment.getCourse() != null ? enrollment.getCourse().getAcademicYear() : null)
                .courseSchedule(enrollment.getCourse() != null ? enrollment.getCourse().getSchedule() : null)
                .courseLocation(enrollment.getCourse() != null ? enrollment.getCourse().getLocation() : null)
                .professorName(enrollment.getCourse() != null && enrollment.getCourse().getProfessor() != null ? 
                        enrollment.getCourse().getProfessor().getFullName() : null)
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus())
                .dropDate(enrollment.getDropDate())
                .withdrawalReason(enrollment.getWithdrawalReason())
                .gradeLetter(enrollment.getGradeLetter())
                .gradePoints(enrollment.getGradePoints())
                .attendancePercentage(enrollment.getAttendancePercentage())
                .isActive(enrollment.getIsActive())
                .notes(enrollment.getNotes())
                .createdAt(enrollment.getCreatedAt())
                .updatedAt(enrollment.getUpdatedAt())
                .build();
    }

    @Override
    public Enrollment toEntity(EnrollmentDTO enrollmentDTO) {
        if (enrollmentDTO == null) {
            return null;
        }

        return Enrollment.builder()
                .id(enrollmentDTO.getId())
                .enrollmentDate(enrollmentDTO.getEnrollmentDate())
                .status(enrollmentDTO.getStatus())
                .dropDate(enrollmentDTO.getDropDate())
                .withdrawalReason(enrollmentDTO.getWithdrawalReason())
                .gradeLetter(enrollmentDTO.getGradeLetter())
                .gradePoints(enrollmentDTO.getGradePoints())
                .attendancePercentage(enrollmentDTO.getAttendancePercentage())
                .isActive(enrollmentDTO.getIsActive())
                .notes(enrollmentDTO.getNotes())
                .build();
    }

    @Override
    public List<EnrollmentDTO> toDtoList(List<Enrollment> enrollments) {
        if (enrollments == null) {
            return null;
        }

        return enrollments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
} 