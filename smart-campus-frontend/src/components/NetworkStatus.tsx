import React from 'react';
import { AlertTriangle, Wifi, WifiOff, RefreshCw, Shield, UserX } from 'lucide-react';
import { getUserRole, hasAnyRole } from '../utils/authUtils';

interface NetworkStatusProps {
  error?: string | null;
  onRetry?: () => void;
  isLoading?: boolean;
  statusCode?: number;
}

export const NetworkStatus: React.FC<NetworkStatusProps> = ({ 
  error, 
  onRetry, 
  isLoading = false,
  statusCode 
}) => {
  const userRole = getUserRole();
  
  const getErrorMessage = () => {
    if (!error) return null;
    
    // Handle 403 Forbidden errors with role-specific messages
    if (statusCode === 403) {
      if (userRole === 'STUDENT') {
        return {
          title: 'Access Restricted',
          message: 'You don\'t have permission to access this feature. Students can only view their own courses, grades, and announcements.',
          icon: <Shield className="w-5 h-5" />,
          color: 'text-orange-600',
          bgColor: 'bg-orange-50',
          borderColor: 'border-orange-200'
        };
      } else if (userRole === 'PROFESSOR') {
        return {
          title: 'Access Restricted',
          message: 'You don\'t have permission to access this feature. Professors can view students and manage their courses.',
          icon: <Shield className="w-5 h-5" />,
          color: 'text-orange-600',
          bgColor: 'bg-orange-50',
          borderColor: 'border-orange-200'
        };
      } else {
        return {
          title: 'Access Denied',
          message: 'You don\'t have sufficient permissions to access this resource.',
          icon: <UserX className="w-5 h-5" />,
          color: 'text-red-600',
          bgColor: 'bg-red-50',
          borderColor: 'border-red-200'
        };
      }
    }
    
    // Handle network errors
    if (error.includes('Network Error') || error.includes('Connection error')) {
      return {
        title: 'Connection Error',
        message: 'Unable to connect to the server. Please check your internet connection and try again.',
        icon: <WifiOff className="w-5 h-5" />,
        color: 'text-red-600',
        bgColor: 'bg-red-50',
        borderColor: 'border-red-200'
      };
    }
    
    // Handle timeout errors
    if (error.includes('timeout') || error.includes('Request timeout')) {
      return {
        title: 'Request Timeout',
        message: 'The server is taking too long to respond. Please try again.',
        icon: <RefreshCw className="w-5 h-5" />,
        color: 'text-yellow-600',
        bgColor: 'bg-yellow-50',
        borderColor: 'border-yellow-200'
      };
    }
    
    // Default error
    return {
      title: 'Error',
      message: error,
      icon: <AlertTriangle className="w-5 h-5" />,
      color: 'text-red-600',
      bgColor: 'bg-red-50',
      borderColor: 'border-red-200'
    };
  };

  const errorInfo = getErrorMessage();

  if (!errorInfo && !isLoading) {
    return (
      <div className="mb-6">
        <div className="flex items-center p-4 bg-green-50 border border-green-200 rounded-xl">
          <Wifi className="w-5 h-5 text-green-600 mr-3" />
          <span className="text-green-800 font-medium">Connected to SmartCampus</span>
        </div>
      </div>
    );
  }

  return (
    <div className="mb-6">
      {isLoading && (
        <div className="flex items-center p-4 bg-blue-50 border border-blue-200 rounded-xl">
          <RefreshCw className="w-5 h-5 text-blue-600 mr-3 animate-spin" />
          <span className="text-blue-800 font-medium">Loading...</span>
        </div>
      )}
      
      {errorInfo && (
        <div className={`flex items-start p-4 ${errorInfo.bgColor} border ${errorInfo.borderColor} rounded-xl`}>
          <div className={`${errorInfo.color} mr-3 mt-0.5`}>
            {errorInfo.icon}
          </div>
          <div className="flex-1">
            <h3 className={`font-semibold ${errorInfo.color} mb-1`}>
              {errorInfo.title}
            </h3>
            <p className="text-gray-700 text-sm mb-3">
              {errorInfo.message}
            </p>
            {onRetry && (
              <button
                onClick={onRetry}
                className="inline-flex items-center px-3 py-2 text-sm font-medium text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 transition-colors duration-200"
              >
                <RefreshCw className="w-4 h-4 mr-2" />
                Try Again
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
}; 