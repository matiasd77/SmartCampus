package com.smartcampus.mapper.impl;

import com.smartcampus.dto.CourseDTO;
import com.smartcampus.entity.Course;
import com.smartcampus.mapper.CourseMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseMapperImpl implements CourseMapper {

    @Override
    public CourseDTO toDto(Course course) {
        if (course == null) {
            return null;
        }

        return CourseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .code(course.getCode())
                .description(course.getDescription())
                .professorId(course.getProfessor() != null ? course.getProfessor().getId() : null)
                .professorName(course.getProfessor() != null ? course.getProfessor().getFullName() : null)
                .professorEmail(course.getProfessor() != null ? course.getProfessor().getEmail() : null)
                .semester(course.getSemester())
                .academicYear(course.getAcademicYear())
                .credits(course.getCredits())
                .schedule(course.getSchedule())
                .location(course.getLocation())
                .status(course.getStatus())
                .maxStudents(course.getMaxStudents())
                .currentEnrollment(course.getCurrentEnrollment())
                .availableSeats(course.getAvailableSeats())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    @Override
    public Course toEntity(CourseDTO courseDTO) {
        if (courseDTO == null) {
            return null;
        }

        return Course.builder()
                .id(courseDTO.getId())
                .name(courseDTO.getName())
                .code(courseDTO.getCode())
                .description(courseDTO.getDescription())
                .semester(courseDTO.getSemester())
                .academicYear(courseDTO.getAcademicYear())
                .credits(courseDTO.getCredits())
                .schedule(courseDTO.getSchedule())
                .location(courseDTO.getLocation())
                .status(courseDTO.getStatus())
                .maxStudents(courseDTO.getMaxStudents())
                .currentEnrollment(courseDTO.getCurrentEnrollment())
                .build();
    }

    @Override
    public List<CourseDTO> toDtoList(List<Course> courses) {
        if (courses == null) {
            return null;
        }

        return courses.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
} 