import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { getStoredToken, getAuthStatus } from '../utils/authUtils';

interface PrivateRouteProps {
  children: React.ReactNode;
  requiredRole?: string;
}

export const PrivateRoute: React.FC<PrivateRouteProps> = ({ 
  children, 
  requiredRole 
}) => {
  const { isAuthenticated, isLoading, user } = useAuth();
  const location = useLocation();

  // Check if we have a token in localStorage as a fallback
  const hasStoredToken = !!getStoredToken();
  const authStatus = getAuthStatus();

  console.log('PrivateRoute: Authentication check:', {
    isAuthenticated,
    isLoading,
    hasStoredToken,
    authStatus,
    currentPath: location.pathname
  });

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 via-blue-50/30 to-indigo-50/50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-indigo-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  // If not authenticated and no stored token, redirect to login
  if (!isAuthenticated && !hasStoredToken) {
    console.log('PrivateRoute: No authentication found, redirecting to login');
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // If we have a stored token but AuthContext says not authenticated,
  // this might be a timing issue - let the user through and let the API handle it
  if (!isAuthenticated && hasStoredToken) {
    console.log('PrivateRoute: Has stored token but AuthContext not ready, allowing access');
    return <>{children}</>;
  }

  // Check role-based access if requiredRole is specified
  if (requiredRole && user?.role !== requiredRole) {
    console.log(`PrivateRoute: User role ${user?.role} does not match required role ${requiredRole}`);
    // Redirect to unauthorized page or dashboard
    return <Navigate to="/dashboard" replace />;
  }

  console.log('PrivateRoute: Authentication successful, rendering protected content');
  return <>{children}</>;
}; 