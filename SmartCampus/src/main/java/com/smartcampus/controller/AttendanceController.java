package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.AttendanceDTO;
import com.smartcampus.entity.AttendanceStatus;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Attendance", description = "Attendance tracking APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class AttendanceController {

    private final com.smartcampus.service.AttendanceService attendanceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Mark Attendance",
        description = "Mark attendance for a student in a course (Professor or Admin only)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Attendance details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class),
                examples = @ExampleObject(
                    name = "Attendance Marking",
                    value = "{\"studentId\": 1, \"courseId\": 1, \"date\": \"2024-01-15\", \"status\": \"PRESENT\", \"notes\": \"Student attended the lecture\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Attendance marked successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error or attendance already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Attendance Error",
                    value = "{\"success\": false, \"message\": \"Attendance already exists for this student, course, and date\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<AttendanceDTO>> markAttendance(@Valid @RequestBody AttendanceDTO attendanceDTO) {
        AttendanceDTO createdAttendance = attendanceService.createAttendance(attendanceDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Attendance marked successfully", createdAttendance));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Update Attendance",
        description = "Update an existing attendance record (Professor or Admin only)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated attendance details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Attendance updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Attendance not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<AttendanceDTO>> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceDTO attendanceDTO) {
        AttendanceDTO updatedAttendance = attendanceService.updateAttendance(id, attendanceDTO);
        return ResponseEntity.ok(ApiResponse.success("Attendance updated successfully", updatedAttendance));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Attendance by ID",
        description = "Retrieve a specific attendance record by its ID"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Attendance retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Attendance not found"
        )
    })
    public ResponseEntity<ApiResponse<AttendanceDTO>> getAttendanceById(@PathVariable Long id) {
        AttendanceDTO attendance = attendanceService.getAttendanceById(id);
        return ResponseEntity.ok(ApiResponse.success("Attendance retrieved successfully", attendance));
    }

    @GetMapping("/student/{studentId}/course/{courseId}/date/{date}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Attendance by Student, Course, and Date",
        description = "Retrieve attendance for a specific student, course, and date"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Attendance retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Attendance not found"
        )
    })
    public ResponseEntity<ApiResponse<AttendanceDTO>> getAttendanceByStudentAndCourseAndDate(
            @PathVariable Long studentId,
            @PathVariable Long courseId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AttendanceDTO attendance = attendanceService.getAttendanceByStudentAndCourseAndDate(studentId, courseId, date);
        return ResponseEntity.ok(ApiResponse.success("Attendance retrieved successfully", attendance));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Attendance",
        description = "Retrieve all attendance records for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student attendance retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getStudentAttendance(@PathVariable Long studentId) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Student attendance retrieved successfully", attendances));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Attendance",
        description = "Retrieve all attendance records for a specific course (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course attendance retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getCourseAttendance(@PathVariable Long courseId) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Course attendance retrieved successfully", attendances));
    }

    @GetMapping("/professor/{professorId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Professor Attendance",
        description = "Retrieve all attendance records for courses taught by a specific professor (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Professor attendance retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getProfessorAttendance(@PathVariable Long professorId) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByProfessorId(professorId);
        return ResponseEntity.ok(ApiResponse.success("Professor attendance retrieved successfully", attendances));
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Course Attendance",
        description = "Retrieve all attendance records for a specific student in a specific course"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student course attendance retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getStudentCourseAttendance(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByStudentIdAndCourseId(studentId, courseId);
        return ResponseEntity.ok(ApiResponse.success("Student course attendance retrieved successfully", attendances));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Attendance by Status",
        description = "Retrieve all attendance records with a specific status (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Attendance by status retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getAttendanceByStatus(@PathVariable AttendanceStatus status) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Attendance by status retrieved successfully", attendances));
    }

    @GetMapping("/student/{studentId}/status/{status}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Attendance by Status",
        description = "Retrieve all attendance records for a specific student with a specific status"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student attendance by status retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getStudentAttendanceByStatus(
            @PathVariable Long studentId,
            @PathVariable AttendanceStatus status) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByStudentIdAndStatus(studentId, status);
        return ResponseEntity.ok(ApiResponse.success("Student attendance by status retrieved successfully", attendances));
    }

    @GetMapping("/course/{courseId}/status/{status}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Attendance by Status",
        description = "Retrieve all attendance records for a specific course with a specific status (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course attendance by status retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getCourseAttendanceByStatus(
            @PathVariable Long courseId,
            @PathVariable AttendanceStatus status) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByCourseIdAndStatus(courseId, status);
        return ResponseEntity.ok(ApiResponse.success("Course attendance by status retrieved successfully", attendances));
    }

    @GetMapping("/student/{studentId}/semester/{semester}/year/{academicYear}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Attendance by Semester",
        description = "Retrieve all attendance records for a specific student in a specific semester"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student semester attendance retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getStudentAttendanceBySemester(
            @PathVariable Long studentId,
            @PathVariable String semester,
            @PathVariable Integer academicYear) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByStudentIdAndSemester(studentId, semester, academicYear);
        return ResponseEntity.ok(ApiResponse.success("Student semester attendance retrieved successfully", attendances));
    }

    @GetMapping("/professor/{professorId}/semester/{semester}/year/{academicYear}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Professor Attendance by Semester",
        description = "Retrieve all attendance records for a specific professor in a specific semester (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Professor semester attendance retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getProfessorAttendanceBySemester(
            @PathVariable Long professorId,
            @PathVariable String semester,
            @PathVariable Integer academicYear) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByProfessorIdAndSemester(professorId, semester, academicYear);
        return ResponseEntity.ok(ApiResponse.success("Professor semester attendance retrieved successfully", attendances));
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Attendance by Date",
        description = "Retrieve all attendance records for a specific date (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Date attendance retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByDate(date);
        return ResponseEntity.ok(ApiResponse.success("Date attendance retrieved successfully", attendances));
    }

    @GetMapping("/student/{studentId}/date/{date}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Attendance by Date",
        description = "Retrieve all attendance records for a specific student on a specific date"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student date attendance retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getStudentAttendanceByDate(
            @PathVariable Long studentId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByStudentIdAndDate(studentId, date);
        return ResponseEntity.ok(ApiResponse.success("Student date attendance retrieved successfully", attendances));
    }

    @GetMapping("/course/{courseId}/date/{date}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Attendance by Date",
        description = "Retrieve all attendance records for a specific course on a specific date (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course date attendance retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getCourseAttendanceByDate(
            @PathVariable Long courseId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByCourseIdAndDate(courseId, date);
        return ResponseEntity.ok(ApiResponse.success("Course date attendance retrieved successfully", attendances));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Attendance by Date Range",
        description = "Retrieve all attendance records within a specific date range (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Date range attendance retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getAttendanceByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Date range attendance retrieved successfully", attendances));
    }

    @GetMapping("/student/{studentId}/date-range")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Attendance by Date Range",
        description = "Retrieve all attendance records for a specific student within a specific date range"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student date range attendance retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getStudentAttendanceByDateRange(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByStudentIdAndDateRange(studentId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Student date range attendance retrieved successfully", attendances));
    }

    @GetMapping("/course/{courseId}/date-range")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Attendance by Date Range",
        description = "Retrieve all attendance records for a specific course within a specific date range (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course date range attendance retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getCourseAttendanceByDateRange(
            @PathVariable Long courseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByCourseIdAndDateRange(courseId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Course date range attendance retrieved successfully", attendances));
    }

    @GetMapping("/late")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Late Attendances",
        description = "Retrieve all late attendance records (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Late attendances retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getLateAttendances() {
        List<AttendanceDTO> attendances = attendanceService.getLateAttendances();
        return ResponseEntity.ok(ApiResponse.success("Late attendances retrieved successfully", attendances));
    }

    @GetMapping("/late/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Late Attendances",
        description = "Retrieve all late attendance records for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student late attendances retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getStudentLateAttendances(@PathVariable Long studentId) {
        List<AttendanceDTO> attendances = attendanceService.getLateAttendancesByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Student late attendances retrieved successfully", attendances));
    }

    @GetMapping("/late/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Late Attendances",
        description = "Retrieve all late attendance records for a specific course (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course late attendances retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getCourseLateAttendances(@PathVariable Long courseId) {
        List<AttendanceDTO> attendances = attendanceService.getLateAttendancesByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Course late attendances retrieved successfully", attendances));
    }

    @GetMapping("/excused")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Excused Attendances",
        description = "Retrieve all excused attendance records (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Excused attendances retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getExcusedAttendances() {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByIsExcused(true);
        return ResponseEntity.ok(ApiResponse.success("Excused attendances retrieved successfully", attendances));
    }

    @GetMapping("/excused/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Student Excused Attendances",
        description = "Retrieve all excused attendance records for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Student excused attendances retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getStudentExcusedAttendances(@PathVariable Long studentId) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByStudentIdAndIsExcused(studentId, true);
        return ResponseEntity.ok(ApiResponse.success("Student excused attendances retrieved successfully", attendances));
    }

    @GetMapping("/excused/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Course Excused Attendances",
        description = "Retrieve all excused attendance records for a specific course (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course excused attendances retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AttendanceDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getCourseExcusedAttendances(@PathVariable Long courseId) {
        List<AttendanceDTO> attendances = attendanceService.getAttendanceByCourseIdAndIsExcused(courseId, true);
        return ResponseEntity.ok(ApiResponse.success("Course excused attendances retrieved successfully", attendances));
    }

    @GetMapping("/count/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Attendance Count by Student",
        description = "Get the count of attendance records for a specific student"
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
    public ResponseEntity<ApiResponse<Long>> getAttendanceCountByStudentId(@PathVariable Long studentId) {
        Long count = attendanceService.getAttendanceCountByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Attendance count by student retrieved successfully", count));
    }

    @GetMapping("/count/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Attendance Count by Course",
        description = "Get the count of attendance records for a specific course (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Count Response",
                    value = "{\"success\": true, \"message\": \"Count retrieved successfully\", \"data\": 150}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> getAttendanceCountByCourseId(@PathVariable Long courseId) {
        Long count = attendanceService.getAttendanceCountByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Attendance count by course retrieved successfully", count));
    }

    @GetMapping("/present/count/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Present Count by Student",
        description = "Get the count of present attendance records for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Present count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Present Count Response",
                    value = "{\"success\": true, \"message\": \"Present count retrieved successfully\", \"data\": 20}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> getPresentCountByStudentId(@PathVariable Long studentId) {
        Long count = attendanceService.getPresentCountByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Present count by student retrieved successfully", count));
    }

    @GetMapping("/absent/count/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Absent Count by Student",
        description = "Get the count of absent attendance records for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Absent count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Absent Count Response",
                    value = "{\"success\": true, \"message\": \"Absent count retrieved successfully\", \"data\": 3}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> getAbsentCountByStudentId(@PathVariable Long studentId) {
        Long count = attendanceService.getAbsentCountByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Absent count by student retrieved successfully", count));
    }

    @GetMapping("/excused/count/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Excused Count by Student",
        description = "Get the count of excused attendance records for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Excused count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Excused Count Response",
                    value = "{\"success\": true, \"message\": \"Excused count retrieved successfully\", \"data\": 2}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> getExcusedCountByStudentId(@PathVariable Long studentId) {
        Long count = attendanceService.getExcusedCountByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Excused count by student retrieved successfully", count));
    }

    @GetMapping("/average/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Average Attendance Percentage by Student",
        description = "Get the average attendance percentage for a specific student"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Average retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Average Response",
                    value = "{\"success\": true, \"message\": \"Average retrieved successfully\", \"data\": 85.5}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Double>> getAverageAttendancePercentageByStudentId(@PathVariable Long studentId) {
        Double average = attendanceService.getAverageAttendancePercentageByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Average attendance percentage by student retrieved successfully", average));
    }

    @GetMapping("/average/course/{courseId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Average Attendance Percentage by Course",
        description = "Get the average attendance percentage for a specific course (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Average retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Average Response",
                    value = "{\"success\": true, \"message\": \"Average retrieved successfully\", \"data\": 78.2}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Double>> getAverageAttendancePercentageByCourseId(@PathVariable Long courseId) {
        Double average = attendanceService.getAverageAttendancePercentageByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Average attendance percentage by course retrieved successfully", average));
    }
} 