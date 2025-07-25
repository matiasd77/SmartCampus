import React from 'react';

interface CourseSkeletonProps {
  count?: number;
}

export const CourseSkeleton: React.FC<CourseSkeletonProps> = ({ count = 6 }) => {
  return (
    <div className="space-y-6">
      {[...Array(count)].map((_, index) => (
        <div key={`skeleton-${index}`} className="bg-white rounded-2xl shadow-lg border border-gray-200 p-6 animate-pulse overflow-hidden relative">
          <div className="absolute top-0 left-0 w-1 h-full bg-gradient-to-b from-blue-500 to-indigo-600"></div>
          <div className="flex items-start space-x-4">
            <div className="w-10 h-10 bg-gray-200 rounded-xl flex-shrink-0"></div>
            <div className="flex-1 space-y-3">
              <div className="flex items-center justify-between">
                <div className="h-6 bg-gray-200 rounded w-3/4"></div>
                <div className="h-5 bg-gray-200 rounded w-16"></div>
              </div>
              <div className="flex space-x-4">
                <div className="h-4 bg-gray-200 rounded w-20"></div>
                <div className="h-4 bg-gray-200 rounded w-16"></div>
                <div className="h-4 bg-gray-200 rounded w-24"></div>
              </div>
              <div className="h-4 bg-gray-200 rounded w-32"></div>
              <div className="space-y-2">
                <div className="h-3 bg-gray-200 rounded w-full"></div>
                <div className="h-3 bg-gray-200 rounded w-4/5"></div>
              </div>
              <div className="flex space-x-3 pt-2">
                <div className="h-8 bg-gray-200 rounded-lg w-16"></div>
                <div className="h-8 bg-gray-200 rounded-lg w-20"></div>
              </div>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}; 