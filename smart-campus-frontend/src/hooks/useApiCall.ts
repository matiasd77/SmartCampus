import { useState, useCallback } from 'react';
import { useToast } from '../contexts/ToastContext';
import type { AxiosResponse } from 'axios';

interface UseApiCallOptions<T> {
  onSuccess?: (data: T) => void;
  onError?: (error: any) => void;
  successMessage?: string;
  errorMessage?: string;
  showSuccessToast?: boolean;
  showErrorToast?: boolean;
  loadingMessage?: string;
}

interface UseApiCallReturn<T> {
  data: T | null;
  loading: boolean;
  error: any;
  execute: (...args: any[]) => Promise<T | null>;
  reset: () => void;
}

export function useApiCall<T = any>(
  apiFunction: (...args: any[]) => Promise<AxiosResponse<T>>,
  options: UseApiCallOptions<T> = {}
): UseApiCallReturn<T> {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<any>(null);
  const { showSuccess, showError, showInfo } = useToast();

  const {
    onSuccess,
    onError,
    successMessage,
    errorMessage,
    showSuccessToast = true,
    showErrorToast = true,
    loadingMessage,
  } = options;

  const execute = useCallback(
    async (...args: any[]): Promise<T | null> => {
      setLoading(true);
      setError(null);

      if (loadingMessage) {
        showInfo('Loading', loadingMessage, 0); // No auto-dismiss for loading
      }

      try {
        const response = await apiFunction(...args);
        const responseData = response.data;

        setData(responseData);

        if (onSuccess) {
          onSuccess(responseData);
        }

        if (showSuccessToast && successMessage) {
          showSuccess('Success', successMessage);
        }

        return responseData;
      } catch (err: any) {
        setError(err);

        if (onError) {
          onError(err);
        }

        if (showErrorToast) {
          const message = errorMessage || err.response?.data?.message || 'An error occurred';
          showError('Error', message);
        }

        return null;
      } finally {
        setLoading(false);
      }
    },
    [
      apiFunction,
      onSuccess,
      onError,
      successMessage,
      errorMessage,
      showSuccessToast,
      showErrorToast,
      loadingMessage,
      showSuccess,
      showError,
      showInfo,
    ]
  );

  const reset = useCallback(() => {
    setData(null);
    setLoading(false);
    setError(null);
  }, []);

  return {
    data,
    loading,
    error,
    execute,
    reset,
  };
}

// Convenience hooks for common API patterns
export function useCreateApiCall<T = any>(
  apiFunction: (data: any) => Promise<AxiosResponse<T>>,
  options: UseApiCallOptions<T> = {}
) {
  return useApiCall(apiFunction, {
    successMessage: 'Created successfully',
    ...options,
  });
}

export function useUpdateApiCall<T = any>(
  apiFunction: (id: number, data: any) => Promise<AxiosResponse<T>>,
  options: UseApiCallOptions<T> = {}
) {
  return useApiCall(apiFunction, {
    successMessage: 'Updated successfully',
    ...options,
  });
}

export function useDeleteApiCall<T = any>(
  apiFunction: (id: number) => Promise<AxiosResponse<T>>,
  options: UseApiCallOptions<T> = {}
) {
  return useApiCall(apiFunction, {
    successMessage: 'Deleted successfully',
    ...options,
  });
}

export function useFetchApiCall<T = any>(
  apiFunction: (...args: any[]) => Promise<AxiosResponse<T>>,
  options: UseApiCallOptions<T> = {}
) {
  return useApiCall(apiFunction, {
    showSuccessToast: false, // Don't show success toast for fetch operations
    ...options,
  });
} 