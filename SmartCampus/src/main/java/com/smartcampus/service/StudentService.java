package com.smartcampus.service;

import com.smartcampus.dto.StudentDTO;
import com.smartcampus.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {
    List<StudentDTO> getAllStudents();
    Page<StudentDTO> getAllStudents(Pageable pageable);
    StudentDTO getStudentById(Long id);
    StudentDTO getStudentByUserId(Long userId);
    StudentDTO getStudentByEmail(String email);
    StudentDTO createStudent(StudentDTO studentDTO);
    StudentDTO updateStudent(Long id, StudentDTO studentDTO);
    void deleteStudent(Long id);
    List<StudentDTO> getStudentsByMajor(String major);
    List<StudentDTO> getStudentsByStatus(String status);
    List<StudentDTO> getStudentsByYear(Integer year);
    long getStudentCount();
    List<StudentDTO> createTestStudents();
} 