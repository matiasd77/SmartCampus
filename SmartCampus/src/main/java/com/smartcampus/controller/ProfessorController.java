package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.ProfessorDTO;
import com.smartcampus.entity.ProfessorStatus;
import com.smartcampus.service.ProfessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professors")
@RequiredArgsConstructor
@Tag(name = "Professors", description = "Professor management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class ProfessorController {

    private final ProfessorService professorService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get All Professors",
        description = "Retrieve a paginated list of all professors in the system (Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Professors retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProfessorDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<Page<ProfessorDTO>>> getAllProfessors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProfessorDTO> professors = professorService.getAllProfessors(pageable);
        return ResponseEntity.ok(ApiResponse.success("Professors retrieved successfully", professors));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @Operation(
        summary = "Get Professor by ID",
        description = "Retrieve a specific professor by their ID (Admin or the professor themselves)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Professor retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProfessorDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Professor not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions"
        )
    })
    public ResponseEntity<ApiResponse<ProfessorDTO>> getProfessorById(@PathVariable Long id) {
        ProfessorDTO professor = professorService.getProfessorById(id);
        return ResponseEntity.ok(ApiResponse.success("Professor retrieved successfully", professor));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create New Professor",
        description = "Create a new professor in the system (Admin only)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Professor details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProfessorDTO.class),
                examples = @ExampleObject(
                    name = "Professor Creation",
                    value = "{\"firstName\": \"John\", \"lastName\": \"Smith\", \"email\": \"john.smith@example.com\", \"department\": \"Computer Science\", \"academicRank\": \"ASSOCIATE_PROFESSOR\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Professor created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProfessorDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error or email already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = "{\"success\": false, \"message\": \"Validation failed\", \"data\": {\"firstName\": \"First name is required\", \"email\": \"Email should be valid\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<ProfessorDTO>> createProfessor(@Valid @RequestBody ProfessorDTO professorDTO) {
        System.out.println("🔍 ProfessorController.createProfessor - Received data: " + professorDTO);
        try {
            ProfessorDTO createdProfessor = professorService.createProfessor(professorDTO);
            System.out.println("🔍 ProfessorController.createProfessor - Success: " + createdProfessor);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Professor created successfully", createdProfessor));
        } catch (Exception e) {
            System.err.println("🔍 ProfessorController.createProfessor - Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create professor: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update Professor",
        description = "Update an existing professor's information (Admin only)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated professor details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProfessorDTO.class)
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Professor updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProfessorDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Professor not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<ProfessorDTO>> updateProfessor(
            @PathVariable Long id,
            @Valid @RequestBody ProfessorDTO professorDTO) {
        ProfessorDTO updatedProfessor = professorService.updateProfessor(id, professorDTO);
        return ResponseEntity.ok(ApiResponse.success("Professor updated successfully", updatedProfessor));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete Professor",
        description = "Delete a professor from the system (Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Professor deleted successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Professor not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Cannot delete professor with associated courses"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<String>> deleteProfessor(@PathVariable Long id) {
        professorService.deleteProfessor(id);
        return ResponseEntity.ok(ApiResponse.success("Professor deleted successfully", null));
    }

    @GetMapping("/department/{department}")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Professors by Department",
        description = "Retrieve all professors in a specific department"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Professors retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProfessorDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<ProfessorDTO>>> getProfessorsByDepartment(@PathVariable String department) {
        List<ProfessorDTO> professors = professorService.getProfessorsByDepartment(department);
        return ResponseEntity.ok(ApiResponse.success("Professors by department retrieved successfully", professors));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get Professors by Status",
        description = "Retrieve all professors with a specific status (Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Professors retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProfessorDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<ProfessorDTO>>> getProfessorsByStatus(@PathVariable ProfessorStatus status) {
        List<ProfessorDTO> professors = professorService.getProfessorsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Professors by status retrieved successfully", professors));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Search Professors by Name",
        description = "Search professors by name (partial match)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Professors retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProfessorDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<ProfessorDTO>>> searchProfessorsByName(@RequestParam String name) {
        List<ProfessorDTO> professors = professorService.getProfessorsByName(name);
        return ResponseEntity.ok(ApiResponse.success("Professors search completed successfully", professors));
    }

    @GetMapping("/department/{department}/search")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Search Professors by Department and Name",
        description = "Search professors by department and name (partial match)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Professors retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProfessorDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<ProfessorDTO>>> searchProfessorsByDepartmentAndName(
            @PathVariable String department,
            @RequestParam String name) {
        List<ProfessorDTO> professors = professorService.getProfessorsByDepartmentAndName(department, name);
        return ResponseEntity.ok(ApiResponse.success("Professors search completed successfully", professors));
    }

    @GetMapping("/count/department/{department}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get Professor Count by Department",
        description = "Get the count of professors in a specific department (Admin only)"
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
    public ResponseEntity<ApiResponse<Long>> getProfessorCountByDepartment(@PathVariable String department) {
        Long count = professorService.getProfessorCountByDepartment(department);
        return ResponseEntity.ok(ApiResponse.success("Professor count by department retrieved successfully", count));
    }

    @GetMapping("/count/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get Professor Count by Status",
        description = "Get the count of professors with a specific status (Admin only)"
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
    public ResponseEntity<ApiResponse<Long>> getProfessorCountByStatus(@PathVariable ProfessorStatus status) {
        Long count = professorService.getProfessorCountByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Professor count by status retrieved successfully", count));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('PROFESSOR')")
    @Operation(
        summary = "Get Current Professor Profile",
        description = "Retrieve the current professor's profile information"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Professor profile retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProfessorDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Professor profile not found"
        )
    })
    public ResponseEntity<ApiResponse<ProfessorDTO>> getCurrentProfessorProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            ProfessorDTO professor = professorService.getProfessorByEmail(email);
            return ResponseEntity.ok(ApiResponse.success("Professor profile retrieved successfully", professor));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Professor profile not found"));
        }
    }
} 