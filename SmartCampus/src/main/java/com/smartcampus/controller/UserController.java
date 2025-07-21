package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.ChangePasswordDTO;
import com.smartcampus.dto.UpdateProfileDTO;
import com.smartcampus.dto.UserDTO;
import com.smartcampus.exception.PasswordChangeException;
import com.smartcampus.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Users", description = "User profile and password management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Get Current User Profile",
        description = "Retrieve the complete profile information of the currently authenticated user. " +
                     "This endpoint returns all user data including personal information, academic details, " +
                     "contact information, and profile statistics.",
        operationId = "getCurrentUserProfile"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User profile retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "success": true,
                        "message": "User profile retrieved successfully",
                        "data": {
                            "id": 1,
                            "name": "John Doe",
                            "email": "john.doe@example.com",
                            "role": "STUDENT",
                            "isActive": true,
                            "phoneNumber": "+1234567890",
                            "address": "123 Main St",
                            "city": "New York",
                            "state": "NY",
                            "zipCode": "10001",
                            "country": "USA",
                            "bio": "Software engineering student",
                            "department": "Computer Science",
                            "major": "Computer Science",
                            "year": 3,
                            "semester": "FALL",
                            "academicYear": "2024-2025",
                            "createdAt": "2024-01-15T10:30:00",
                            "updatedAt": "2024-01-20T14:45:00",
                            "lastLoginAt": "2024-01-20T14:45:00"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Unauthorized",
                    value = """
                    {
                        "success": false,
                        "message": "Authentication required",
                        "data": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Forbidden",
                    value = """
                    {
                        "success": false,
                        "message": "Access denied",
                        "data": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Not Found",
                    value = """
                    {
                        "success": false,
                        "message": "User not found",
                        "data": null
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        UserDTO userDTO = userService.getUserByEmail(email);
        
        return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .success(true)
                .message("User profile retrieved successfully")
                .data(userDTO)
                .build());
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Update Current User Profile",
        description = "Update the profile information of the currently authenticated user. " +
                     "This endpoint supports partial updates - only the fields provided in the request " +
                     "will be updated. All fields are optional and validated according to their constraints.",
        operationId = "updateCurrentUserProfile",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Profile update details - all fields are optional",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UpdateProfileDTO.class),
                examples = @ExampleObject(
                    name = "Basic Profile Update",
                    value = """
                    {
                        "name": "John Smith",
                        "email": "john.smith@example.com",
                        "phoneNumber": "+1234567890",
                        "address": "123 Main St",
                        "city": "New York",
                        "state": "NY",
                        "zipCode": "10001",
                        "country": "USA",
                        "bio": "Software engineering student passionate about technology"
                    }
                    """
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "success": true,
                        "message": "User profile updated successfully",
                        "data": {
                            "id": 1,
                            "name": "John Smith",
                            "email": "john.smith@example.com",
                            "phoneNumber": "+1234567890",
                            "address": "123 Main St",
                            "city": "New York",
                            "state": "NY",
                            "zipCode": "10001",
                            "country": "USA",
                            "bio": "Software engineering student passionate about technology",
                            "updatedAt": "2024-01-20T15:30:00"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error or email already taken",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                    {
                        "success": false,
                        "message": "Profile update failed",
                        "data": {
                            "email": "Email is already taken by another user",
                            "name": "Name must be between 2 and 100 characters"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Unauthorized",
                    value = """
                    {
                        "success": false,
                        "message": "Authentication required",
                        "data": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Forbidden",
                    value = """
                    {
                        "success": false,
                        "message": "Access denied",
                        "data": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Not Found",
                    value = """
                    {
                        "success": false,
                        "message": "User not found",
                        "data": null
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<UserDTO>> updateCurrentUserProfile(
            @Valid @RequestBody UpdateProfileDTO updateProfileDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        UserDTO currentUser = userService.getUserByEmail(email);
        
        // Check if email is being changed and if it's available
        if (updateProfileDTO.getEmail() != null && !updateProfileDTO.getEmail().equals(email)) {
            if (!userService.isEmailAvailable(updateProfileDTO.getEmail(), currentUser.getId())) {
                return ResponseEntity.badRequest().body(ApiResponse.<UserDTO>builder()
                        .success(false)
                        .message("Email is already taken by another user")
                        .build());
            }
        }
        
        UserDTO updatedUser = userService.updateUserProfile(currentUser.getId(), updateProfileDTO);
        
        return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .success(true)
                .message("User profile updated successfully")
                .data(updatedUser)
                .build());
    }

    @PutMapping("/me/password")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Change Current User Password",
        description = "Change the password of the currently authenticated user with comprehensive security validation. " +
                     "The new password must meet strength requirements and pass security checks including " +
                     "common pattern detection and personal information validation.",
        operationId = "changeCurrentUserPassword",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Password change details with security validation",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ChangePasswordDTO.class),
                examples = @ExampleObject(
                    name = "Password Change",
                    value = """
                    {
                        "currentPassword": "OldPassword123!",
                        "newPassword": "NewPassword456!",
                        "confirmPassword": "NewPassword456!"
                    }
                    """
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Password changed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "success": true,
                        "message": "Password changed successfully"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Password change failed - validation errors",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Password Error",
                    value = """
                    {
                        "success": false,
                        "message": "Password change failed",
                        "data": "Current password is incorrect"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Unauthorized",
                    value = """
                    {
                        "success": false,
                        "message": "Authentication required",
                        "data": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Forbidden",
                    value = """
                    {
                        "success": false,
                        "message": "Access denied",
                        "data": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Not Found",
                    value = """
                    {
                        "success": false,
                        "message": "User not found",
                        "data": null
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<String>> changeCurrentUserPassword(
            @Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        UserDTO currentUser = userService.getUserByEmail(email);
        
        // Validate current password
        if (!userService.validatePassword(email, changePasswordDTO.getCurrentPassword())) {
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Current password is incorrect")
                    .build());
        }
        
        // Validate new password
        if (!changePasswordDTO.isPasswordMatch()) {
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("New password and confirm password do not match")
                    .build());
        }
        
        if (!changePasswordDTO.isNewPasswordDifferent()) {
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("New password must be different from current password")
                    .build());
        }
        
        try {
            userService.changeUserPassword(currentUser.getId(), changePasswordDTO.getNewPassword());
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Password changed successfully")
                    .build());
        } catch (PasswordChangeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Password change failed: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/me/password-strength")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Check Password Strength",
        description = "Check the strength of a password without changing it. This endpoint provides " +
                     "detailed feedback on password strength including score, description, and suggestions " +
                     "for improvement.",
        operationId = "checkPasswordStrength"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Password strength checked successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Password Strength",
                    value = """
                    {
                        "success": true,
                        "message": "Password strength checked successfully",
                        "data": {
                            "score": 4,
                            "description": "Strong",
                            "isStrong": true,
                            "hasCommonPatterns": false,
                            "suggestions": ["Password is strong"]
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Unauthorized",
                    value = """
                    {
                        "success": false,
                        "message": "Authentication required",
                        "data": null
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Object>> checkPasswordStrength(
            @Parameter(description = "Password to check for strength", required = true, example = "NewPassword456!")
            @RequestParam String password) {
        
        ChangePasswordDTO tempDTO = ChangePasswordDTO.builder().newPassword(password).build();
        
        PasswordStrengthResult result = new PasswordStrengthResult(
            tempDTO.getPasswordStrengthScore(),
            tempDTO.getPasswordStrengthDescription(),
            tempDTO.isPasswordStrong(),
            tempDTO.containsCommonPatterns()
        );
        
        return ResponseEntity.ok(ApiResponse.<Object>builder()
                .success(true)
                .message("Password strength checked successfully")
                .data(result)
                .build());
    }

    @GetMapping("/me/email-availability")
    @PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Check Email Availability for Current User",
        description = "Check if an email address is available for the current user to use. " +
                     "This endpoint excludes the current user's email from the availability check, " +
                     "allowing users to keep their current email or change to a new one.",
        operationId = "checkEmailAvailabilityForCurrentUser"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Email availability checked successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Available",
                    value = """
                    {
                        "success": true,
                        "message": "Email availability checked successfully",
                        "data": true
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Unauthorized",
                    value = """
                    {
                        "success": false,
                        "message": "Authentication required",
                        "data": null
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailabilityForCurrentUser(
            @Parameter(description = "Email address to check for availability", required = true, example = "newemail@example.com")
            @RequestParam String email) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        UserDTO currentUser = userService.getUserByEmail(currentUserEmail);
        boolean isAvailable = userService.isEmailAvailable(email, currentUser.getId());
        
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .success(true)
                .message("Email availability checked successfully")
                .data(isAvailable)
                .build());
    }

    // Helper class for password strength result
    public static class PasswordStrengthResult {
        public final int score;
        public final String description;
        public final boolean isStrong;
        public final boolean hasCommonPatterns;
        public final String[] suggestions;

        public PasswordStrengthResult(int score, String description, boolean isStrong, boolean hasCommonPatterns) {
            this.score = score;
            this.description = description;
            this.isStrong = isStrong;
            this.hasCommonPatterns = hasCommonPatterns;
            this.suggestions = getSuggestions(score, hasCommonPatterns);
        }

        private String[] getSuggestions(int score, boolean hasCommonPatterns) {
            if (hasCommonPatterns) {
                return new String[]{"Avoid common patterns like '123', 'qwerty', or 'password'"};
            }
            
            switch (score) {
                case 0:
                case 1:
                    return new String[]{"Add uppercase letters", "Add numbers", "Add special characters", "Make it longer"};
                case 2:
                    return new String[]{"Add more character variety", "Make it longer"};
                case 3:
                    return new String[]{"Add special characters", "Make it longer"};
                case 4:
                    return new String[]{"Password is strong"};
                default:
                    return new String[]{"Password is very strong"};
            }
        }
    }
} 