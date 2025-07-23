// Authentication API response types

export interface LoginRequest {
  email: string;
  password: string;
}

export interface JwtResponse {
  token: string;
  type: string;
  id: number;
  name: string;
  email: string;
  role: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
}

export interface LoginResponse extends ApiResponse<JwtResponse> {}

export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  name?: string;
  firstName?: string;
  lastName?: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  role?: 'STUDENT' | 'PROFESSOR' | 'ADMIN'; // Optional since frontend will always send STUDENT
}

// User Profile Types (matching backend UserDTO)
export interface UserProfile {
  id: number;
  name: string;
  email: string;
  role: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  lastLoginAt?: string;
  
  // Contact Information
  phoneNumber?: string;
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  country?: string;
  bio?: string;
  
  // Academic Information (for students/professors)
  department?: string;
  major?: string;
  year?: number;
  semester?: string;
  academicYear?: string;
  
  // Profile Extras
  avatar?: string;
  preferences?: {
    theme?: string;
    language?: string;
    notifications?: {
      email?: boolean;
      push?: boolean;
      sms?: boolean;
    };
  };
}

export interface ProfileUpdateRequest {
  name?: string;
  email?: string;
  phoneNumber?: string;
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  country?: string;
  bio?: string;
  department?: string;
  major?: string;
  year?: number;
  semester?: string;
  academicYear?: string;
}

export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
} 