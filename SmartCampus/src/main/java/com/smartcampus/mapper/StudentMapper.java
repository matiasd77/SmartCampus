package com.smartcampus.mapper;

import com.smartcampus.dto.StudentDTO;
import com.smartcampus.entity.Student;

import java.util.List;

public interface StudentMapper {
    StudentDTO toDto(Student student);
    Student toEntity(StudentDTO studentDTO);
    List<StudentDTO> toDtoList(List<Student> students);
} 