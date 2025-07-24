package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.JwtResponse;
import com.smartcampus.dto.LoginRequest;
import com.smartcampus.dto.RegisterRequest;
import com.smartcampus.entity.Role;
import com.smartcampus.entity.User;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(
        summary = "User Login",
        description = "Authenticate user with email and password, returns JWT token",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Login credentials",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequest.class),
                examples = @ExampleObject(
                    name = "Student Login",
                    value = "{\"email\": \"student@example.com\", \"password\": \"Password123!\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JwtResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\"success\": true, \"message\": \"Login successful\", \"data\": {\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"user\": {\"id\": 1, \"name\": \"John Doe\", \"email\": \"student@example.com\", \"role\": \"STUDENT\"}}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = "{\"success\": false, \"message\": \"Invalid email or password\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = "{\"success\": false, \"message\": \"Validation failed\", \"data\": {\"email\": \"Email should be valid\", \"password\": \"Password is required\"}}"
                )
            )
        )
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login attempt for email: {}", loginRequest.getEmail());
            
            JwtResponse response = authService.login(loginRequest);
            
            log.info("Login successful for email: {}", loginRequest.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
            
        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Invalid email or password", "Please check your credentials and try again."));
                    
        } catch (UsernameNotFoundException e) {
            log.warn("User not found for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Invalid email or password", "Please check your credentials and try again."));
                    
        } catch (Exception e) {
            log.error("Unexpected error during login for email: {}", loginRequest.getEmail(), e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Login failed", "An unexpected error occurred. Please try again later."));
        }
    }

    @PostMapping("/register")
    @Operation(
        summary = "Student Registration",
        description = "Register a new student user. Only STUDENT role is allowed for public registration. " +
                     "Professors and Administrators must be created through admin interfaces.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Student registration details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RegisterRequest.class),
                examples = @ExampleObject(
                    name = "Student Registration",
                    value = "{\"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"password\": \"Password123!\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Registration successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\"success\": true, \"message\": \"Registration successful\", \"data\": {\"id\": 1, \"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"role\": \"STUDENT\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error or email already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = "{\"success\": false, \"message\": \"Validation failed\", \"data\": {\"email\": \"Email should be valid\", \"password\": \"Password must be between 8 and 100 characters\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Email already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Conflict Error",
                    value = "{\"success\": false, \"message\": \"Email already exists\"}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = authService.register(registerRequest);
        return ResponseEntity.status(201).body(ApiResponse.success("Registration successful", user));
    }

    @PostMapping("/create-test-admin")
    @Operation(
        summary = "Create Test Admin (Debug)",
        description = "Create a test admin user for debugging purposes"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Test admin created successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Admin already exists"
        )
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> createTestAdmin() {
        try {
            log.info("Creating test admin user");
            
            // Check if admin already exists
            if (userRepository.existsByEmail("admin@smartcampus.com")) {
                return ResponseEntity.status(409)
                        .body(ApiResponse.<Map<String, Object>>error("Admin user already exists"));
            }
            
            // Create admin user with proper BCrypt encoding
            User admin = User.builder()
                    .name("System Administrator")
                    .email("admin@smartcampus.com")
                    .password(passwordEncoder.encode("Password123!")) // Password123!
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();
            
            User savedAdmin = userRepository.save(admin);
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", savedAdmin.getId());
            result.put("name", savedAdmin.getName());
            result.put("email", savedAdmin.getEmail());
            result.put("role", savedAdmin.getRole());
            result.put("isActive", savedAdmin.getIsActive());
            result.put("createdAt", savedAdmin.getCreatedAt());
            result.put("loginCredentials", Map.of(
                "email", "admin@smartcampus.com",
                "password", "Password123!"
            ));
            
            log.info("Test admin created successfully: {}", result);
            
            return ResponseEntity.status(201)
                    .body(ApiResponse.success("Test admin created successfully", result));
        } catch (Exception e) {
            log.error("Error creating test admin", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.<Map<String, Object>>error("Failed to create test admin"));
        }
    }

    @GetMapping("/current-user")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
        summary = "Get Current User",
        description = "Get the current authenticated user's information"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Current user information retrieved successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.<Map<String, Object>>error("Not authenticated"));
            }
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("email", authentication.getPrincipal());
            userInfo.put("authorities", authentication.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toList()));
            userInfo.put("authenticated", authentication.isAuthenticated());
            
            log.info("Current user info requested - email: {}, authorities: {}", 
                userInfo.get("email"), userInfo.get("authorities"));
            
            return ResponseEntity.ok(ApiResponse.success("Current user information retrieved", userInfo));
        } catch (Exception e) {
            log.error("Error getting current user", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.<Map<String, Object>>error("Failed to get current user"));
        }
    }

    @GetMapping("/test-user/{email}")
    @Operation(
        summary = "Test User Existence",
        description = "Debug endpoint to check if a user exists in the database"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User information retrieved successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> testUser(@PathVariable String email) {
        try {
            log.info("Testing user existence for email: {}", email);
            
            // Check if user exists
            boolean userExists = userRepository.existsByEmail(email);
            
            Map<String, Object> result = new HashMap<>();
            result.put("email", email);
            result.put("exists", userExists);
            
            if (userExists) {
                User user = userRepository.findByEmail(email).orElse(null);
                if (user != null) {
                    result.put("id", user.getId());
                    result.put("name", user.getName());
                    result.put("role", user.getRole());
                    result.put("isActive", user.getIsActive());
                    result.put("createdAt", user.getCreatedAt());
                    result.put("passwordLength", user.getPassword() != null ? user.getPassword().length() : 0);
                    result.put("passwordStartsWith", user.getPassword() != null && user.getPassword().startsWith("$2a$") ? "BCrypt" : "Other");
                }
            }
            
            log.info("User test result: {}", result);
            
            return ResponseEntity.ok(ApiResponse.success("User test completed", result));
        } catch (Exception e) {
            log.error("Error testing user: {}", email, e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.<Map<String, Object>>error("Failed to test user"));
        }
    }

    @GetMapping("/debug")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
        summary = "Debug Authentication",
        description = "Debug endpoint to check authentication status and authorities"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Debug information retrieved successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> debugAuth() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("authenticated", authentication != null && authentication.isAuthenticated());
            debugInfo.put("principal", authentication != null ? authentication.getPrincipal() : null);
            debugInfo.put("authorities", authentication != null ? 
                authentication.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toList()) : null);
            debugInfo.put("credentials", authentication != null ? authentication.getCredentials() : null);
            debugInfo.put("details", authentication != null ? authentication.getDetails() : null);
            
            log.info("Debug auth called - authenticated: {}, authorities: {}", 
                debugInfo.get("authenticated"), debugInfo.get("authorities"));
            
            return ResponseEntity.ok(ApiResponse.success("Debug information retrieved", debugInfo));
        } catch (Exception e) {
            log.error("Error in debug endpoint", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.<Map<String, Object>>error("Debug failed"));
        }
    }

    @PostMapping("/update-admin-password")
    @Operation(
        summary = "Update Admin Password (Debug)",
        description = "Update the admin user password to a known working value for debugging"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Password updated successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Admin user not found"
        )
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateAdminPassword() {
        try {
            User admin = userRepository.findByEmail("admin@smartcampus.com")
                    .orElse(null);
            
            if (admin == null) {
                return ResponseEntity.status(404).body(ApiResponse.<Map<String, Object>>builder()
                        .success(false)
                        .message("Admin user not found")
                        .data(null)
                        .build());
            }
            
            // Update password to Password123! (BCrypt hash)
            String newPasswordHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi";
            admin.setPassword(newPasswordHash);
            userRepository.save(admin);
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Admin password updated successfully");
            result.put("email", admin.getEmail());
            result.put("role", admin.getRole());
            result.put("isActive", admin.getIsActive());
            
            log.info("Admin password updated for user: {}", admin.getEmail());
            
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Admin password updated successfully")
                    .data(result)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error updating admin password", e);
            return ResponseEntity.status(500).body(ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Error updating admin password: " + e.getMessage())
                    .data(null)
                    .build());
        }
    }

    @PostMapping("/replace-admin")
    @Operation(
        summary = "Replace Admin User (Debug)",
        description = "Delete existing admin and create a fresh one with correct password"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Admin replaced successfully"
        )
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> replaceAdmin() {
        try {
            log.info("Replacing admin user");
            
            // Delete existing admin if exists
            userRepository.findByEmail("admin@smartcampus.com")
                    .ifPresent(user -> {
                        userRepository.delete(user);
                        log.info("Deleted existing admin user: {}", user.getEmail());
                    });
            
            // Create fresh admin user with proper BCrypt encoding
            User admin = User.builder()
                    .name("System Administrator")
                    .email("admin@smartcampus.com")
                    .password(passwordEncoder.encode("Password123!")) // Password123!
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();
            
            User savedAdmin = userRepository.save(admin);
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", savedAdmin.getId());
            result.put("name", savedAdmin.getName());
            result.put("email", savedAdmin.getEmail());
            result.put("role", savedAdmin.getRole());
            result.put("isActive", savedAdmin.getIsActive());
            result.put("createdAt", savedAdmin.getCreatedAt());
            result.put("loginCredentials", Map.of(
                "email", "admin@smartcampus.com",
                "password", "Password123!"
            ));
            
            log.info("Admin replaced successfully: {}", result);
            
            return ResponseEntity.ok(ApiResponse.success("Admin replaced successfully", result));
        } catch (Exception e) {
            log.error("Error replacing admin", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.<Map<String, Object>>error("Failed to replace admin"));
        }
    }
} 