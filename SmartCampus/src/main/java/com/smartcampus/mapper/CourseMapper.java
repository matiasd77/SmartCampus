package com.smartcampus.mapper;

import com.smartcampus.dto.CourseDTO;
import com.smartcampus.entity.Course;

import java.util.List;

public interface CourseMapper {
    CourseDTO toDto(Course course);
    Course toEntity(CourseDTO courseDTO);
    List<CourseDTO> toDtoList(List<Course> courses);
} 