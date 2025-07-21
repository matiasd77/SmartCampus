package com.smartcampus.mapper;

import com.smartcampus.dto.EnrollmentDTO;
import com.smartcampus.entity.Enrollment;

import java.util.List;

public interface EnrollmentMapper {
    EnrollmentDTO toDto(Enrollment enrollment);
    Enrollment toEntity(EnrollmentDTO enrollmentDTO);
    List<EnrollmentDTO> toDtoList(List<Enrollment> enrollments);
} 