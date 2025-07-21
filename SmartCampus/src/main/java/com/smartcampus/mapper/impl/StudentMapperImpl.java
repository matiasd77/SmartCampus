package com.smartcampus.mapper.impl;

import com.smartcampus.dto.StudentDTO;
import com.smartcampus.entity.Student;
import com.smartcampus.mapper.StudentMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentMapperImpl implements StudentMapper {

    @Override
    public StudentDTO toDto(Student student) {
        if (student == null) {
            return null;
        }

        return StudentDTO.builder()
                .id(student.getId())
                .studentId(student.getStudentId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getUser() != null ? student.getUser().getEmail() : null)
                .dateOfBirth(student.getDateOfBirth())
                .phoneNumber(student.getPhoneNumber())
                .address(student.getAddress())
                .major(student.getMajor())
                .yearOfStudy(student.getYearOfStudy())
                .gpa(student.getGpa())
                .status(student.getStatus())
                .userId(student.getUser() != null ? student.getUser().getId() : null)
                .build();
    }

    @Override
    public Student toEntity(StudentDTO studentDTO) {
        if (studentDTO == null) {
            return null;
        }

        return Student.builder()
                .id(studentDTO.getId())
                .studentId(studentDTO.getStudentId())
                .firstName(studentDTO.getFirstName())
                .lastName(studentDTO.getLastName())
                .dateOfBirth(studentDTO.getDateOfBirth())
                .phoneNumber(studentDTO.getPhoneNumber())
                .address(studentDTO.getAddress())
                .major(studentDTO.getMajor())
                .yearOfStudy(studentDTO.getYearOfStudy())
                .gpa(studentDTO.getGpa())
                .status(studentDTO.getStatus())
                .build();
    }

    @Override
    public List<StudentDTO> toDtoList(List<Student> students) {
        if (students == null) {
            return null;
        }

        return students.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
} 