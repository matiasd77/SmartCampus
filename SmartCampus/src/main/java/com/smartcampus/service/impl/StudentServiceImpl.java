package com.smartcampus.service.impl;

import com.smartcampus.dto.StudentDTO;
import com.smartcampus.entity.Student;
import com.smartcampus.entity.StudentStatus;
import com.smartcampus.entity.User;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.mapper.StudentMapper;
import com.smartcampus.repository.StudentRepository;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final StudentMapper studentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudents() {
        System.out.println("DEBUG: StudentServiceImpl.getAllStudents() called");
        List<Student> students = studentRepository.findAll();
        System.out.println("DEBUG: Found " + students.size() + " students in database");
        
        if (students.isEmpty()) {
            System.out.println("DEBUG: No students found in database");
        } else {
            System.out.println("DEBUG: First student details:");
            Student firstStudent = students.get(0);
            System.out.println("  - ID: " + firstStudent.getId());
            System.out.println("  - Student ID: " + firstStudent.getStudentId());
            System.out.println("  - Name: " + firstStudent.getFirstName() + " " + firstStudent.getLastName());
            System.out.println("  - User: " + (firstStudent.getUser() != null ? "Linked to user ID " + firstStudent.getUser().getId() : "No user linked"));
        }
        
        List<StudentDTO> studentDTOs = studentMapper.toDtoList(students);
        System.out.println("DEBUG: Mapped to " + studentDTOs.size() + " DTOs");
        
        return studentDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return studentMapper.toDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO getStudentByUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with user id: " + userId));
        return studentMapper.toDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO getStudentByEmail(String email) {
        Student student = studentRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
        return studentMapper.toDto(student);
    }

    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        System.out.println("🔍 StudentServiceImpl.createStudent - Starting with data: " + studentDTO);
        
        // Check if student ID already exists
        if (studentRepository.existsByStudentId(studentDTO.getStudentId())) {
            System.err.println("🔍 StudentServiceImpl.createStudent - Student ID already exists: " + studentDTO.getStudentId());
            throw new RuntimeException("Student with ID " + studentDTO.getStudentId() + " already exists");
        }

        // Check if user exists and is a student
        User user = userRepository.findById(studentDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + studentDTO.getUserId()));

        System.out.println("🔍 StudentServiceImpl.createStudent - Found user: " + user);

        if (user.getRole() != com.smartcampus.entity.Role.STUDENT) {
            System.err.println("🔍 StudentServiceImpl.createStudent - User role is not STUDENT: " + user.getRole());
            throw new RuntimeException("User must have STUDENT role");
        }

        // Check if user already has a student profile
        if (studentRepository.existsByUserId(studentDTO.getUserId())) {
            System.err.println("🔍 StudentServiceImpl.createStudent - User already has student profile: " + studentDTO.getUserId());
            throw new RuntimeException("User already has a student profile");
        }

        System.out.println("🔍 StudentServiceImpl.createStudent - Creating student entity...");
        Student student = studentMapper.toEntity(studentDTO);
        System.out.println("🔍 StudentServiceImpl.createStudent - Mapped entity: " + student);
        
        student.setUser(user);
        System.out.println("🔍 StudentServiceImpl.createStudent - Set user on student: " + student);
        
        System.out.println("🔍 StudentServiceImpl.createStudent - About to save student...");
        Student savedStudent = studentRepository.save(student);
        System.out.println("🔍 StudentServiceImpl.createStudent - Student saved: " + savedStudent);
        
        StudentDTO result = studentMapper.toDto(savedStudent);
        System.out.println("🔍 StudentServiceImpl.createStudent - Returning DTO: " + result);
        return result;
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        // Check if student ID is being changed and if it already exists
        if (!existingStudent.getStudentId().equals(studentDTO.getStudentId()) &&
                studentRepository.existsByStudentId(studentDTO.getStudentId())) {
            throw new RuntimeException("Student with ID " + studentDTO.getStudentId() + " already exists");
        }

        // Update fields
        existingStudent.setStudentId(studentDTO.getStudentId());
        existingStudent.setFirstName(studentDTO.getFirstName());
        existingStudent.setLastName(studentDTO.getLastName());
        existingStudent.setDateOfBirth(studentDTO.getDateOfBirth());
        existingStudent.setPhoneNumber(studentDTO.getPhoneNumber());
        existingStudent.setAddress(studentDTO.getAddress());
        existingStudent.setMajor(studentDTO.getMajor());
        existingStudent.setYearOfStudy(studentDTO.getYearOfStudy());
        existingStudent.setGpa(studentDTO.getGpa());
        existingStudent.setStatus(studentDTO.getStatus());

        Student updatedStudent = studentRepository.save(existingStudent);
        return studentMapper.toDto(updatedStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsByMajor(String major) {
        List<Student> students = studentRepository.findByMajor(major);
        return studentMapper.toDtoList(students);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsByStatus(String status) {
        try {
            StudentStatus studentStatus = StudentStatus.valueOf(status.toUpperCase());
            List<Student> students = studentRepository.findByStatus(studentStatus);
            return studentMapper.toDtoList(students);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsByYear(Integer year) {
        List<Student> students = studentRepository.findByYearOfStudy(year);
        return studentMapper.toDtoList(students);
    }

    @Override
    @Transactional(readOnly = true)
    public long getStudentCount() {
        return studentRepository.count();
    }

    @Override
    public List<StudentDTO> createTestStudents() {
        // This method is for testing purposes - create some sample students
        // Implementation would depend on your test data requirements
        throw new UnsupportedOperationException("createTestStudents not implemented");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentDTO> getAllStudents(Pageable pageable) {
        Page<Student> students = studentRepository.findAll(pageable);
        return students.map(studentMapper::toDto);
    }
} 