package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.GradeDTO;
import com.smartcampus.entity.GradeStatus;
import com.smartcampus.entity.GradeType;
import com.smartcampus.service.GradeService;
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
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Grades", description = "Grade management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Assign Grade",
        description = "Assign a grade to a student's enrollment (Professor or Admin only)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Grade details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class),
                examples = @ExampleObject(
                    name = "Grade Assignment",
                    value = "{\"enrollmentId\": 1, \"gradeValue\": 85.5, \"maxPoints\": 100.0, \"gradeType\": \"ASSIGNMENT\", \"assignmentName\": \"Midterm Exam\", \"comment\": \"Good work!\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Grade assigned successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error or grade already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Grade Error",
                    value = "{\"success\": false, \"message\": \"Grade already exists for this enrollment\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<GradeDTO>> assignGrade(@Valid @RequestBody GradeDTO gradeDTO) {
        GradeDTO createdGrade = gradeService.createGrade(gradeDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Grade assigned successfully", createdGrade));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Update Grade",
        description = "Update an existing grade (Professor or Admin only)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated grade details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Grade updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Grade not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<GradeDTO>> updateGrade(
            @PathVariable Long id,
            @Valid @RequestBody GradeDTO gradeDTO) {
        GradeDTO updatedGrade = gradeService.updateGrade(id, gradeDTO);
        return ResponseEntity.ok(ApiResponse.success("Grade updated successfully", updatedGrade));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Grade by ID",
        description = "Retrieve a specific grade by its ID"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Grade retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Grade not found"
        )
    })
    public ResponseEntity<ApiResponse<GradeDTO>> getGradeById(@PathVariable Long id) {
        GradeDTO grade = gradeService.getGradeById(id);
        return ResponseEntity.ok(ApiResponse.success("Grade retrieved successfully", grade));
    }

    @GetMapping("/enrollment/{enrollmentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Grade by Enrollment ID",
        description = "Retrieve a grade by enrollment ID"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Grade retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Grade not found"
        )
    })
    public ResponseEntity<ApiResponse<GradeDTO>> getGradeByEnrollmentId(@PathVariable Long enrollmentId) {
        GradeDTO grade = gradeService.getGradeByEnrollmentId(enrollmentId);
        return ResponseEntity.ok(ApiResponse.success("Grade retrieved successfully", grade));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Grades",
        description = "Retrieve all grades for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student grades retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getStudentGrades(@PathVariable Long studentId) {
        List<GradeDTO> grades = gradeService.getGradesByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Student grades retrieved successfully", grades));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Grades",
        description = "Retrieve all grades for a specific course (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course grades retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getCourseGrades(@PathVariable Long courseId) {
        List<GradeDTO> grades = gradeService.getGradesByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Course grades retrieved successfully", grades));
    }

    @GetMapping("/professor/{professorId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Professor Grades",
        description = "Retrieve all grades for courses taught by a specific professor (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Professor grades retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getProfessorGrades(@PathVariable Long professorId) {
        List<GradeDTO> grades = gradeService.getGradesByProfessorId(professorId);
        return ResponseEntity.ok(ApiResponse.success("Professor grades retrieved successfully", grades));
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Course Grades",
        description = "Retrieve all grades for a specific student in a specific course"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student course grades retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getStudentCourseGrades(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        List<GradeDTO> grades = gradeService.getGradesByStudentIdAndCourseId(studentId, courseId);
        return ResponseEntity.ok(ApiResponse.success("Student course grades retrieved successfully", grades));
    }

    @GetMapping("/student/{studentId}/type/{gradeType}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Grades by Type",
        description = "Retrieve all grades for a specific student with a specific grade type"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student grades by type retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getStudentGradesByType(
            @PathVariable Long studentId,
            @PathVariable GradeType gradeType) {
        List<GradeDTO> grades = gradeService.getGradesByStudentIdAndGradeType(studentId, gradeType);
        return ResponseEntity.ok(ApiResponse.success("Student grades by type retrieved successfully", grades));
    }

    @GetMapping("/course/{courseId}/type/{gradeType}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Grades by Type",
        description = "Retrieve all grades for a specific course with a specific grade type (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course grades by type retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getCourseGradesByType(
            @PathVariable Long courseId,
            @PathVariable GradeType gradeType) {
        List<GradeDTO> grades = gradeService.getGradesByCourseIdAndGradeType(courseId, gradeType);
        return ResponseEntity.ok(ApiResponse.success("Course grades by type retrieved successfully", grades));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Grades by Status",
        description = "Retrieve all grades with a specific status (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Grades by status retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getGradesByStatus(@PathVariable GradeStatus status) {
        List<GradeDTO> grades = gradeService.getGradesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Grades by status retrieved successfully", grades));
    }

    @GetMapping("/final")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Final Grades",
        description = "Retrieve all final grades (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Final grades retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getFinalGrades() {
        List<GradeDTO> grades = gradeService.getFinalGrades();
        return ResponseEntity.ok(ApiResponse.success("Final grades retrieved successfully", grades));
    }

    @GetMapping("/student/{studentId}/final/{isFinal}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Grades by Final Status",
        description = "Retrieve all grades for a specific student with final status"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student grades by final status retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getStudentGradesByFinalStatus(
            @PathVariable Long studentId,
            @PathVariable Boolean isFinal) {
        List<GradeDTO> grades = gradeService.getGradesByStudentIdAndIsFinal(studentId, isFinal);
        return ResponseEntity.ok(ApiResponse.success("Student grades by final status retrieved successfully", grades));
    }

    @GetMapping("/course/{courseId}/final/{isFinal}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Grades by Final Status",
        description = "Retrieve all grades for a specific course with final status (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course grades by final status retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getCourseGradesByFinalStatus(
            @PathVariable Long courseId,
            @PathVariable Boolean isFinal) {
        List<GradeDTO> grades = gradeService.getGradesByCourseIdAndIsFinal(courseId, isFinal);
        return ResponseEntity.ok(ApiResponse.success("Course grades by final status retrieved successfully", grades));
    }

    @GetMapping("/student/{studentId}/semester/{semester}/year/{academicYear}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Grades by Semester",
        description = "Retrieve all grades for a specific student in a specific semester"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student semester grades retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getStudentGradesBySemester(
            @PathVariable Long studentId,
            @PathVariable String semester,
            @PathVariable Integer academicYear) {
        List<GradeDTO> grades = gradeService.getGradesByStudentIdAndSemester(studentId, semester, academicYear);
        return ResponseEntity.ok(ApiResponse.success("Student semester grades retrieved successfully", grades));
    }

    @GetMapping("/professor/{professorId}/semester/{semester}/year/{academicYear}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Professor Grades by Semester",
        description = "Retrieve all grades for a specific professor in a specific semester (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Professor semester grades retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getProfessorGradesBySemester(
            @PathVariable Long professorId,
            @PathVariable String semester,
            @PathVariable Integer academicYear) {
        List<GradeDTO> grades = gradeService.getGradesByProfessorIdAndSemester(professorId, semester, academicYear);
        return ResponseEntity.ok(ApiResponse.success("Professor semester grades retrieved successfully", grades));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Grades by Date Range",
        description = "Retrieve all grades within a specific date range (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Date range grades retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getGradesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<GradeDTO> grades = gradeService.getGradesByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Date range grades retrieved successfully", grades));
    }

    @GetMapping("/student/{studentId}/date-range")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Grades by Date Range",
        description = "Retrieve all grades for a specific student within a specific date range"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student date range grades retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getStudentGradesByDateRange(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<GradeDTO> grades = gradeService.getGradesByStudentIdAndDateRange(studentId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Student date range grades retrieved successfully", grades));
    }

    @GetMapping("/course/{courseId}/date-range")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Grades by Date Range",
        description = "Retrieve all grades for a specific course within a specific date range (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course date range grades retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getCourseGradesByDateRange(
            @PathVariable Long courseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<GradeDTO> grades = gradeService.getGradesByCourseIdAndDateRange(courseId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Course date range grades retrieved successfully", grades));
    }

    @GetMapping("/student/{studentId}/grade-range")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Grades by Grade Range",
        description = "Retrieve all grades for a specific student within a specific grade range"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student grade range grades retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getStudentGradesByGradeRange(
            @PathVariable Long studentId,
            @RequestParam Double minGrade,
            @RequestParam Double maxGrade) {
        List<GradeDTO> grades = gradeService.getGradesByStudentIdAndGradeRange(studentId, minGrade, maxGrade);
        return ResponseEntity.ok(ApiResponse.success("Student grade range grades retrieved successfully", grades));
    }

    @GetMapping("/course/{courseId}/grade-range")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Grades by Grade Range",
        description = "Retrieve all grades for a specific course within a specific grade range (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course grade range grades retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getCourseGradesByGradeRange(
            @PathVariable Long courseId,
            @RequestParam Double minGrade,
            @RequestParam Double maxGrade) {
        List<GradeDTO> grades = gradeService.getGradesByCourseIdAndGradeRange(courseId, minGrade, maxGrade);
        return ResponseEntity.ok(ApiResponse.success("Course grade range grades retrieved successfully", grades));
    }

    @GetMapping("/student/{studentId}/letter/{gradeLetter}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Grades by Letter Grade",
        description = "Retrieve all grades for a specific student with a specific letter grade"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student letter grade grades retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getStudentGradesByLetterGrade(
            @PathVariable Long studentId,
            @PathVariable String gradeLetter) {
        List<GradeDTO> grades = gradeService.getGradesByStudentIdAndGradeLetter(studentId, gradeLetter);
        return ResponseEntity.ok(ApiResponse.success("Student letter grade grades retrieved successfully", grades));
    }

    @GetMapping("/course/{courseId}/letter/{gradeLetter}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Grades by Letter Grade",
        description = "Retrieve all grades for a specific course with a specific letter grade (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course letter grade grades retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getCourseGradesByLetterGrade(
            @PathVariable Long courseId,
            @PathVariable String gradeLetter) {
        List<GradeDTO> grades = gradeService.getGradesByCourseIdAndGradeLetter(courseId, gradeLetter);
        return ResponseEntity.ok(ApiResponse.success("Course letter grade grades retrieved successfully", grades));
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
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getGradedEnrollmentsByStudentId(@PathVariable Long studentId) {
        List<GradeDTO> grades = gradeService.getGradedEnrollmentsByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Graded student enrollments retrieved successfully", grades));
    }

    @GetMapping("/ungraded/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Ungraded Enrollments by Student",
        description = "Retrieve all ungraded enrollments for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Ungraded student enrollments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getUngradedEnrollmentsByStudentId(@PathVariable Long studentId) {
        List<GradeDTO> grades = gradeService.getUngradedEnrollmentsByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Ungraded student enrollments retrieved successfully", grades));
    }

    @GetMapping("/graded/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Graded Enrollments by Course",
        description = "Retrieve all graded enrollments for a specific course (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Graded course enrollments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getGradedEnrollmentsByCourseId(@PathVariable Long courseId) {
        List<GradeDTO> grades = gradeService.getGradedEnrollmentsByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Graded course enrollments retrieved successfully", grades));
    }

    @GetMapping("/ungraded/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Ungraded Enrollments by Course",
        description = "Retrieve all ungraded enrollments for a specific course (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Ungraded course enrollments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GradeDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<GradeDTO>>> getUngradedEnrollmentsByCourseId(@PathVariable Long courseId) {
        List<GradeDTO> grades = gradeService.getUngradedEnrollmentsByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Ungraded course enrollments retrieved successfully", grades));
    }

    @GetMapping("/count/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Grade Count by Student",
        description = "Get the count of grades for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Count Response",
                    value = "{\"success\": true, \"message\": \"Count retrieved successfully\", \"data\": 8}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> getGradeCountByStudentId(@PathVariable Long studentId) {
        Long count = gradeService.getGradeCountByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Grade count by student retrieved successfully", count));
    }

    @GetMapping("/count/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Grade Count by Course",
        description = "Get the count of grades for a specific course (Professor or Admin only)"
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
    public ResponseEntity<ApiResponse<Long>> getGradeCountByCourseId(@PathVariable Long courseId) {
        Long count = gradeService.getGradeCountByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Grade count by course retrieved successfully", count));
    }

    @GetMapping("/count/professor/{professorId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Grade Count by Professor",
        description = "Get the count of grades for courses taught by a specific professor (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Count Response",
                    value = "{\"success\": true, \"message\": \"Count retrieved successfully\", \"data\": 45}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> getGradeCountByProfessorId(@PathVariable Long professorId) {
        Long count = gradeService.getGradeCountByProfessorId(professorId);
        return ResponseEntity.ok(ApiResponse.success("Grade count by professor retrieved successfully", count));
    }

    @GetMapping("/average/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Average Grade by Student",
        description = "Get the average grade for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Average retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Average Response",
                    value = "{\"success\": true, \"message\": \"Average retrieved successfully\", \"data\": 87.5}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Double>> getAverageGradeByStudentId(@PathVariable Long studentId) {
        Double average = gradeService.getAverageGradeByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Average grade by student retrieved successfully", average));
    }

    @GetMapping("/average/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Average Grade by Course",
        description = "Get the average grade for a specific course (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Average retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Average Response",
                    value = "{\"success\": true, \"message\": \"Average retrieved successfully\", \"data\": 82.3}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Double>> getAverageGradeByCourseId(@PathVariable Long courseId) {
        Double average = gradeService.getAverageGradeByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Average grade by course retrieved successfully", average));
    }

    @GetMapping("/average/professor/{professorId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Average Grade by Professor",
        description = "Get the average grade for courses taught by a specific professor (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Average retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Average Response",
                    value = "{\"success\": true, \"message\": \"Average retrieved successfully\", \"data\": 85.7}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Double>> getAverageGradeByProfessorId(@PathVariable Long professorId) {
        Double average = gradeService.getAverageGradeByProfessorId(professorId);
        return ResponseEntity.ok(ApiResponse.success("Average grade by professor retrieved successfully", average));
    }

    @GetMapping("/max/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Max Grade by Student",
        description = "Get the maximum grade for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Max grade retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Max Response",
                    value = "{\"success\": true, \"message\": \"Max grade retrieved successfully\", \"data\": 95.0}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Double>> getMaxGradeByStudentId(@PathVariable Long studentId) {
        Double maxGrade = gradeService.getMaxGradeByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Max grade by student retrieved successfully", maxGrade));
    }

    @GetMapping("/min/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Min Grade by Student",
        description = "Get the minimum grade for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Min grade retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Min Response",
                    value = "{\"success\": true, \"message\": \"Min grade retrieved successfully\", \"data\": 72.0}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Double>> getMinGradeByStudentId(@PathVariable Long studentId) {
        Double minGrade = gradeService.getMinGradeByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Min grade by student retrieved successfully", minGrade));
    }

    @GetMapping("/max/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Max Grade by Course",
        description = "Get the maximum grade for a specific course (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Max grade retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Max Response",
                    value = "{\"success\": true, \"message\": \"Max grade retrieved successfully\", \"data\": 98.0}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Double>> getMaxGradeByCourseId(@PathVariable Long courseId) {
        Double maxGrade = gradeService.getMaxGradeByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Max grade by course retrieved successfully", maxGrade));
    }

    @GetMapping("/min/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Min Grade by Course",
        description = "Get the minimum grade for a specific course (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Min grade retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Min Response",
                    value = "{\"success\": true, \"message\": \"Min grade retrieved successfully\", \"data\": 65.0}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Double>> getMinGradeByCourseId(@PathVariable Long courseId) {
        Double minGrade = gradeService.getMinGradeByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Min grade by course retrieved successfully", minGrade));
    }
} 