import React, { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { authAPI } from '../services/api';
import { clearAuthData, setAuthData, getStoredToken, getStoredUser } from '../utils/authUtils';
import type { User, LoginResponse, JwtResponse } from '../types/auth';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<boolean>;
  logout: () => void;
  checkAuth: () => Promise<boolean>;
  updateUser: (userData: Partial<User>) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const isAuthenticated = !!token && !!user;

  // Initialize authentication state from localStorage
  useEffect(() => {
    const initializeAuth = async () => {
      try {
        console.log('AuthContext: Initializing authentication...');
        
        const savedToken = getStoredToken();
        const savedUser = getStoredUser();
        
        if (savedToken && savedUser) {
          console.log('AuthContext: Found saved authentication data');
          
          // Validate token format and expiration before setting
          try {
            const payload = JSON.parse(atob(savedToken.split('.')[1]));
            const currentTime = Date.now() / 1000;
            
            if (payload.exp < currentTime) {
              console.warn('AuthContext: Token is expired, clearing auth data');
              clearAuthData();
              setToken(null);
              setUser(null);
              return;
            }
            
            console.log('AuthContext: Token format is valid, expires at:', new Date(payload.exp * 1000).toISOString());
            console.log('AuthContext: Token authorities:', payload.authorities || payload.roles || 'No authorities found');
            
          } catch (error) {
            console.error('AuthContext: Invalid token format, clearing auth data:', error);
            clearAuthData();
            setToken(null);
            setUser(null);
            return;
          }
          
          // Set the token and user from localStorage
          setToken(savedToken);
          setUser(savedUser);
          
          // Validate token with backend
          try {
            console.log('AuthContext: Validating token with backend...');
            const userData = await authAPI.getCurrentUser();
            console.log('AuthContext: Token validation successful, user data:', userData);
            
            // Update user data from backend response
            if (userData) {
              setUser(userData);
            }
          } catch (error: any) {
            console.error('AuthContext: Token validation failed:', error);
            
            if (error.response?.status === 401) {
              console.error('AuthContext: 401 Unauthorized - token is invalid, clearing auth data');
              clearAuthData();
              setToken(null);
              setUser(null);
            } else if (error.response?.status === 403) {
              console.error('AuthContext: 403 Forbidden - insufficient permissions, but token is valid');
              // Keep user logged in but log the issue
              console.error('AuthContext: User may not have required permissions for some endpoints');
            } else {
              console.warn('AuthContext: Network error during token validation, keeping user logged in');
            }
          }
        } else {
          console.log('AuthContext: No saved authentication found');
        }
      } catch (error) {
        console.error('AuthContext: Error initializing authentication:', error);
        // Only clear data if there's a critical error
        if (error instanceof Error && error.message.includes('JSON')) {
          // JSON parsing error - clear corrupted data
          clearAuthData();
          setToken(null);
          setUser(null);
        }
      } finally {
        setIsLoading(false);
      }
    };

    initializeAuth();
  }, []);

  // Save token and user to localStorage whenever they change
  useEffect(() => {
    if (token && user) {
      console.log('AuthContext: Saving authentication to localStorage');
      setAuthData(token, user);
    } else if (!token && !user) {
      // Clear localStorage when both token and user are null
      console.log('AuthContext: Clearing authentication from localStorage');
      clearAuthData();
    }
  }, [token, user]);

  // Listen for logout events from API interceptors
  useEffect(() => {
    const handleLogout = () => {
      console.log('AuthContext: Received logout event from API interceptor');
      setToken(null);
      setUser(null);
    };

    window.addEventListener('auth:logout', handleLogout);

    return () => {
      window.removeEventListener('auth:logout', handleLogout);
    };
  }, []);

  const login = async (email: string, password: string): Promise<boolean> => {
    try {
      setIsLoading(true);
      
      console.log('AuthContext.login - Starting login process for:', email);
      
      const response: LoginResponse = await authAPI.login(email, password);
      
      console.log('AuthContext.login - Raw response:', response);
      console.log('AuthContext.login - Response structure:', {
        success: response.success,
        message: response.message,
        hasData: !!response.data,
        dataKeys: response.data ? Object.keys(response.data) : 'no data'
      });
      
      // Check if the response indicates success
      if (!response.success) {
        console.error('AuthContext.login - Backend returned success: false');
        console.error('AuthContext.login - Error message:', response.message);
        return false;
      }
      
      // Extract JWT token and user data from the nested structure
      // Backend returns: { success: true, message: "...", data: { token: "...", user: {...} } }
      const jwtData: JwtResponse = response.data!;
      
      if (!jwtData || !jwtData.token) {
        console.error('AuthContext.login - No token found in response data');
        console.error('AuthContext.login - JWT data structure:', jwtData);
        return false;
      }
      
      console.log('AuthContext.login - Token extracted successfully');
      console.log('AuthContext.login - User data:', {
        id: jwtData.id,
        name: jwtData.name,
        email: jwtData.email,
        role: jwtData.role
      });
      
      // Create user object from JWT response data
      const userData: User = {
        id: jwtData.id,
        username: jwtData.email, // Use email as username since backend doesn't have separate username
        email: jwtData.email,
        role: jwtData.role,
        firstName: jwtData.name?.split(' ')[0], // Extract first name from full name
        lastName: jwtData.name?.split(' ').slice(1).join(' ') // Extract last name from full name
      };
      
      // Save token and user data to localStorage and state
      setAuthData(jwtData.token, userData);
      setToken(jwtData.token);
      setUser(userData);
      
      console.log('AuthContext.login - Login successful, token and user data saved to localStorage');
      return true;
      
    } catch (error) {
      console.error('AuthContext.login - Login failed with error:', error);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    console.log('AuthContext.logout - Clearing authentication data');
    setToken(null);
    setUser(null);
    clearAuthData();
    
    // Redirect to login page
    if (window.location.pathname !== '/login') {
      window.location.href = '/login';
    }
  };

  const checkAuth = async (): Promise<boolean> => {
    try {
      if (!token) {
        console.log('AuthContext.checkAuth - No token available');
        return false;
      }

      // First check token format and expiration
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const currentTime = Date.now() / 1000;
        
        if (payload.exp < currentTime) {
          console.error('AuthContext.checkAuth - Token is expired');
          logout();
          return false;
        }
        
        console.log('AuthContext.checkAuth - Token format valid, authorities:', payload.authorities || payload.roles || 'No authorities found');
      } catch (error) {
        console.error('AuthContext.checkAuth - Invalid token format:', error);
        logout();
        return false;
      }

      console.log('AuthContext.checkAuth - Validating token with backend...');
      const userData = await authAPI.getCurrentUser();
      setUser(userData);
      console.log('AuthContext.checkAuth - Token validation successful');
      return true;
    } catch (error: any) {
      console.error('AuthContext.checkAuth - Token validation failed:', error);
      
      // Handle specific error cases
      if (error.response?.status === 403) {
        console.error('AuthContext.checkAuth - 403 Forbidden: Insufficient permissions');
        console.error('AuthContext.checkAuth - Error details:', {
          status: error.response.status,
          statusText: error.response.statusText,
          data: error.response.data,
          message: error.message
        });
        
        // Log current token details for debugging
        if (token) {
          try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            console.error('AuthContext.checkAuth - Current token details:', {
              subject: payload.sub,
              authorities: payload.authorities || payload.roles || 'No authorities found',
              issuedAt: new Date(payload.iat * 1000).toISOString(),
              expiresAt: new Date(payload.exp * 1000).toISOString()
            });
          } catch (e) {
            console.error('AuthContext.checkAuth - Failed to parse current token:', e);
          }
        }
        
        // Don't logout on 403 - this might be a role issue
        return false;
      } else if (error.response?.status === 401) {
        console.error('AuthContext.checkAuth - 401 Unauthorized: Token invalid or expired');
        logout();
        return false;
      } else if (!error.response) {
        console.error('AuthContext.checkAuth - Network error: Unable to reach server');
      }
      
      return false;
    }
  };

  const updateUser = (userData: Partial<User>) => {
    if (user) {
      const updatedUser = { ...user, ...userData };
      setUser(updatedUser);
      // localStorage will be updated via useEffect
    }
  };

  const value: AuthContextType = {
    user,
    token,
    isAuthenticated,
    isLoading,
    login,
    logout,
    checkAuth,
    updateUser,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}; 