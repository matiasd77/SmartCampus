import React, { createContext, useContext, useState, useEffect, useRef } from 'react';
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
  refreshToken: () => Promise<boolean>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const refreshIntervalRef = useRef<number | null>(null);

  const isAuthenticated = !!token && !!user;

  // Function to check if token is about to expire (within 5 minutes)
  const isTokenExpiringSoon = (token: string): boolean => {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Date.now() / 1000;
      const timeUntilExpiry = payload.exp - currentTime;
      const fiveMinutes = 5 * 60; // 5 minutes in seconds
      return timeUntilExpiry < fiveMinutes;
    } catch (error) {
      console.error('Error checking token expiration:', error);
      return true; // Assume expired if we can't parse
    }
  };

  // Function to refresh the access token
  const refreshToken = async (): Promise<boolean> => {
    try {
      console.log('AuthContext: Attempting to refresh token...');
      const newToken = await authAPI.refreshToken();
      
      if (newToken) {
        console.log('AuthContext: Token refreshed successfully');
        setToken(newToken);
        setAuthData(newToken, user!);
        return true;
      } else {
        console.warn('AuthContext: Token refresh failed');
        return false;
      }
    } catch (error) {
      console.error('AuthContext: Error refreshing token:', error);
      return false;
    }
  };

  // Function to start automatic token refresh
  const startTokenRefresh = () => {
    if (refreshIntervalRef.current) {
      clearInterval(refreshIntervalRef.current);
    }
    
    // Check token every 5 minutes
    refreshIntervalRef.current = window.setInterval(async () => {
      if (token && isTokenExpiringSoon(token)) {
        console.log('AuthContext: Token expiring soon, refreshing...');
        const success = await refreshToken();
        if (!success) {
          console.warn('AuthContext: Token refresh failed, logging out');
          logout();
        }
      }
    }, 5 * 60 * 1000); // 5 minutes
  };

  // Function to stop automatic token refresh
  const stopTokenRefresh = () => {
    if (refreshIntervalRef.current) {
      clearInterval(refreshIntervalRef.current);
      refreshIntervalRef.current = null;
    }
  };

  // Initialize authentication state from localStorage
  useEffect(() => {
    const initializeAuth = async () => {
      try {
        console.log('AuthContext: Initializing authentication...');
        setIsLoading(true);
        
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
              setIsLoading(false);
              return;
            }
            
            console.log('AuthContext: Token format is valid, expires at:', new Date(payload.exp * 1000).toISOString());
            
          } catch (error) {
            console.error('AuthContext: Invalid token format, clearing auth data:', error);
            clearAuthData();
            setToken(null);
            setUser(null);
            setIsLoading(false);
            return;
          }
          
          // Set the token and user from localStorage immediately
          setToken(savedToken);
          
          // Ensure user data has all required fields with proper fallbacks
          const restoredUser = {
            id: (savedUser as any).id || (savedUser as any).userId || null,
            username: (savedUser as any).username || (savedUser as any).email || 'unknown',
            email: (savedUser as any).email || 'unknown@example.com',
            role: (savedUser as any).role || (savedUser as any).userRole || 'STUDENT',
            name: (savedUser as any).name || (savedUser as any).fullName || (savedUser as any).username || (savedUser as any).email || 'Unknown User'
          };
          
          console.log('AuthContext: Restored user data:', restoredUser);
          setUser(restoredUser);
          
          // Start automatic token refresh
          startTokenRefresh();
          
          // Validate token with backend in the background
          try {
            console.log('AuthContext: Validating token with backend...');
            
            // First try the new auth endpoint for debugging
            try {
              const authData = await authAPI.getCurrentUserAuth();
              console.log('AuthContext: Auth endpoint validation successful');
              
              // Use the auth data to update user state
              if (authData) {
                const updatedUser = {
                  id: (authData as any).id || (authData as any).userId || restoredUser.id,
                  username: (authData as any).username || (authData as any).email || restoredUser.username,
                  email: (authData as any).email || restoredUser.email,
                  role: (authData as any).role || (authData as any).userRole || restoredUser.role,
                  name: (authData as any).name || (authData as any).fullName || (authData as any).username || (authData as any).email || restoredUser.name
                };
                console.log('AuthContext: Updated user from backend');
                setUser(updatedUser);
              }
            } catch (authError: any) {
              console.warn('AuthContext: Auth endpoint failed, trying users/me:', authError.response?.status);
              
              // Fallback to users/me endpoint
              try {
                const userData = await authAPI.getCurrentUser();
                console.log('AuthContext: Token validation successful');
                
                // Update user data from backend response
                if (userData) {
                  const updatedUser = {
                    id: (userData as any).id || (userData as any).userId || restoredUser.id,
                    username: (userData as any).username || (userData as any).email || restoredUser.username,
                    email: (userData as any).email || restoredUser.email,
                    role: (userData as any).role || (userData as any).userRole || restoredUser.role,
                    name: (userData as any).name || (userData as any).fullName || (userData as any).username || (userData as any).email || restoredUser.name
                  };
                  console.log('AuthContext: Updated user from users/me');
                  setUser(updatedUser);
                }
              } catch (userError: any) {
                console.warn('AuthContext: Users/me endpoint also failed:', userError.response?.status);
                // Don't clear authentication if both endpoints fail - keep the stored data
                console.warn('AuthContext: Keeping stored authentication data despite endpoint failures');
              }
            }
          } catch (error) {
            console.error('AuthContext: Error validating token with backend:', error);
            // Don't clear authentication on backend validation failure
            console.warn('AuthContext: Keeping stored authentication data despite backend validation failure');
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
        // Always set loading to false after initialization
        console.log('AuthContext: Authentication initialization complete');
        setIsLoading(false);
      }
    };

    initializeAuth();
  }, []);

  // Save token and user to localStorage whenever they change
  useEffect(() => {
    if (token && user) {
      console.log('AuthContext: Saving authentication to localStorage', {
        hasToken: !!token,
        hasUser: !!user,
        userRole: user.role,
        userId: user.id
      });
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
      console.log('AuthContext.login - Starting login process for:', email);
      setIsLoading(true);
      
      const response = await authAPI.login(email, password);
      
      if (response && response.success && response.data) {
        const jwtResponse = response.data as JwtResponse;
        
        console.log('AuthContext.login - Login successful, setting authentication data');
        console.log('AuthContext.login - User data:', {
          id: jwtResponse.id,
          name: jwtResponse.name,
          email: jwtResponse.email,
          role: jwtResponse.role
        });
        
        // Set authentication data
        setToken(jwtResponse.token);
        const userData = {
          id: jwtResponse.id!,
          username: jwtResponse.email!, // Use email as username
          email: jwtResponse.email!,
          role: jwtResponse.role!,
          name: jwtResponse.name!
        };
        setUser(userData);
        
        // Save to localStorage
        setAuthData(jwtResponse.token, userData);
        
        // Start automatic token refresh
        startTokenRefresh();
        
        console.log('AuthContext.login - Authentication setup complete');
        return true;
      } else {
        console.error('AuthContext.login - Invalid response structure:', response);
        return false;
      }
    } catch (error: any) {
      console.error('AuthContext.login - Login failed:', error);
      console.error('AuthContext.login - Error response:', error.response?.data);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    console.log('AuthContext.logout - Logging out user');
    
    // Stop automatic token refresh
    stopTokenRefresh();
    
    // Clear authentication data
    clearAuthData();
    setToken(null);
    setUser(null);
    
    console.log('AuthContext.logout - Logout complete');
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
    refreshToken,
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