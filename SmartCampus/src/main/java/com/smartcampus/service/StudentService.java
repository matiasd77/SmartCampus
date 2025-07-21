package com.smartcampus.service;

import com.smartcampus.dto.StudentDTO;
import com.smartcampus.entity.Student;

import java.util.List;

public interface StudentService {
    List<StudentDTO> getAllStudents();
    StudentDTO getStudentById(Long id);
    StudentDTO getStudentByUserId(Long userId);
    StudentDTO getStudentByEmail(String email);
    StudentDTO createStudent(StudentDTO studentDTO);
    StudentDTO updateStudent(Long id, StudentDTO studentDTO);
    void deleteStudent(Long id);
    List<StudentDTO> getStudentsByMajor(String major);
    List<StudentDTO> getStudentsByStatus(String status);
    List<StudentDTO> getStudentsByYear(Integer year);
} 