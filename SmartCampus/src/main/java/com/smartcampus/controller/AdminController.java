package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.UserDTO;
import com.smartcampus.service.UserService;
import com.smartcampus.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final UserService userService;
    private final StudentService studentService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get Admin Dashboard",
        description = "Retrieve admin dashboard data (Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Dashboard data retrieved successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<String>> getAdminDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Welcome to Admin Dashboard", "Admin dashboard data"));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get All Users",
        description = "Retrieve a list of all users in the system (Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("All users retrieved successfully", users));
    }

    @GetMapping("/debug/database")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Debug Database Contents",
        description = "Debug endpoint to check database contents (Admin only)"
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> debugDatabase() {
        Map<String, Object> debugInfo = new HashMap<>();
        
        try {
            List<UserDTO> users = userService.getAllUsers();
            debugInfo.put("usersCount", users.size());
            debugInfo.put("users", users);
            
            List<com.smartcampus.dto.StudentDTO> students = studentService.getAllStudents();
            debugInfo.put("studentsCount", students.size());
            debugInfo.put("students", students);
            
            debugInfo.put("message", "Database contents retrieved successfully");
            debugInfo.put("success", true);
            
        } catch (Exception e) {
            debugInfo.put("message", "Error retrieving database contents: " + e.getMessage());
            debugInfo.put("success", false);
            debugInfo.put("error", e.toString());
        }
        
        return ResponseEntity.ok(ApiResponse.success("Debug information retrieved", debugInfo));
    }
} 