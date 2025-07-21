package com.smartcampus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for changing user password with comprehensive security validation")
public class ChangePasswordDTO {

    @Schema(
        description = "User's current password for verification",
        example = "OldPassword123!",
        required = true
    )
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @Schema(
        description = "New password that meets security requirements",
        example = "NewPassword456!",
        required = true,
        minLength = 8,
        maxLength = 128,
        pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$"
    )
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 128, message = "New password must be between 8 and 128 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "New password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&)"
    )
    private String newPassword;

    @Schema(
        description = "Confirmation of the new password (must match newPassword)",
        example = "NewPassword456!",
        required = true
    )
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    // Helper method to check if new password matches confirm password
    public boolean isPasswordMatch() {
        return newPassword != null && confirmPassword != null && newPassword.equals(confirmPassword);
    }

    // Helper method to check if new password is different from current password
    public boolean isNewPasswordDifferent() {
        return currentPassword != null && newPassword != null && !currentPassword.equals(newPassword);
    }

    // Helper method to validate password strength
    public boolean isPasswordStrong() {
        if (newPassword == null || newPassword.length() < 8) {
            return false;
        }
        
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : newPassword.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if ("@$!%*?&".indexOf(c) >= 0) hasSpecial = true;
        }
        
        return hasLower && hasUpper && hasDigit && hasSpecial;
    }

    // Helper method to get password strength score (0-4)
    public int getPasswordStrengthScore() {
        if (newPassword == null) {
            return 0;
        }
        
        int score = 0;
        
        // Length check
        if (newPassword.length() >= 8) score++;
        if (newPassword.length() >= 12) score++;
        
        // Character variety checks
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : newPassword.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if ("@$!%*?&".indexOf(c) >= 0) hasSpecial = true;
        }
        
        if (hasLower) score++;
        if (hasUpper) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;
        
        return Math.min(score, 4);
    }

    // Helper method to get password strength description
    public String getPasswordStrengthDescription() {
        int score = getPasswordStrengthScore();
        switch (score) {
            case 0:
            case 1:
                return "Very Weak";
            case 2:
                return "Weak";
            case 3:
                return "Moderate";
            case 4:
                return "Strong";
            default:
                return "Unknown";
        }
    }

    // Helper method to check if password contains common patterns
    public boolean containsCommonPatterns() {
        if (newPassword == null) {
            return false;
        }
        
        String password = newPassword.toLowerCase();
        
        // Common patterns to avoid
        String[] commonPatterns = {
            "password", "123456", "qwerty", "admin", "user", "test",
            "abc123", "password123", "admin123", "user123", "test123",
            "123456789", "qwerty123", "password1", "admin1", "user1"
        };
        
        for (String pattern : commonPatterns) {
            if (password.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }

    // Helper method to check if password contains personal information
    public boolean containsPersonalInfo(String email, String name) {
        if (newPassword == null) {
            return false;
        }
        
        String password = newPassword.toLowerCase();
        
        // Check for email parts
        if (email != null) {
            String[] emailParts = email.toLowerCase().split("@");
            if (emailParts.length > 0) {
                String username = emailParts[0];
                if (password.contains(username)) {
                    return true;
                }
            }
        }
        
        // Check for name parts
        if (name != null) {
            String[] nameParts = name.toLowerCase().split("\\s+");
            for (String part : nameParts) {
                if (part.length() > 2 && password.contains(part)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    // Helper method to validate all password requirements
    public boolean isValidPassword() {
        return isPasswordMatch() && 
               isNewPasswordDifferent() && 
               isPasswordStrong() && 
               !containsCommonPatterns();
    }

    // Helper method to get validation errors
    public String getValidationErrors() {
        if (!isPasswordMatch()) {
            return "New password and confirm password do not match";
        }
        
        if (!isNewPasswordDifferent()) {
            return "New password must be different from current password";
        }
        
        if (!isPasswordStrong()) {
            return "New password must be at least 8 characters long and contain uppercase, lowercase, digit, and special character";
        }
        
        if (containsCommonPatterns()) {
            return "New password contains common patterns that are not allowed";
        }
        
        return null;
    }
} 