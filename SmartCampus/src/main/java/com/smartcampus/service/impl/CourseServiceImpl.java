package com.smartcampus.service.impl;

import com.smartcampus.dto.CourseDTO;
import com.smartcampus.entity.Course;
import com.smartcampus.entity.CourseStatus;
import com.smartcampus.entity.Professor;
import com.smartcampus.exception.CourseNotFoundException;
import com.smartcampus.exception.ProfessorNotFoundException;
import com.smartcampus.mapper.CourseMapper;
import com.smartcampus.repository.CourseRepository;
import com.smartcampus.repository.ProfessorRepository;
import com.smartcampus.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;
    private final CourseMapper courseMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courseMapper.toDtoList(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseDTO> getAllCourses(Pageable pageable) {
        Page<Course> courses = courseRepository.findAll(pageable);
        return courses.map(courseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> CourseNotFoundException.withId(id));
        return courseMapper.toDto(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO getCourseByCode(String code) {
        Course course = courseRepository.findByCode(code)
                .orElseThrow(() -> CourseNotFoundException.withCode(code));
        return courseMapper.toDto(course);
    }

    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {
        // Check if course code already exists
        if (courseRepository.existsByCode(courseDTO.getCode())) {
            throw new RuntimeException("Course code is already taken by another course");
        }

        // Validate professor exists
        Professor professor = professorRepository.findById(courseDTO.getProfessorId())
                .orElseThrow(() -> ProfessorNotFoundException.withId(courseDTO.getProfessorId()));

        Course course = courseMapper.toEntity(courseDTO);
        course.setProfessor(professor);
        
        // Set default status if not provided
        if (course.getStatus() == null) {
            course.setStatus(CourseStatus.ACTIVE);
        }
        
        // Initialize enrollment count
        if (course.getCurrentEnrollment() == null) {
            course.setCurrentEnrollment(0);
        }
        
        // Set audit fields
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toDto(savedCourse);
    }

    @Override
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> CourseNotFoundException.withId(id));

        // Check if course code is being changed and if it's already taken
        if (!existingCourse.getCode().equals(courseDTO.getCode()) &&
            courseRepository.existsByCode(courseDTO.getCode())) {
            throw new RuntimeException("Course code is already taken by another course");
        }

        // Validate professor exists if being changed
        if (!existingCourse.getProfessor().getId().equals(courseDTO.getProfessorId())) {
            Professor professor = professorRepository.findById(courseDTO.getProfessorId())
                    .orElseThrow(() -> ProfessorNotFoundException.withId(courseDTO.getProfessorId()));
            existingCourse.setProfessor(professor);
        }

        // Update fields
        existingCourse.setName(courseDTO.getName());
        existingCourse.setCode(courseDTO.getCode());
        existingCourse.setDescription(courseDTO.getDescription());
        existingCourse.setSemester(courseDTO.getSemester());
        existingCourse.setAcademicYear(courseDTO.getAcademicYear());
        existingCourse.setCredits(courseDTO.getCredits());
        existingCourse.setSchedule(courseDTO.getSchedule());
        existingCourse.setLocation(courseDTO.getLocation());
        existingCourse.setStatus(courseDTO.getStatus());
        existingCourse.setMaxStudents(courseDTO.getMaxStudents());
        existingCourse.setUpdatedAt(LocalDateTime.now());

        Course updatedCourse = courseRepository.save(existingCourse);
        return courseMapper.toDto(updatedCourse);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> CourseNotFoundException.withId(id));
        
        // Check if course has enrolled students
        if (course.getCurrentEnrollment() != null && course.getCurrentEnrollment() > 0) {
            throw new RuntimeException("Cannot delete course with enrolled students. Please unenroll students first.");
        }
        
        courseRepository.delete(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByProfessorId(Long professorId) {
        List<Course> courses = courseRepository.findByProfessorId(professorId);
        return courseMapper.toDtoList(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByStatus(CourseStatus status) {
        List<Course> courses = courseRepository.findByStatus(status);
        return courseMapper.toDtoList(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesBySemester(String semester) {
        List<Course> courses = courseRepository.findBySemester(semester);
        return courseMapper.toDtoList(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByAcademicYear(Integer academicYear) {
        List<Course> courses = courseRepository.findByAcademicYear(academicYear);
        return courseMapper.toDtoList(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByDepartment(String department) {
        List<Course> courses = courseRepository.findByDepartment(department);
        return courseMapper.toDtoList(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getAvailableCourses() {
        List<Course> courses = courseRepository.findAvailableCourses();
        return courseMapper.toDtoList(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> searchCoursesByNameOrCode(String searchTerm) {
        List<Course> courses = courseRepository.findByNameOrCodeContaining(searchTerm, searchTerm);
        return courseMapper.toDtoList(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByProfessorIdAndSearchTerm(Long professorId, String searchTerm) {
        List<Course> courses = courseRepository.findByProfessorIdAndSearchTerm(professorId, searchTerm);
        return courseMapper.toDtoList(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCourseCountByProfessorId(Long professorId) {
        return courseRepository.countByProfessorId(professorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCourseCountByStatus(CourseStatus status) {
        return courseRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCourseCountBySemesterAndAcademicYear(String semester, Integer academicYear) {
        return courseRepository.countBySemesterAndAcademicYear(semester, academicYear);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCodeAvailable(String code, Long excludeId) {
        if (excludeId == null) {
            return !courseRepository.existsByCode(code);
        }
        return courseRepository.findByCodeAndIdNot(code, excludeId).isEmpty();
    }
} 