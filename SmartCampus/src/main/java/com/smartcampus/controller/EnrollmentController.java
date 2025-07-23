package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.EnrollmentDTO;
import com.smartcampus.entity.EnrollmentStatus;
import com.smartcampus.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Course enrollment management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Enroll Student in Course",
        description = "Enroll a student in a course (Students can enroll themselves, Professors/Admins can enroll any student)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Enrollment details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class),
                examples = @ExampleObject(
                    name = "Enrollment Creation",
                    value = "{\"studentId\": 1, \"courseId\": 1}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Student enrolled successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error or enrollment conflict",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Enrollment Error",
                    value = "{\"success\": false, \"message\": \"Student is already enrolled in this course\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<EnrollmentDTO>> enrollStudent(@Valid @RequestBody EnrollmentDTO enrollmentDTO) {
        EnrollmentDTO createdEnrollment = enrollmentService.createEnrollment(enrollmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student enrolled successfully", createdEnrollment));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get All Enrollments",
        description = "Retrieve a list of all enrollments in the system (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Enrollments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<List<EnrollmentDTO>>> getAllEnrollments() {
        List<EnrollmentDTO> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(ApiResponse.success("Enrollments retrieved successfully", enrollments));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Enrollment by ID",
        description = "Retrieve a specific enrollment by its ID"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Enrollment retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Enrollment not found"
        )
    })
    public ResponseEntity<ApiResponse<EnrollmentDTO>> getEnrollmentById(@PathVariable Long id) {
        EnrollmentDTO enrollment = enrollmentService.getEnrollmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Enrollment retrieved successfully", enrollment));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Enrollments by Student",
        description = "Retrieve all enrollments for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student enrollments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<EnrollmentDTO>>> getEnrollmentsByStudentId(@PathVariable Long studentId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Student enrollments retrieved successfully", enrollments));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Enrollments by Course",
        description = "Retrieve all enrollments for a specific course (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course enrollments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<List<EnrollmentDTO>>> getEnrollmentsByCourseId(@PathVariable Long courseId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Course enrollments retrieved successfully", enrollments));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Enrollments by Status",
        description = "Retrieve all enrollments with a specific status (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Enrollments by status retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<EnrollmentDTO>>> getEnrollmentsByStatus(@PathVariable EnrollmentStatus status) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Enrollments by status retrieved successfully", enrollments));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Active Enrollments",
        description = "Retrieve all active enrollments (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Active enrollments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<EnrollmentDTO>>> getActiveEnrollments() {
        List<EnrollmentDTO> enrollments = enrollmentService.getActiveEnrollments();
        return ResponseEntity.ok(ApiResponse.success("Active enrollments retrieved successfully", enrollments));
    }

    @GetMapping("/student/{studentId}/status/{status}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Enrollments by Status",
        description = "Retrieve all enrollments for a specific student with a specific status"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student enrollments by status retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<EnrollmentDTO>>> getEnrollmentsByStudentIdAndStatus(
            @PathVariable Long studentId,
            @PathVariable EnrollmentStatus status) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudentIdAndStatus(studentId, status);
        return ResponseEntity.ok(ApiResponse.success("Student enrollments by status retrieved successfully", enrollments));
    }

    @GetMapping("/course/{courseId}/status/{status}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Enrollments by Status",
        description = "Retrieve all enrollments for a specific course with a specific status (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course enrollments by status retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<EnrollmentDTO>>> getEnrollmentsByCourseIdAndStatus(
            @PathVariable Long courseId,
            @PathVariable EnrollmentStatus status) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByCourseIdAndStatus(courseId, status);
        return ResponseEntity.ok(ApiResponse.success("Course enrollments by status retrieved successfully", enrollments));
    }

    @GetMapping("/professor/{professorId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Enrollments by Professor",
        description = "Retrieve all enrollments for courses taught by a specific professor (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Professor enrollments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<EnrollmentDTO>>> getEnrollmentsByProfessorId(@PathVariable Long professorId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByProfessorId(professorId);
        return ResponseEntity.ok(ApiResponse.success("Professor enrollments retrieved successfully", enrollments));
    }

    @GetMapping("/professor/{professorId}/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Enrollments by Professor and Course",
        description = "Retrieve all enrollments for a specific course taught by a specific professor (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Professor course enrollments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<EnrollmentDTO>>> getEnrollmentsByProfessorIdAndCourseId(
            @PathVariable Long professorId,
            @PathVariable Long courseId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByProfessorIdAndCourseId(professorId, courseId);
        return ResponseEntity.ok(ApiResponse.success("Professor course enrollments retrieved successfully", enrollments));
    }

    @GetMapping("/student/{studentId}/semester/{semester}/year/{academicYear}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Enrollments by Semester",
        description = "Retrieve all enrollments for a specific student in a specific semester"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student semester enrollments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<EnrollmentDTO>>> getEnrollmentsByStudentIdAndSemester(
            @PathVariable Long studentId,
            @PathVariable String semester,
            @PathVariable Integer academicYear) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudentIdAndSemester(studentId, semester, academicYear);
        return ResponseEntity.ok(ApiResponse.success("Student semester enrollments retrieved successfully", enrollments));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Enrollments by Date Range",
        description = "Retrieve all enrollments within a specific date range (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Date range enrollments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<EnrollmentDTO>>> getEnrollmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Date range enrollments retrieved successfully", enrollments));
    }

    @GetMapping("/student/{studentId}/date-range")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Enrollments by Date Range",
        description = "Retrieve all enrollments for a specific student within a specific date range"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student date range enrollments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<EnrollmentDTO>>> getEnrollmentsByStudentIdAndDateRange(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudentIdAndDateRange(studentId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Student date range enrollments retrieved successfully", enrollments));
    }

    @PutMapping("/{id}/drop")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Drop Enrollment",
        description = "Drop a student from a course (withdrawal)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Drop reason",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Drop Request",
                    value = "{\"reason\": \"Schedule conflict\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Enrollment dropped successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Cannot drop enrollment"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Enrollment not found"
        )
    })
    public ResponseEntity<ApiResponse<EnrollmentDTO>> dropEnrollment(
            @PathVariable Long id,
            @RequestBody(required = false) String reason) {
        EnrollmentDTO droppedEnrollment = enrollmentService.dropEnrollment(id, reason);
        return ResponseEntity.ok(ApiResponse.success("Enrollment dropped successfully", droppedEnrollment));
    }

    @GetMapping("/check/{studentId}/{courseId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Check if Student is Enrolled",
        description = "Check if a student is enrolled in a specific course"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Enrollment status checked successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Check Response",
                    value = "{\"success\": true, \"message\": \"Enrollment status checked successfully\", \"data\": true}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Boolean>> isStudentEnrolledInCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        boolean isEnrolled = enrollmentService.isStudentEnrolledInCourse(studentId, courseId);
        return ResponseEntity.ok(ApiResponse.success("Enrollment status checked successfully", isEnrolled));
    }

    @GetMapping("/count/student/{studentId}/status/{status}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Enrollment Count by Student and Status",
        description = "Get the count of enrollments for a specific student with a specific status"
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
    public ResponseEntity<ApiResponse<Long>> getEnrollmentCountByStudentIdAndStatus(
            @PathVariable Long studentId,
            @PathVariable EnrollmentStatus status) {
        Long count = enrollmentService.getEnrollmentCountByStudentIdAndStatus(studentId, status);
        return ResponseEntity.ok(ApiResponse.success("Enrollment count by student and status retrieved successfully", count));
    }

    @GetMapping("/count/course/{courseId}/status/{status}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Enrollment Count by Course and Status",
        description = "Get the count of enrollments for a specific course with a specific status (Professor or Admin only)"
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
    public ResponseEntity<ApiResponse<Long>> getEnrollmentCountByCourseIdAndStatus(
            @PathVariable Long courseId,
            @PathVariable EnrollmentStatus status) {
        Long count = enrollmentService.getEnrollmentCountByCourseIdAndStatus(courseId, status);
        return ResponseEntity.ok(ApiResponse.success("Enrollment count by course and status retrieved successfully", count));
    }

    @GetMapping("/count/professor/{professorId}/status/{status}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Enrollment Count by Professor and Status",
        description = "Get the count of enrollments for courses taught by a specific professor with a specific status (Professor or Admin only)"
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
    public ResponseEntity<ApiResponse<Long>> getEnrollmentCountByProfessorIdAndStatus(
            @PathVariable Long professorId,
            @PathVariable EnrollmentStatus status) {
        Long count = enrollmentService.getEnrollmentCountByProfessorIdAndStatus(professorId, status);
        return ResponseEntity.ok(ApiResponse.success("Enrollment count by professor and status retrieved successfully", count));
    }

    @GetMapping("/count/active/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Active Enrollment Count by Student",
        description = "Get the count of active enrollments for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Count Response",
                    value = "{\"success\": true, \"message\": \"Count retrieved successfully\", \"data\": 4}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> getActiveEnrollmentCountByStudentId(@PathVariable Long studentId) {
        Long count = enrollmentService.getActiveEnrollmentCountByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Active enrollment count by student retrieved successfully", count));
    }

    @GetMapping("/count/active/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Active Enrollment Count by Course",
        description = "Get the count of active enrollments for a specific course (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Count Response",
                    value = "{\"success\": true, \"message\": \"Count retrieved successfully\", \"data\": 28}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> getActiveEnrollmentCountByCourseId(@PathVariable Long courseId) {
        Long count = enrollmentService.getActiveEnrollmentCountByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Active enrollment count by course retrieved successfully", count));
    }

    @GetMapping("/active/student/{studentId}/semester/{semester}/year/{academicYear}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Active Student Enrollments by Semester",
        description = "Retrieve all active enrollments for a specific student in a specific semester"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Active student semester enrollments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<EnrollmentDTO>>> getActiveEnrollmentsByStudentAndSemester(
            @PathVariable Long studentId,
            @PathVariable String semester,
            @PathVariable Integer academicYear) {
        List<EnrollmentDTO> enrollments = enrollmentService.getActiveEnrollmentsByStudentAndSemester(studentId, semester, academicYear);
        return ResponseEntity.ok(ApiResponse.success("Active student semester enrollments retrieved successfully", enrollments));
    }

    @GetMapping("/graded/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Graded Enrollments by Student",
        description = "Retrieve all graded enrollments for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Graded student enrollments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<EnrollmentDTO>>> getGradedEnrollmentsByStudentId(@PathVariable Long studentId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getGradedEnrollmentsByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Graded student enrollments retrieved successfully", enrollments));
    }

    @GetMapping("/ungraded/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Ungraded Active Enrollments by Student",
        description = "Retrieve all ungraded active enrollments for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Ungraded active student enrollments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<EnrollmentDTO>>> getUngradedActiveEnrollmentsByStudentId(@PathVariable Long studentId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getUngradedActiveEnrollmentsByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Ungraded active student enrollments retrieved successfully", enrollments));
    }
} 