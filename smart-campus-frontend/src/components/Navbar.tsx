import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { getUserDisplayName } from '../utils/userUtils';
import { 
  Home, 
  Megaphone, 
  Bell, 
  Users, 
  GraduationCap, 
  User, 
  Settings, 
  LogOut,
  Menu,
  X,
  BookOpen,
  Award
} from 'lucide-react';

interface NavbarProps {
  showDashboardLink?: boolean;
}

export const Navbar: React.FC<NavbarProps> = ({ showDashboardLink = true }) => {
  const { user, logout } = useAuth();
  const { showSuccess } = useToast();
  const navigate = useNavigate();
  const location = useLocation();
  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    showSuccess('Logged out successfully', 'You have been logged out of your account.');
    navigate('/login');
  };

  const toggleProfileMenu = () => {
    setIsProfileMenuOpen(!isProfileMenuOpen);
  };

  const closeProfileMenu = () => {
    setIsProfileMenuOpen(false);
  };

  const toggleMobileMenu = () => {
    setIsMobileMenuOpen(!isMobileMenuOpen);
  };

  const isActiveLink = (path: string) => {
    return location.pathname === path;
  };

  const getNavLinkClasses = (path: string) => {
    const baseClasses = "flex items-center px-4 py-3 text-sm font-medium rounded-xl transition-all duration-200 group relative";
    const activeClasses = "bg-gradient-to-r from-indigo-500 to-indigo-600 text-white shadow-lg";
    const inactiveClasses = "text-gray-600 hover:text-indigo-600 hover:bg-indigo-50";
    
    return `${baseClasses} ${isActiveLink(path) ? activeClasses : inactiveClasses}`;
  };

  const getMobileNavLinkClasses = (path: string) => {
    const baseClasses = "flex items-center px-4 py-3 text-sm font-medium rounded-xl transition-all duration-200";
    const activeClasses = "bg-indigo-100 text-indigo-700 border-l-4 border-indigo-500";
    const inactiveClasses = "text-gray-600 hover:text-indigo-600 hover:bg-gray-50";
    
    return `${baseClasses} ${isActiveLink(path) ? activeClasses : inactiveClasses}`;
  };

  return (
    <nav className="bg-white/95 backdrop-blur-md shadow-lg border-b border-gray-200/50 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo and Brand */}
          <div className="flex items-center">
            <Link 
              to={showDashboardLink ? "/dashboard" : "/"} 
              className="flex items-center space-x-3 text-xl font-bold text-indigo-600 hover:text-indigo-700 transition-colors duration-200"
            >
              <div className="w-10 h-10 bg-gradient-to-br from-indigo-500 to-violet-600 rounded-xl flex items-center justify-center shadow-lg">
                <GraduationCap className="w-5 h-5 text-white" />
              </div>
              <span className="hidden sm:block">SmartCampus</span>
            </Link>
          </div>

          {/* Desktop Navigation Links */}
          <div className="hidden lg:flex items-center space-x-2">
            {showDashboardLink && (
              <Link 
                to="/dashboard" 
                className={getNavLinkClasses("/dashboard")}
              >
                <Home className="w-4 h-4 mr-2" />
                Dashboard
                {isActiveLink("/dashboard") && (
                  <div className="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-2 h-2 bg-white rounded-full"></div>
                )}
              </Link>
            )}
            
            <Link 
              to="/courses" 
              className={getNavLinkClasses("/courses")}
            >
              <BookOpen className="w-4 h-4 mr-2" />
              Courses
              {isActiveLink("/courses") && (
                <div className="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-2 h-2 bg-white rounded-full"></div>
              )}
            </Link>
            
            <Link 
              to="/announcements" 
              className={getNavLinkClasses("/announcements")}
            >
              <Megaphone className="w-4 h-4 mr-2" />
              Announcements
              {isActiveLink("/announcements") && (
                <div className="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-2 h-2 bg-white rounded-full"></div>
              )}
            </Link>
            
            <Link 
              to="/notifications" 
              className={getNavLinkClasses("/notifications")}
            >
              <Bell className="w-4 h-4 mr-2" />
              Notifications
              {isActiveLink("/notifications") && (
                <div className="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-2 h-2 bg-white rounded-full"></div>
              )}
            </Link>
            
            <Link 
              to="/grades" 
              className={getNavLinkClasses("/grades")}
            >
              <Award className="w-4 h-4 mr-2" />
              Grades
              {isActiveLink("/grades") && (
                <div className="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-2 h-2 bg-white rounded-full"></div>
              )}
            </Link>
            
            {user?.role === 'ADMIN' && (
              <>
                <Link 
                  to="/students" 
                  className={getNavLinkClasses("/students")}
                >
                  <Users className="w-4 h-4 mr-2" />
                  Students
                  {isActiveLink("/students") && (
                    <div className="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-2 h-2 bg-white rounded-full"></div>
                  )}
                </Link>
                <Link 
                  to="/professors" 
                  className={getNavLinkClasses("/professors")}
                >
                  <GraduationCap className="w-4 h-4 mr-2" />
                  Professors
                  {isActiveLink("/professors") && (
                    <div className="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-2 h-2 bg-white rounded-full"></div>
                  )}
                </Link>
              </>
            )}
            
            {/* Professor can also access students */}
            {user?.role === 'PROFESSOR' && (
              <Link 
                to="/students" 
                className={getNavLinkClasses("/students")}
              >
                <Users className="w-4 h-4 mr-2" />
                Students
                {isActiveLink("/students") && (
                  <div className="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-2 h-2 bg-white rounded-full"></div>
                )}
              </Link>
            )}
          </div>

          {/* Right Side - User Info and Profile */}
          <div className="flex items-center space-x-4">
            {/* Welcome Message */}
            <span className="hidden xl:block text-sm font-medium text-gray-700 bg-gray-100 px-3 py-2 rounded-lg">
              Welcome, {getUserDisplayName(user)}
            </span>

            {/* Mobile menu button */}
            <button
              onClick={toggleMobileMenu}
              className="lg:hidden inline-flex items-center justify-center p-2 rounded-xl text-gray-600 hover:text-gray-900 hover:bg-gray-100 transition-colors duration-200"
            >
              {isMobileMenuOpen ? (
                <X className="w-5 h-5" />
              ) : (
                <Menu className="w-5 h-5" />
              )}
            </button>

            {/* Profile Menu */}
            <div className="relative">
              <button
                onClick={toggleProfileMenu}
                className="flex items-center space-x-3 text-gray-600 hover:text-gray-900 transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 rounded-xl p-2 hover:bg-gray-100"
              >
                <div className="w-10 h-10 bg-gradient-to-br from-indigo-100 to-indigo-200 rounded-xl flex items-center justify-center shadow-md">
                  <User className="w-5 h-5 text-indigo-600" />
                </div>
                <span className="hidden md:block text-sm font-medium">{getUserDisplayName(user)}</span>
              </button>

              {/* Profile Dropdown */}
              {isProfileMenuOpen && (
                <div className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-xl border border-gray-200 py-2 z-50">
                  <div className="px-4 py-3 border-b border-gray-100">
                    <p className="text-sm font-medium text-gray-900">{getUserDisplayName(user)}</p>
                    <p className="text-xs text-gray-500 capitalize">{user?.role?.toLowerCase()}</p>
                  </div>
                  
                  <Link
                    to="/profile"
                    onClick={closeProfileMenu}
                    className="flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-indigo-50 hover:text-indigo-700 transition-colors duration-200"
                  >
                    <User className="w-4 h-4 mr-3" />
                    Profile
                  </Link>
                  
                  <Link
                    to="/settings"
                    onClick={closeProfileMenu}
                    className="flex items-center px-4 py-3 text-sm text-gray-700 hover:bg-indigo-50 hover:text-indigo-700 transition-colors duration-200"
                  >
                    <Settings className="w-4 h-4 mr-3" />
                    Settings
                  </Link>
                  
                  <hr className="my-2 border-gray-200" />
                  
                  <button
                    onClick={handleLogout}
                    className="flex items-center w-full px-4 py-3 text-sm text-red-600 hover:bg-red-50 transition-colors duration-200"
                  >
                    <LogOut className="w-4 h-4 mr-3" />
                    Logout
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Mobile Navigation Menu */}
        {isMobileMenuOpen && (
          <div className="lg:hidden border-t border-gray-200 py-4 bg-white/95 backdrop-blur-md">
            <div className="space-y-1">
              {showDashboardLink && (
                <Link 
                  to="/dashboard" 
                  onClick={() => setIsMobileMenuOpen(false)}
                  className={getMobileNavLinkClasses("/dashboard")}
                >
                  <Home className="w-4 h-4 mr-3" />
                  Dashboard
                </Link>
              )}
              
              <Link 
                to="/courses" 
                onClick={() => setIsMobileMenuOpen(false)}
                className={getMobileNavLinkClasses("/courses")}
              >
                <BookOpen className="w-4 h-4 mr-3" />
                Courses
              </Link>
              
              <Link 
                to="/announcements" 
                onClick={() => setIsMobileMenuOpen(false)}
                className={getMobileNavLinkClasses("/announcements")}
              >
                <Megaphone className="w-4 h-4 mr-3" />
                Announcements
              </Link>
              
              <Link 
                to="/notifications" 
                onClick={() => setIsMobileMenuOpen(false)}
                className={getMobileNavLinkClasses("/notifications")}
              >
                <Bell className="w-4 h-4 mr-3" />
                Notifications
              </Link>
              
              <Link 
                to="/grades" 
                onClick={() => setIsMobileMenuOpen(false)}
                className={getMobileNavLinkClasses("/grades")}
              >
                <Award className="w-4 h-4 mr-3" />
                Grades
              </Link>
              
              {user?.role === 'ADMIN' && (
                <>
                  <Link 
                    to="/students" 
                    onClick={() => setIsMobileMenuOpen(false)}
                    className={getMobileNavLinkClasses("/students")}
                  >
                    <Users className="w-4 h-4 mr-3" />
                    Students
                  </Link>
                  <Link 
                    to="/professors" 
                    onClick={() => setIsMobileMenuOpen(false)}
                    className={getMobileNavLinkClasses("/professors")}
                  >
                    <GraduationCap className="w-4 h-4 mr-3" />
                    Professors
                  </Link>
                </>
              )}
              
              {/* Professor can also access students */}
              {user?.role === 'PROFESSOR' && (
                <Link 
                  to="/students" 
                  onClick={() => setIsMobileMenuOpen(false)}
                  className={getMobileNavLinkClasses("/students")}
                >
                  <Users className="w-4 h-4 mr-3" />
                  Students
                </Link>
              )}
            </div>
          </div>
        )}
      </div>

      {/* Click outside to close dropdown */}
      {isProfileMenuOpen && (
        <div 
          className="fixed inset-0 z-40" 
          onClick={closeProfileMenu}
        />
      )}
    </nav>
  );
}; 