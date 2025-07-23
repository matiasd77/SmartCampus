import React, { useEffect, useState } from 'react';
import { 
  CheckCircle, 
  XCircle, 
  AlertTriangle, 
  Info, 
  X,
  Sparkles
} from 'lucide-react';

export type ToastType = 'success' | 'error' | 'warning' | 'info';

export interface ToastProps {
  id: string;
  type: ToastType;
  title: string;
  message?: string;
  duration?: number;
  onClose: (id: string) => void;
}

const toastStyles = {
  success: {
    bgColor: 'bg-gradient-to-r from-green-50 to-emerald-50',
    borderColor: 'border-green-200',
    textColor: 'text-green-800',
    iconColor: 'text-green-500',
    iconBg: 'bg-gradient-to-br from-green-500 to-emerald-600',
    shadow: 'shadow-green-100',
    icon: <CheckCircle className="w-5 h-5" />,
  },
  error: {
    bgColor: 'bg-gradient-to-r from-red-50 to-pink-50',
    borderColor: 'border-red-200',
    textColor: 'text-red-800',
    iconColor: 'text-red-500',
    iconBg: 'bg-gradient-to-br from-red-500 to-pink-600',
    shadow: 'shadow-red-100',
    icon: <XCircle className="w-5 h-5" />,
  },
  warning: {
    bgColor: 'bg-gradient-to-r from-yellow-50 to-amber-50',
    borderColor: 'border-yellow-200',
    textColor: 'text-yellow-800',
    iconColor: 'text-yellow-500',
    iconBg: 'bg-gradient-to-br from-yellow-500 to-amber-600',
    shadow: 'shadow-yellow-100',
    icon: <AlertTriangle className="w-5 h-5" />,
  },
  info: {
    bgColor: 'bg-gradient-to-r from-blue-50 to-indigo-50',
    borderColor: 'border-blue-200',
    textColor: 'text-blue-800',
    iconColor: 'text-blue-500',
    iconBg: 'bg-gradient-to-br from-blue-500 to-indigo-600',
    shadow: 'shadow-blue-100',
    icon: <Info className="w-5 h-5" />,
  },
};

export const Toast: React.FC<ToastProps> = ({
  id,
  type,
  title,
  message,
  duration = 5000,
  onClose,
}) => {
  const [isVisible, setIsVisible] = useState(false);
  const [isLeaving, setIsLeaving] = useState(false);
  const [progress, setProgress] = useState(100);

  const styles = toastStyles[type];

  useEffect(() => {
    // Trigger entrance animation
    const entranceTimer = setTimeout(() => {
      setIsVisible(true);
    }, 100);

    // Auto-dismiss timer
    if (duration > 0) {
      const startTime = Date.now();
      const endTime = startTime + duration;

      const progressTimer = setInterval(() => {
        const now = Date.now();
        const remaining = Math.max(0, endTime - now);
        const newProgress = (remaining / duration) * 100;
        setProgress(newProgress);

        if (remaining <= 0) {
          clearInterval(progressTimer);
          handleClose();
        }
      }, 10);

      return () => {
        clearTimeout(entranceTimer);
        clearInterval(progressTimer);
      };
    }

    return () => clearTimeout(entranceTimer);
  }, [duration]);

  const handleClose = () => {
    setIsLeaving(true);
    setTimeout(() => {
      setIsVisible(false);
      onClose(id);
    }, 300); // Match the transition duration
  };

  if (!isVisible) return null;

  return (
    <div
      className={`
        transform transition-all duration-300 ease-in-out
        ${isLeaving ? 'translate-x-full opacity-0 scale-95' : 'translate-x-0 opacity-100 scale-100'}
        max-w-sm w-full bg-white shadow-xl rounded-2xl pointer-events-auto
        border ${styles.borderColor} ${styles.bgColor} ${styles.shadow}
        overflow-hidden relative
        animate-in slide-in-from-right-full fade-in duration-300
      `}
    >
      {/* Progress bar */}
      {duration > 0 && (
        <div className="absolute top-0 left-0 right-0 h-1 bg-gray-200">
          <div 
            className={`h-full transition-all duration-100 ease-linear ${styles.iconBg}`}
            style={{ width: `${progress}%` }}
          />
        </div>
      )}

      <div className="p-4">
        <div className="flex items-start">
          {/* Icon */}
          <div className={`flex-shrink-0 w-10 h-10 ${styles.iconBg} rounded-xl flex items-center justify-center shadow-lg mr-3`}>
            <div className="text-white">
              {styles.icon}
            </div>
          </div>

          {/* Content */}
          <div className="flex-1 min-w-0">
            <p className={`text-sm font-semibold ${styles.textColor} leading-tight`}>
              {title}
            </p>
            {message && (
              <p className={`mt-1 text-sm ${styles.textColor} opacity-90 leading-relaxed`}>
                {message}
              </p>
            )}
          </div>

          {/* Close button */}
          <div className="flex-shrink-0 ml-3">
            <button
              onClick={handleClose}
              className={`
                inline-flex items-center justify-center w-8 h-8 rounded-lg
                ${styles.textColor} hover:bg-white/50 focus:outline-none 
                focus:ring-2 focus:ring-offset-2 focus:ring-offset-transparent
                transition-all duration-200 hover:scale-110
                focus:ring-${type === 'success' ? 'green' : type === 'error' ? 'red' : type === 'warning' ? 'yellow' : 'blue'}-500
              `}
            >
              <span className="sr-only">Close</span>
              <X className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      {/* Decorative elements */}
      <div className="absolute top-0 right-0 w-16 h-16 bg-gradient-to-br from-transparent to-white/20 rounded-full -translate-y-8 translate-x-8"></div>
      <div className="absolute bottom-0 left-0 w-8 h-8 bg-gradient-to-tr from-transparent to-white/10 rounded-full translate-y-4 -translate-x-4"></div>
    </div>
  );
}; 