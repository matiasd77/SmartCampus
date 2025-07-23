import React from 'react';
import type { Notification } from '../types/dashboard';
import { NOTIFICATION_PRIORITIES, NOTIFICATION_TYPES, NOTIFICATION_STATUSES } from '../types/dashboard';
import { 
  Bell, 
  Calendar, 
  User, 
  Eye, 
  CheckCircle, 
  Archive,
  Trash2,
  ArrowRight,
  Clock,
  AlertCircle,
  Info,
  AlertTriangle,
  CheckSquare
} from 'lucide-react';

interface NotificationCardProps {
  notification: Notification;
  onView: (notification: Notification) => void;
  onMarkAsRead?: (id: number) => void;
  onMarkAsArchived?: (id: number) => void;
  onDelete?: (id: number) => void;
  showActions?: boolean;
}

export const NotificationCard: React.FC<NotificationCardProps> = ({
  notification,
  onView,
  onMarkAsRead,
  onMarkAsArchived,
  onDelete,
  showActions = false,
}) => {
  const getPriorityInfo = (priority: string) => {
    return NOTIFICATION_PRIORITIES.find(p => p.value === priority) || 
           { value: priority, label: priority, color: 'text-gray-600', bgColor: 'bg-gray-100' };
  };

  const getTypeInfo = (type: string) => {
    return NOTIFICATION_TYPES.find(t => t.value === type) || 
           { value: type, label: type, color: 'text-gray-600', bgColor: 'bg-gray-100', icon: '📝' };
  };

  const getStatusInfo = (status: string) => {
    return NOTIFICATION_STATUSES.find(s => s.value === status) || 
           { value: status, label: status, color: 'text-gray-600', bgColor: 'bg-gray-100' };
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInMinutes = Math.floor((now.getTime() - date.getTime()) / (1000 * 60));
    
    if (diffInMinutes < 1) return 'Just now';
    if (diffInMinutes < 60) return `${diffInMinutes}m ago`;
    if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)}h ago`;
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const truncateMessage = (message: string, maxLength: number = 100) => {
    if (message.length <= maxLength) return message;
    return message.substring(0, maxLength) + '...';
  };

  const priorityInfo = getPriorityInfo(notification.priority);
  const typeInfo = getTypeInfo(notification.type);
  const statusInfo = getStatusInfo(notification.status);

  const getNotificationIcon = (type: string) => {
    switch (type) {
      case 'GENERAL':
        return <Bell className="w-5 h-5 text-blue-500" />;
      case 'COURSE':
        return <Info className="w-5 h-5 text-green-500" />;
      case 'GRADE':
        return <CheckSquare className="w-5 h-5 text-yellow-500" />;
      case 'ATTENDANCE':
        return <AlertCircle className="w-5 h-5 text-purple-500" />;
      case 'DEADLINE':
        return <AlertTriangle className="w-5 h-5 text-red-500" />;
      case 'REMINDER':
        return <Clock className="w-5 h-5 text-orange-500" />;
      default:
        return <Bell className="w-5 h-5 text-gray-500" />;
    }
  };

  const getNotificationColor = (type: string, priority: string) => {
    // Priority-based colors override type colors for high/urgent
    if (priority === 'URGENT') {
      return {
        bg: 'bg-gradient-to-r from-red-50 to-pink-50',
        border: 'border-red-200',
        hover: 'hover:border-red-300 hover:shadow-red-100',
        iconBg: 'bg-gradient-to-br from-red-500 to-pink-600'
      };
    }
    if (priority === 'HIGH') {
      return {
        bg: 'bg-gradient-to-r from-orange-50 to-red-50',
        border: 'border-orange-200',
        hover: 'hover:border-orange-300 hover:shadow-orange-100',
        iconBg: 'bg-gradient-to-br from-orange-500 to-red-600'
      };
    }

    // Type-based colors for medium/low priority
    switch (type) {
      case 'GENERAL':
        return {
          bg: 'bg-gradient-to-r from-blue-50 to-indigo-50',
          border: 'border-blue-200',
          hover: 'hover:border-blue-300 hover:shadow-blue-100',
          iconBg: 'bg-gradient-to-br from-blue-500 to-indigo-600'
        };
      case 'COURSE':
        return {
          bg: 'bg-gradient-to-r from-green-50 to-emerald-50',
          border: 'border-green-200',
          hover: 'hover:border-green-300 hover:shadow-green-100',
          iconBg: 'bg-gradient-to-br from-green-500 to-emerald-600'
        };
      case 'GRADE':
        return {
          bg: 'bg-gradient-to-r from-yellow-50 to-amber-50',
          border: 'border-yellow-200',
          hover: 'hover:border-yellow-300 hover:shadow-yellow-100',
          iconBg: 'bg-gradient-to-br from-yellow-500 to-amber-600'
        };
      case 'ATTENDANCE':
        return {
          bg: 'bg-gradient-to-r from-purple-50 to-violet-50',
          border: 'border-purple-200',
          hover: 'hover:border-purple-300 hover:shadow-purple-100',
          iconBg: 'bg-gradient-to-br from-purple-500 to-violet-600'
        };
      case 'DEADLINE':
        return {
          bg: 'bg-gradient-to-r from-red-50 to-pink-50',
          border: 'border-red-200',
          hover: 'hover:border-red-300 hover:shadow-red-100',
          iconBg: 'bg-gradient-to-br from-red-500 to-pink-600'
        };
      case 'REMINDER':
        return {
          bg: 'bg-gradient-to-r from-orange-50 to-amber-50',
          border: 'border-orange-200',
          hover: 'hover:border-orange-300 hover:shadow-orange-100',
          iconBg: 'bg-gradient-to-br from-orange-500 to-amber-600'
        };
      default:
        return {
          bg: 'bg-gradient-to-r from-gray-50 to-slate-50',
          border: 'border-gray-200',
          hover: 'hover:border-gray-300 hover:shadow-gray-100',
          iconBg: 'bg-gradient-to-br from-gray-500 to-slate-600'
        };
    }
  };

  const colors = getNotificationColor(notification.type, notification.priority);

  return (
    <div className={`bg-white rounded-2xl shadow-lg border ${colors.border} hover:shadow-xl transition-all duration-300 group overflow-hidden relative ${
      !notification.read ? 'ring-2 ring-blue-200' : ''
    }`}>
      {/* Unread indicator */}
      {!notification.read && (
        <div className="absolute top-0 left-0 w-1 h-full bg-gradient-to-b from-blue-500 to-indigo-600"></div>
      )}
      
      {/* Background decoration */}
      <div className="absolute top-0 right-0 w-24 h-24 bg-gradient-to-br from-transparent to-white/20 rounded-full -translate-y-12 translate-x-12 group-hover:scale-110 transition-transform duration-300"></div>
      
      <div className="p-6 relative z-10">
        {/* Header */}
        <div className="flex items-start justify-between mb-4">
          <div className="flex-1">
            <div className="flex items-center mb-3">
              <div className={`w-12 h-12 ${colors.iconBg} rounded-xl flex items-center justify-center shadow-lg mr-4 group-hover:scale-110 transition-transform duration-200`}>
                {getNotificationIcon(notification.type)}
              </div>
              <div className="flex-1">
                <div className="flex items-center mb-2">
                  <h3 className={`text-xl font-bold ${notification.read ? 'text-gray-900' : 'text-gray-900'} group-hover:text-blue-600 transition-colors duration-200`}>
                    {notification.title}
                  </h3>
                  {!notification.read && (
                    <span className="ml-3 inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-blue-100 text-blue-800 animate-pulse">
                      New
                    </span>
                  )}
                </div>
                <div className="flex items-center space-x-3 text-sm">
                  <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold ${priorityInfo.bgColor} ${priorityInfo.color}`}>
                    {priorityInfo.label}
                  </span>
                  <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold ${typeInfo.bgColor} ${typeInfo.color}`}>
                    {typeInfo.label}
                  </span>
                  <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold ${statusInfo.bgColor} ${statusInfo.color}`}>
                    {statusInfo.label}
                  </span>
                </div>
              </div>
            </div>
          </div>
          
          {showActions && (
            <div className="flex items-center space-x-2 ml-4 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
              {!notification.read && onMarkAsRead && (
                <button
                  onClick={() => onMarkAsRead(notification.id)}
                  className="w-8 h-8 bg-gradient-to-br from-green-500 to-emerald-600 rounded-lg flex items-center justify-center shadow-md hover:shadow-lg transition-all duration-200 hover:scale-110"
                  title="Mark as read"
                >
                  <CheckCircle className="w-4 h-4 text-white" />
                </button>
              )}
              {notification.status !== 'ARCHIVED' && onMarkAsArchived && (
                <button
                  onClick={() => onMarkAsArchived(notification.id)}
                  className="w-8 h-8 bg-gradient-to-br from-gray-500 to-slate-600 rounded-lg flex items-center justify-center shadow-md hover:shadow-lg transition-all duration-200 hover:scale-110"
                  title="Archive"
                >
                  <Archive className="w-4 h-4 text-white" />
                </button>
              )}
              {onDelete && (
                <button
                  onClick={() => onDelete(notification.id)}
                  className="w-8 h-8 bg-gradient-to-br from-red-500 to-pink-600 rounded-lg flex items-center justify-center shadow-md hover:shadow-lg transition-all duration-200 hover:scale-110"
                  title="Delete notification"
                >
                  <Trash2 className="w-4 h-4 text-white" />
                </button>
              )}
            </div>
          )}
        </div>

        {/* Message */}
        <div className="mb-6">
          <p className={`text-sm leading-relaxed ${notification.read ? 'text-gray-600' : 'text-gray-700'} line-clamp-3`}>
            {truncateMessage(notification.message)}
          </p>
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between pt-4 border-t border-gray-100">
          <div className="flex items-center space-x-4 text-sm text-gray-500">
            {notification.sender && (
              <div className="flex items-center">
                <User className="w-4 h-4 mr-1 text-gray-400" />
                <span className="font-medium text-gray-700">
                  {notification.sender.firstName} {notification.sender.lastName}
                </span>
              </div>
            )}
            <div className="flex items-center">
              <Calendar className="w-4 h-4 mr-1 text-gray-400" />
              <span className="font-medium">{formatDate(notification.createdAt)}</span>
            </div>
            {notification.readAt && (
              <div className="flex items-center">
                <CheckCircle className="w-4 h-4 mr-1 text-gray-400" />
                <span className="font-medium">Read {formatDate(notification.readAt)}</span>
              </div>
            )}
          </div>
          <button
            onClick={() => onView(notification)}
            className="inline-flex items-center px-4 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold rounded-xl shadow-md hover:shadow-lg transition-all duration-200 hover:scale-105 group-hover:from-blue-600 group-hover:to-indigo-700"
          >
            <Eye className="w-4 h-4 mr-2" />
            View Details
            <ArrowRight className="w-4 h-4 ml-2 group-hover:translate-x-1 transition-transform duration-200" />
          </button>
        </div>
      </div>
    </div>
  );
}; 