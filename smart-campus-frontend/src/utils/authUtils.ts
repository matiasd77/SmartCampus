// Global authentication utilities

// Check if a JWT token is expired (basic check)
export const isTokenExpired = (token: string): boolean => {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const currentTime = Date.now() / 1000;
    return payload.exp < currentTime;
  } catch (error) {
    console.error('Error parsing JWT token:', error);
    return true; // Assume expired if we can't parse it
  }
};

// Check if a token exists and is not expired
export const isTokenValid = (): boolean => {
  const token = getStoredToken();
  if (!token) return false;
  return !isTokenExpired(token);
};

export const clearAuthData = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  
  // Dispatch a custom event to notify components
  window.dispatchEvent(new CustomEvent('auth:logout'));
};

export const getStoredToken = (): string | null => {
  return localStorage.getItem('token');
};

export const getStoredUser = () => {
  const userStr = localStorage.getItem('user');
  return userStr ? JSON.parse(userStr) : null;
};

export const setAuthData = (token: string, user: any) => {
  localStorage.setItem('token', token);
  localStorage.setItem('user', JSON.stringify(user));
};

// Get authentication status from localStorage
export const getAuthStatus = () => {
  const token = getStoredToken();
  const user = getStoredUser();
  
  return {
    hasToken: !!token,
    hasUser: !!user,
    isAuthenticated: !!token && !!user,
    tokenValid: token ? !isTokenExpired(token) : false
  };
}; 

/**
 * Get the current user's role from the stored user data
 */
export const getUserRole = (): string | null => {
  try {
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      console.log('getUserRole: No user data in localStorage');
      return null;
    }
    
    const user = JSON.parse(userStr);
    console.log('getUserRole: User data from localStorage:', {
      id: user.id,
      email: user.email,
      role: user.role,
      hasRole: !!user.role
    });
    return user.role || null;
  } catch (error) {
    console.error('Error parsing user data:', error);
    return null;
  }
};

/**
 * Check if the current user has a specific role
 */
export const hasRole = (role: string): boolean => {
  const userRole = getUserRole();
  return userRole === role;
};

/**
 * Check if the current user has any of the specified roles
 */
export const hasAnyRole = (roles: string[]): boolean => {
  const userRole = getUserRole();
  return userRole ? roles.includes(userRole) : false;
};

/**
 * Check if the current user is an admin
 */
export const isAdmin = (): boolean => {
  return hasRole('ADMIN');
};

/**
 * Check if the current user is a professor
 */
export const isProfessor = (): boolean => {
  return hasRole('PROFESSOR');
};

/**
 * Check if the current user is a student
 */
export const isStudent = (): boolean => {
  return hasRole('STUDENT');
};

/**
 * Check if the current user can access admin features
 */
export const canAccessAdminFeatures = (): boolean => {
  return hasAnyRole(['ADMIN']);
};

/**
 * Check if the current user can access professor features
 */
export const canAccessProfessorFeatures = (): boolean => {
  return hasAnyRole(['PROFESSOR', 'ADMIN']);
};

/**
 * Check if the current user can access student management
 */
export const canAccessStudentManagement = (): boolean => {
  return hasAnyRole(['PROFESSOR', 'ADMIN']);
};

/**
 * Check if the current user can access professor management
 */
export const canAccessProfessorManagement = (): boolean => {
  return hasAnyRole(['ADMIN']);
};

/**
 * Get user permissions based on role
 */
export const getUserPermissions = () => {
  const role = getUserRole();
  
  return {
    canViewStudents: hasAnyRole(['PROFESSOR', 'ADMIN']),
    canViewProfessors: hasAnyRole(['ADMIN']),
    canManageStudents: hasAnyRole(['ADMIN']),
    canManageProfessors: hasAnyRole(['ADMIN']),
    canViewDashboardStats: hasAnyRole(['PROFESSOR', 'ADMIN']),
    canAccessAdminPanel: hasAnyRole(['ADMIN']),
    role: role
  };
}; 