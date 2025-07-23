import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { errorHandler } from './errorHandler';
import { useToast } from '../contexts/ToastContext';

const ErrorHandlerSetup: React.FC = () => {
  const navigate = useNavigate();
  const { showError, showWarning, showInfo, showSuccess } = useToast();

  useEffect(() => {
    // Set up global error handling with toast integration
    errorHandler.setToastHandler((type, title, message) => {
      switch (type) {
        case 'error':
          showError(title, message);
          break;
        case 'warning':
          showWarning(title, message);
          break;
        case 'info':
          showInfo(title, message);
          break;
        case 'success':
          showSuccess(title, message);
          break;
      }
    });

    // Set up redirect handler
    errorHandler.setRedirectHandler((path) => {
      navigate(path);
    });

    // Set up global error handlers
    const handleUnhandledRejection = (event: PromiseRejectionEvent) => {
      console.error('Unhandled Promise Rejection:', event.reason);
      
      // Handle network errors
      if (event.reason?.message?.includes('Network Error')) {
        showError('Network Error', 'Unable to connect to the server. Please check your internet connection and ensure the backend is running.');
      } else if (event.reason?.message?.includes('timeout')) {
        showError('Request Timeout', 'The request took too long to complete. Please try again.');
      } else {
        showError('Unexpected Error', 'An unexpected error occurred. Please try refreshing the page.');
      }
    };

    const handleGlobalError = (event: ErrorEvent) => {
      console.error('Global Error:', event.error);
      
      // Don't show toast for common errors that are handled elsewhere
      if (event.error?.message?.includes('ResizeObserver')) {
        return; // Ignore ResizeObserver errors
      }
      
      showError('Application Error', 'An unexpected error occurred. Please try refreshing the page.');
    };

    // Add global error listeners
    window.addEventListener('unhandledrejection', handleUnhandledRejection);
    window.addEventListener('error', handleGlobalError);

    console.log('ErrorHandlerSetup: Global error handling configured with toast integration');

    // Cleanup function
    return () => {
      window.removeEventListener('unhandledrejection', handleUnhandledRejection);
      window.removeEventListener('error', handleGlobalError);
    };
  }, [navigate, showError, showWarning, showInfo, showSuccess]);

  return null; // This component doesn't render anything
};

export default ErrorHandlerSetup; 