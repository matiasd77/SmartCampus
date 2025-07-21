package com.smartcampus.service;

import com.smartcampus.dto.CourseDTO;
import com.smartcampus.entity.CourseStatus;

import java.util.List;

public interface CourseService {
    List<CourseDTO> getAllCourses();
    CourseDTO getCourseById(Long id);
    CourseDTO getCourseByCode(String code);
    CourseDTO createCourse(CourseDTO courseDTO);
    CourseDTO updateCourse(Long id, CourseDTO courseDTO);
    void deleteCourse(Long id);
    List<CourseDTO> getCoursesByProfessorId(Long professorId);
    List<CourseDTO> getCoursesByStatus(CourseStatus status);
    List<CourseDTO> getCoursesBySemester(String semester);
    List<CourseDTO> getCoursesByAcademicYear(Integer academicYear);
    List<CourseDTO> getCoursesByDepartment(String department);
    List<CourseDTO> getAvailableCourses();
    List<CourseDTO> searchCoursesByNameOrCode(String searchTerm);
    List<CourseDTO> getCoursesByProfessorIdAndSearchTerm(Long professorId, String searchTerm);
    Long getCourseCountByProfessorId(Long professorId);
    Long getCourseCountByStatus(CourseStatus status);
    Long getCourseCountBySemesterAndAcademicYear(String semester, Integer academicYear);
    boolean isCodeAvailable(String code, Long excludeId);
} 