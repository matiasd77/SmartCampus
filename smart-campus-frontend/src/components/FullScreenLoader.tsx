import React from 'react';

interface FullScreenLoaderProps {
  message?: string;
}

export const FullScreenLoader: React.FC<FullScreenLoaderProps> = ({ 
  message = "Loading..." 
}) => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
      <div className="text-center">
        {/* Spinner */}
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-indigo-600 mx-auto mb-4"></div>
        
        {/* Loading text */}
        <p className="text-gray-600 text-lg font-medium">{message}</p>
        
        {/* Optional subtitle */}
        <p className="text-gray-500 text-sm mt-2">Please wait while we verify your session...</p>
        
        {/* Dots animation */}
        <div className="flex justify-center mt-4 space-x-1">
          <div className="w-2 h-2 bg-indigo-400 rounded-full animate-bounce"></div>
          <div className="w-2 h-2 bg-indigo-400 rounded-full animate-bounce" style={{ animationDelay: '0.1s' }}></div>
          <div className="w-2 h-2 bg-indigo-400 rounded-full animate-bounce" style={{ animationDelay: '0.2s' }}></div>
        </div>
      </div>
    </div>
  );
}; 