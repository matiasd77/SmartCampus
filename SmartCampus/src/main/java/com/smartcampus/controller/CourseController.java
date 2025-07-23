package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.CourseDTO;
import com.smartcampus.entity.CourseStatus;
import com.smartcampus.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get All Courses",
        description = "Retrieve a list of all courses in the system"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Courses retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(ApiResponse.success("Courses retrieved successfully", courses));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course by ID",
        description = "Retrieve a specific course by its ID"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Course not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<CourseDTO>> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(ApiResponse.success("Course retrieved successfully", course));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course by Code",
        description = "Retrieve a specific course by its code"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Course not found"
        )
    })
    public ResponseEntity<ApiResponse<CourseDTO>> getCourseByCode(@PathVariable String code) {
        CourseDTO course = courseService.getCourseByCode(code);
        return ResponseEntity.ok(ApiResponse.success("Course retrieved successfully", course));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Create New Course",
        description = "Create a new course in the system (Professor or Admin only)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Course details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseDTO.class),
                examples = @ExampleObject(
                    name = "Course Creation",
                    value = "{\"name\": \"Introduction to Computer Science\", \"code\": \"CS101\", \"description\": \"Basic concepts of programming\", \"professorId\": 1, \"credits\": 3, \"semester\": \"Fall\", \"academicYear\": 2024}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Course created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error or course code already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = "{\"success\": false, \"message\": \"Validation failed\", \"data\": {\"name\": \"Course name is required\", \"code\": \"Course code is required\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<CourseDTO>> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        CourseDTO createdCourse = courseService.createCourse(courseDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course created successfully", createdCourse));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Update Course",
        description = "Update an existing course's information (Professor or Admin only)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated course details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseDTO.class)
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Course not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<CourseDTO>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO courseDTO) {
        CourseDTO updatedCourse = courseService.updateCourse(id, courseDTO);
        return ResponseEntity.ok(ApiResponse.success("Course updated successfully", updatedCourse));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Delete Course",
        description = "Delete a course from the system (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course deleted successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Course not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Cannot delete course with enrolled students"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<String>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success("Course deleted successfully", null));
    }

    @GetMapping("/professor/{professorId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Courses by Professor",
        description = "Retrieve all courses taught by a specific professor"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Courses retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getCoursesByProfessorId(@PathVariable Long professorId) {
        List<CourseDTO> courses = courseService.getCoursesByProfessorId(professorId);
        return ResponseEntity.ok(ApiResponse.success("Courses by professor retrieved successfully", courses));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Courses by Status",
        description = "Retrieve all courses with a specific status"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Courses retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getCoursesByStatus(@PathVariable CourseStatus status) {
        List<CourseDTO> courses = courseService.getCoursesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Courses by status retrieved successfully", courses));
    }

    @GetMapping("/semester/{semester}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Courses by Semester",
        description = "Retrieve all courses in a specific semester"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Courses retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getCoursesBySemester(@PathVariable String semester) {
        List<CourseDTO> courses = courseService.getCoursesBySemester(semester);
        return ResponseEntity.ok(ApiResponse.success("Courses by semester retrieved successfully", courses));
    }

    @GetMapping("/year/{academicYear}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Courses by Academic Year",
        description = "Retrieve all courses in a specific academic year"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Courses retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getCoursesByAcademicYear(@PathVariable Integer academicYear) {
        List<CourseDTO> courses = courseService.getCoursesByAcademicYear(academicYear);
        return ResponseEntity.ok(ApiResponse.success("Courses by academic year retrieved successfully", courses));
    }

    @GetMapping("/department/{department}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Courses by Department",
        description = "Retrieve all courses in a specific department"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Courses retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getCoursesByDepartment(@PathVariable String department) {
        List<CourseDTO> courses = courseService.getCoursesByDepartment(department);
        return ResponseEntity.ok(ApiResponse.success("Courses by department retrieved successfully", courses));
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Available Courses",
        description = "Retrieve all courses with available seats"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Available courses retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getAvailableCourses() {
        List<CourseDTO> courses = courseService.getAvailableCourses();
        return ResponseEntity.ok(ApiResponse.success("Available courses retrieved successfully", courses));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Search Courses",
        description = "Search courses by name or code (partial match)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Courses search completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<CourseDTO>>> searchCourses(@RequestParam String searchTerm) {
        List<CourseDTO> courses = courseService.searchCoursesByNameOrCode(searchTerm);
        return ResponseEntity.ok(ApiResponse.success("Courses search completed successfully", courses));
    }

    @GetMapping("/professor/{professorId}/search")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Search Courses by Professor",
        description = "Search courses by professor ID and search term"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Courses search completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CourseDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<CourseDTO>>> searchCoursesByProfessor(
            @PathVariable Long professorId,
            @RequestParam String searchTerm) {
        List<CourseDTO> courses = courseService.getCoursesByProfessorIdAndSearchTerm(professorId, searchTerm);
        return ResponseEntity.ok(ApiResponse.success("Courses search completed successfully", courses));
    }

    @GetMapping("/count/professor/{professorId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Count by Professor",
        description = "Get the count of courses taught by a specific professor (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Count Response",
                    value = "{\"success\": true, \"message\": \"Count retrieved successfully\", \"data\": 5}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> getCourseCountByProfessorId(@PathVariable Long professorId) {
        Long count = courseService.getCourseCountByProfessorId(professorId);
        return ResponseEntity.ok(ApiResponse.success("Course count by professor retrieved successfully", count));
    }

    @GetMapping("/count/status/{status}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Count by Status",
        description = "Get the count of courses with a specific status (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Count Response",
                    value = "{\"success\": true, \"message\": \"Count retrieved successfully\", \"data\": 15}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> getCourseCountByStatus(@PathVariable CourseStatus status) {
        Long count = courseService.getCourseCountByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Course count by status retrieved successfully", count));
    }

    @GetMapping("/count/semester/{semester}/year/{academicYear}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Count by Semester and Year",
        description = "Get the count of courses in a specific semester and academic year (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Count Response",
                    value = "{\"success\": true, \"message\": \"Count retrieved successfully\", \"data\": 25}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> getCourseCountBySemesterAndAcademicYear(
            @PathVariable String semester,
            @PathVariable Integer academicYear) {
        Long count = courseService.getCourseCountBySemesterAndAcademicYear(semester, academicYear);
        return ResponseEntity.ok(ApiResponse.success("Course count by semester and year retrieved successfully", count));
    }
} 