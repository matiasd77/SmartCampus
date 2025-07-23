import type { AxiosError, AxiosResponse } from 'axios';

export interface ApiError {
  message: string;
  status: number;
  code?: string;
  details?: any;
}

export interface ErrorHandlerConfig {
  showToast?: boolean;
  logError?: boolean;
  redirectOnAuthError?: boolean;
}

export class ErrorHandler {
  private static instance: ErrorHandler;
  private toastHandler?: (type: 'error' | 'warning' | 'info' | 'success', title: string, message?: string) => void;
  private redirectHandler?: (path: string) => void;

  private constructor() {}

  static getInstance(): ErrorHandler {
    if (!ErrorHandler.instance) {
      ErrorHandler.instance = new ErrorHandler();
    }
    return ErrorHandler.instance;
  }

  setToastHandler(handler: (type: 'error' | 'warning' | 'info' | 'success', title: string, message?: string) => void) {
    this.toastHandler = handler;
  }

  setRedirectHandler(handler: (path: string) => void) {
    this.redirectHandler = handler;
  }

  private getErrorMessage(error: AxiosError): string {
    const responseData = error.response?.data as any;
    
    // Check for server error response
    if (responseData?.message) {
      return responseData.message;
    }

    // Check for validation errors
    if (responseData?.errors && Array.isArray(responseData.errors)) {
      return responseData.errors.map((err: any) => err.message || err).join(', ');
    }

    // Check for specific error types
    if (responseData?.error) {
      return responseData.error;
    }

    // Network errors
    if (error.code === 'NETWORK_ERROR' || error.code === 'ERR_NETWORK') {
      return 'Network error. Please check your internet connection.';
    }

    if (error.code === 'ECONNABORTED') {
      return 'Request timeout. Please try again.';
    }

    // Default error messages based on status codes
    switch (error.response?.status) {
      case 400:
        return 'Bad request. Please check your input.';
      case 401:
        return 'Authentication required. Please log in again.';
      case 403:
        return 'Access denied. You don\'t have permission to perform this action.';
      case 404:
        return 'Resource not found.';
      case 409:
        return 'Conflict. The resource already exists or has been modified.';
      case 422:
        return 'Validation error. Please check your input.';
      case 429:
        return 'Too many requests. Please try again later.';
      case 500:
        return 'Internal server error. Please try again later.';
      case 502:
        return 'Bad gateway. Please try again later.';
      case 503:
        return 'Service unavailable. Please try again later.';
      case 504:
        return 'Gateway timeout. Please try again later.';
      default:
        return error.message || 'An unexpected error occurred.';
    }
  }

  private getErrorTitle(error: AxiosError): string {
    const status = error.response?.status;
    
    switch (status) {
      case 400:
        return 'Invalid Request';
      case 401:
        return 'Authentication Error';
      case 403:
        return 'Access Denied';
      case 404:
        return 'Not Found';
      case 409:
        return 'Conflict';
      case 422:
        return 'Validation Error';
      case 429:
        return 'Too Many Requests';
      case 500:
        return 'Server Error';
      case 502:
      case 503:
      case 504:
        return 'Service Error';
      default:
        return 'Error';
    }
  }

  private shouldShowToast(error: AxiosError, config?: ErrorHandlerConfig): boolean {
    if (config?.showToast === false) return false;
    
    // Don't show toast for 401 errors if redirectOnAuthError is true
    if (error.response?.status === 401 && config?.redirectOnAuthError) {
      return false;
    }

    // Don't show toast for network errors if they're handled elsewhere
    if (error.code === 'NETWORK_ERROR' || error.code === 'ERR_NETWORK') {
      return config?.showToast === undefined || config?.showToast === true;
    }

    return true;
  }

  handleError(error: AxiosError, config?: ErrorHandlerConfig): ApiError {
    const message = this.getErrorMessage(error);
    const title = this.getErrorTitle(error);
    const status = error.response?.status || 0;

    // Log error if enabled
    if (config?.logError !== false) {
      console.error('API Error:', {
        status,
        message,
        url: error.config?.url,
        method: error.config?.method,
        data: error.response?.data,
        error,
      });
    }

    // Show toast notification
    if (this.shouldShowToast(error, config)) {
      this.toastHandler?.('error', title, message);
    }

    // Handle authentication errors
    if (status === 401 && config?.redirectOnAuthError !== false) {
      this.handleAuthError();
    }

    return {
      message,
      status,
      code: error.code,
      details: error.response?.data,
    };
  }

  private handleAuthError() {
    // Clear local storage
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    
    // Redirect to login
    this.redirectHandler?.('/login');
  }

  handleSuccess(response: AxiosResponse, message?: string, config?: ErrorHandlerConfig) {
    if (config?.showToast !== false && message) {
      this.toastHandler?.('success', 'Success', message);
    }
  }

  // Convenience methods for common error types
  handleNetworkError(error: AxiosError, config?: ErrorHandlerConfig) {
    const networkError = {
      ...error,
      response: { status: 0, data: { message: 'Network error. Please check your internet connection.' } },
    } as AxiosError;
    
    return this.handleError(networkError, config);
  }

  handleValidationError(error: AxiosError, config?: ErrorHandlerConfig) {
    return this.handleError(error, { ...config, showToast: true });
  }

  handleAuthErrorPublic(error: AxiosError, config?: ErrorHandlerConfig) {
    return this.handleError(error, { ...config, redirectOnAuthError: true });
  }
}

// Export singleton instance
export const errorHandler = ErrorHandler.getInstance(); 