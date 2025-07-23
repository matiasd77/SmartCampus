import React from 'react';
import { Link } from 'react-router-dom';
import { 
  Megaphone, 
  Bell, 
  Award, 
  BookOpen, 
  Activity, 
  Clock,
  ArrowRight
} from 'lucide-react';

interface Activity {
  id: number;
  type: string;
  description: string;
  userName?: string;
  timestamp: string;
}

interface ActivityFeedProps {
  activities: Activity[];
  isLoading: boolean;
  error?: string;
}

export const ActivityFeed: React.FC<ActivityFeedProps> = ({
  activities,
  isLoading,
  error
}) => {
  const formatActivityTime = (timestamp: string) => {
    const now = new Date();
    const activityTime = new Date(timestamp);
    const diffInMinutes = Math.floor((now.getTime() - activityTime.getTime()) / (1000 * 60));
    
    if (diffInMinutes < 1) return 'Just now';
    if (diffInMinutes < 60) return `${diffInMinutes}m ago`;
    if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)}h ago`;
    return `${Math.floor(diffInMinutes / 1440)}d ago`;
  };

  const getActivityIcon = (type: string) => {
    const iconClasses = "w-6 h-6 text-white";
    switch (type) {
      case 'announcement':
        return <Megaphone className={iconClasses} />;
      case 'notification':
        return <Bell className={iconClasses} />;
      case 'grade':
        return <Award className={iconClasses} />;
      case 'course':
        return <BookOpen className={iconClasses} />;
      default:
        return <Activity className={iconClasses} />;
    }
  };

  const getActivityColor = (type: string) => {
    switch (type) {
      case 'announcement':
        return {
          bg: 'bg-gradient-to-r from-indigo-50 to-violet-50',
          border: 'border-indigo-200/50',
          hover: 'hover:border-indigo-300 hover:shadow-indigo-100',
          iconBg: 'bg-gradient-to-br from-indigo-500 to-violet-600',
          text: 'text-indigo-700'
        };
      case 'notification':
        return {
          bg: 'bg-gradient-to-r from-violet-50 to-purple-50',
          border: 'border-violet-200/50',
          hover: 'hover:border-violet-300 hover:shadow-violet-100',
          iconBg: 'bg-gradient-to-br from-violet-500 to-purple-600',
          text: 'text-violet-700'
        };
      case 'grade':
        return {
          bg: 'bg-gradient-to-r from-amber-50 to-orange-50',
          border: 'border-amber-200/50',
          hover: 'hover:border-amber-300 hover:shadow-amber-100',
          iconBg: 'bg-gradient-to-br from-amber-500 to-orange-600',
          text: 'text-amber-700'
        };
      case 'course':
        return {
          bg: 'bg-gradient-to-r from-emerald-50 to-green-50',
          border: 'border-emerald-200/50',
          hover: 'hover:border-emerald-300 hover:shadow-emerald-100',
          iconBg: 'bg-gradient-to-br from-emerald-500 to-green-600',
          text: 'text-emerald-700'
        };
      default:
        return {
          bg: 'bg-gradient-to-r from-gray-50 to-slate-50',
          border: 'border-gray-200/50',
          hover: 'hover:border-gray-300 hover:shadow-gray-100',
          iconBg: 'bg-gradient-to-br from-gray-500 to-slate-600',
          text: 'text-gray-700'
        };
    }
  };

  if (isLoading) {
    return (
      <div className="space-y-6">
        {[...Array(3)].map((_, index) => (
          <div key={index} className="flex items-start space-x-4 animate-pulse">
            <div className="w-12 h-12 bg-gray-200 rounded-2xl flex-shrink-0"></div>
            <div className="flex-1 min-w-0">
              <div className="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
              <div className="h-3 bg-gray-200 rounded w-1/2"></div>
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <div className="w-24 h-24 bg-gradient-to-br from-red-100 to-pink-100 rounded-3xl flex items-center justify-center mx-auto mb-8 shadow-xl">
          <Activity className="w-12 h-12 text-red-500" />
        </div>
        <h3 className="text-2xl font-bold text-gray-900 mb-4">Unable to load activity</h3>
        <p className="text-gray-600 mb-8 max-w-md mx-auto text-lg">{error}</p>
        <button className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-indigo-500 to-violet-600 text-white font-semibold rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-300 hover:scale-105">
          <Activity className="w-5 h-5 mr-3" />
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div>
      {activities.length > 0 ? (
        <div className="space-y-6">
          {activities.map((activity, index) => {
            const colors = getActivityColor(activity.type);
            return (
              <div 
                key={activity.id} 
                className={`group relative overflow-hidden p-6 rounded-2xl border ${colors.bg} ${colors.border} ${colors.hover} transition-all duration-300 hover:scale-[1.02]`}
              >
                {/* Timeline connector */}
                {index < activities.length - 1 && (
                  <div className="absolute left-6 top-20 w-0.5 h-12 bg-gradient-to-b from-gray-300 to-transparent"></div>
                )}
                
                {/* Decorative background */}
                <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-transparent to-white/20 rounded-full -translate-y-16 translate-x-16 group-hover:scale-110 transition-transform duration-300"></div>
                
                <div className="flex items-start space-x-5 relative z-10">
                  <div className="flex-shrink-0">
                    <div className={`w-14 h-14 ${colors.iconBg} rounded-2xl flex items-center justify-center shadow-lg group-hover:scale-110 group-hover:shadow-xl transition-all duration-300`}>
                      {getActivityIcon(activity.type)}
                    </div>
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-base font-semibold text-gray-900 group-hover:text-gray-800 transition-colors duration-300 leading-relaxed">
                      {activity.description}
                    </p>
                    <div className="flex items-center mt-3 text-sm text-gray-600">
                      {activity.userName && (
                        <>
                          <span className="font-semibold text-gray-700">{activity.userName}</span>
                          <span className="mx-2 text-gray-400">•</span>
                        </>
                      )}
                      <div className="flex items-center">
                        <Clock className="w-4 h-4 mr-2 text-gray-400" />
                        <span className="font-semibold">{formatActivityTime(activity.timestamp)}</span>
                      </div>
                    </div>
                  </div>
                  <div className="flex-shrink-0 opacity-0 group-hover:opacity-100 transition-all duration-300">
                    <div className="w-10 h-10 bg-white rounded-xl flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform duration-300">
                      <ArrowRight className="w-5 h-5 text-gray-400" />
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
          
          <div className="text-center pt-8 border-t border-gray-100">
            <Link 
              to="/announcements" 
              className="inline-flex items-center px-8 py-4 bg-gradient-to-r from-indigo-500 to-violet-600 text-white font-semibold rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-300 hover:scale-105"
            >
              View all announcements
              <ArrowRight className="w-5 h-5 ml-3" />
            </Link>
          </div>
        </div>
      ) : (
        <div className="text-center py-16">
          <div className="w-24 h-24 bg-gradient-to-br from-gray-100 to-slate-100 rounded-3xl flex items-center justify-center mx-auto mb-8 shadow-xl">
            <Activity className="w-12 h-12 text-gray-400" />
          </div>
          <h3 className="text-2xl font-bold text-gray-900 mb-4">No recent activity</h3>
          <p className="text-gray-600 mb-10 max-w-md mx-auto text-lg">Check the announcements section for the latest updates and stay connected with your campus community.</p>
          <Link 
            to="/announcements" 
            className="inline-flex items-center px-8 py-4 bg-gradient-to-r from-indigo-500 to-violet-600 text-white font-semibold rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-300 hover:scale-105"
          >
            <Megaphone className="w-5 h-5 mr-3" />
            View Announcements
          </Link>
        </div>
      )}
    </div>
  );
}; 