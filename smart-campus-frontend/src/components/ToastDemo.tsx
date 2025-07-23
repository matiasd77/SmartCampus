import React from 'react';
import { useToast } from '../contexts/ToastContext';
import { 
  CheckCircle, 
  XCircle, 
  AlertTriangle, 
  Info, 
  MessageSquare,
  Sparkles,
  Zap
} from 'lucide-react';

export const ToastDemo: React.FC = () => {
  const { showSuccess, showError, showWarning, showInfo } = useToast();

  const handleShowSuccess = () => {
    showSuccess('Success!', 'Operation completed successfully.');
  };

  const handleShowError = () => {
    showError('Error!', 'Something went wrong. Please try again.');
  };

  const handleShowWarning = () => {
    showWarning('Warning!', 'Please review your input before proceeding.');
  };

  const handleShowInfo = () => {
    showInfo('Information', 'Here is some helpful information.');
  };

  const handleShowLongMessage = () => {
    showInfo(
      'Long Message',
      'This is a very long message that demonstrates how the toast handles longer content. It should wrap properly and still look good with proper line height and spacing.'
    );
  };

  const handleShowNoMessage = () => {
    showSuccess('Success!');
  };

  const handleShowMultiple = () => {
    showSuccess('First Toast', 'This is the first notification.');
    setTimeout(() => showError('Second Toast', 'This is the second notification.'), 500);
    setTimeout(() => showWarning('Third Toast', 'This is the third notification.'), 1000);
    setTimeout(() => showInfo('Fourth Toast', 'This is the fourth notification.'), 1500);
  };

  return (
    <div className="card p-8 overflow-hidden relative">
      {/* Background decoration */}
      <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-indigo-100/50 to-violet-100/50 rounded-full -translate-y-16 translate-x-16"></div>
      <div className="absolute bottom-0 left-0 w-24 h-24 bg-gradient-to-tr from-emerald-100/50 to-indigo-100/50 rounded-full translate-y-12 -translate-x-12"></div>
      
      <div className="relative z-10">
        <div className="flex items-center mb-8">
          <div className="w-12 h-12 bg-gradient-accent rounded-xl flex items-center justify-center shadow-medium mr-4">
            <Sparkles className="w-6 h-6 text-white" />
          </div>
          <div>
            <h3 className="text-heading-2 mb-2">Toast Notifications Demo</h3>
            <p className="text-body text-gray-600">
              Click the buttons below to see different types of toast notifications.
            </p>
          </div>
        </div>
        
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
          <button
            onClick={handleShowSuccess}
            className="btn-secondary p-4 flex items-center justify-center"
          >
            <CheckCircle className="w-5 h-5 mr-2" />
            Success Toast
          </button>
          
          <button
            onClick={handleShowError}
            className="group relative overflow-hidden bg-gradient-to-r from-red-500 to-pink-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-normal hover:scale-105 p-4 flex items-center justify-center"
          >
            <div className="absolute inset-0 bg-gradient-to-r from-red-600 to-pink-700 opacity-0 group-hover:opacity-100 transition-normal"></div>
            <div className="relative flex items-center">
              <XCircle className="w-5 h-5 mr-2" />
              Error Toast
            </div>
          </button>
          
          <button
            onClick={handleShowWarning}
            className="group relative overflow-hidden bg-gradient-to-r from-amber-500 to-orange-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-normal hover:scale-105 p-4 flex items-center justify-center"
          >
            <div className="absolute inset-0 bg-gradient-to-r from-amber-600 to-orange-700 opacity-0 group-hover:opacity-100 transition-normal"></div>
            <div className="relative flex items-center">
              <AlertTriangle className="w-5 h-5 mr-2" />
              Warning Toast
            </div>
          </button>
          
          <button
            onClick={handleShowInfo}
            className="btn-primary p-4 flex items-center justify-center"
          >
            <Info className="w-5 h-5 mr-2" />
            Info Toast
          </button>
          
          <button
            onClick={handleShowLongMessage}
            className="btn-accent p-4 flex items-center justify-center"
          >
            <MessageSquare className="w-5 h-5 mr-2" />
            Long Message
          </button>
          
          <button
            onClick={handleShowNoMessage}
            className="btn-outline p-4 flex items-center justify-center"
          >
            <Zap className="w-5 h-5 mr-2" />
            No Message
          </button>
        </div>

        {/* Special demo button */}
        <div className="text-center">
          <button
            onClick={handleShowMultiple}
            className="group relative overflow-hidden bg-gradient-to-r from-indigo-500 via-violet-500 to-emerald-500 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-normal hover:scale-105 px-8 py-4 flex items-center justify-center mx-auto"
          >
            <div className="absolute inset-0 bg-gradient-to-r from-indigo-600 via-violet-600 to-emerald-600 opacity-0 group-hover:opacity-100 transition-normal"></div>
            <div className="relative flex items-center">
              <Sparkles className="w-5 h-5 mr-2" />
              Show Multiple Toasts
            </div>
          </button>
          <p className="text-caption mt-3">
            Demonstrates staggered toast animations
          </p>
        </div>
      </div>
    </div>
  );
}; 