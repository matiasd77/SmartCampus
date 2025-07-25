import axios from 'axios';
import { errorHandler } from '../utils/errorHandler';
import { clearAuthData, getStoredToken, getUserRole, hasAnyRole } from '../utils/authUtils';
import type { LoginRequest, LoginResponse, RegisterRequest, User } from '../types/auth';

// API Configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// Create axios instance with improved configuration
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000, // Increased timeout to 30 seconds for testing
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
  // Enable credentials for CORS
  withCredentials: true,
  // Add retry configuration
  validateStatus: (status) => {
    return status >= 200 && status < 300; // Only accept 2xx status codes
  },
});

// Request interceptor to add JWT token and handle requests
api.interceptors.request.use(
  (config) => {
    // Skip auth interceptor if header is present
    if (config.headers?.['X-Skip-Auth-Interceptor'] === 'true') {
      console.log(`API Request: ${config.method?.toUpperCase()} ${config.url} - Skipping auth interceptor`);
      return config;
    }

    // Get token from localStorage and automatically include in all requests
    const token = getStoredToken();
    
    if (token && token.trim() !== '') {
      // Check if token is expired before using it
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const currentTime = Date.now() / 1000;
        
        if (payload.exp < currentTime) {
          console.warn(`API Request: ${config.method?.toUpperCase()} ${config.url} - Token is expired, clearing auth data`);
          clearAuthData();
          // Don't add expired token to request
        } else {
          config.headers.Authorization = `Bearer ${token}`;
          console.log(`API Request: ${config.method?.toUpperCase()} ${config.url} - Authorization header included`);
          
          // Special logging for /users/me endpoint
          if (config.url === '/users/me') {
            console.log('API Request: /users/me - Token details:', {
              tokenLength: token.length,
              tokenStart: token.substring(0, 20) + '...',
              tokenEnd: '...' + token.substring(token.length - 20),
              hasBearer: config.headers.Authorization?.startsWith('Bearer '),
              fullHeader: config.headers.Authorization
            });
          }
          
          // Log token details for debugging (only in development)
          if (import.meta.env.DEV) {
            console.log('Token Details:', {
              subject: payload.sub,
              authorities: payload.authorities || payload.roles || 'No authorities found',
              issuedAt: new Date(payload.iat * 1000).toISOString(),
              expiresAt: new Date(payload.exp * 1000).toISOString(),
              issuer: payload.iss
            });
          }
        }
      } catch (error) {
        console.error('Error parsing JWT token:', error);
        console.error('Token that failed to parse:', token);
        clearAuthData();
      }
    } else {
      console.log(`API Request: ${config.method?.toUpperCase()} ${config.url} - No valid token found`);
    }
    
    // Log request details for debugging (only in development)
    if (import.meta.env.DEV) {
      console.log('Request Config:', {
        url: config.url,
        method: config.method,
        headers: config.headers,
        data: config.data,
        params: config.params,
        timestamp: new Date().toISOString(),
      });
    }
    
    return config;
  },
  (error) => {
    console.error('Request Interceptor Error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor to handle token expiration and global errors
api.interceptors.response.use(
  (response) => {
    // Log successful responses in development
    if (import.meta.env.DEV) {
      console.log(`API Response: ${response.config.method?.toUpperCase()} ${response.config.url} - Status: ${response.status}`);
    }
    return response;
  },
  async (error) => {
    // Enhanced error logging
    console.error('API Error Details:', {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      url: error.config?.url,
      method: error.config?.method,
      data: error.response?.data,
      isNetworkError: !error.response,
      isTimeout: error.code === 'ECONNABORTED',
      isCORS: error.message.includes('CORS') || error.message.includes('cors'),
    });

    // Handle network errors (no response)
    if (!error.response) {
      let errorMessage = 'Connection error - please check your internet connection';
      
      if (error.code === 'ECONNABORTED') {
        errorMessage = 'Request timeout - server is not responding. Please try again.';
        console.error('Timeout Error Details:', {
          url: error.config?.url,
          method: error.config?.method,
          timeout: error.config?.timeout,
          message: 'Request exceeded timeout limit'
        });
      } else if (error.message.includes('Network Error')) {
        errorMessage = 'Network error - unable to connect to server. Please check if the backend is running on http://localhost:8080.';
      } else if (error.message.includes('CORS') || error.message.includes('cors')) {
        errorMessage = 'CORS error - please check backend CORS configuration.';
      } else if (error.message.includes('ERR_NETWORK')) {
        errorMessage = 'Network connection failed. Please check your internet connection and ensure the backend server is running.';
      }
      
      error.message = errorMessage;
      console.error('Network Error:', errorMessage);
    }

    // Handle authentication errors (401 Unauthorized)
    if (error.response?.status === 401) {
      console.log('API: Unauthorized request (401), handling authentication error');
      
      const currentPath = window.location.pathname;
      const isAuthRequest = error.config?.url?.includes('/auth/');
      const isRefreshRequest = error.config?.url?.includes('/auth/refresh');
      const skipAuthInterceptor = error.config?.headers?.['X-Skip-Auth-Interceptor'] === 'true';
      
      // Don't try to refresh if this is already a refresh request or if auth interceptor is skipped
      if (!isRefreshRequest && !isAuthRequest && !skipAuthInterceptor) {
        try {
          // Try to refresh the token
          console.log('API: Attempting token refresh...');
          const refreshResponse = await api.post('/auth/refresh', {}, { 
            headers: { 'X-Skip-Auth-Interceptor': 'true' }
          });
          
          if (refreshResponse.data?.token) {
            console.log('API: Token refresh successful, retrying original request');
            // Update the token in the original request
            error.config.headers.Authorization = `Bearer ${refreshResponse.data.token}`;
            // Retry the original request
            return api.request(error.config);
          }
        } catch (refreshError) {
          console.log('API: Token refresh failed, logging out user');
        }
      }
      
      // If refresh failed or this is an auth request, handle logout
      if (currentPath !== '/login' && currentPath !== '/register' && !isAuthRequest) {
        console.log('API: Clearing authentication and redirecting to login');
        clearAuthData();
        window.dispatchEvent(new CustomEvent('auth:logout'));
        window.location.href = '/login';
      } else {
        console.log('API: 401 error on auth page or auth request, not redirecting');
      }
    }

    // Handle authorization errors (403 Forbidden)
    if (error.response?.status === 403) {
      console.error('API: Forbidden request (403), authorization error');
      console.error('API: 403 Error Details:', {
        url: error.config?.url,
        method: error.config?.method,
        headers: error.config?.headers,
        responseData: error.response?.data,
        token: error.config?.headers?.Authorization ? 'Present' : 'Missing'
      });
      
      // Log the current token for debugging
      const token = getStoredToken();
      if (token) {
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          console.error('API: Current token details:', {
            subject: payload.sub,
            authorities: payload.authorities || payload.roles || 'No authorities found',
            issuedAt: new Date(payload.iat * 1000).toISOString(),
            expiresAt: new Date(payload.exp * 1000).toISOString()
          });
        } catch (e) {
          console.error('API: Failed to parse current token:', e);
        }
      }
      
      // Don't automatically logout on 403 - let the component handle it
      // This could be a role/permission issue rather than an invalid token
    }

    // Handle server errors
    if (error.response?.status >= 500) {
      error.message = 'Server error - please try again later. If the problem persists, contact support.';
    }

    // Handle CORS errors
    if (error.response?.status === 0 || error.message.includes('CORS')) {
      error.message = 'CORS error - please check backend configuration. Backend should allow requests from http://localhost:5173.';
    }

    // Handle all errors globally
    errorHandler.handleError(error);
    return Promise.reject(error);
  }
);

// Auth API calls
export const authAPI = {
  login: async (email: string, password: string): Promise<LoginResponse> => {
    const requestBody: LoginRequest = { email, password };
    
    console.log('authAPI.login - Request body:', requestBody);
    console.log('authAPI.login - Request URL: /auth/login');
    
    try {
      const response = await api.post('/auth/login', requestBody);
      console.log('authAPI.login - Response:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('authAPI.login - Error:', error);
      console.error('authAPI.login - Error response:', error.response?.data);
      console.error('authAPI.login - Error status:', error.response?.status);
      throw error;
    }
  },
  
  register: async (userData: RegisterRequest): Promise<LoginResponse> => {
    // Ensure only STUDENT role is sent for public registration
    const requestBody: RegisterRequest = {
      name: userData.name.trim(),
      email: userData.email.trim().toLowerCase(),
      password: userData.password,
      role: 'STUDENT' // Force STUDENT role for public registration
    };
    
    console.log('authAPI.register - Request body:', requestBody);
    console.log('authAPI.register - Request URL: /auth/register');
    console.log('authAPI.register - Note: Only STUDENT role allowed for public registration');
    
    try {
      const response = await api.post('/auth/register', requestBody);
      console.log('authAPI.register - Response:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('authAPI.register - Error:', error);
      console.error('authAPI.register - Error response:', error.response?.data);
      console.error('authAPI.register - Error status:', error.response?.status);
      throw error;
    }
  },
  
  getCurrentUser: async (): Promise<User> => {
    console.log('authAPI.getCurrentUser - Making request to /users/me');
    
    // Debug token and role information
    const token = getStoredToken();
    const userRole = getUserRole();
    console.log('authAPI.getCurrentUser - Token present:', !!token);
    console.log('authAPI.getCurrentUser - User role from storage:', userRole);
    
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        console.log('authAPI.getCurrentUser - Token payload:', {
          subject: payload.sub,
          authorities: payload.authorities || payload.roles || 'No authorities found',
          issuedAt: new Date(payload.iat * 1000).toISOString(),
          expiresAt: new Date(payload.exp * 1000).toISOString(),
          issuer: payload.iss
        });
      } catch (error) {
        console.error('authAPI.getCurrentUser - Error parsing token:', error);
      }
    }
    
    try {
      const response = await api.get('/users/me');
      console.log('authAPI.getCurrentUser - Response:', response.data);
      
      // Handle ApiResponse wrapper structure
      if (response.data && response.data.success && response.data.data) {
        console.log('authAPI.getCurrentUser - Extracting user data from ApiResponse');
        return response.data.data;
      } else if (response.data && response.data.id) {
        console.log('authAPI.getCurrentUser - Using direct user object');
        return response.data;
      } else {
        console.error('authAPI.getCurrentUser - Unexpected response structure:', response.data);
        throw new Error('Invalid response structure from getCurrentUser');
      }
    } catch (error: any) {
      console.error('authAPI.getCurrentUser - Error:', error);
      console.error('authAPI.getCurrentUser - Error response:', error.response?.data);
      console.error('authAPI.getCurrentUser - Error status:', error.response?.status);
      console.error('authAPI.getCurrentUser - Error statusText:', error.response?.statusText);
      throw error;
    }
  },

  getCurrentUserAuth: async (): Promise<any> => {
    console.log('authAPI.getCurrentUserAuth - Making request to /auth/current-user');
    
    // Debug token information
    const token = getStoredToken();
    console.log('authAPI.getCurrentUserAuth - Token present:', !!token);
    
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        console.log('authAPI.getCurrentUserAuth - Token payload:', {
          subject: payload.sub,
          authorities: payload.authorities || payload.roles || 'No authorities found',
          issuedAt: new Date(payload.iat * 1000).toISOString(),
          expiresAt: new Date(payload.exp * 1000).toISOString(),
          issuer: payload.iss
        });
      } catch (error) {
        console.error('authAPI.getCurrentUserAuth - Error parsing token:', error);
      }
    }
    
    try {
      const response = await api.get('/auth/current-user');
      console.log('authAPI.getCurrentUserAuth - Response:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('authAPI.getCurrentUserAuth - Error:', error);
      console.error('authAPI.getCurrentUserAuth - Error response:', error.response?.data);
      console.error('authAPI.getCurrentUserAuth - Error status:', error.response?.status);
      throw error;
    }
  },

  refreshToken: async (): Promise<string | null> => {
    console.log('authAPI.refreshToken - Making request to /auth/refresh');
    
    try {
      const response = await api.post('/auth/refresh', {}, { withCredentials: true });
      console.log('authAPI.refreshToken - Response:', response.data);
      
      if (response.data && response.data.success && response.data.data && response.data.data.token) {
        console.log('authAPI.refreshToken - New access token received');
        return response.data.data.token;
      } else {
        console.error('authAPI.refreshToken - Invalid response structure:', response.data);
        return null;
      }
    } catch (error: any) {
      console.error('authAPI.refreshToken - Error:', error);
      console.error('authAPI.refreshToken - Error response:', error.response?.data);
      console.error('authAPI.refreshToken - Error status:', error.response?.status);
      return null;
    }
  },
};

// Profile API calls
export const profileAPI = {
  getProfile: async (retryCount = 0): Promise<any> => {
    const maxRetries = 2;
    console.log(`profileAPI.getProfile - Making request to /users/me (attempt ${retryCount + 1}/${maxRetries + 1})`);
    
    try {
      // Add request timeout specifically for profile requests
      const response = await api.get('/users/me', {
        timeout: 15000, // 15 seconds timeout for profile requests
      });
      console.log('profileAPI.getProfile - Response:', response.data);
      
      // Handle ApiResponse wrapper structure
      if (response.data && response.data.success && response.data.data) {
        console.log('profileAPI.getProfile - Extracting user data from ApiResponse');
        return response.data.data;
      } else if (response.data && response.data.id) {
        console.log('profileAPI.getProfile - Using direct user object');
        return response.data;
      } else {
        console.error('profileAPI.getProfile - Unexpected response structure:', response.data);
        throw new Error('Invalid response structure from getProfile');
      }
    } catch (error: any) {
      console.error(`profileAPI.getProfile - Error (attempt ${retryCount + 1}):`, error);
      console.error('profileAPI.getProfile - Error details:', {
        message: error.message,
        status: error.response?.status,
        statusText: error.response?.statusText,
        isNetworkError: !error.response,
        isTimeout: error.code === 'ECONNABORTED',
        retryCount,
      });
      
      // Retry logic for network errors and timeouts
      if (retryCount < maxRetries && (!error.response || error.code === 'ECONNABORTED')) {
        console.log(`profileAPI.getProfile - Retrying in 2 seconds... (attempt ${retryCount + 1}/${maxRetries})`);
        await new Promise(resolve => setTimeout(resolve, 2000)); // Wait 2 seconds before retry
        return profileAPI.getProfile(retryCount + 1);
      }
      
      throw error;
    }
  },
  
  updateProfile: async (profileData: {
    name?: string;
    email?: string;
    phoneNumber?: string;
    address?: string;
    city?: string;
    state?: string;
    zipCode?: string;
    country?: string;
    bio?: string;
  }) => {
    console.log('profileAPI.updateProfile - Request data:', profileData);
    try {
      const response = await api.put('/users/me', profileData);
      console.log('profileAPI.updateProfile - Response:', response.data);
      
      // Handle ApiResponse wrapper structure
      if (response.data && response.data.success && response.data.data) {
        console.log('profileAPI.updateProfile - Extracting user data from ApiResponse');
        return response.data.data;
      } else if (response.data && response.data.id) {
        console.log('profileAPI.updateProfile - Using direct user object');
        return response.data;
      } else {
        console.error('profileAPI.updateProfile - Unexpected response structure:', response.data);
        throw new Error('Invalid response structure from updateProfile');
      }
    } catch (error: any) {
      console.error('profileAPI.updateProfile - Error:', error);
      console.error('profileAPI.updateProfile - Error response:', error.response?.data);
      throw error;
    }
  },
  
  changePassword: async (passwordData: {
    currentPassword: string;
    newPassword: string;
    confirmPassword: string;
  }) => {
    console.log('profileAPI.changePassword - Making request to /users/me/change-password');
    try {
      const response = await api.post('/users/me/change-password', passwordData);
      console.log('profileAPI.changePassword - Response:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('profileAPI.changePassword - Error:', error);
      console.error('profileAPI.changePassword - Error response:', error.response?.data);
      throw error;
    }
  },
  
  uploadAvatar: async (file: File) => {
    console.log('profileAPI.uploadAvatar - Making request to /users/me/avatar');
    const formData = new FormData();
    formData.append('avatar', file);
    
    try {
      const response = await api.post('/users/me/avatar', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      console.log('profileAPI.uploadAvatar - Response:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('profileAPI.uploadAvatar - Error:', error);
      console.error('profileAPI.uploadAvatar - Error response:', error.response?.data);
      throw error;
    }
  },
  
  deleteAvatar: async () => {
    console.log('profileAPI.deleteAvatar - Making request to /users/me/avatar');
    try {
      const response = await api.delete('/users/me/avatar');
      console.log('profileAPI.deleteAvatar - Response:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('profileAPI.deleteAvatar - Error:', error);
      console.error('profileAPI.deleteAvatar - Error response:', error.response?.data);
      throw error;
    }
  },
};

// Dashboard API calls
export const dashboardAPI = {
  getSummary: async () => {
    try {
      const response = await api.get('/dashboard/summary');
      return response.data;
    } catch (error) {
      console.error('dashboardAPI.getSummary - Error:', error);
      throw error;
    }
  },
  
  getStudentsCount: async () => {
    // Only fetch students count if user has permission (PROFESSOR or ADMIN)
    if (!hasAnyRole(['PROFESSOR', 'ADMIN'])) {
      console.log('dashboardAPI.getStudentsCount - User does not have permission to view students count');
      return { count: 0 };
    }
    
    console.log('dashboardAPI.getStudentsCount - Fetching students count from /students');
    try {
      const response = await api.get('/students');
      console.log('dashboardAPI.getStudentsCount - Response:', response.data);
      const students = response.data.data || response.data;
      return { count: Array.isArray(students) ? students.length : 0 };
    } catch (error) {
      console.error('dashboardAPI.getStudentsCount - Error:', error);
      return { count: 0 };
    }
  },
  
  getProfessorsCount: async () => {
    // Only fetch professors count if user has permission (ADMIN only)
    if (!hasAnyRole(['ADMIN'])) {
      console.log('dashboardAPI.getProfessorsCount - User does not have permission to view professors count');
      return { count: 0 };
    }
    
    console.log('dashboardAPI.getProfessorsCount - Fetching professors count from /professors');
    try {
      const response = await api.get('/professors');
      console.log('dashboardAPI.getProfessorsCount - Response:', response.data);
      const professors = response.data.data || response.data;
      return { count: Array.isArray(professors) ? professors.length : 0 };
    } catch (error) {
      console.error('dashboardAPI.getProfessorsCount - Error:', error);
      return { count: 0 };
    }
  },
  
  getAnnouncementsCount: async () => {
    console.log('dashboardAPI.getAnnouncementsCount - Fetching announcements count from /announcements/stats/count/active');
    try {
      const response = await api.get('/announcements/stats/count/active');
      console.log('dashboardAPI.getAnnouncementsCount - Response:', response.data);
      const count = response.data.data || 0;
      return { count };
    } catch (error) {
      console.error('dashboardAPI.getAnnouncementsCount - Error:', error);
      return { count: 0 };
    }
  },
  
  getNotificationsCount: async () => {
    console.log('dashboardAPI.getNotificationsCount - Fetching notifications count from /notifications/unread/count');
    try {
      const response = await api.get('/notifications/unread/count');
      console.log('dashboardAPI.getNotificationsCount - Response:', response.data);
      const count = response.data.data || 0;
      return { count };
    } catch (error) {
      console.error('dashboardAPI.getNotificationsCount - Error:', error);
      return { count: 0 };
    }
  },

  getRecentActivity: async () => {
    console.log('dashboardAPI.getRecentActivity - Fetching recent activity from /announcements/recent');
    try {
      const response = await api.get('/announcements/recent');
      console.log('dashboardAPI.getRecentActivity - Response:', response.data);
      const announcements = response.data.data || response.data;
      const activities = Array.isArray(announcements) ? announcements.slice(0, 5).map((announcement: any) => ({
        id: announcement.id,
        type: 'announcement',
        description: announcement.title,
        userName: announcement.postedBy?.name || announcement.postedBy?.firstName + ' ' + announcement.postedBy?.lastName || 'System',
        timestamp: announcement.createdAt || announcement.postedAt || new Date().toISOString()
      })) : [];
      return { activities };
    } catch (error) {
      console.error('dashboardAPI.getRecentActivity - Error:', error);
      return { activities: [] };
    }
  },
};

// Courses API calls
export const coursesAPI = {
  getCourses: async (page = 0, size = 10, filters?: {
    search?: string;
    status?: string;
    department?: string;
    available?: boolean;
  }) => {
    try {
      const params = { page, size, ...filters };
      console.log('coursesAPI.getCourses: Making request with params:', params);
      const response = await api.get('/courses', { params });
      console.log('coursesAPI.getCourses: Response received:', response.data);
      return response.data;
    } catch (error) {
      console.error('coursesAPI.getCourses - Error:', error);
      throw error;
    }
  },
  
  getCourseById: async (id: number) => {
    try {
      const response = await api.get(`/courses/${id}`);
      return response.data;
    } catch (error) {
      console.error('coursesAPI.getCourseById - Error:', error);
      throw error;
    }
  },
  
  createCourse: async (courseData: {
    name: string;
    code: string;
    description: string;
    credits: number;
    department: string;
    professorId: number;
    maxStudents: number;
    status: string;
  }) => {
    try {
      const response = await api.post('/courses', courseData);
      return response.data;
    } catch (error) {
      console.error('coursesAPI.createCourse - Error:', error);
      throw error;
    }
  },
  
  updateCourse: async (id: number, courseData: {
    name?: string;
    code?: string;
    description?: string;
    credits?: number;
    department?: string;
    professorId?: number;
    maxStudents?: number;
    status?: string;
  }) => {
    try {
      const response = await api.put(`/courses/${id}`, courseData);
      return response.data;
    } catch (error) {
      console.error('coursesAPI.updateCourse - Error:', error);
      throw error;
    }
  },
  
  deleteCourse: async (id: number) => {
    try {
      const response = await api.delete(`/courses/${id}`);
      return response.data;
    } catch (error) {
      console.error('coursesAPI.deleteCourse - Error:', error);
      throw error;
    }
  },
  
  getCoursesByDepartment: async (department: string) => {
    try {
      const response = await api.get(`/courses/department/${department}`);
      return response.data;
    } catch (error) {
      console.error('coursesAPI.getCoursesByDepartment - Error:', error);
      throw error;
    }
  },
  
  getAvailableCourses: async () => {
    try {
      const response = await api.get('/courses/available');
      return response.data;
    } catch (error) {
      console.error('coursesAPI.getAvailableCourses - Error:', error);
      throw error;
    }
  },
  
  getDepartments: async () => {
    try {
      const response = await api.get('/courses/departments');
      return response.data;
    } catch (error) {
      console.error('coursesAPI.getDepartments - Error:', error);
      throw error;
    }
  },
};

// Enrollments API calls
export const enrollmentsAPI = {
  getEnrollments: async (page = 0, size = 10, filters?: {
    studentId?: number;
    courseId?: number;
    status?: string;
  }) => {
    try {
      const params = { page, size, ...filters };
      const response = await api.get('/enrollments', { params });
      return response.data;
    } catch (error) {
      console.error('enrollmentsAPI.getEnrollments - Error:', error);
      throw error;
    }
  },
  
  getEnrollmentById: async (id: number) => {
    try {
      const response = await api.get(`/enrollments/${id}`);
      return response.data;
    } catch (error) {
      console.error('enrollmentsAPI.getEnrollmentById - Error:', error);
      throw error;
    }
  },
  
  createEnrollment: async (enrollmentData: {
    studentId: number;
    courseId: number;
  }) => {
    try {
      const response = await api.post('/enrollments', enrollmentData);
      return response.data;
    } catch (error) {
      console.error('enrollmentsAPI.createEnrollment - Error:', error);
      throw error;
    }
  },
  
  updateEnrollment: async (id: number, enrollmentData: {
    status?: string;
  }) => {
    try {
      const response = await api.put(`/enrollments/${id}`, enrollmentData);
      return response.data;
    } catch (error) {
      console.error('enrollmentsAPI.updateEnrollment - Error:', error);
      throw error;
    }
  },
  
  deleteEnrollment: async (id: number) => {
    try {
      const response = await api.delete(`/enrollments/${id}`);
      return response.data;
    } catch (error) {
      console.error('enrollmentsAPI.deleteEnrollment - Error:', error);
      throw error;
    }
  },
  
  getStudentEnrollments: async (studentId: number) => {
    try {
      const response = await api.get(`/enrollments/student/${studentId}`);
      return response.data;
    } catch (error) {
      console.error('enrollmentsAPI.getStudentEnrollments - Error:', error);
      throw error;
    }
  },
  
  getCourseEnrollments: async (courseId: number) => {
    try {
      const response = await api.get(`/enrollments/course/${courseId}`);
      return response.data;
    } catch (error) {
      console.error('enrollmentsAPI.getCourseEnrollments - Error:', error);
      throw error;
    }
  },
};

// Grades API calls
export const gradesAPI = {
  getGrades: async (page = 0, size = 10, filters?: {
    studentId?: number;
    courseId?: number;
    enrollmentId?: number;
    type?: string;
    status?: string;
  }) => {
    try {
      const params = { page, size, ...filters };
      const response = await api.get('/grades', { params });
      return response.data;
    } catch (error) {
      console.error('gradesAPI.getGrades - Error:', error);
      throw error;
    }
  },
  
  getGradeById: async (id: number) => {
    try {
      const response = await api.get(`/grades/${id}`);
      return response.data;
    } catch (error) {
      console.error('gradesAPI.getGradeById - Error:', error);
      throw error;
    }
  },
  
  createGrade: async (gradeData: {
    enrollmentId: number;
    gradeValue: number;
    maxPoints: number;
    gradeType: string;
    assignmentName: string;
    comment?: string;
  }) => {
    try {
      const response = await api.post('/grades', gradeData);
      return response.data;
    } catch (error) {
      console.error('gradesAPI.createGrade - Error:', error);
      throw error;
    }
  },
  
  updateGrade: async (id: number, gradeData: {
    gradeValue?: number;
    maxPoints?: number;
    gradeType?: string;
    assignmentName?: string;
    comment?: string;
    status?: string;
  }) => {
    try {
      const response = await api.put(`/grades/${id}`, gradeData);
      return response.data;
    } catch (error) {
      console.error('gradesAPI.updateGrade - Error:', error);
      throw error;
    }
  },
  
  deleteGrade: async (id: number) => {
    try {
      const response = await api.delete(`/grades/${id}`);
      return response.data;
    } catch (error) {
      console.error('gradesAPI.deleteGrade - Error:', error);
      throw error;
    }
  },
  
  getStudentGrades: async (studentId: number) => {
    try {
      const response = await api.get(`/grades/student/${studentId}`);
      return response.data;
    } catch (error) {
      console.error('gradesAPI.getStudentGrades - Error:', error);
      throw error;
    }
  },
  
  getCourseGrades: async (courseId: number) => {
    try {
      const response = await api.get(`/grades/course/${courseId}`);
      return response.data;
    } catch (error) {
      console.error('gradesAPI.getCourseGrades - Error:', error);
      throw error;
    }
  },
  
  getEnrollmentGrades: async (enrollmentId: number) => {
    try {
      const response = await api.get(`/grades/enrollment/${enrollmentId}`);
      return response.data;
    } catch (error) {
      console.error('gradesAPI.getEnrollmentGrades - Error:', error);
      throw error;
    }
  },
};

// Attendance API calls
export const attendanceAPI = {
  getAttendance: async (page = 0, size = 10, filters?: {
    studentId?: number;
    courseId?: number;
    date?: string;
    status?: string;
  }) => {
    try {
      const params = { page, size, ...filters };
      const response = await api.get('/attendance', { params });
      return response.data;
    } catch (error) {
      console.error('attendanceAPI.getAttendance - Error:', error);
      throw error;
    }
  },
  
  getAttendanceById: async (id: number) => {
    try {
      const response = await api.get(`/attendance/${id}`);
      return response.data;
    } catch (error) {
      console.error('attendanceAPI.getAttendanceById - Error:', error);
      throw error;
    }
  },
  
  createAttendance: async (attendanceData: {
    studentId: number;
    courseId: number;
    date: string;
    status: string;
    notes?: string;
  }) => {
    try {
      const response = await api.post('/attendance', attendanceData);
      return response.data;
    } catch (error) {
      console.error('attendanceAPI.createAttendance - Error:', error);
      throw error;
    }
  },
  
  updateAttendance: async (id: number, attendanceData: {
    status?: string;
    notes?: string;
  }) => {
    try {
      const response = await api.put(`/attendance/${id}`, attendanceData);
      return response.data;
    } catch (error) {
      console.error('attendanceAPI.updateAttendance - Error:', error);
      throw error;
    }
  },
  
  deleteAttendance: async (id: number) => {
    try {
      const response = await api.delete(`/attendance/${id}`);
      return response.data;
    } catch (error) {
      console.error('attendanceAPI.deleteAttendance - Error:', error);
      throw error;
    }
  },
  
  getStudentAttendance: async (studentId: number, courseId?: number) => {
    try {
      const params = courseId ? { courseId } : {};
      const response = await api.get(`/attendance/student/${studentId}`, { params });
      return response.data;
    } catch (error) {
      console.error('attendanceAPI.getStudentAttendance - Error:', error);
      throw error;
    }
  },
  
  getCourseAttendance: async (courseId: number, date?: string) => {
    try {
      const params = date ? { date } : {};
      const response = await api.get(`/attendance/course/${courseId}`, { params });
      return response.data;
    } catch (error) {
      console.error('attendanceAPI.getCourseAttendance - Error:', error);
      throw error;
    }
  },
  
  getAttendanceByDate: async (date: string, courseId?: number) => {
    try {
      const params = courseId ? { courseId } : {};
      const response = await api.get(`/attendance/date/${date}`, { params });
      return response.data;
    } catch (error) {
      console.error('attendanceAPI.getAttendanceByDate - Error:', error);
      throw error;
    }
  },
};

// Announcements API calls
export const announcementsAPI = {
  getAnnouncements: async (page = 0, size = 10) => {
    try {
      console.log('announcementsAPI.getAnnouncements: Making request with page:', page, 'size:', size);
      const response = await api.get('/announcements/paginated', { 
        params: { page, size } 
      });
      console.log('announcementsAPI.getAnnouncements: Response received:', response.data);
      return response.data;
    } catch (error) {
      console.error('announcementsAPI.getAnnouncements - Error:', error);
      throw error;
    }
  },
  
  getAnnouncementById: async (id: number) => {
    try {
      const response = await api.get(`/announcements/${id}`);
      return response.data;
    } catch (error) {
      console.error('announcementsAPI.getAnnouncementById - Error:', error);
      throw error;
    }
  },
  
  createAnnouncement: async (announcementData: {
    title: string;
    content: string;
    courseId?: number;
    priority: string;
    type: string;
    isPublic: boolean;
  }) => {
    try {
      // Get current user from localStorage
      const userData = localStorage.getItem('user');
      let postedById = 0;
      
      if (userData) {
        try {
          const user = JSON.parse(userData);
          postedById = user.id;
          console.log('announcementsAPI.createAnnouncement: Found user ID:', postedById);
        } catch (e) {
          console.error('Error parsing user data:', e);
        }
      } else {
        console.warn('announcementsAPI.createAnnouncement: No user data found in localStorage');
      }
      
      // Add postedById to the announcement data
      const requestData = {
        ...announcementData,
        postedById
      };
      
      console.log('announcementsAPI.createAnnouncement: Original data:', announcementData);
      console.log('announcementsAPI.createAnnouncement: Sending data with postedById:', requestData);
      const response = await api.post('/announcements', requestData);
      console.log('announcementsAPI.createAnnouncement: Response received:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('announcementsAPI.createAnnouncement - Error:', error);
      if (error.response) {
        console.error('announcementsAPI.createAnnouncement - Response status:', error.response.status);
        console.error('announcementsAPI.createAnnouncement - Response data:', error.response.data);
      }
      throw error;
    }
  },
  
  updateAnnouncement: async (id: number, announcementData: {
    title?: string;
    content?: string;
    courseId?: number;
    priority?: string;
    type?: string;
    isPublic?: boolean;
    status?: string;
  }) => {
    try {
      const response = await api.put(`/announcements/${id}`, announcementData);
      return response.data;
    } catch (error) {
      console.error('announcementsAPI.updateAnnouncement - Error:', error);
      throw error;
    }
  },
  
  deleteAnnouncement: async (id: number) => {
    try {
      const response = await api.delete(`/announcements/${id}`);
      return response.data;
    } catch (error) {
      console.error('announcementsAPI.deleteAnnouncement - Error:', error);
      throw error;
    }
  },
  
  getActiveAnnouncements: async () => {
    try {
      const response = await api.get('/announcements/active');
      return response.data;
    } catch (error) {
      console.error('announcementsAPI.getActiveAnnouncements - Error:', error);
      throw error;
    }
  },
  
  getPublicAnnouncements: async () => {
    try {
      const response = await api.get('/announcements/public');
      return response.data;
    } catch (error) {
      console.error('announcementsAPI.getPublicAnnouncements - Error:', error);
      throw error;
    }
  },
  
  getCourseAnnouncements: async (courseId: number) => {
    try {
      const response = await api.get(`/announcements/course/${courseId}`);
      return response.data;
    } catch (error) {
      console.error('announcementsAPI.getCourseAnnouncements - Error:', error);
      throw error;
    }
  },
};

// Notifications API calls
export const notificationsAPI = {
  getNotifications: async (page = 0, size = 10, filters?: {
    status?: string;
    priority?: string;
    type?: string;
    read?: boolean;
  }) => {
    try {
      console.log('notificationsAPI.getNotifications: Making request with page:', page, 'size:', size, 'filters:', filters);
      const response = await api.get('/notifications/paginated', { 
        params: { page, size, ...filters } 
      });
      console.log('notificationsAPI.getNotifications: Response received:', response.data);
      return response.data;
    } catch (error) {
      console.error('notificationsAPI.getNotifications - Error:', error);
      throw error;
    }
  },
  
  getNotificationById: async (id: number) => {
    try {
      const response = await api.get(`/notifications/${id}`);
      return response.data;
    } catch (error) {
      console.error('notificationsAPI.getNotificationById - Error:', error);
      throw error;
    }
  },
  
  markAsRead: async (id: number) => {
    try {
      const response = await api.patch(`/notifications/${id}/read`);
      return response.data;
    } catch (error) {
      console.error('notificationsAPI.markAsRead - Error:', error);
      throw error;
    }
  },
  
  markAllAsRead: async () => {
    try {
      const response = await api.patch('/notifications/read-all');
      return response.data;
    } catch (error) {
      console.error('notificationsAPI.markAllAsRead - Error:', error);
      throw error;
    }
  },
  
  markAsArchived: async (id: number) => {
    try {
      const response = await api.patch(`/notifications/${id}/archive`);
      return response.data;
    } catch (error) {
      console.error('notificationsAPI.markAsArchived - Error:', error);
      throw error;
    }
  },
  
  deleteNotification: async (id: number) => {
    try {
      const response = await api.delete(`/notifications/${id}`);
      return response.data;
    } catch (error) {
      console.error('notificationsAPI.deleteNotification - Error:', error);
      throw error;
    }
  },
  
  getUnreadCount: async () => {
    try {
      const response = await api.get('/notifications/unread/count');
      return response.data;
    } catch (error) {
      console.error('notificationsAPI.getUnreadCount - Error:', error);
      throw error;
    }
  },
  
  getNotificationsByType: async (type: string) => {
    try {
      const response = await api.get(`/notifications/type/${type}`);
      return response.data;
    } catch (error) {
      console.error('notificationsAPI.getNotificationsByType - Error:', error);
      throw error;
    }
  },
  
  getNotificationsByPriority: async (priority: string) => {
    try {
      const response = await api.get(`/notifications/priority/${priority}`);
      return response.data;
    } catch (error) {
      console.error('notificationsAPI.getNotificationsByPriority - Error:', error);
      throw error;
    }
  },

  createNotification: async (notificationData: {
    title: string;
    message: string;
    userId: number;
    type: string;
    priority: string;
  }) => {
    try {
      const response = await api.post('/notifications', notificationData);
      return response.data;
    } catch (error) {
      console.error('notificationsAPI.createNotification - Error:', error);
      throw error;
    }
  },
};

// Students API calls
export const studentsAPI = {
  getStudents: async (filters?: {
    search?: string;
    status?: string;
    major?: string;
    year?: string;
  }) => {
    try {
      console.log('studentsAPI.getStudents: Making request with filters:', filters);
      const response = await api.get('/students', { params: filters });
      console.log('studentsAPI.getStudents: Response received:', response.data);
      return response.data;
    } catch (error) {
      console.error('studentsAPI.getStudents - Error:', error);
      throw error;
    }
  },
  
  getStudentById: async (id: number) => {
    try {
      const response = await api.get(`/students/${id}`);
      return response.data;
    } catch (error) {
      console.error('studentsAPI.getStudentById - Error:', error);
      throw error;
    }
  },
  
  createStudent: async (studentData: {
    studentId: string;
    firstName: string;
    lastName: string;
    email: string;
    yearOfStudy: number;
    major: string;
    status: string;
    userId: number;
  }) => {
    try {
      const response = await api.post('/students', studentData);
      return response.data;
    } catch (error) {
      console.error('studentsAPI.createStudent - Error:', error);
      throw error;
    }
  },
  
  updateStudent: async (id: number, studentData: {
    studentId?: string;
    firstName?: string;
    lastName?: string;
    email?: string;
    yearOfStudy?: number;
    major?: string;
    status?: string;
    userId?: number;
  }) => {
    try {
      const response = await api.put(`/students/${id}`, studentData);
      return response.data;
    } catch (error) {
      console.error('studentsAPI.updateStudent - Error:', error);
      throw error;
    }
  },
  
  deleteStudent: async (id: number) => {
    try {
      const response = await api.delete(`/students/${id}`);
      return response.data;
    } catch (error) {
      console.error('studentsAPI.deleteStudent - Error:', error);
      throw error;
    }
  },
  
  getStudentsByStatus: async (status: string) => {
    try {
      const response = await api.get(`/students/status/${status}`);
      return response.data;
    } catch (error) {
      console.error('studentsAPI.getStudentsByStatus - Error:', error);
      throw error;
    }
  },
  
  getStudentsByYear: async (year: number) => {
    try {
      const response = await api.get(`/students/year/${year}`);
      return response.data;
    } catch (error) {
      console.error('studentsAPI.getStudentsByYear - Error:', error);
      throw error;
    }
  },
  
  getStudentsByMajor: async (major: string) => {
    try {
      const response = await api.get(`/students/major/${major}`);
      return response.data;
    } catch (error) {
      console.error('studentsAPI.getStudentsByMajor - Error:', error);
      throw error;
    }
  },
};

// Professors API calls
export const professorsAPI = {
  getProfessors: async (page = 0, size = 10, filters?: {
    search?: string;
    status?: string;
    department?: string;
    rank?: string;
  }) => {
    try {
      const params = { page, size, ...filters };
      console.log('professorsAPI.getProfessors: Making request with params:', params);
      const response = await api.get('/professors', { params });
      console.log('professorsAPI.getProfessors: Response received:', response.data);
      return response.data;
    } catch (error) {
      console.error('professorsAPI.getProfessors - Error:', error);
      throw error;
    }
  },
  
  getProfessorById: async (id: number) => {
    try {
      const response = await api.get(`/professors/${id}`);
      return response.data;
    } catch (error) {
      console.error('professorsAPI.getProfessorById - Error:', error);
      throw error;
    }
  },
  
  createProfessor: async (professorData: {
    firstName: string;
    lastName: string;
    email: string;
    department: string;
    rank: string;
    status: string;
  }) => {
    try {
      const response = await api.post('/professors', professorData);
      return response.data;
    } catch (error) {
      console.error('professorsAPI.createProfessor - Error:', error);
      throw error;
    }
  },
  
  updateProfessor: async (id: number, professorData: {
    firstName?: string;
    lastName?: string;
    email?: string;
    department?: string;
    rank?: string;
    status?: string;
  }) => {
    try {
      const response = await api.put(`/professors/${id}`, professorData);
      return response.data;
    } catch (error) {
      console.error('professorsAPI.updateProfessor - Error:', error);
      throw error;
    }
  },
  
  deleteProfessor: async (id: number) => {
    try {
      const response = await api.delete(`/professors/${id}`);
      return response.data;
    } catch (error) {
      console.error('professorsAPI.deleteProfessor - Error:', error);
      throw error;
    }
  },
  
  getProfessorsByDepartment: async (department: string) => {
    try {
      const response = await api.get(`/professors/department/${department}`);
      return response.data;
    } catch (error) {
      console.error('professorsAPI.getProfessorsByDepartment - Error:', error);
      throw error;
    }
  },
  
  getProfessorsByRank: async (rank: string) => {
    try {
      const response = await api.get(`/professors/rank/${rank}`);
      return response.data;
    } catch (error) {
      console.error('professorsAPI.getProfessorsByRank - Error:', error);
      throw error;
    }
  },
  
  getProfessorsByStatus: async (status: string) => {
    try {
      const response = await api.get(`/professors/status/${status}`);
      return response.data;
    } catch (error) {
      console.error('professorsAPI.getProfessorsByStatus - Error:', error);
      throw error;
    }
  },
};

// Admin API endpoints
export const adminAPI = {
  getAllUsers: async () => {
    try {
      const response = await api.get('/admin/users');
      return response.data;
    } catch (error) {
      console.error('adminAPI.getAllUsers - Error:', error);
      throw error;
    }
  },
  
  deleteUser: async (id: number) => {
    try {
      const response = await api.delete(`/admin/users/${id}`);
      return response.data;
    } catch (error) {
      console.error('adminAPI.deleteUser - Error:', error);
      throw error;
    }
  },
  
  updateUser: async (id: number, userData: {
    firstName?: string;
    lastName?: string;
    email?: string;
    role?: string;
    isActive?: boolean;
  }) => {
    try {
      const response = await api.put(`/admin/users/${id}`, userData);
      return response.data;
    } catch (error) {
      console.error('adminAPI.updateUser - Error:', error);
      throw error;
    }
  },
  
  createUser: async (userData: {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    role: string;
  }) => {
    try {
      const response = await api.post('/admin/users', userData);
      return response.data;
    } catch (error) {
      console.error('adminAPI.createUser - Error:', error);
      throw error;
    }
  },
};

export default api; 