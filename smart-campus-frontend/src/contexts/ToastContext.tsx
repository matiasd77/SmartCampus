import React, { createContext, useContext, useState, useCallback } from 'react';
import { ToastContainer } from '../components/ToastContainer';
import type { ToastItem } from '../components/ToastContainer';
import type { ToastType } from '../components/Toast';

interface ToastContextType {
  showToast: (type: ToastType, title: string, message?: string, duration?: number) => void;
  showSuccess: (title: string, message?: string, duration?: number) => void;
  showError: (title: string, message?: string, duration?: number) => void;
  showWarning: (title: string, message?: string, duration?: number) => void;
  showInfo: (title: string, message?: string, duration?: number) => void;
  removeToast: (id: string) => void;
  clearToasts: () => void;
}

const ToastContext = createContext<ToastContextType | undefined>(undefined);

export const useToast = () => {
  const context = useContext(ToastContext);
  if (context === undefined) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
};

interface ToastProviderProps {
  children: React.ReactNode;
}

export const ToastProvider: React.FC<ToastProviderProps> = ({ children }) => {
  const [toasts, setToasts] = useState<ToastItem[]>([]);

  const generateId = useCallback(() => {
    return Math.random().toString(36).substr(2, 9);
  }, []);

  const showToast = useCallback((
    type: ToastType,
    title: string,
    message?: string,
    duration: number = 5000
  ) => {
    const id = generateId();
    const newToast: ToastItem = {
      id,
      type,
      title,
      message,
      duration,
    };

    setToasts(prev => [...prev, newToast]);
  }, [generateId]);

  const showSuccess = useCallback((
    title: string,
    message?: string,
    duration?: number
  ) => {
    showToast('success', title, message, duration);
  }, [showToast]);

  const showError = useCallback((
    title: string,
    message?: string,
    duration?: number
  ) => {
    showToast('error', title, message, duration);
  }, [showToast]);

  const showWarning = useCallback((
    title: string,
    message?: string,
    duration?: number
  ) => {
    showToast('warning', title, message, duration);
  }, [showToast]);

  const showInfo = useCallback((
    title: string,
    message?: string,
    duration?: number
  ) => {
    showToast('info', title, message, duration);
  }, [showToast]);

  const removeToast = useCallback((id: string) => {
    setToasts(prev => prev.filter(toast => toast.id !== id));
  }, []);

  const clearToasts = useCallback(() => {
    setToasts([]);
  }, []);

  const value: ToastContextType = {
    showToast,
    showSuccess,
    showError,
    showWarning,
    showInfo,
    removeToast,
    clearToasts,
  };

  return (
    <ToastContext.Provider value={value}>
      {children}
      <ToastContainer toasts={toasts} onClose={removeToast} />
    </ToastContext.Provider>
  );
}; 