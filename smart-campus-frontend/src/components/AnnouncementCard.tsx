import React from 'react';
import type { Announcement } from '../types/dashboard';
import { ANNOUNCEMENT_PRIORITIES, ANNOUNCEMENT_TYPES, ANNOUNCEMENT_STATUSES } from '../types/dashboard';
import { 
  Megaphone, 
  User, 
  Calendar, 
  AlertTriangle,
  Eye,
  EyeOff,
  MoreVertical,
  Edit,
  Trash2,
  Archive,
  ArrowRight
} from 'lucide-react';

interface AnnouncementCardProps {
  announcement: Announcement;
  onView: (announcement: Announcement) => void;
  onEdit?: (announcement: Announcement) => void;
  onDelete?: (id: number) => void;
  showActions?: boolean;
}

export const AnnouncementCard: React.FC<AnnouncementCardProps> = ({
  announcement,
  onView,
  onEdit,
  onDelete,
  showActions = false,
}) => {
  const getPriorityInfo = (priority: string) => {
    return ANNOUNCEMENT_PRIORITIES.find(p => p.value === priority) || 
           { value: priority, label: priority, color: 'text-gray-600', bgColor: 'bg-gray-100' };
  };

  const getTypeInfo = (type: string) => {
    return ANNOUNCEMENT_TYPES.find(t => t.value === type) || 
           { value: type, label: type, color: 'text-gray-600' };
  };

  const getStatusInfo = (status: string) => {
    return ANNOUNCEMENT_STATUSES.find(s => s.value === status) || 
           { value: status, label: status, color: 'text-gray-600', bgColor: 'bg-gray-100' };
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInHours = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60));
    
    if (diffInHours < 24) {
      if (diffInHours < 1) return 'Just now';
      return `${diffInHours}h ago`;
    }
    
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const truncateContent = (content: string, maxLength: number = 120) => {
    if (content.length <= maxLength) return content;
    return content.substring(0, maxLength) + '...';
  };

  const priorityInfo = getPriorityInfo(announcement.priority);
  const typeInfo = getTypeInfo(announcement.type);
  const statusInfo = getStatusInfo(announcement.status);

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'URGENT':
        return 'bg-gradient-to-r from-red-500 to-pink-600';
      case 'HIGH':
        return 'bg-gradient-to-r from-orange-500 to-red-600';
      case 'MEDIUM':
        return 'bg-gradient-to-r from-yellow-500 to-orange-600';
      case 'LOW':
        return 'bg-gradient-to-r from-gray-500 to-slate-600';
      default:
        return 'bg-gradient-to-r from-blue-500 to-indigo-600';
    }
  };

  return (
    <div className="bg-white rounded-2xl shadow-lg border border-gray-200 hover:shadow-xl transition-all duration-300 group overflow-hidden relative">
      {/* Priority indicator */}
      <div className={`absolute top-0 left-0 w-1 h-full ${getPriorityColor(announcement.priority)}`}></div>
      
      {/* Background decoration */}
      <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-transparent to-gray-50/50 rounded-full -translate-y-16 translate-x-16 group-hover:scale-110 transition-transform duration-300"></div>
      
      <div className="p-6 relative z-10">
        {/* Header */}
        <div className="flex items-start justify-between mb-4">
          <div className="flex-1">
            <div className="flex items-center mb-3">
              <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg mr-3">
                <Megaphone className="w-5 h-5 text-white" />
              </div>
              <div className="flex-1">
                <h3 className="text-xl font-bold text-gray-900 mb-1 line-clamp-2 group-hover:text-blue-600 transition-colors duration-200">
                  {announcement.title}
                </h3>
                <div className="flex items-center space-x-3 text-sm">
                  <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold ${priorityInfo.bgColor} ${priorityInfo.color}`}>
                    {priorityInfo.label}
                  </span>
                  <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-blue-100 text-blue-700`}>
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
              {onEdit && (
                <button
                  onClick={() => onEdit(announcement)}
                  className="w-8 h-8 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-lg flex items-center justify-center shadow-md hover:shadow-lg transition-all duration-200 hover:scale-110"
                  title="Edit announcement"
                >
                  <Edit className="w-4 h-4 text-white" />
                </button>
              )}
              {onDelete && (
                <button
                  onClick={() => onDelete(announcement.id)}
                  className="w-8 h-8 bg-gradient-to-br from-red-500 to-pink-600 rounded-lg flex items-center justify-center shadow-md hover:shadow-lg transition-all duration-200 hover:scale-110"
                  title="Delete announcement"
                >
                  <Trash2 className="w-4 h-4 text-white" />
                </button>
              )}
            </div>
          )}
        </div>

        {/* Content */}
        <div className="mb-6">
          <p className="text-gray-600 text-sm leading-relaxed line-clamp-3">
            {truncateContent(announcement.content)}
          </p>
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between pt-4 border-t border-gray-100">
          <div className="flex items-center space-x-4 text-sm text-gray-500">
            <div className="flex items-center">
              <User className="w-4 h-4 mr-1 text-gray-400" />
              <span className="font-medium text-gray-700">
                {announcement.createdByUser ? `${announcement.createdByUser.firstName} ${announcement.createdByUser.lastName}` : announcement.createdBy}
              </span>
            </div>
            <div className="flex items-center">
              <Calendar className="w-4 h-4 mr-1 text-gray-400" />
              <span className="font-medium">{formatDate(announcement.createdAt)}</span>
            </div>
          </div>
          <button
            onClick={() => onView(announcement)}
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