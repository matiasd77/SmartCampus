import React from 'react';

interface SummaryCardProps {
  title: string;
  value: string | number;
  icon: React.ReactNode;
  color: 'blue' | 'green' | 'yellow' | 'red' | 'purple' | 'indigo';
  isLoading?: boolean;
  error?: string;
  isLink?: boolean;
  linkTo?: string;
}

const colorConfigs = {
  blue: {
    bg: 'bg-gradient-to-br from-indigo-50 to-violet-50',
    iconBg: 'bg-gradient-primary',
    iconColor: 'text-white',
    border: 'border-indigo-200',
    hover: 'hover:border-indigo-300 hover:shadow-indigo-100',
    text: 'text-indigo-900',
    value: 'text-indigo-700',
    shadow: 'shadow-indigo-100',
    gradient: 'from-indigo-500 to-indigo-600'
  },
  green: {
    bg: 'bg-gradient-to-br from-emerald-50 to-green-50',
    iconBg: 'bg-gradient-secondary',
    iconColor: 'text-white',
    border: 'border-emerald-200',
    hover: 'hover:border-emerald-300 hover:shadow-emerald-100',
    text: 'text-emerald-900',
    value: 'text-emerald-700',
    shadow: 'shadow-emerald-100',
    gradient: 'from-emerald-500 to-emerald-600'
  },
  yellow: {
    bg: 'bg-gradient-to-br from-amber-50 to-yellow-50',
    iconBg: 'bg-gradient-to-br from-amber-500 to-orange-600',
    iconColor: 'text-white',
    border: 'border-amber-200',
    hover: 'hover:border-amber-300 hover:shadow-amber-100',
    text: 'text-amber-900',
    value: 'text-amber-700',
    shadow: 'shadow-amber-100',
    gradient: 'from-amber-500 to-orange-600'
  },
  red: {
    bg: 'bg-gradient-to-br from-red-50 to-pink-50',
    iconBg: 'bg-gradient-to-br from-red-500 to-pink-600',
    iconColor: 'text-white',
    border: 'border-red-200',
    hover: 'hover:border-red-300 hover:shadow-red-100',
    text: 'text-red-900',
    value: 'text-red-700',
    shadow: 'shadow-red-100',
    gradient: 'from-red-500 to-pink-600'
  },
  purple: {
    bg: 'bg-gradient-to-br from-violet-50 to-purple-50',
    iconBg: 'bg-gradient-accent',
    iconColor: 'text-white',
    border: 'border-violet-200',
    hover: 'hover:border-violet-300 hover:shadow-violet-100',
    text: 'text-violet-900',
    value: 'text-violet-700',
    shadow: 'shadow-violet-100',
    gradient: 'from-violet-500 to-violet-600'
  },
  indigo: {
    bg: 'bg-gradient-to-br from-indigo-50 to-blue-50',
    iconBg: 'bg-gradient-primary',
    iconColor: 'text-white',
    border: 'border-indigo-200',
    hover: 'hover:border-indigo-300 hover:shadow-indigo-100',
    text: 'text-indigo-900',
    value: 'text-indigo-700',
    shadow: 'shadow-indigo-100',
    gradient: 'from-indigo-500 to-indigo-600'
  }
};

export const SummaryCard: React.FC<SummaryCardProps> = ({
  title,
  value,
  icon,
  color,
  isLoading = false,
  error,
  isLink = false,
  linkTo
}) => {
  const config = colorConfigs[color];

  if (isLoading) {
    return (
      <div className={`card ${config.border} p-6 loading-pulse overflow-hidden relative`}>
        <div className="absolute inset-0 bg-gradient-to-br from-transparent via-white/20 to-transparent"></div>
        <div className="flex items-center justify-between relative z-10">
          <div className="flex items-center">
            <div className="w-14 h-14 bg-gray-200 rounded-xl flex items-center justify-center">
              <div className="w-7 h-7 bg-gray-300 rounded"></div>
            </div>
            <div className="ml-4">
              <div className="h-4 bg-gray-200 rounded w-24 mb-2"></div>
              <div className="h-8 bg-gray-200 rounded w-16"></div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="card border-red-200 p-6 overflow-hidden relative">
        <div className="absolute inset-0 bg-gradient-to-br from-red-50/50 to-transparent"></div>
        <div className="flex items-center justify-between relative z-10">
          <div className="flex items-center">
            <div className="w-14 h-14 bg-gradient-to-br from-red-500 to-pink-600 rounded-xl flex items-center justify-center shadow-medium">
              <svg className="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
              </svg>
            </div>
            <div className="ml-4">
              <h3 className="text-body-small font-semibold text-gray-900">{title}</h3>
              <p className="text-body-small text-red-600 font-semibold">{error}</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  const CardContent = () => (
    <div className={`card ${config.border} p-6 transition-normal hover:shadow-strong ${config.hover} group overflow-hidden relative`}>
      {/* Gradient overlay */}
      <div className="absolute inset-0 bg-gradient-to-br from-transparent via-white/10 to-transparent opacity-0 group-hover:opacity-100 transition-normal"></div>
      
      {/* Background pattern */}
      <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-transparent to-gray-50/50 rounded-full -translate-y-16 translate-x-16 group-hover:scale-110 transition-normal"></div>
      
      <div className="flex items-center justify-between relative z-10">
        <div className="flex items-center">
          <div className={`w-14 h-14 ${config.iconBg} rounded-xl flex items-center justify-center shadow-medium group-hover:scale-110 group-hover:shadow-strong transition-normal`}>
            <div className={config.iconColor}>
              {icon}
            </div>
          </div>
          <div className="ml-4">
            <h3 className="text-body-small font-semibold text-gray-600 mb-1 group-hover:text-gray-800 transition-normal">{title}</h3>
            <p className={`text-heading-2 font-bold ${config.value} group-hover:scale-105 transition-normal`}>
              {typeof value === 'number' ? value.toLocaleString() : value}
            </p>
          </div>
        </div>
        {isLink && (
          <div className={`w-10 h-10 ${config.iconBg} rounded-xl flex items-center justify-center opacity-0 group-hover:opacity-100 transition-normal group-hover:scale-110 shadow-medium`}>
            <svg className={`w-5 h-5 ${config.iconColor}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </div>
        )}
      </div>
      
      {/* Decorative elements */}
      <div className="absolute bottom-2 right-2 w-8 h-8 bg-gradient-to-br from-transparent to-gray-100/30 rounded-full group-hover:scale-150 transition-normal"></div>
    </div>
  );

  if (isLink && linkTo) {
    return (
      <a href={linkTo} className="block">
        <CardContent />
      </a>
    );
  }

  return <CardContent />;
}; 